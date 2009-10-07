package com.kemai.wremja.model.filter;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;

import com.kemai.util.Predicate;
import com.kemai.wremja.model.ProjectActivity;

/**
 * A {@link Predicate} which evaluates to true for all activities on the current day.
 * 
 * @author kutzi
 */
public class CurrentDayPredicate implements Predicate<ProjectActivity> {

    private final DateMidnight currentDay;

    public CurrentDayPredicate() {
        this(new DateTime());
    }
    
    /**
     * Constructor just for unit tests!
     */
    CurrentDayPredicate(DateTime currentTime) {
        this.currentDay = currentTime.toDateMidnight();
    }

    @Override
    public boolean evaluate(ProjectActivity activity) {
        if(activity == null) {
            return false;
        }
        
        return activity.getDay().toDateMidnight().equals( this.currentDay );
    }
}
