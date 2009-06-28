package com.kemai.wremja.gui.model.report;

import org.apache.commons.lang.builder.EqualsBuilder;

/**
 * Item of the hours by week report.
 * @author remast
 */
public class HoursByWeek extends HoursByPeriod implements Comparable<HoursByWeek> {
    
    /** The week of the year. */
    private int week;
    
    public HoursByWeek(final int week, final double hours) {
        super(hours);
        this.week = week;
    }

    /**
     * @return the week
     */
    public int getWeek() {
        return week;
    }

    @Override
    public boolean equals(final Object that) {
        if (this == that) {
            return true;
        }
        if (!(that instanceof HoursByWeek)) {
            return false;
        }

        final HoursByWeek accAct = (HoursByWeek) that;
        
        final EqualsBuilder eqBuilder = new EqualsBuilder();
        eqBuilder.append(this.getWeek(), accAct.getWeek());
        return eqBuilder.isEquals();
    }

    @SuppressWarnings("boxing")
	@Override
    public int compareTo(HoursByWeek that) {
        if (that == null) {
            return 0;
        }
        
        if (this.equals(that)) {
            return 0;
        }
        
        return Integer.valueOf(this.week).compareTo(that.getWeek());
    }
    
    @Override
    public int hashCode() {
        return this.getWeek();
    }

}
