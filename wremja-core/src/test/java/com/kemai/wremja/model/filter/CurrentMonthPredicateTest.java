package com.kemai.wremja.model.filter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.joda.time.DateTime;
import org.junit.Test;

import com.kemai.util.Predicate;
import com.kemai.wremja.model.ProjectActivity;

public class CurrentMonthPredicateTest {
    
    @Test
    public void testEvaluation() {
        Predicate<ProjectActivity> predicate = new CurrentMonthPredicate();
        
        assertFalse(predicate.evaluate(null));
        
        DateTime start = new DateTime().minusMonths(1);
        ProjectActivity activity = new ProjectActivity(start, start.plusMinutes(1), null);
        assertFalse(predicate.evaluate(activity));
        
        start  = new DateTime().plusMonths(1);
        activity = new ProjectActivity(start, start.plusMinutes(1), null);
        assertFalse(predicate.evaluate(activity));
        
        start  = new DateTime();
        activity = new ProjectActivity(start, start.plusMinutes(1), null);
        assertTrue(predicate.evaluate(activity));

        // don't match in previous year
        start = start.minusYears(1);
        activity = new ProjectActivity(start, start.plusMinutes(1), null);
        assertFalse(predicate.evaluate(activity));
        
        // and also not 9 years in the future
        start = start.plusYears(10);
        activity = new ProjectActivity(start, start.plusMinutes(1), null);
        assertFalse(predicate.evaluate(activity));
    }
}
