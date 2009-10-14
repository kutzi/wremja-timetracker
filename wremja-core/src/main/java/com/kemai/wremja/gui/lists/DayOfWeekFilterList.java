package com.kemai.wremja.gui.lists;

import static com.kemai.wremja.gui.settings.SettingsConstants.ALL_ITEMS_FILTER_DUMMY;
import static com.kemai.wremja.gui.settings.SettingsConstants.CURRENT_ITEM_FILTER_DUMMY;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.SortedList;

import com.kemai.swing.util.LabeledItem;
import com.kemai.util.DateUtils;
import com.kemai.util.TextResourceBundle;

public class DayOfWeekFilterList {
    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(DayOfWeekFilterList.class);

    public static final DateTimeFormatter DAY_FORMAT = DateTimeFormat.forPattern("E"); //$NON-NLS-1$
    public static final DateTimeFormatter DAY_FORMAT2 = DateTimeFormat.forPattern("dd.MM.yy"); //$NON-NLS-1$

    /** filter item for the all days. */
    public static final LabeledItem<Integer> ALL_DAYS_FILTER_ITEM = new LabeledItem<Integer>(
            Integer.valueOf(ALL_ITEMS_FILTER_DUMMY),
            " " + textBundle.textFor("DayFilterList.AllDaysLabel") //$NON-NLS-1$
    );

    /** filter item for the current day. */
    public static final LabeledItem<Integer> CURRENT_DAY_FILTER_ITEM = new LabeledItem<Integer>(
            Integer.valueOf(CURRENT_ITEM_FILTER_DUMMY),
            " " + textBundle.textFor("DayFilterList.CurrentDayLabel", DAY_FORMAT2.print(DateUtils.getNow())) //$NON-NLS-1$
    );

    /** The actual list containing all days. */
    private EventList<LabeledItem<Integer>> dayList;

    /**
     * Creates a new list.
     */
    public DayOfWeekFilterList() {
        this.dayList = new BasicEventList<LabeledItem<Integer>>();

        initialize();
    }

    /**
     * Initializes the list with all months from model.
     */
    private void initialize() {
        this.dayList.clear();
        this.dayList.add(ALL_DAYS_FILTER_ITEM);
        this.dayList.add(CURRENT_DAY_FILTER_ITEM);

        DateTime time = new DateTime();
        
        for(int i = DateTimeConstants.MONDAY; i <= DateTimeConstants.SUNDAY; i++) {
            LabeledItem<Integer> item = new LabeledItem<Integer>(i,
                    " " + DAY_FORMAT.print(time.withDayOfWeek(i)));
            this.dayList.add(item);
        }
    }

    public SortedList<LabeledItem<Integer>> getDayList() {
        return new SortedList<LabeledItem<Integer>>(this.dayList);
    }
}
