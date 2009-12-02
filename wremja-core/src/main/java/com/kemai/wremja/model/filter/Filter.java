package com.kemai.wremja.model.filter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;

import com.kemai.util.Predicate;
import com.kemai.wremja.gui.settings.SettingsConstants;
import com.kemai.wremja.model.Project;
import com.kemai.wremja.model.ProjectActivity;

/**
 * Filter for selecting only those project activities which satisfy 
 * some selected criteria.
 * @author remast
 * @author kutzi
 */
public class Filter {
    
    /** The predicate to filter by day of week. */
    private static final Object DAY_PREDICATE = new Object();
    
    /** The predicate to filter by week of year. */
    private static final String WEEK_PREDICATE = "WEEK_PREDICATE";
    
    /** The predicate to filter by month. */
    private static final String MONTH_PREDICATE = "MONTH_PREDICATE";
    
    /** The predicate to filter by year. */
    private static final String YEAR_PREDICATE = "YEAR_PREDICATE";
    
    /** The predicate to filter by project. */
    private static final String PROJECT_PREDICATE = "PROJECT_PREDICATE";

    
    /** The predicates of the filter. */
    private final Map<Object, Predicate<ProjectActivity>> predicates
        = new HashMap<Object, Predicate<ProjectActivity>>();
    
    // Ugly HACK for the 'smart' week filter
    private final Map<Object, Predicate<ProjectActivity>> disabledPredicates
        = new HashMap<Object, Predicate<ProjectActivity>>();
    
    /** The month to filter by. */
    private Integer month;
    
    /** The year to filter by. */
    private Integer year;

    /** The project to filter by. */
    private Project project;
    
    /** If to display only billable projects. */
    private boolean onlyBillable;

    /**
     * Create filter with no predicates.
     */
    public Filter() {
    }

    /**
     * Apply this filter to given elements.
     * 
     * @param elements
     *            the elements to apply filter to
     * @return a list of elements satisfying the filter
     */
    public List<ProjectActivity> applyFilters(final List<ProjectActivity> elements) {
        ArrayList<ProjectActivity> filteredElements = new ArrayList<ProjectActivity>(elements);
        for (Predicate<ProjectActivity> predicate : predicates.values()) {
            for (ProjectActivity activity : new ArrayList<ProjectActivity>(filteredElements)) {
                if (!predicate.evaluate(activity))
                    filteredElements.remove(activity);
            }
        }

        filteredElements.trimToSize();
        return filteredElements;
    }

    /**
     * Checks whether the given activity matches the filter criteria.
     * @param activity the project activity to check
     * @return <code>true</code> if activity matches the filter
     * otherwise <code>false</code>
     */
    public final boolean matchesCriteria(final ProjectActivity activity) {
        for (Predicate<ProjectActivity> predicate : predicates.values()) {
            if (!predicate.evaluate(activity)) {
                return false;
            }
        }
        return true;
    }
 
    
    public boolean matchesNow() {
        if( predicates.isEmpty() ) {
            return true;
        }
        
        long nowMs = System.currentTimeMillis();
        ProjectActivity nowActivity = new ProjectActivity(
                new DateTime( nowMs-1 ), new DateTime(nowMs), null);
        for (Predicate<ProjectActivity> predicate : predicates.values()) {
            if (predicate instanceof TimePredicate<?>) {
                if (!predicate.evaluate(nowActivity)) {
                    return false;
                }
            }
        }
        
        return true;
    }

    public void setDayOfWeek(int dayOfWeek) {
        this.predicates.remove(DAY_PREDICATE);

        // clear filter if 'all' is selected
        if (dayOfWeek == SettingsConstants.ALL_ITEMS_FILTER_DUMMY) {
            return;
        }

        Predicate<ProjectActivity> newWeekOfYearPredicate = null;
        if(dayOfWeek == SettingsConstants.CURRENT_ITEM_FILTER_DUMMY) {
            newWeekOfYearPredicate = new CurrentDayPredicate();
        } else {
            newWeekOfYearPredicate = new DayOfWeekPredicate(dayOfWeek);
        }
        
        if(newWeekOfYearPredicate instanceof CurrentWeekPredicate) {
            Predicate<ProjectActivity> yearPred =
                this.predicates.remove(YEAR_PREDICATE);
            if(yearPred != null) {
                this.disabledPredicates.put(YEAR_PREDICATE, yearPred);
            }
            
            Predicate<ProjectActivity> monthPred =
                this.predicates.remove(MONTH_PREDICATE);
            if(monthPred != null) {
                this.disabledPredicates.put(MONTH_PREDICATE, monthPred);
            }
            
            Predicate<ProjectActivity> weekPred =
                this.predicates.remove(WEEK_PREDICATE);
            if(weekPred != null) {
                this.disabledPredicates.put(WEEK_PREDICATE, weekPred);
            }
        } else {
            for(Map.Entry<Object, Predicate<ProjectActivity>> entry : this.disabledPredicates.entrySet()) {
                this.predicates.put(entry.getKey(), entry.getValue());
            }
            this.disabledPredicates.clear();
        }
        
        this.predicates.put(DAY_PREDICATE, newWeekOfYearPredicate);
    }
    
    /**
     * Sets the weekOfYear to filter by.
     * @param weekOfYear the weekOfYear to set.
     */
    public void setWeekOfYear(int weekOfYear) {
        this.predicates.remove(WEEK_PREDICATE);

        // clear filter if 'all' is selected
        if (weekOfYear == SettingsConstants.ALL_ITEMS_FILTER_DUMMY) {
            return;
        }

        final Predicate<ProjectActivity> newWeekOfYearPredicate;
        if(weekOfYear == SettingsConstants.CURRENT_ITEM_FILTER_DUMMY) {
            newWeekOfYearPredicate = new CurrentWeekPredicate();
        } else {
            newWeekOfYearPredicate = new WeekOfYearPredicate(weekOfYear);
        }
        
        if(newWeekOfYearPredicate instanceof CurrentWeekPredicate) {
            Predicate<ProjectActivity> yearPred =
                this.predicates.remove(YEAR_PREDICATE);
            if(yearPred != null) {
                this.disabledPredicates.put(YEAR_PREDICATE, yearPred);
            }
            
            Predicate<ProjectActivity> monthPred =
                this.predicates.remove(MONTH_PREDICATE);
            if(yearPred != null) {
                this.disabledPredicates.put(MONTH_PREDICATE, monthPred);
            }
        } else {
            for(Map.Entry<Object, Predicate<ProjectActivity>> entry : this.disabledPredicates.entrySet()) {
                this.predicates.put(entry.getKey(), entry.getValue());
            }
            this.disabledPredicates.clear();
        }
        
        this.predicates.put(WEEK_PREDICATE, newWeekOfYearPredicate);
    }
    
    /**
     * Sets the month to filter by.
     * @param month the month to set
     */
    public void setMonth(int month) {
        this.predicates.remove(MONTH_PREDICATE);

        // If month is null set month predicate also to null.
        if (month == SettingsConstants.ALL_ITEMS_FILTER_DUMMY) {
            this.month = null;
            return;
        }

        this.month = Integer.valueOf(month);
        
        final Predicate<ProjectActivity> newMonthPredicate;
        if(month == SettingsConstants.CURRENT_ITEM_FILTER_DUMMY) {
            newMonthPredicate = new CurrentMonthPredicate();
        } else {
            newMonthPredicate = new MonthPredicate(month);
        }
        
        if(newMonthPredicate instanceof CurrentMonthPredicate) {
            Predicate<ProjectActivity> yearPred =
                this.predicates.remove(YEAR_PREDICATE);
            if(yearPred != null) {
                this.disabledPredicates.put(YEAR_PREDICATE, yearPred);
            }
        } else {
            for(Map.Entry<Object, Predicate<ProjectActivity>> entry : this.disabledPredicates.entrySet()) {
                this.predicates.put(entry.getKey(), entry.getValue());
            }
            this.disabledPredicates.clear();
        }
        
        this.predicates.put(MONTH_PREDICATE, newMonthPredicate);
    }

    public Integer getMonth() {
        return this.month;
    }
    
    /**
     * Getter for the year.
     * @return the year
     */
    public Integer getYear() {
        return this.year;
    }

    /**
     * Sets the year to filter by.
     * @param year the year to set
     */
    public void setYear(final int year) {
        this.predicates.remove(YEAR_PREDICATE);

        // If year is null set year predicate also to null.
        if (year == SettingsConstants.ALL_ITEMS_FILTER_DUMMY) {
            this.year = null;
            return;
        }
        this.year = Integer.valueOf(year);

        final Predicate<ProjectActivity> newYearPredicate = new YearPredicate(year);
        this.predicates.put(YEAR_PREDICATE, newYearPredicate);
    }

    /**
     * Sets the project to filter by.
     * @param project the project to set
     */
    public void setProject(final Project project) {
        this.project = project;

        this.predicates.remove(PROJECT_PREDICATE);

        // If project is null set project predicate also to null.
        if (this.project == null) {
            return;
        }

        final Predicate<ProjectActivity> newProjectPredicate = new ProjectPredicate(project);
        this.predicates.put(PROJECT_PREDICATE, newProjectPredicate);
    }

    public void setOnlyBillable(boolean b) {
        this.predicates.remove(PROJECT_PREDICATE);
        
        this.onlyBillable = b;
        
        if (this.onlyBillable) {
            Predicate<ProjectActivity> predicate = new BillableProjectPredicate();
            this.predicates.put(PROJECT_PREDICATE, predicate);
        }
    }
    
    public Filter copy() {
        Filter copy = new Filter();
        copy.predicates.putAll(this.predicates);
        copy.disabledPredicates.putAll(this.disabledPredicates);
        copy.month = this.month;
        copy.onlyBillable = this.onlyBillable;
        copy.project = this.project;
        copy.year = this.year;
        
        return copy;
    }
}
