package com.kemai.wremja.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.joda.time.DateTime;
import org.junit.Test;

import com.kemai.util.DateUtils;
import com.kemai.wremja.AbstractWremjaTestCase;

public class ProjectActivityTest extends AbstractWremjaTestCase {

	@Test
    public void testCalculateDuration() {
        ProjectActivity act = new ProjectActivity(new DateTime(0L), new DateTime(1000L * 60 * 60 * 30), null);
        DateTime startTime = DateUtils.getNow();

        act = new ProjectActivity(startTime, startTime.plusMinutes(45), null);
        assertEquals(0.75, act.getDuration(), 0.001);

        act = new ProjectActivity(startTime, startTime.plusMinutes(30), null);
        assertEquals(0.5, act.getDuration(), 0.001);

        act = new ProjectActivity(startTime, startTime.plusHours(1).plusMinutes(30), null);
        assertEquals(1.5, act.getDuration(), 0.001);
    }

    /**
     * Tests that start and end times are on the same day,
     * unless end time is at 0:00h in which case end date is on the next day.
     */
	@Test
    public void testStartAndEndOnSameDay() {
        ProjectActivity act = new ProjectActivity(new DateTime(2009, 1, 1, 0, 0, 0, 0),
                new DateTime(2009, 1, 1, 23, 0, 0 ,0 ), null);

        assertEquals( 1, act.getStart().getDayOfMonth() );
        assertEquals( 1, act.getEnd().getDayOfMonth() );

        // when end is at 0:00h it must be on the next day
        act.setEndTime(0, 0);
        assertEquals( 2, act.getEnd().getDayOfMonth() );

        // otherwise it must be on the same day as start
        act.setEndTime(12, 0);
        assertEquals(1, act.getEnd().getDayOfMonth());

        // test again: when end is at 0:00h it must be on the next day
        act.setEndTime(0, 0);
        assertEquals( 2, act.getEnd().getDayOfMonth() );

        // start day must not change:
        act.setEndTime(11, 55);
        assertEquals(1, act.getStart().getDayOfMonth());

        act.setEndTime(0, 0);
        assertEquals(1, act.getStart().getDayOfMonth());
    }

    /**
     * Tests that an exception is thrown when someone tries to set
     * end < start.
     */
	@Test
    public void testStartNotAfterEnd() {
        try {
            new ProjectActivity(new DateTime(2009, 1, 1, 13, 0, 0 ,0),
                    new DateTime(2009, 1, 1, 12, 0, 0, 0), null);
            fail("Expected IllegalArgumentException");
        } catch( IllegalArgumentException e) {
            // ok, expected
        }

        try {
            ProjectActivity act = new ProjectActivity(new DateTime(2009, 1, 1, 11, 0, 0 ,0),
                    new DateTime(2009, 1, 1, 12, 0, 0, 0), null);
            act.setEndTime(10, 0);
            fail("Expected IllegalArgumentException");
        } catch( IllegalArgumentException e) {
            // ok, expected
        }

        try {
            ProjectActivity act = new ProjectActivity(new DateTime(2009, 1, 1, 11, 0, 0 ,0),
                    new DateTime(2009, 1, 1, 12, 0, 0, 0), null);
            act.setEndTime(13, 0);
        } catch( IllegalArgumentException e) {
            fail("Unexpected IllegalArgumentException");
        }
    }

    /**
     * Tests the setDay method.
     */
	@Test
    public void testSetDay() {
        {
            ProjectActivity act = new ProjectActivity(
                    new DateTime(2009, 1, 1, 11, 0, 0 ,0),
                    new DateTime(2009, 1, 1, 12, 47, 0, 0), 
                    null
            );
            DateTime day = act.getDay();
            assertEquals(1, day.getDayOfMonth());
            assertEquals(1, day.getMonthOfYear());
            assertEquals(2009, day.getYear());

            act.setDay(new DateTime(2020, 7, 13, 11, 0, 0 ,0));
            day = act.getDay();
            assertEquals(13, day.getDayOfMonth());
            assertEquals(7, day.getMonthOfYear());
            assertEquals(2020, day.getYear());

            DateTime end = act.getEnd();
            assertEquals(13, end.getDayOfMonth());
            assertEquals(7, end.getMonthOfYear());
            assertEquals(2020, end.getYear());
            assertEquals(12, end.getHourOfDay());
            assertEquals(47, end.getMinuteOfHour());
        }

        // these time with an activity ending at 0:00h
        {
            ProjectActivity act = new ProjectActivity(
                    new DateTime(2009, 1, 1, 11, 0, 0 ,0),
                    new DateTime(2009, 1, 2, 0, 0, 0, 0), 
                    null
            );
            DateTime day = act.getDay();
            assertEquals(1, day.getDayOfMonth());
            assertEquals(1, day.getMonthOfYear());
            assertEquals(2009, day.getYear());

            act.setDay(new DateTime(2020, 7, 13, 11, 0, 0 ,0));
            day = act.getDay();
            assertEquals(13, day.getDayOfMonth());
            assertEquals(7, day.getMonthOfYear());
            assertEquals(2020, day.getYear());

            DateTime end = act.getEnd();
            assertEquals(14, end.getDayOfMonth());
            assertEquals(7, end.getMonthOfYear());
            assertEquals(2020, end.getYear());
            assertEquals(0, end.getHourOfDay());
            assertEquals(0, end.getMinuteOfHour());
        }
    }
    
	@Test
    public void testIntersection() {
    	DateTime start = new DateTime(2009, 1, 1, 12, 0, 0, 0);
		DateTime end = new DateTime(2009, 1, 1, 13, 0, 0, 0);
		ProjectActivity a = new ProjectActivity(start,
    			end, null);
		assertTrue(a.hasIntersection(a));
		
		{
			ProjectActivity o = new ProjectActivity(start, end, null);
			assertTrue(a.hasIntersection(o));
			assertTrue(o.hasIntersection(a));
		}
		
		{
			ProjectActivity o = new ProjectActivity(
					start.minusHours(1),
					start, null);
			assertFalse(a.hasIntersection(o));
			assertFalse(o.hasIntersection(a));
		}
		
		{
			ProjectActivity o = new ProjectActivity(end,
					end.plusHours(1),
					null);
			assertFalse(a.hasIntersection(o));
			assertFalse(o.hasIntersection(a));
		}
		
		{
			ProjectActivity o = new ProjectActivity(
					start.plusMinutes(30),
					start.plusMinutes(30),
					null);
			assertTrue(a.hasIntersection(o));
			assertTrue(o.hasIntersection(a));
		}
		
		{
			ProjectActivity o = new ProjectActivity(
					start.plusMinutes(30),
					start.plusMinutes(30),
					null);
			assertTrue(a.hasIntersection(o));
			assertTrue(o.hasIntersection(a));
		}

		{
			ProjectActivity o = new ProjectActivity(
					start.plusMinutes(10),
					end.plusMinutes(10),
					null);
			assertTrue(a.hasIntersection(o));
			assertTrue(o.hasIntersection(a));
		}
		
		{
			ProjectActivity o = new ProjectActivity(
					start.minusMinutes(10),
					end.minusMinutes(10),
					null);
			assertTrue(a.hasIntersection(o));
			assertTrue(o.hasIntersection(a));
		}
		
    }
}
