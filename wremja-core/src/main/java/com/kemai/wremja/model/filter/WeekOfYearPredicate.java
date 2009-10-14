package com.kemai.wremja.model.filter;

import com.kemai.wremja.model.ProjectActivity;

/**
 * Holds for all project activities of one week of the year.
 * @author remast
 * @author kutzi
 */
public class WeekOfYearPredicate implements TimePredicate<ProjectActivity> {

    /**
     * The week of year to check for.
     */
    private final int weekOfYear;

    /**
     * Constructor for a new predicate.
     * @param weekOfYear the week of year of the predicate
     */
    public WeekOfYearPredicate(int weekOfYear) {
        this.weekOfYear = weekOfYear;
    }

    /**
     * Checks if this predicate holds for the given activity.
     * @param activity the activity to check
     * @return <code>true</code> if the given project activity
     * is on that week of year else <code>false</code>
     */
    public final boolean evaluate(final ProjectActivity activity) {
        if (activity == null) {
            return false;
        }

        return this.weekOfYear == activity.getStart().getWeekOfWeekyear();
    }

}
