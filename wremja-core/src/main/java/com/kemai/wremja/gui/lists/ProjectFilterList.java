package com.kemai.wremja.gui.lists;

import static com.kemai.wremja.gui.settings.SettingsConstants.ALL_ITEMS_FILTER_DUMMY;

import java.util.Observable;
import java.util.Observer;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.SortedList;

import com.kemai.swing.util.LabeledItem;
import com.kemai.util.TextResourceBundle;
import com.kemai.wremja.gui.events.WremjaEvent;
import com.kemai.wremja.gui.model.PresentationModel;
import com.kemai.wremja.model.Project;

/**
 * The list containing all projects available for the filter.
 * @author remast
 * TODO: Enhance so that only projects occur in list that there are activities for.
 */
public class ProjectFilterList implements Observer {

    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(ProjectFilterList.class);

    /** The model. */
    private final PresentationModel model;

    public static final Project ALL_PROJECTS_DUMMY = new Project(ALL_ITEMS_FILTER_DUMMY, "*", "*"); //$NON-NLS-1$ //$NON-NLS-2$
    
    public static final Project BILLABLE_PROJECTS_DUMMY = new Project(-5, "*", "*"); //$NON-NLS-1$ //$NON-NLS-2$

    public static final LabeledItem<Project> ALL_PROJECTS_FILTER_ITEM = new LabeledItem<Project>(ALL_PROJECTS_DUMMY,
            "<" + textBundle.textFor("ProjectFilterList.AllProjectsLabel") + ">"); //$NON-NLS-1$
    
    /** The actual list containing all projects. */
    private final EventList<LabeledItem<Project>> projectList;

    /**
     * Creates a new list for the given model.
     * @param model the model to create list for
     */
    public ProjectFilterList(final PresentationModel model) {
        this.model = model;
        this.projectList = new BasicEventList<LabeledItem<Project>>();
        this.model.addObserver(this);

        initialize();
    }

    /**
     * Initializes the list with all projects from model.
     */
    private void initialize() {
        this.projectList.clear();
        this.projectList.add(ALL_PROJECTS_FILTER_ITEM);
        this.projectList.add(new LabeledItem<Project>(BILLABLE_PROJECTS_DUMMY,
                "<" + textBundle.textFor("ProjectFilterList.BillableProjectsLabel") + ">"));

        for (Project activity : this.model.getData().getProjects()) {
            this.addProject(activity);
        }
    }

    public SortedList<LabeledItem<Project>> getProjectList() {
        return new SortedList<LabeledItem<Project>>(this.projectList);
    }

    public void update(final Observable source, final Object eventObject) {
        if (eventObject == null || !(eventObject instanceof WremjaEvent)) {
            return;
        }

        final WremjaEvent event = (WremjaEvent) eventObject;

        switch (event.getType()) {

        case PROJECT_ADDED:
            this.addProject((Project) event.getData());
            break;

        case PROJECT_REMOVED:
            this.removeProject((Project) event.getData());
            break;
        }
    }

    /**
     * Adds the given project to the list.
     * @param project the project to be added
     */
    private void addProject(final Project project) {
        if (project == null) {
            return;
        }

        final LabeledItem<Project> filterItem = new LabeledItem<Project>(project);
        if (!this.projectList.contains(filterItem)) {
            this.projectList.add(new LabeledItem<Project>(project));
        }
    }

    /**
     * Removes the given project from the list.
     * @param project the project to be removed
     */
    private void removeProject(final Project project) {
        if (project == null) {
            return;
        }

        final LabeledItem<Project> filterItem = new LabeledItem<Project>(project);
        if (this.projectList.contains(filterItem)) {
            this.projectList.remove(filterItem);
        }
    }
}
