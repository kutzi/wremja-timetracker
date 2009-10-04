package com.kemai.wremja.gui.panels.report;

import java.text.DateFormat;

import org.jdesktop.swingx.renderer.FormatStringValue;
import org.jdesktop.swingx.renderer.StringValue;

import ca.odell.glazedlists.gui.TableFormat;

import com.kemai.wremja.gui.model.report.HoursByProject;
import com.kemai.wremja.gui.model.report.HoursByProjectReport;
import com.kemai.wremja.gui.panels.table.HoursByProjectTableFormat;

/**
 * Panel for displaying the report of working hours by project.
 * @see HoursByProjectReport
 * @author remast
 * @author kutzi
 */
@SuppressWarnings("serial") 
public class HoursByProjectPanel extends HoursByPeriodPanel<HoursByProject, HoursByProjectReport> {
    
    /**
     * Creates a new panel for the given report of hours by project.
     * @param report the report with hours by project
     */
    public HoursByProjectPanel(final HoursByProjectReport report) {
        super(report);
    }
    
    @Override
    protected TableFormat<? super HoursByProject> getTableFormat() {
        return new HoursByProjectTableFormat();
    }

    @Override
    protected StringValue getValueConverterFor1stColumn() {
        return new FormatStringValue(DateFormat.getDateInstance());
    }
}
