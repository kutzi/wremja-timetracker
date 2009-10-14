package com.kemai.wremja.model.filter;

import org.joda.time.DateTime;

import com.kemai.util.Predicate;
import com.kemai.wremja.model.ProjectActivity;

/**
 * A {@link Predicate} which evaluates to true for all activities in the current week.
 * 
 * @author kutzi
 */
public class CurrentWeekPredicate implements TimePredicate<ProjectActivity> {

    private final DateTime currentTime;

    public CurrentWeekPredicate() {
        this(new DateTime());
    }
    
    /**
     * Constructor just for unit tests!
     */
    CurrentWeekPredicate(DateTime currentTime) {
        this.currentTime = currentTime;
    }

    @Override
    public boolean evaluate(ProjectActivity activity) {
        if(activity == null) {
            return false;
        }
        
        DateTime activityDate = activity.getStart();
        int currentWeek = currentTime.getWeekOfWeekyear();
        int currentWeekYear = currentTime.getWeekyear();

        if( currentWeek == activityDate.getWeekOfWeekyear()
            && currentWeekYear == activityDate.getWeekyear()) {
            return true;
        }
        
        return false;
    }

}
