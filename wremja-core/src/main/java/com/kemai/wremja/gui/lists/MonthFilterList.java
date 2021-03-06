package com.kemai.wremja.gui.lists;

import static com.kemai.wremja.gui.settings.SettingsConstants.ALL_ITEMS_FILTER_DUMMY;
import static com.kemai.wremja.gui.settings.SettingsConstants.CURRENT_ITEM_FILTER_DUMMY;

import java.beans.PropertyChangeEvent;
import java.util.Observable;
import java.util.Observer;

import org.apache.commons.lang.StringUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.SortedList;

import com.kemai.swing.util.LabeledItem;
import com.kemai.util.DateUtils;
import com.kemai.util.TextResourceBundle;
import com.kemai.wremja.gui.events.WremjaEvent;
import com.kemai.wremja.gui.model.PresentationModel;
import com.kemai.wremja.model.ProjectActivity;

/**
 * The list containing all months available for the filter.
 * @author remast
 */
public class MonthFilterList implements Observer {

    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(MonthFilterList.class);

    public static final DateTimeFormatter MONTH_FORMAT = DateTimeFormat.forPattern("MMMM"); //$NON-NLS-1$

    /** The model. */
    private final PresentationModel model;

    /** filter item for the all months dummy. */
    public static final LabeledItem<Integer> ALL_MONTHS_FILTER_ITEM = new LabeledItem<Integer>(
            Integer.valueOf(ALL_ITEMS_FILTER_DUMMY),
            textBundle.textFor("MonthFilterList.AllMonthsLabel") //$NON-NLS-1$
    );

    /** filter item for the current month dummy. */
    public static final LabeledItem<Integer> CURRENT_MONTH_FILTER_ITEM = new LabeledItem<Integer>(
            Integer.valueOf(CURRENT_ITEM_FILTER_DUMMY),
            textBundle.textFor("MonthFilterList.CurrentMonthLabel", MONTH_FORMAT.print(DateUtils.getNow())) //$NON-NLS-1$
    );

    /** The actual list containing all months. */
    private EventList<LabeledItem<Integer>> monthList;

    /**
     * Creates a new list for the given model.
     * @param model the model to create list for
     */
    public MonthFilterList(final PresentationModel model) {
        this.model = model;
        this.monthList = new BasicEventList<LabeledItem<Integer>>();

        this.model.addObserver(this);

        initialize();
    }

    /**
     * Initializes the list with all months from model.
     */
    private void initialize() {
        this.monthList.clear();
        this.monthList.add(ALL_MONTHS_FILTER_ITEM);
        this.monthList.add(CURRENT_MONTH_FILTER_ITEM);

        for (ProjectActivity activity : this.model.getData().getActivities()) {
            this.addMonth(activity);
        }
    }

    public SortedList<LabeledItem<Integer>> getMonthList() {
        return new SortedList<LabeledItem<Integer>>(this.monthList);
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
            this.addMonth((ProjectActivity) event.getData());
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
     * Adds the month of the given activity to the list.
     * @param activity the activity whose month is to be added
     */
    private void addMonth(final ProjectActivity activity) {
        if (activity == null) {
            return;
        }

        final String month = MONTH_FORMAT.print(activity.getStart());
        Integer monthInt = Integer.valueOf(activity.getStart().getMonthOfYear());
        final LabeledItem<Integer> monthItem = new LabeledItem<Integer>(
                monthInt, 
                month
        );

        if (!this.monthList.contains(monthItem)) {
            this.monthList.add(monthItem);
        }
    }
}
