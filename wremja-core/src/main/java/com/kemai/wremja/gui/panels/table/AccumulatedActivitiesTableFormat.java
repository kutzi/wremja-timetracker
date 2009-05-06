package com.kemai.wremja.gui.panels.table;


import ca.odell.glazedlists.gui.TableFormat;

import com.kemai.util.TextResourceBundle;
import com.kemai.wremja.gui.GuiConstants;
import com.kemai.wremja.model.report.AccumulatedProjectActivity;

/**
 * Table format for accumulated activities.
 * 
 * @author remast
 */
public class AccumulatedActivitiesTableFormat implements TableFormat<AccumulatedProjectActivity> {

    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(GuiConstants.class);

    /**
     * Gets the number of columns for the table.
     */
    public int getColumnCount() {
        return 3;
    }

    /**
     * Gets the name of the given column.
     * @param column the number of the column
     */
    public String getColumnName(final int col) {
        switch (col) {
            case 0:
                return textBundle.textFor("AccumulatedActivitiesTableFormat.DayHeading"); //$NON-NLS-1$
            case 1:
                return textBundle.textFor("AccumulatedActivitiesTableFormat.ProjectHeading"); //$NON-NLS-1$
            case 2:
                return textBundle.textFor("AccumulatedActivitiesTableFormat.DurationHeading"); //$NON-NLS-1$
            default:
                return ""; //$NON-NLS-1$
        }
    }

    public Object getColumnValue(final AccumulatedProjectActivity accActivity, final int col) {
        switch (col) {
            case 0:
                return accActivity.getDay();
            case 1:
                return accActivity.getProject();
            case 2:
                return accActivity.getTime();
            default:
                return ""; //$NON-NLS-1$
        }
    }

}
