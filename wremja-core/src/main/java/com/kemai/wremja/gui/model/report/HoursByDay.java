package com.kemai.wremja.gui.model.report;

import java.util.Date;

import org.joda.time.DateTime;

/**
 * Item of the hours by day report.
 * @author remast
 * @author kutzi
 */
public class HoursByDay extends HoursPer implements Comparable<HoursByDay> {
    
    /** The day of the year. */
    private final DateTime day;
    
    public HoursByDay(final DateTime day, final double hours) {
        super(hours);
        this.day = day;
    }

    /**
     * @return the week
     */
    public Date getDay() {
        return day.toDate();
    }

    @Override
    public boolean equals(final Object that) {
        if (this == that) {
            return true;
        }
        if (!(that instanceof HoursByDay)) {
            return false;
        }

        final HoursByDay accAct = (HoursByDay) that;
        return this.day.getDayOfYear() == accAct.day.getDayOfYear();
    }

    @Override
    public int compareTo(HoursByDay that) {
        if (that == null) {
            return 0;
        }
        
        if (this.equals(that)) {
            return 0;
        }
        
        // Sort by start date but the other way round. That way the latest
        // activity is always on top.
        return this.getDay().compareTo(that.getDay()) * -1;
    }
    
    @Override
    public int hashCode() {
        return this.getDay().hashCode();
    }

}
