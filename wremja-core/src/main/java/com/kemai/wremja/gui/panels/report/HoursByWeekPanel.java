package com.kemai.wremja.gui.panels.report;

import java.text.DecimalFormat;

import org.jdesktop.swingx.renderer.FormatStringValue;
import org.jdesktop.swingx.renderer.StringValue;

import ca.odell.glazedlists.gui.TableFormat;

import com.kemai.wremja.gui.model.report.HoursByWeek;
import com.kemai.wremja.gui.model.report.HoursByWeekReport;
import com.kemai.wremja.gui.panels.table.HoursByWeekTableFormat;

/**
 * Panel for displaying the report of working hours by week.
 * @see HoursByWeekReport
 * @author remast
 */
@SuppressWarnings("serial") //$NON-NLS-1$
public class HoursByWeekPanel extends HoursByPeriodPanel<HoursByWeek, HoursByWeekReport> {

    /**
     * Creates a new panel for the given report of hours by week.
     * @param report the report with hours by week
     */
    public HoursByWeekPanel(final HoursByWeekReport report) {
        super( report );
    }

    @Override
    protected TableFormat<? super HoursByWeek> getTableFormat() {
        return new HoursByWeekTableFormat();
    }

    @Override
    protected StringValue getValueConverterFor1stColumn() {
        return new FormatStringValue(new DecimalFormat("##00"));
    }
}
