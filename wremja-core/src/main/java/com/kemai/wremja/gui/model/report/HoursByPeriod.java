package com.kemai.wremja.gui.model.report;

/**
 * Hours per a period (e.g. hours per day)
 * 
 * TODO: 'period' doesn't get it anymore since HoursByProject is also implemented via this
 * 
 * @author kutzi
 */
public abstract class HoursByPeriod {
    
    /** The amount of hours worked in that period. */
    private double hours;
    
    private boolean changing = false;
    
    HoursByPeriod(final double hours) {
        this.hours = hours;
    }

    /**
     * @return the hours
     */
    public double getHours() {
        return hours;
    }
    
    /**
     * Adds the given hours to the hours in that period.
     * @param additionalHours the hours to add
     */
    public void addHours(HoursByPeriod additionalHours) {
        this.hours += additionalHours.getHours();
        this.changing = this.changing || additionalHours.isChanging();
    }
    
    /**
     * Returns if the report 'is changing' i.e. if the hours are (more or less fixed)
     * or if it changes from minute to minute (e.g. because an activity is currently running)
     */
    public boolean isChanging() {
        return this.changing;
    }
    
    public void setChanging( boolean changing ) {
        this.changing = changing;
    }
}
