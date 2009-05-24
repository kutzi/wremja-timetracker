package com.kemai.wremja.model;

import java.io.Serializable;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

import com.kemai.util.DateUtils;
import com.kemai.wremja.FormatUtils;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * An activity for a project.
 * 
 * Invariants of this class (not enforced, yet):
 * - start time must not be after end time
 * - start and end date of an activity must always be on the same day
 * unless end date is at 0:00h. In that situation end date is on the following date to the start date.
 * 
 * @author remast
 */
@XStreamAlias("projectActivity")
public class ProjectActivity implements Serializable, Comparable<ProjectActivity> {

    private static final long serialVersionUID = 1L;

    /** Start date of this activity. */
    private DateTime start;

    /** End date of this activity. */
    private DateTime end;

    /** The project associated with this activity. */
    private Project project;

    /** The description of this activity. */
    private String description;

    public static final String PROPERTY_START = "com.kemai.wremja.model.ProjectActivity.start";

    public static final String PROPERTY_END = "com.kemai.wremja.model.ProjectActivity.end";

    /** Artificial property if the day in year of the activity changes. */
    public static final String PROPERTY_DATE = "com.kemai.wremja.model.ProjectActivity.date";

    public static final String PROPERTY_PROJECT = "com.kemai.wremja.model.ProjectActivity.project";

    public static final String PROPERTY_DESCRIPTION = "com.kemai.wremja.model.ProjectActivity.description";

    /**
     * Creates a new {@link ProjectActivity} with an empty description.
     * 
     * @throws IllegalArgumentException if end time is before start time
     */
    public ProjectActivity(final DateTime start, final DateTime end, final Project project) {
        this(start, end, project, null);
    }

    /**
     * Creates a new {@link ProjectActivity}.
     * 
     * @throws IllegalArgumentException if end time is before start time
     */
    public ProjectActivity(final DateTime start, final DateTime end, final Project project,
            final String description) {
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("End time may not be before start time!");
        }
        this.start = start;
        this.end = end;
        this.project = project;
        this.description = description;
    }
    
    /**
     * Getter for the description.
     * @return the description to get
     */
    public String getDescription() {
        return description;
    }

    /**
     * Setter for the description.
     * @param description the description to set
     */
    public void setDescription(final String description) {
        if (StringUtils.equals(this.description, description)) {
            return;
        }
        
        this.description = description;
    }

    /**
     * Sets the day of the activity.
     * 
     * @param day the new activity day.
     *   Hours, minutes, seconds and so on in the passed value are ignored.
     */
    public void setDay(final DateTime day) {
        DateTime newStartDay = getStart();
        this.start = newStartDay.withYear(day.getYear()).withMonthOfYear(day.getMonthOfYear())
            .withDayOfMonth(day.getDayOfMonth());
        
        
        
        DateTime newEndDay = getEnd();
        newEndDay = newEndDay.withYear(day.getYear()).withMonthOfYear(day.getMonthOfYear())
            .withDayOfMonth(day.getDayOfMonth());
        if (newEndDay.getHourOfDay() == 0 && newEndDay.getMinuteOfHour() == 0) {
            newEndDay = newEndDay.plusDays(1);
        }
        
        this.end = newEndDay;
    }
    
    /**
     * Returns the day of the activity. 
     * Hours, minutes, seconds of the returned value are to be ignored.
     */
    public DateTime getDay() {
        return getStart().withMillisOfDay(0);
    }

    /**
     * Getter for the end.
     * @return the end to get
     */
    public DateTime getEnd() {
        return end;
    }
    
    /**
     * Sets the end hours and minutes while respecting the class invariants.
     * 
     * Note: When setting the end date to 0:00h it is always supposed to mean
     * midnight i.e. 0:00h the next day!
     * @throws IllegalArgumentException if end time is before start time
     */
    public void setEndTime(final int hours, final int minutes) {
        DateTime endDate = getEnd();
        if (hours == endDate.getHourOfDay() && minutes == endDate.getMinuteOfHour() ) {
            return;
        }
        
        if (endDate.getHourOfDay() == 0 && endDate.getMinuteOfHour() == 0) { // adjust day if old end was on midnight
            endDate = endDate.minusDays(1);
        } else if (hours == 0 && minutes == 0) { // adjust day if new end is on midnight
            endDate = endDate.plusDays(1);
        }
        
        endDate = endDate.withHourOfDay(hours).withMinuteOfHour(minutes);
        
        if (endDate.isBefore(getStart())) {
            throw new IllegalArgumentException("End time may not be before start time!");
        }
        
        this.end = endDate;
    }

    /**
     * @return the project
     */
    public Project getProject() {
        return project;
    }

    /**
     * Setter for the project.
     * @param project the project to set
     */
    public void setProject(final Project project) {
        if(project == null) {
            throw new IllegalArgumentException("Null project is not allowed!");
        }
        if (ObjectUtils.equals(this.project, project)) {
            return;
        }
        
        this.project = project;
    }

    /**
     * @return the start
     */
    public DateTime getStart() {
        return start;
    }
    
    /**
     * Sets the start hours and minutes while respecting the class invariants.
     * @throws IllegalArgumentException if end time is before start time
     */
    public void setStartTime(final int hours, final int minutes) {
        DateTime startTime = getStart();
        if (hours == startTime.getHourOfDay() && minutes == startTime.getMinuteOfHour()) {
            return;
        }
        
        startTime = startTime.withHourOfDay(hours).withMinuteOfHour(minutes);
        
        if (startTime.isAfter(getEnd())) {
            throw new IllegalArgumentException("End time may not be before start time!");
        }
        
        this.start = startTime;
    }
    
    @Override
    public String toString() {
        return FormatUtils.formatDate(getStart()) + " "
                + FormatUtils.formatTime(getStart()) + " - " + FormatUtils.formatTime(getEnd()) + " ("
                + FormatUtils.getDurationFormat().format(this.getDuration()) + " h) " + this.project;
    }

    @Override
    public int compareTo(final ProjectActivity activity) {
        if (activity == null) {
            return 0;
        }

        // Sort by start date but the other way round. That way the latest
        // activity is always on top.
        return this.getStart().compareTo(activity.getStart()) * -1;
    }
    
    /**
     * Calculate the duration of the given activity in decimal hours.
     * @return decimal value of the duration (e.g. for 30 minutes, 0.5 and so on)
     */    
    public final double getDuration() {
        return DateUtils.getDurationAsFractionHours(this.start, this.end);
    }
}
