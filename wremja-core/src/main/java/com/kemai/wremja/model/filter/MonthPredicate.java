package com.kemai.wremja.model.filter;

import com.kemai.util.Predicate;
import com.kemai.wremja.model.ProjectActivity;

/**
 * Holds for all project activities of one month.
 * @author remast
 */
public class MonthPredicate implements Predicate<ProjectActivity> {

    /**
     * The month to check for.
     */
    private final int month;

    /**
     * Creates a new predicate that holds for the given month.
     * @param dateInMonth the month the predicate holds for
     */
    public MonthPredicate(int month) {
        this.month = month;
    }

    /**
     * Checks if this predicate holds for the given object.
     * @param object the object to check
     * @return <code>true</code> if the given object is a project activity
     * of that month else <code>false</code>
     */
    public boolean evaluate(final ProjectActivity object) {
        if (object == null) {
            return false;
        }

        final ProjectActivity activity = object;
        return this.month == activity.getStart().getMonthOfYear();
    }

}
