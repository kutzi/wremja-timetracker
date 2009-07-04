package com.kemai.wremja.model.report;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import com.kemai.wremja.model.Project;


public class AccumulatedProjectActivityTest {

    private Project projectA;
    private Project projectB;

    @Before
    public void setup() {
        this.projectA = new Project(1, "A", "A");
        this.projectB = new Project(2, "B", "B");
    }
    
    @Test
    public void testEquals() {
        DateTime dt1 = new DateTime(2009, 1, 1, 0, 0, 0, 0);
        
        {
            AccumulatedProjectActivity a = new AccumulatedProjectActivity(projectA, dt1, 0.0);
            AccumulatedProjectActivity b = new AccumulatedProjectActivity(projectA, dt1.plusHours(5), 0.0);
            
            assertEquals(a, b);
        }
        
        {
            AccumulatedProjectActivity a = new AccumulatedProjectActivity(projectA, dt1, 0.0);
            AccumulatedProjectActivity b = new AccumulatedProjectActivity(projectA, dt1.plusHours(5), 5);
            
            assertEquals(a, b);
        }
        
        {
            AccumulatedProjectActivity a = new AccumulatedProjectActivity(projectA, dt1, 0.0);
            AccumulatedProjectActivity b = new AccumulatedProjectActivity(projectB, dt1, 0.0);
            
            assertFalse(a.equals(b));
        }
        
        {
            AccumulatedProjectActivity a = new AccumulatedProjectActivity(projectA, dt1, 0.0);
            AccumulatedProjectActivity b = new AccumulatedProjectActivity(projectA, dt1.plusDays(1), 0.0);
            
            assertFalse(a.equals(b));
        }
        
        {
            AccumulatedProjectActivity a = new AccumulatedProjectActivity(projectA, dt1, 0.0);
            AccumulatedProjectActivity b = new AccumulatedProjectActivity(projectA, dt1.plusYears(1), 0.0);
            
            assertFalse(a.equals(b));
        }
    }
    
    @Test
    public void testCompareTo() {
        DateTime dt1 = new DateTime(2009, 1, 1, 0, 0, 0, 0);
        
        {
            AccumulatedProjectActivity a = new AccumulatedProjectActivity(projectA, dt1, 0.0);
            AccumulatedProjectActivity b = new AccumulatedProjectActivity(projectA, dt1.plusHours(5), 0.0);
            
            assertEquals(0, a.compareTo(b));
            assertEquals(0, b.compareTo(a));
        }
        
        {
            AccumulatedProjectActivity a = new AccumulatedProjectActivity(projectA, dt1, 0.0);
            AccumulatedProjectActivity b = new AccumulatedProjectActivity(projectA, dt1.plusHours(5), 5);
            
            assertEquals(0, a.compareTo(b));
            assertEquals(0, b.compareTo(a));
        }
        
        {
            AccumulatedProjectActivity a = new AccumulatedProjectActivity(projectA, dt1, 0.0);
            AccumulatedProjectActivity b = new AccumulatedProjectActivity(projectB, dt1, 0.0);
            
            assertTrue(a.compareTo(b) < 0);
            assertTrue(b.compareTo(a) > 0);
        }
        
        {
            AccumulatedProjectActivity a = new AccumulatedProjectActivity(projectA, dt1, 0.0);
            AccumulatedProjectActivity b = new AccumulatedProjectActivity(projectA, dt1.plusDays(1), 0.0);
            
            assertTrue(a.compareTo(b) > 0);
            assertTrue(b.compareTo(a) < 0);
        }
        
        {
            AccumulatedProjectActivity a = new AccumulatedProjectActivity(projectA, dt1, 0.0);
            AccumulatedProjectActivity b = new AccumulatedProjectActivity(projectA, dt1.plusYears(1), 0.0);

            assertTrue(a.compareTo(b) > 0);
            assertTrue(b.compareTo(a) < 0);
        }
    }
}
