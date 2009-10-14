package com.kemai.wremja.model.filter;

import org.joda.time.DateTime;

import com.kemai.util.Predicate;
import com.kemai.wremja.model.ProjectActivity;

/**
 * A {@link Predicate} which evaluates to true for all activities in the current month.
 * 
 * @author kutzi
 */
public class CurrentMonthPredicate implements TimePredicate<ProjectActivity> {

    private final DateTime currentTime;

    public CurrentMonthPredicate() {
        this(new DateTime());
    }
    
    /**
     * Constructor just for unit tests!
     */
    CurrentMonthPredicate(DateTime currentTime) {
        this.currentTime = currentTime;
    }

    @Override
    public boolean evaluate(ProjectActivity activity) {
        if(activity == null) {
            return false;
        }
        
        DateTime activityDate = activity.getStart();
        int currentMonth = currentTime.getMonthOfYear();
        int currentYear = currentTime.getYear();

        if( currentMonth == activityDate.getMonthOfYear()
            && currentYear == activityDate.getYear()) {
            return true;
        }
        
        return false;
    }

}
