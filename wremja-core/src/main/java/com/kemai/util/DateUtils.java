package com.kemai.util;

import org.joda.time.DateTime;
import org.joda.time.ReadableInstant;

/**
 * Miscellaneous utility methods for dealing with dates.
 * 
 * @author remast
 * @author kutzi
 */
public abstract class DateUtils {
    
    /** Hide constructor. */
    private DateUtils() {
    }

    /**
     * Get current time rounded to minutes.
     * @return
     */
    public static DateTime getNow() {
        DateTime now = new DateTime();
        DateTime nowRounded = now.minuteOfDay().roundHalfCeilingCopy();
        return nowRounded;
    }

    /**
     * Returns <code>true</code> iff the 2 dates are on the same day.
     */
    public static boolean isSameDay(DateTime dt1, DateTime dt2) {
        return dt1.toDateMidnight().equals(dt2.toDateMidnight());
    }

    /**
     * Sets <code>timeToAdjust</code> to the same year and week-of-year as <code>day</code>.
     * 
     * @param midnightOnNextDay if <code>true</code> treats midnight (0:00h) as belonging to the next day.
     *   Otherwise 0:00h is treated as being the start of the current day.
     */
    public static DateTime adjustToSameDay(final DateTime day, final DateTime timeToAdjust,
            boolean midnightOnNextDay) {
        DateTime result = timeToAdjust.withYear(day.getYear()).withDayOfYear(day.getDayOfYear());
        if( midnightOnNextDay && result.getHourOfDay() == 0 && result.getMinuteOfHour() == 0 ) {
            result = result.plusDays(1);
        }
        return result;
    }

    /**
     * Returns <code>true</code> iff the first time is before or equal to the second time.
     *
     * @param time1 the first time
     * @param time2 the second time
     */
    public static boolean isBeforeOrEqual(final ReadableInstant time1, final ReadableInstant time2) {
        return time1.isBefore(time2) || time1.isEqual(time2);
    }
    
    /**
     * Returns the duration of the period denoted by start and end in fration hours.
     */
    public static double getDurationAsFractionHours( ReadableInstant start, ReadableInstant end ) {
        final long timeMilliSec = end.getMillis() - start.getMillis();
        final long timeMin = timeMilliSec / 1000 / 60;
        final long hours = timeMin / 60;

        final long mins = timeMin % 60;
        final double minsD = Math.round(mins * (1 + 2.0 / 3.0)) / 100.0;

        return hours + minsD;
    }
}
