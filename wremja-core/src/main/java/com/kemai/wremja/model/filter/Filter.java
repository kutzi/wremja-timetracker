package com.kemai.wremja.model.filter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;

import com.kemai.util.Predicate;
import com.kemai.wremja.gui.lists.MonthFilterList;
import com.kemai.wremja.gui.lists.WeekOfYearFilterList;
import com.kemai.wremja.gui.lists.YearFilterList;
import com.kemai.wremja.model.Project;
import com.kemai.wremja.model.ProjectActivity;

/**
 * Filter for selecting only those project activities which satisfy 
 * some selected criteria.
 * @author remast
 * @author kutzi
 */
public class Filter {

    /** The predicates of the filter. */
    private final Map<String, Predicate<ProjectActivity>> predicates
        = new HashMap<String, Predicate<ProjectActivity>>();
    
    // Ugly HACK for the 'smart' week filter
    private final Map<String, Predicate<ProjectActivity>> disabledPredicates
        = new HashMap<String, Predicate<ProjectActivity>>();
    
    /** The predicate to filter by week of year. */
    private static final String WEEK_PREDICATE = "WEEK_PREDICATE";
    
    /** The predicate to filter by month. */
    private static final String MONTH_PREDICATE = "MONTH_PREDICATE";
    
    /** The month to filter by. */
    private Integer month;
    
    /** The year to filter by. */
    private Integer year;

    
    /** The predicate to filter by year. */
    private static final String YEAR_PREDICATE = "YEAR_PREDICATE";

    /** The project to filter by. */
    private Project project;

    /** The predicate to filter by project. */
    private static final String PROJECT_PREDICATE = "PROJECT_PREDICATE";

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
        return matchesCriteria(nowActivity);
    }
    
    /**
     * Sets the weekOfYear to filter by.
     * @param weekOfYear the weekOfYear to set.
     */
    public void setWeekOfYear(int weekOfYear) {
        this.predicates.remove(WEEK_PREDICATE);

        // clear filter if 'all' is selected
        if (weekOfYear == WeekOfYearFilterList.ALL_WEEKS_OF_YEAR_DUMMY) {
            return;
        }

        final Predicate<ProjectActivity> newWeekOfYearPredicate;
        if(weekOfYear == WeekOfYearFilterList.CURRENT_WEEK_OF_YEAR_DUMMY) {
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
            for(Map.Entry<String, Predicate<ProjectActivity>> entry : this.disabledPredicates.entrySet()) {
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
        if (month == MonthFilterList.ALL_MONTHS_DUMMY) {
            this.month = null;
            return;
        }

        this.month = Integer.valueOf(month);
        
        final Predicate<ProjectActivity> newMonthPredicate;
        if(month == MonthFilterList.CURRENT_MONTH_DUMMY) {
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
            for(Map.Entry<String, Predicate<ProjectActivity>> entry : this.disabledPredicates.entrySet()) {
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
        if (year == YearFilterList.ALL_YEARS_DUMMY) {
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
}
