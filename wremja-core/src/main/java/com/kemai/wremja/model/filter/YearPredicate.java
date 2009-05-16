package com.kemai.wremja.model.filter;

import com.kemai.util.Predicate;
import com.kemai.wremja.model.ProjectActivity;

/**
 * Holds for all project activities of one year.
 * @author remast
 */
public class YearPredicate implements Predicate<ProjectActivity> {

    /**
     * The year to check for.
     */
    private final int year;

    /**
     * Constructor for a new predicate.
     * @param dateInYear the year of the predicate
     */
    public YearPredicate(final int year) {
        this.year = year;
    }

    /**
     * Checks if this predicate holds for the given object.
     * @param object the object to check
     * @return <code>true</code> if the given object is a project activity
     * of that year else <code>false</code>
     */
    public final boolean evaluate(final ProjectActivity activity) {
        if (activity == null) {
            return false;
        }

        return this.year == activity.getStart().getYear();
    }

}
