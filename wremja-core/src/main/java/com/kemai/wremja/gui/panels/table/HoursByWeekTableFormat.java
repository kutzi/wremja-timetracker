package com.kemai.wremja.gui.panels.table;


import com.kemai.util.TextResourceBundle;
import com.kemai.wremja.gui.Launcher;
import com.kemai.wremja.gui.model.report.HoursByWeek;

import ca.odell.glazedlists.gui.TableFormat;

/**
 * @author remast
 */
public class HoursByWeekTableFormat implements TableFormat<HoursByWeek> {

    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(Launcher.class);

    /**
     * Gets the number of columns for the table.
     */
    public int getColumnCount() {
        return 2;
    }

    /**
     * Gets the name of the given column.
     * @param column the number of the column
     */
    public String getColumnName(final int column) {
        switch (column) {
        case 0:
            return textBundle.textFor("HoursByWeekTableFormat.WeekHeading"); //$NON-NLS-1$
        case 1:
            return textBundle.textFor("HoursByWeekTableFormat.HoursHeading"); //$NON-NLS-1$
        default:
            return null;
        }
    }

    public Object getColumnValue(final HoursByWeek baseObject, final int column) {
        switch (column) {
        case 0:
            return baseObject.getWeek();
        case 1:
            return baseObject.getHours();
        default:
            return null;
        }
    }

}
