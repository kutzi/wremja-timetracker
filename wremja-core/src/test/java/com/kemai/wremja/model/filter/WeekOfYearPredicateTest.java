package com.kemai.wremja.model.filter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.joda.time.DateTime;
import org.junit.Test;

import com.kemai.util.Predicate;
import com.kemai.wremja.model.ProjectActivity;

public class WeekOfYearPredicateTest {
    
    @Test
    public void testEvaluation() {
        Predicate<ProjectActivity> predicate = new WeekOfYearPredicate(42);
        
        assertFalse(predicate.evaluate(null));
        
        DateTime start = new DateTime().withWeekOfWeekyear(41);
        ProjectActivity activity = new ProjectActivity(start, start.plusMinutes(1), null);
        assertFalse(predicate.evaluate(activity));
        
        start  = new DateTime().withWeekOfWeekyear(43);
        activity = new ProjectActivity(start, start.plusMinutes(1), null);
        assertFalse(predicate.evaluate(activity));
        
        start  = new DateTime().withWeekOfWeekyear(42);
        activity = new ProjectActivity(start, start.plusMinutes(1), null);
        assertTrue(predicate.evaluate(activity));

        start = start.minusYears(1).withWeekOfWeekyear(42);
        activity = new ProjectActivity(start, start.plusMinutes(1), null);
        assertTrue(predicate.evaluate(activity));
        
        start = start.plusYears(10).withWeekOfWeekyear(42);
        activity = new ProjectActivity(start, start.plusMinutes(1), null);
        assertTrue(predicate.evaluate(activity));
    }
}
