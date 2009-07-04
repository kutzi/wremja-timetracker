package com.kemai.wremja.model.report;

import java.util.List;
import java.util.Observable;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.SortedList;

import com.kemai.wremja.model.ActivityRepository;
import com.kemai.wremja.model.ProjectActivity;
import com.kemai.wremja.model.ReadableRepository;
import com.kemai.wremja.model.filter.Filter;

public class AccumulatedActivitiesReport extends Observable {

    /** The data of the report. */
    private ReadableRepository data;

    /** Accumulated activities of the report. */
    private SortedList<AccumulatedProjectActivity> accumulatedActivitiesByDay;

    /** The filter by which the tracked data is filtered. */
    protected Filter filter;

    /**
     * Create report from data.
     */
    public AccumulatedActivitiesReport(final ReadableRepository data, Filter filter) {
        this.data = data;
        this.filter = filter;
        this.accumulatedActivitiesByDay = new SortedList<AccumulatedProjectActivity>(
                new BasicEventList<AccumulatedProjectActivity>());

        accumulate();
    }

    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder();

        // accumulate activities for every day
        for (AccumulatedProjectActivity activity : accumulatedActivitiesByDay) {
            result.append(activity.toString() + ":"); //$NON-NLS-1$
        }

        return "[" + result.toString() + "]"; //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Add given activity to the accumulated report.
     * 
     * @param activity
     *            the activity to be added
     */
    public void acummulateActivity(final ProjectActivity activity) {
        AccumulatedProjectActivity newAccActivity = new AccumulatedProjectActivity(activity.getProject(), activity
                .getStart(), activity.getDuration());
        if (filter != null && !filter.matchesCriteria(activity)) {
            return;
        }

        if (this.accumulatedActivitiesByDay.contains(newAccActivity)) {
            final AccumulatedProjectActivity accActivity = this.accumulatedActivitiesByDay.get(accumulatedActivitiesByDay.indexOf(newAccActivity));
            accActivity.addTime(newAccActivity.getTime());
        } else {
            this.accumulatedActivitiesByDay.add(newAccActivity);
        }
    }

    /** Utility method for accumulating. */
    protected void accumulate() {
        this.accumulatedActivitiesByDay.clear();

        for (ProjectActivity activity : getFilteredActivities()) {
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
     * Returns the accumulated activities sorted by day (descending).
     *
     * @return the accumulatedActivitiesByDay
     */
    public SortedList<AccumulatedProjectActivity> getAccumulatedActivitiesByDay() {
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
    public void setFilter(final Filter filter) {
        this.filter = filter;
        accumulate();
    }

    /**
     * @param data
     *            the data to set
     */
    public void setData(final ActivityRepository data) {
        this.data = data;
        accumulate();
    }
}
