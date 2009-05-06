package com.kemai.wremja.gui.panels.table;


import ca.odell.glazedlists.gui.TableFormat;

import com.kemai.util.TextResourceBundle;
import com.kemai.wremja.gui.GuiConstants;
import com.kemai.wremja.gui.model.report.HoursByProject;

/**
 * @author remast
 */
public class HoursByProjectTableFormat implements TableFormat<HoursByProject> {

    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(GuiConstants.class);

    /**
     * Gets the number of columns for the table.
     */
    public final int getColumnCount() {
        return 2;
    }

    /**
     * Gets the name of the given column.
     * @param column the number of the column
     */
    public final String getColumnName(final int column) {
        switch (column) {
        case 0:
            return textBundle.textFor("HoursByDayProjectFormat.ProjectHeading"); //$NON-NLS-1$
        case 1:
            return textBundle.textFor("HoursByDayProjectFormat.HoursHeading"); //$NON-NLS-1$
        default:
            return null;
        }
    }

    public Object getColumnValue(final HoursByProject baseObject, final int column) {
        switch (column) {
        case 0:
            return baseObject.getProject();
        case 1:
            return baseObject.getHours();
        default:
            return null;
        }
    }

}
