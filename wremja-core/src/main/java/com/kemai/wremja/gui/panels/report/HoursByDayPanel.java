package com.kemai.wremja.gui.panels.report;

import java.text.DateFormat;

import org.jdesktop.swingx.renderer.FormatStringValue;
import org.jdesktop.swingx.renderer.StringValue;

import ca.odell.glazedlists.gui.TableFormat;

import com.kemai.wremja.gui.model.report.HoursByDay;
import com.kemai.wremja.gui.model.report.HoursByDayReport;
import com.kemai.wremja.gui.panels.table.HoursByDayTableFormat;

/**
 * Panel for displaying the report of working hours by day.
 * @see HoursByDayReport
 * @author remast
 * @author kutzi
 */
@SuppressWarnings("serial") //$NON-NLS-1$
public class HoursByDayPanel extends HoursByPeriodPanel<HoursByDay, HoursByDayReport> {

    /**
     * Creates a new panel for the given report of hours by day.
     * @param report the report with hours by day
     */
    public HoursByDayPanel(final HoursByDayReport report) {
        super( report );
    }

    @Override
    protected TableFormat<? super HoursByDay> getTableFormat() {
        return new HoursByDayTableFormat();
    }

    @Override
    protected StringValue getValueConverterFor1stColumn() {
        return new FormatStringValue(DateFormat.getDateInstance());
    }
}
