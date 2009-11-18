package com.kemai.wremja.gui.model.report;

/**
 * Hours per something (e.g. hours per day, hours per project, ...)
 * 
 * @author kutzi
 */
public abstract class HoursPer {
    
    /** The amount of hours worked per ... */
    private double hours;
    
    private boolean changing = false;
    
    HoursPer(final double hours) {
        this.hours = hours;
    }

    /**
     * @return the hours
     */
    public double getHours() {
        return hours;
    }
    
    /**
     * Adds the given hours to the hours total.
     *
     * @param additionalHours the hours to add
     */
    public void addHours(HoursPer additionalHours) {
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
