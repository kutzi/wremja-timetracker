package com.kemai.wremja.model.filter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.junit.Test;

import com.kemai.util.Predicate;
import com.kemai.wremja.model.ProjectActivity;

public class CurrentWeekPredicateTest {
    
    @Test
    public void testEvaluation() {
        Predicate<ProjectActivity> predicate = new CurrentWeekPredicate();
        
        assertFalse(predicate.evaluate(null));
        
        int currentWeek = new DateTime().getWeekOfWeekyear();
        
        DateTime start = new DateTime().withWeekOfWeekyear(currentWeek - 1);
        ProjectActivity activity = new ProjectActivity(start, start.plusMinutes(1), null);
        assertFalse(predicate.evaluate(activity));
        
        start  = new DateTime().withWeekOfWeekyear(currentWeek + 1);
        activity = new ProjectActivity(start, start.plusMinutes(1), null);
        assertFalse(predicate.evaluate(activity));
        
        start  = new DateTime().withWeekOfWeekyear(currentWeek);
        activity = new ProjectActivity(start, start.plusMinutes(1), null);
        assertTrue(predicate.evaluate(activity));

        start = start.minusYears(1).withWeekOfWeekyear(currentWeek);
        activity = new ProjectActivity(start, start.plusMinutes(1), null);
        assertFalse(predicate.evaluate(activity));
        
        start = start.plusYears(10).withWeekOfWeekyear(currentWeek);
        activity = new ProjectActivity(start, start.plusMinutes(1), null);
        assertFalse(predicate.evaluate(activity));
    }
    
    @Test
    public void testWeekOverNewYear() {
        // first week of 2009 started in 2008
        DateTime currentTime = new DateTime().withYear(2009)
            .withMonthOfYear(DateTimeConstants.JANUARY).withDayOfMonth(2);
        Predicate<ProjectActivity> predicate = new CurrentWeekPredicate(currentTime);
        
        DateTime start = new DateTime().withYear(2009)
            .withMonthOfYear(DateTimeConstants.JANUARY).withDayOfMonth(3);
        ProjectActivity activity = new ProjectActivity(start, start.plusMinutes(1), null);
        assertTrue(predicate.evaluate(activity));
        
        start = start.withDayOfMonth(5);
        activity = new ProjectActivity(start, start.plusMinutes(1), null);
        assertFalse(predicate.evaluate(activity));
        
        // start in 2008
        start = new DateTime().withYear(2008)
            .withMonthOfYear(DateTimeConstants.DECEMBER).withDayOfMonth(30);
        activity = new ProjectActivity(start, start.plusMinutes(1), null);
        assertTrue(predicate.evaluate(activity));
    }
    
    @Test
    public void testWeekOverNewYear2() {
        // last week of 2009 ends 2010
        DateTime currentTime = new DateTime().withYear(2009)
            .withMonthOfYear(DateTimeConstants.DECEMBER).withDayOfMonth(31);
        Predicate<ProjectActivity> predicate = new CurrentWeekPredicate(currentTime);
        
        DateTime start = new DateTime().withYear(2009)
            .withMonthOfYear(DateTimeConstants.DECEMBER).withDayOfMonth(30);
        ProjectActivity activity = new ProjectActivity(start, start.plusMinutes(1), null);
        assertTrue(predicate.evaluate(activity));
        
        start = start.withDayOfMonth(27);
        activity = new ProjectActivity(start, start.plusMinutes(1), null);
        assertFalse(predicate.evaluate(activity));
        
        // start in 2010
        start = new DateTime().withYear(2010)
            .withMonthOfYear(DateTimeConstants.JANUARY).withDayOfMonth(2);
        activity = new ProjectActivity(start, start.plusMinutes(1), null);
        assertTrue(predicate.evaluate(activity));
    }
}
