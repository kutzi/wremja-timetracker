package com.kemai.wremja.gui.lists;

import java.beans.PropertyChangeEvent;
import java.util.Observable;
import java.util.Observer;

import org.apache.commons.lang.StringUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.kemai.swing.util.LabeledItem;
import com.kemai.util.DateUtils;
import com.kemai.util.TextResourceBundle;
import com.kemai.wremja.gui.events.WremjaEvent;
import com.kemai.wremja.gui.model.PresentationModel;
import com.kemai.wremja.model.ProjectActivity;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.SortedList;

/**
 * The list containing all weeks of year available for the filter.
 * @author remast
 */
public class WeekOfYearFilterList implements Observer {

    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(WeekOfYearFilterList.class);

    public static final DateTimeFormatter WEEK_OF_YEAR_FORMAT = DateTimeFormat.forPattern("ww"); //$NON-NLS-1$

    /** The model. */
    private final PresentationModel model;

    /** Value for the all weeks of year dummy. */
    public static final int ALL_WEEKS_OF_YEAR_DUMMY = -10;

    /** Filter item for the all weeks of year dummy. */
    public static final LabeledItem<Integer> ALL_WEEKS_OF_YEAR_FILTER_ITEM = new LabeledItem<Integer>(
            ALL_WEEKS_OF_YEAR_DUMMY, 
            textBundle.textFor("WeekOfYearFilterList.AllWeeksOfYearLabel") //$NON-NLS-1$
    );

    /** Value for the current week of year dummy. */
    public static final int CURRENT_WEEK_OF_YEAR_DUMMY = -5;

    /** Filter item for the current week of year dummy. */
    public static final LabeledItem<Integer> CURRENT_WEEK_OF_YEAR_FILTER_ITEM = new LabeledItem<Integer>(
            CURRENT_WEEK_OF_YEAR_DUMMY,
            textBundle.textFor("WeekOfYearFilterList.CurrentWeekOfYearLabel", WEEK_OF_YEAR_FORMAT.print(DateUtils.getNowAsDateTime())) //$NON-NLS-1$
    );

    /** The actual list containing all weeks of year. */
    private EventList<LabeledItem<Integer>> weekOfYearList;

    /**
     * Creates a new list for the given model.
     * @param model the model to create list for
     */
    public WeekOfYearFilterList(final PresentationModel model) {
        this.model = model;
        this.weekOfYearList = new BasicEventList<LabeledItem<Integer>>();

        this.model.addObserver(this);

        initialize();
    }

    /**
     * Initializes the list with all weeks of year from model.
     */
    private void initialize() {
        this.weekOfYearList.clear();
        this.weekOfYearList.add(ALL_WEEKS_OF_YEAR_FILTER_ITEM);
        this.weekOfYearList.add(CURRENT_WEEK_OF_YEAR_FILTER_ITEM);

        for (ProjectActivity activity : this.model.getData().getActivities()) {
            this.addWeekOfYear(activity);
        }
    }

    public SortedList<LabeledItem<Integer>> getWeekList() {
        return new SortedList<LabeledItem<Integer>>(this.weekOfYearList);
    }

    /**
     * {@inheritDoc}
     */
    public void update(final Observable source, final Object eventObject) {
        if (eventObject == null || !(eventObject instanceof WremjaEvent)) {
            return;
        }

        final WremjaEvent event = (WremjaEvent) eventObject;

        switch (event.getType()) {

        case PROJECT_ACTIVITY_ADDED:
            this.addWeekOfYear((ProjectActivity) event.getData());
            break;

        case PROJECT_ACTIVITY_CHANGED:
            final PropertyChangeEvent propertyChangeEvent = event.getPropertyChangeEvent();
            if (StringUtils.equals(ProjectActivity.PROPERTY_DATE, propertyChangeEvent.getPropertyName())) {
                this.initialize();
            }
            break;

        case PROJECT_ACTIVITY_REMOVED:
            this.initialize();
            break;
        }
    }

    /**
     * Adds the week of year of the given activity to the list.
     * @param activity the activity whose week of year is to be added
     */
    private void addWeekOfYear(final ProjectActivity activity) {
        if (activity == null) {
            return;
        }
        
        final String weekOfYear = WEEK_OF_YEAR_FORMAT.print(activity.getStart());
        final LabeledItem<Integer> filterItem = new LabeledItem<Integer>(Integer.valueOf(weekOfYear), weekOfYear);
        if (!this.weekOfYearList.contains(filterItem))
            this.weekOfYearList.add(filterItem);
    }
}
