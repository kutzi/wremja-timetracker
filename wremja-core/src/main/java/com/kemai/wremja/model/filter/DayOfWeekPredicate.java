package com.kemai.wremja.model.filter;

import org.joda.time.DateTimeConstants;

import com.kemai.util.Predicate;
import com.kemai.wremja.model.ProjectActivity;


public class DayOfWeekPredicate implements Predicate<ProjectActivity> {

    private final int dayOfWeek;
    
    public DayOfWeekPredicate(int dayOfWeek) {
        if (dayOfWeek < DateTimeConstants.MONDAY || dayOfWeek > DateTimeConstants.SUNDAY) {
            throw new IllegalArgumentException("Invalid day of week: " + dayOfWeek);
        }
        this.dayOfWeek = dayOfWeek;
    }
    
    @Override
    public boolean evaluate(ProjectActivity activity) {
        if (activity == null) {
            return false;
        }
        
        return activity.getDay().getDayOfWeek() == this.dayOfWeek;
    }

}
