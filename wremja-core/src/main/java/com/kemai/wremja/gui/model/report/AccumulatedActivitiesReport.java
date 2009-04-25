package com.kemai.wremja.gui.model.report;

import java.util.List;
import java.util.Observable;

import com.kemai.wremja.model.ActivityRepository;
import com.kemai.wremja.model.ProjectActivity;
import com.kemai.wremja.model.filter.Filter;
import com.kemai.wremja.model.report.AccumulatedProjectActivity;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;

/**
 * Report for the accumulated working hours.
 * @author remast
 */
public class AccumulatedActivitiesReport extends Observable {

    /** The data of the report. */
    private ActivityRepository data;

    /** Accumulated activities of the report. */
    private EventList<AccumulatedProjectActivity> accumulatedActivitiesByDay;

    /** The filter by which the tracked data is filtered. */
    protected Filter filter;

    /**
     * Create report from data.
     */
    public AccumulatedActivitiesReport(final ActivityRepository data) {
        this.data = data;
        accumulatedActivitiesByDay = new BasicEventList<AccumulatedProjectActivity>();

        accumulate();
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();

        // accumulate activities for every day
        for (AccumulatedProjectActivity activity : accumulatedActivitiesByDay) {
            result.append(activity.toString()).append(":"); //$NON-NLS-1$
        }

        return "[" + result + "]"; //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Add given activity to the accumulated report.
     * @param activity
     *            the activity to be added
     */
    private void acummulateActivity(final ProjectActivity activity) {
        AccumulatedProjectActivity newAccActivity = new AccumulatedProjectActivity(activity.getProject(), activity
                .getStart(), activity.getDuration());
        if(filter != null && !filter.matchesCriteria(activity))
            return;

        if (this.accumulatedActivitiesByDay.contains(newAccActivity)) {
            AccumulatedProjectActivity accActivity = this.accumulatedActivitiesByDay.get(accumulatedActivitiesByDay
                    .indexOf(newAccActivity));
            accActivity.addTime(newAccActivity.getTime());
        } else {
            this.accumulatedActivitiesByDay.add(newAccActivity);
        }
    }

    /** Utility method for accumulating. */
    protected void accumulate() {
        this.accumulatedActivitiesByDay.clear();

        final List<ProjectActivity> filteredActivities = getFilteredActivities();
        for (ProjectActivity activity : filteredActivities) {
            this.acummulateActivity(activity);
        }
    }

    /**
     * Get all filtered activities.
     * @return all activities after applying the filter.
     */
    private List<ProjectActivity> getFilteredActivities() {
        if (filter != null) {
          return filter.applyFilters(this.data.getActivities());
        } else {
          return this.data.getActivities();
        }
    }

    /**
     * @return the accumulatedActivitiesByDay
     */
    public EventList<AccumulatedProjectActivity> getAccumulatedActivitiesByDay() {
        return accumulatedActivitiesByDay;
    }

    /**
     * @return the filter
     */
    public Filter getFilter() {
        return filter;
    }

    /**
     * @param filter
     *            the filter to set
     */
    protected void setFilter(final Filter filter) {
        this.filter = filter;
        accumulate();
    }
}
