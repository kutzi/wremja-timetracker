package com.kemai.wremja.model.filter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.junit.Test;

import com.kemai.util.Predicate;
import com.kemai.wremja.model.ProjectActivity;

public class DayOfWeekPredicateTest {
    @Test
    public void testEvaluation() {
        Predicate<ProjectActivity> predicate = new DayOfWeekPredicate(DateTimeConstants.TUESDAY);
        
        assertFalse(predicate.evaluate(null));
        
        DateTime start = new DateTime().withDayOfWeek(DateTimeConstants.MONDAY);
        ProjectActivity activity = new ProjectActivity(start, start.plusMinutes(1), null);
        assertFalse(predicate.evaluate(activity));
        
        start  = new DateTime().withDayOfWeek(DateTimeConstants.WEDNESDAY);
        activity = new ProjectActivity(start, start.plusMinutes(1), null);
        assertFalse(predicate.evaluate(activity));
        
        start  = new DateTime().withDayOfWeek(DateTimeConstants.TUESDAY);
        activity = new ProjectActivity(start, start.plusMinutes(1), null);
        assertTrue(predicate.evaluate(activity));

        start = start.minusWeeks(1);
        activity = new ProjectActivity(start, start.plusMinutes(1), null);
        assertTrue(predicate.evaluate(activity));
        
        start = start.plusWeeks(32);
        activity = new ProjectActivity(start, start.plusMinutes(1), null);
        assertTrue(predicate.evaluate(activity));
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testIllegalDayOfWeek() {
        new DayOfWeekPredicate(0);
    }
}
