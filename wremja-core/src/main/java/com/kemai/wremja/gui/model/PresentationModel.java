package com.kemai.wremja.gui.model;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.util.Collection;
import java.util.Observable;

import javax.swing.Timer;

import org.apache.commons.lang.ObjectUtils;
import org.joda.time.DateTime;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.SortedList;

import com.kemai.util.DateUtils;
import com.kemai.util.TextResourceBundle;
import com.kemai.wremja.gui.GuiConstants;
import com.kemai.wremja.gui.events.WremjaEvent;
import com.kemai.wremja.gui.lists.MonthFilterList;
import com.kemai.wremja.gui.lists.ProjectFilterList;
import com.kemai.wremja.gui.lists.WeekOfYearFilterList;
import com.kemai.wremja.gui.lists.YearFilterList;
import com.kemai.wremja.gui.model.edit.EditStack;
import com.kemai.wremja.gui.model.io.DataBackup;
import com.kemai.wremja.gui.model.report.HoursByDayReport;
import com.kemai.wremja.gui.model.report.HoursByProjectReport;
import com.kemai.wremja.gui.model.report.HoursByWeekReport;
import com.kemai.wremja.gui.model.report.ObservingAccumulatedActivitiesReport;
import com.kemai.wremja.gui.settings.UserSettings;
import com.kemai.wremja.model.ActivityRepository;
import com.kemai.wremja.model.Project;
import com.kemai.wremja.model.ProjectActivity;
import com.kemai.wremja.model.filter.Filter;
import com.kemai.wremja.model.io.ProTrackWriter;

/**
 * The model of the Baralga application. This is the model capturing both the state
 * of the application as well as the application logic.
 * For further information on the pattern see <a href="http://www.martinfowler.com/eaaDev/PresentationModel.html">presentation model</a>.
 * @author remast
 */
public class PresentationModel extends Observable {

    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(GuiConstants.class);

    /** The list of projects. */
    private final SortedList<Project> projectList;

    /** The list of project activities. */
    private final SortedList<ProjectActivity> activitiesList;

    /** The currently selected project. */
    private Project selectedProject;

    /** The description of the current activity. */
    private String description;

    /** Flag indicating whether selected project is active or not. */
    private boolean active;

    /** Start date of activity. */
    private DateTime start;

    /** Stop date of activity. */
    private DateTime stop;

    /** The data file that is presented by this model. */
    private volatile ActivityRepository data;

    /** Current activity filter. */
    private Filter filter;

    /** The stack of edit actions (for undo and redo). */
    private EditStack editStack;

    /** Timer for the time passed since activity was started. */
    private final Timer timer;
    
    //private final Object startStopLock = new Object();

    /**
     * Creates a new model.
     */
    public PresentationModel() {
        this.data = new ActivityRepository();
        this.projectList = new SortedList<Project>(new BasicEventList<Project>());
        this.activitiesList = new SortedList<ProjectActivity>(new BasicEventList<ProjectActivity>());

        initialize();
        
        // Fire timer event every minute
        this.timer = new Timer(1000 * 60, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Fire event
                final WremjaEvent event = new WremjaEvent(WremjaEvent.Type.DURATION_CHANGED, PresentationModel.this);
                event.setData(Double.valueOf(getCurrentDuration()));
                
                
                PresentationModel.this.notify(event);
            }
        });
    }
    
    public PresentationModel(ActivityRepository data) {
        this();
        setData(data, false);
    }

    /**
     * Initializes the model.
     */
    private void initialize() {
        this.active = this.data.isActive();
        this.start = this.data.getStart();
        this.selectedProject = this.data.getActiveProject();

        this.projectList.clear();
        this.projectList.addAll(this.data.getProjects());        

        this.activitiesList.clear();

        // Set restored filter from settings
        setFilter(UserSettings.instance().restoreFromSettings(), this);

        // b) restore project (can be done here only as we need to search all projects)
        final long selectedProjectId = UserSettings.instance().getFilterSelectedProjectId(ProjectFilterList.ALL_PROJECTS_DUMMY_VALUE);
        filter.setProject(this.data.findProjectById(selectedProjectId));
        applyFilter();

        this.description = UserSettings.instance().getLastDescription();

        // If there is a active project that has been started on another day,
        // we end it here.
        if (active && start.getDayOfYear() != DateUtils.getNow().getDayOfYear() ) {
            try {
                stop();
            } catch (ProjectActivityStateException e) {
                // Ignore
            }
        }
        
        // If last activity is still active, we must restart the timer
        if( active ) {
            this.timer.start();
        }

        // Edit stack
        if (editStack == null) {
            editStack = new EditStack(this);
            this.addObserver(editStack);
        }
    }

    private void applyFilter() {
        this.activitiesList.clear();

        if (this.filter == null) {
            this.activitiesList.addAll(this.data.getActivities());
        } else {
            this.activitiesList.addAll(this.filter.applyFilters(this.data.getActivities()));
        }
    }

    /**
     * Add the given project.
     * @param project the project to add
     * @param source the source of the edit activity
     */
    public final void addProject(final Project project, final Object source) {
        getData().add(project);
        this.projectList.add(project);

        final WremjaEvent event = new WremjaEvent(WremjaEvent.Type.PROJECT_ADDED, source);
        event.setData(project);

        notify(event);
    }

    /**
     * Remove the given project.
     * @param project the project to remove
     * @param source the source of the edit activity
     */
    public final void removeProject(final Project project, final Object source) {
        getData().remove(project);
        this.projectList.remove(project);

        final WremjaEvent event = new WremjaEvent(WremjaEvent.Type.PROJECT_REMOVED, source);
        event.setData(project);

        notify(event);
    }

    /**
     * Start a project activity at the given time.<br/>
     * <em>This method is meant for unit testing only!!</em>
     * @throws ProjectActivityStateException if there is already a running project
     *   or if no project is selected, currently
     * @throws NullPointerException if startTime is null
     */
    void start(final DateTime startTime) throws ProjectActivityStateException {
        if (getSelectedProject() == null) {
            throw new ProjectActivityStateException(textBundle.textFor("PresentationModel.NoActiveProjectSelectedError")); //$NON-NLS-1$
        }
        
        if (isActive()) {
            throw new ProjectActivityStateException("There is already an activity running"); // TODO L10N
        }

        // Mark as active
        setActive(true);

        setStart(startTime);
        getData().start(startTime);

        this.timer.start();
        
        // Fire start event
        final WremjaEvent event = new WremjaEvent(WremjaEvent.Type.PROJECT_ACTIVITY_STARTED);
        notify(event);
    }

    /**
     * Start a project activity.
     * @throws ProjectActivityStateException if there is already a running project
     */
    public final void start() throws ProjectActivityStateException {
        start(DateUtils.getNow());
    }

    /**
     * Helper method to notify all observers of an event.
     * @param event the event to forward to the observers
     */
    private void notify(final WremjaEvent event) {
        setChanged();
        notifyObservers(event);
    }

    /**
     * Fires an event that a projects property has changed.
     * @param changedProject the project that's changed
     * @param propertyChangeEvent the event to fire
     */
    public void fireProjectChangedEvent(final Project changedProject, final PropertyChangeEvent propertyChangeEvent) {
        final WremjaEvent event = new WremjaEvent(WremjaEvent.Type.PROJECT_CHANGED);
        event.setData(changedProject);
        event.setPropertyChangeEvent(propertyChangeEvent);

        notify(event);
    }
    
    /**
     * Fires an event that a project activity's property has changed.
     * @param changedActivity the project activity that's changed
     * @param propertyChangeEvent the event to fire
     */
    public void fireProjectActivityChangedEvent(final ProjectActivity changedActivity, final PropertyChangeEvent propertyChangeEvent) {
        final WremjaEvent event = new WremjaEvent(WremjaEvent.Type.PROJECT_ACTIVITY_CHANGED);
        event.setData(changedActivity);
        event.setPropertyChangeEvent(propertyChangeEvent);

        notify(event);
    }

    /**
     * Stop a project activity.
     * @throws ProjectActivityStateException if there is no running project
     * @see #stop(boolean)
     */
    public final void stop() throws ProjectActivityStateException {
        // Stop with notifying observers.
        stop(true);
    }

    /**
     * Stops the currently active project activity.
     *
     * @throws ProjectActivityStateException if there is no running project
     */
    public final void stop(final boolean notifyObservers) throws ProjectActivityStateException {
        stop(DateUtils.getNow(), notifyObservers);
    }
    
    public final synchronized void stop(DateTime stopTime, boolean notifyObservers) throws ProjectActivityStateException {
        stop(stopTime, notifyObservers, false);
    }
    
    /**
     * Stops a project activity at the given stopTime.
     * DON'T USE THIS METHOD - unless you really know what you do.
     * Use {{@link #stop()} instead!
     *
     * @param notifyObservers should observers be notified about the new activity (false during shutdown)
     * @param force force creation of activity i.e. overriding UserSettings.instance().isDiscardEmptyActivities()
     * @return the new {@link ProjectActivity} that was created
     *   or null if the activity had zero duration and 'purge.emptyActivities' is true
     * @throws ProjectActivityStateException if there is no running project
     */
    public final synchronized ProjectActivity stop(DateTime stopTime, boolean notifyObservers,
            boolean force) throws ProjectActivityStateException {
        if (!isActive()) {
            throw new ProjectActivityStateException(textBundle.textFor("PresentationModel.NoActiveProjectError")); //$NON-NLS-1$
        }

        if( !force && UserSettings.instance().isDiscardEmptyActivities()) {
            if( this.start.getMinuteOfDay() == stopTime.getMinuteOfDay() ) {
                
                clearOldActivity(notifyObservers);
                // Create Stop Event
                WremjaEvent event = new WremjaEvent(WremjaEvent.Type.PROJECT_ACTIVITY_STOPPED);
                notify(event);
                return null;
            }
        }
        
        WremjaEvent eventOnEndDay = null;
        DateTime stop2 = null;

        final ProjectActivity activityOnStartDay;
        final ProjectActivity lastActivity;
        // If start is on a different day from now end the activity at 0:00 one day after start.
        // Also make a new activity from 0:00 the next day until the stop time of the next day.
        if (!org.apache.commons.lang.time.DateUtils.isSameDay(start.toDate(), stopTime.toDate())) {
            // FIXME: this probably doesn't work if start is not yesterday, but maybe 3 days ago!
            DateTime dt = new DateTime(start);
            dt = dt.plusDays(1);

            stop = dt.toDateMidnight().toDateTime();
            
            activityOnStartDay = new ProjectActivity(start, stop,
                    getSelectedProject(), this.description);
            getData().addActivity(activityOnStartDay);
            this.activitiesList.add(activityOnStartDay);

            stop2 = DateUtils.getNow();
            final DateTime start2 = stop;

            final ProjectActivity activityOnEndDay = new ProjectActivity(start2, stop2,
                    getSelectedProject(), this.description);
            getData().addActivity(activityOnEndDay);
            this.activitiesList.add(activityOnEndDay);
            lastActivity = activityOnEndDay;

            // Create Event for Project Activity
            eventOnEndDay  = new WremjaEvent(WremjaEvent.Type.PROJECT_ACTIVITY_ADDED);
            eventOnEndDay.setData(activityOnEndDay);
        } else {
            stop = stopTime;
            activityOnStartDay = new ProjectActivity(start, stop,
                    getSelectedProject(), this.description);
            getData().addActivity(activityOnStartDay);
            this.activitiesList.add(activityOnStartDay);
            lastActivity = activityOnStartDay;
        }

        clearOldActivity(notifyObservers);

        if (notifyObservers) {
            // Create Event for Project Activity
            WremjaEvent event  = new WremjaEvent(WremjaEvent.Type.PROJECT_ACTIVITY_ADDED);
            event.setData(activityOnStartDay);
            notify(event);

            if (eventOnEndDay != null)  {
                notify(eventOnEndDay);
                stop = stop2;
            }

            // Create Stop Event
            event = new WremjaEvent(WremjaEvent.Type.PROJECT_ACTIVITY_STOPPED);
            notify(event);
        }
        
        return lastActivity;
    }

    private void clearOldActivity(boolean notifyObservers) {
        // Clear old activity
        description = "";
        UserSettings.instance().setLastDescription(description);
        setActive(false);
        getData().stop();
        setStart(null, notifyObservers);
        this.timer.stop();
    }

    /**
     * Changes to the given project.
     * @param activeProject the new active project
     */
    public final void changeProject(final Project activeProject) {
        // If there's no change we're done.
        if (ObjectUtils.equals(getSelectedProject(), activeProject)) {
            return;
        }

        // Store previous project
        final Project previousProject = getSelectedProject();

        // Set selected project to new project
        this.selectedProject = activeProject;

        // Set active project to new project
        this.data.setActiveProject(activeProject);

        final DateTime now = DateUtils.getNow();

        // If a project is currently running we create a new project activity.
        if (isActive()) {
            // 1. Stop the running project.
            setStop(now);

            // 2. Track recorded project activity.
            if( stop.getMinuteOfDay() != start.getMinuteOfDay()
                || !UserSettings.instance().isDiscardEmptyActivities() ) {
                final ProjectActivity activity = new ProjectActivity(start, stop, previousProject, description);
    
                getData().addActivity(activity);
                this.activitiesList.add(activity);
                
                // 3. Broadcast project activity event.
                final WremjaEvent event = new WremjaEvent(WremjaEvent.Type.PROJECT_ACTIVITY_ADDED);
                event.setData(activity);
                notify(event);
            }

            // Clear description
            description = "";
            UserSettings.instance().setLastDescription(description);

            // Set start time to now.
            setStart(now);
        }

        // Fire project changed event
        final WremjaEvent event = new WremjaEvent(WremjaEvent.Type.PROJECT_CHANGED);
        event.setData(activeProject);
        notify(event);
    }

    /**
     * Save the model.
     * @throws Exception on error during saving
     */
    public final void save() throws Exception {
        // save last touch
        UserSettings.instance().setLastTouchTimestamp(new DateTime());
        
        // If there are no changes there's nothing to do.
        if (!data.isDirty())  {
            return;
        }

        // do a backup of the old file first
        final File proTrackFile = new File(UserSettings.instance().getDataFileLocation());
        DataBackup.createBackup(proTrackFile);

        // Save changed data to disk.
        final ProTrackWriter writer = new ProTrackWriter(data);
        writer.write(proTrackFile);        
        this.data.setDirty(false);
    }

    /**
     * Add a new activity to the model.
     * @param activity the activity to add
     */
    public final void addActivity(final ProjectActivity activity, final Object source) {
        getData().addActivity(activity);

        // Add activity if there is no filter or the filter matches
        if (this.filter == null || this.filter.matchesCriteria(activity)) {
            this.getActivitiesList().add(activity);
        }

        // Fire event
        final WremjaEvent event = new WremjaEvent(WremjaEvent.Type.PROJECT_ACTIVITY_ADDED, source);
        event.setData(activity);
        notify(event);
    }

    /**
     * Remove an activity from the model.
     * @param activity the activity to remove
     */
    public final void removeActivity(final ProjectActivity activity, final Object source) {
        getData().removeActivity(activity);
        this.getActivitiesList().remove(activity);

        // Fire event
        final WremjaEvent event = new WremjaEvent(WremjaEvent.Type.PROJECT_ACTIVITY_REMOVED, source);
        event.setData(activity);
        notify(event);
    }
    
    /**
     * Remove a collection of activities from the model.
     * @param activities the activities to remove
     */
    public final void removeActivities(final Collection<ProjectActivity> activities, final Object source) {
        getData().removeActivities(activities);
        this.getActivitiesList().removeAll(activities);

        // Fire event
        final WremjaEvent event = new WremjaEvent(WremjaEvent.Type.PROJECT_ACTIVITY_REMOVED, source);
        event.setData(activities);
        notify(event);
    }

    /**
     * Remove an activity from the model.
     * @param activity the activity to remove
     */
    public final void replaceActivity(final ProjectActivity oldActivity, final ProjectActivity newActivity,
            final Object source) {
        getData().replaceActivity(oldActivity, newActivity);

        // Remove activity if there is no filter or the filter matches
        if (this.filter == null || this.filter.matchesCriteria(oldActivity)) {
            this.getActivitiesList().remove(oldActivity);
        }
        
        // Add activity if there is no filter or the filter matches
        if (this.filter == null || this.filter.matchesCriteria(newActivity)) {
            this.getActivitiesList().add(newActivity);
        }

        // Fire events
        // TODO: because of the way the events are evaluated in the observers are evaluated
        // order of the events is currently important: first ADDED then REMOVED
        final WremjaEvent event2 = new WremjaEvent(WremjaEvent.Type.PROJECT_ACTIVITY_ADDED, source);
        event2.setData(newActivity);
        notify(event2);
        
        final WremjaEvent event = new WremjaEvent(WremjaEvent.Type.PROJECT_ACTIVITY_REMOVED, source);
        event.setData(oldActivity);
        notify(event);
    }

    /**
     * Getter for the list of projects.
     * @return the list with all projects
     */
    public SortedList<Project> getProjectList() {
        return projectList;
    }

    /**
     * Getter for the list of project activities.
     * @return the list with all project activities
     */
    public SortedList<ProjectActivity> getActivitiesList() {
        return activitiesList;
    }

    public ProjectFilterList getProjectFilterList() {
        return new ProjectFilterList(this);
    }

    /**
     * Get all years in which there are project activities.
     * @return List of years with activities as String.
     */
    public YearFilterList getYearFilterList() {
        return new YearFilterList(this);
    }

    /**
     * Get all months in which there are project activities.
     * @return List of months with activities as String.
     */
    public MonthFilterList getMonthFilterList() {
        return new MonthFilterList(this);
    }

    /**
     * Get all weeks in which there are project activities.
     * @return List of weeks with activities as String.
     */
    public WeekOfYearFilterList getWeekFilterList() {
        return new WeekOfYearFilterList(this);
    }

    public ObservingAccumulatedActivitiesReport getFilteredReport() {
        return new ObservingAccumulatedActivitiesReport(this);
    }

    public HoursByWeekReport getHoursByWeekReport() {
        return new HoursByWeekReport(this);
    }

    public HoursByDayReport getHoursByDayReport() {
        return new HoursByDayReport(this);
    }

    public HoursByProjectReport getHoursByProjectReport() {
        return new HoursByProjectReport(this);
    }

    /**
     * Gets the start of the current activity.
     * @return the start
     */
    public DateTime getStart() {
        return start;
    }
    
    /**
     * Returns the current duration of the running activity or <code>0</code>
     * if nor activity is running.
     */
    public double getCurrentDuration() {
        if( !isActive() ) {
            return 0D;
        } else {
            return DateUtils.getDurationAsFractionHours(getStart(), DateUtils.getNow());
        }
    }

    /**
     * Sets the start of a new activity.
     * @param start the start to set
     */
    public void setStart(final DateTime start) {
        setStart(start, true);
    }
    
    private void setStart(final DateTime start, boolean notify) {
        // TODO: synchronized (this.startStopLock) {
            this.start = start;
            this.data.setStartTime(start);
        //}
        
        if(notify) {
            // Fire event
            final WremjaEvent event = new WremjaEvent(WremjaEvent.Type.START_CHANGED, this);
            event.setData(start);
            
            notify(event);
        }
    }

    /**
     * @return the stop
     */
    public DateTime getStop() {
        return stop;
    }

    /**
     * @param stop the stop to set
     */
    private void setStop(final DateTime stop) {
        this.stop = stop;
    }

    /**
     * Checks whether a project activity is currently running.
     * @return the active
     */
    public boolean isActive() {
        return active;
    }

    /**
     * @param active the active to set
     */
    public void setActive(final boolean active) {
        this.active = active;
    }

    /**
     * @return the activeProject
     */
    public Project getSelectedProject() {
        return selectedProject;
    }

    /**
     * @return the data
     */
    public ActivityRepository getData() {
        return data;
    }

    public void setData(final ActivityRepository data) {
        setData(data, true);
    }
    
    /**
     * @param data the data to set
     */
    public void setData(final ActivityRepository data, boolean setDirty) {
        if (ObjectUtils.equals(this.data, data)) {
            return;
        }

        this.data = data;
        
        initialize();
        
        if(setDirty) {
            this.data.setDirty( true );
        }

        // Fire event
        final WremjaEvent event = new WremjaEvent(WremjaEvent.Type.DATA_CHANGED, this);
        notify(event);
    }

    /**
     * @return the filter
     */
    public Filter getFilter() {
        return filter;
    }

    /**
     * @param filter the filter to set
     * @param source the source of the new filter
     */
    public void setFilter(final Filter filter, final Object source) {
        if (ObjectUtils.equals(this.filter, filter)) {
            return;
        }
        // Store filter
        this.filter = filter;

        applyFilter();

        // Fire event
        final WremjaEvent event = new WremjaEvent(WremjaEvent.Type.FILTER_CHANGED, source);
        event.setData(filter);

        notify(event);
    }

    /**
     * Returns the description of the current activity.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the current activity.
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * @return the editStack
     */
    public EditStack getEditStack() {
        return editStack;
    }

    public boolean isDirty() {
        return this.data.isDirty();
    }
}
