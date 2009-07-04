package com.kemai.wremja.model.report;

import java.util.Date;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.joda.time.DateTime;

import com.kemai.wremja.model.Project;

/**
 * An activity which represents the accumulated time for all activities
 * in a given project on a single day.
 * 
 * @author kutzi
 */
public class AccumulatedProjectActivity implements Comparable<AccumulatedProjectActivity> {

    private final DateTime day;

    private final Project project;

    private double time;

    public AccumulatedProjectActivity(final Project project, final DateTime day, final double time) {
        this.project = project;
        this.day = day;
        this.time = time;
    }

    /**
     * @return the day
     */
    public Date getDay() {
        return day.toDate();
    }

    /**
     * @return the project
     */
    public Project getProject() {
        return project;
    }

    /**
     * Returns the total time spent on this accumulated activity.
     * 
     * @return the time in fraction hours
     */
    public double getTime() {
        return time;
    }

    /** Adds the given time to this accumulated activity. */
    public void addTime(final double toAdd) {
        this.time += toAdd;
    }

    @Override
    public String toString() {
        return this.project.toString() + ", " + this.getDay() + ", " + this.time + "h"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

    @Override
    public boolean equals(final Object that) {
        if (this == that) {
            return true;
        }
        
        if (!(that instanceof AccumulatedProjectActivity)) {
            return false;
        }

        final AccumulatedProjectActivity accAct = (AccumulatedProjectActivity) that;
        
        final EqualsBuilder eqBuilder = new EqualsBuilder();
        eqBuilder.append(this.getProject(), accAct.getProject());
        
        return eqBuilder.isEquals() && this.day.getDayOfYear() == accAct.day.getDayOfYear();
    }
    
    @Override
    public int hashCode() {
        // Unique for each project so use hash code of project
        return this.getProject().hashCode();
    }
    
    
    @Override
    public int compareTo(final AccumulatedProjectActivity activity) {
        if (activity == null) {
            return 0;
        }
        
        // Sort by day in decreasing order. That way the latest
        // activity is always on top.
        int thisDay = this.day.getDayOfYear();
        int thatDay = activity.day.getDayOfYear();
        if(thisDay < thatDay) {
        	return 1;
        } else if(thisDay > thatDay) {
        	return -1;
        }
        
        return this.getProject().compareTo(activity.getProject());
    }
}
