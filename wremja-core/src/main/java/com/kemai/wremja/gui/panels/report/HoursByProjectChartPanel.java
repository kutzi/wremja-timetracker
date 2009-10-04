package com.kemai.wremja.gui.panels.report;

import java.awt.BorderLayout;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

import org.jdesktop.swingx.JXPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieToolTipGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

import com.kemai.wremja.FormatUtils;
import com.kemai.wremja.gui.model.report.HoursByProject;
import com.kemai.wremja.gui.model.report.HoursByProjectReport;

/**
 * Panel for displaying the report of working hours by project.
 * @see HoursByProjectReport
 * 
 * @author remast
 * @author kutzi
 */
public class HoursByProjectChartPanel extends JXPanel implements Observer {

    private static final long serialVersionUID = 1L;

	/**
     * The report displayed by this panel.
     */
    private final HoursByProjectReport report;

    /** Dataset for the displayed chart. */
    private final DefaultPieDataset hoursByProjectDataset = new DefaultPieDataset();

    /**
     * Creates a new panel for the given report of hours by project.
     * @param report the report with hours by project
     */
    public HoursByProjectChartPanel(final HoursByProjectReport report) {
        this.report = report;
        this.setLayout(new BorderLayout());

        this.report.addObserver(this);

        initialize();
    }

    /**
     * Set up GUI components.
     */
    private void initialize() {
        initChartData();

        final JFreeChart chart = ChartFactory.createPieChart3D(null, hoursByProjectDataset, false, true, false);
        chart.setBorderVisible(false);
        chart.setAntiAlias(true);
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setToolTipGenerator(new StandardPieToolTipGenerator(StandardPieToolTipGenerator.DEFAULT_TOOLTIP_FORMAT,
        		FormatUtils.getDurationFormatWithH(), NumberFormat.getPercentInstance(Locale.getDefault())));

        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setMouseZoomable(false);
        chartPanel.setEnabled(false);
        chartPanel.setPopupMenu(null);

        this.add(chartPanel, BorderLayout.CENTER);
    }

    private void initChartData() {
        hoursByProjectDataset.clear();
        
        for (HoursByProject hoursByProject : report.getHoursByPeriod()) {
            hoursByProjectDataset.setValue(hoursByProject.getProject(), hoursByProject.getHours());
        }
    }

    public void update(final Observable o, final Object arg) {
        if (o != null && o instanceof HoursByProjectReport) {
            initChartData();
        }
    }

}
