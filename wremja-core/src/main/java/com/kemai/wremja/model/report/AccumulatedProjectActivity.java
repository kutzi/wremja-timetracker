package com.kemai.wremja.model.report;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.base.AbstractInstant;

import com.kemai.wremja.model.Project;
import com.kemai.wremja.model.ProjectActivity;

/**
 * An activity which represents the accumulated time for all activities
 * in a given project on a single day.
 * 
 * @author kutzi
 */
public class AccumulatedProjectActivity implements Comparable<AccumulatedProjectActivity> {

    private final DateMidnight day;

    private final Project project;

    private double time;
    
    private List<ProjectActivity> activities = new ArrayList<>();

    @Deprecated
    public AccumulatedProjectActivity(final Project project, final DateTime day, final double time) {
        this.project = project;
        this.day = day.toDateMidnight();
        this.time = time;
    }

    public AccumulatedProjectActivity(ProjectActivity activity) {
		this.project = activity.getProject();
        this.day = activity.getStart().toDateMidnight();
        this.time = activity.getDuration();
		activities.add(activity);
    }

	/**
     * @return the day
     */
    public Date getDay() {
        return day.toDate();
    }
    
    public AbstractInstant getDateTime() {
        return this.day;
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
    public void addTime(ProjectActivity activity) {
        this.time += activity.getDuration();
        activities.add(activity);
    }
    
    public List<ProjectActivity> getActivities() {
    	return Collections.unmodifiableList(activities);
    }

    @Override
    public String toString() {
        return this.project.toString() + ", " + this.day + ", " + this.time + "h"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
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
        eqBuilder.append(this.day, accAct.day);
        
        return eqBuilder.isEquals();
    }
    
    @Override
    public int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder();
        builder.append(getProject());
        builder.append(this.day);
        
        return builder.toHashCode();
    }
    
    /**
     * Compares to another {@link AccumulatedProjectActivity} using
     * the day and the project.
     * 
     * Sorts first by day in descending order (i.e. newer day come 1st) then sorts
     * by project in ascending order (i.e. projects with names earlier in the alphabet come 1st).
     */
    @Override
    public int compareTo(final AccumulatedProjectActivity o) {
        if (o == null) {
            return 0;
        }
        
        // Sort by day in decreasing order. That way the latest
        // activity is always on top.
        int compare = this.day.compareTo(o.day) * -1;
        if(compare != 0) {
            return compare;
        }
        return this.getProject().compareTo(o.getProject());
    }
}
