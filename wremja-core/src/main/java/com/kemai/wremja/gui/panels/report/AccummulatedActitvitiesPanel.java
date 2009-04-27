package com.kemai.wremja.gui.panels.report;

import java.awt.BorderLayout;
import java.awt.Color;
import java.text.DateFormat;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JScrollPane;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.renderer.DefaultTableRenderer;
import org.jdesktop.swingx.renderer.FormatStringValue;

import com.kemai.wremja.FormatUtils;
import com.kemai.wremja.gui.GuiConstants;
import com.kemai.wremja.gui.model.report.ObservingAccumulatedActivitiesReport;
import com.kemai.wremja.gui.panels.table.AccumulatedActivitiesTableFormat;
import com.kemai.wremja.model.report.AccumulatedActivitiesReport;
import com.kemai.wremja.model.report.AccumulatedProjectActivity;

import ca.odell.glazedlists.swing.EventTableModel;

/**
 * Panel containing the accumulated hours spent on each project on one day.
 * @author remast
 */
@SuppressWarnings("serial") //$NON-NLS-1$
public class AccummulatedActitvitiesPanel extends JXPanel implements Observer {

    private AccumulatedActivitiesReport report;
    
    private EventTableModel<AccumulatedProjectActivity> tableModel;

    public AccummulatedActitvitiesPanel(final AccumulatedActivitiesReport report) {
        this.report = report;
        this.setLayout(new BorderLayout());

        this.report.addObserver(this);
        
        initialize();
    }

    /**
     * Set up GUI components.
     */
    private void initialize() {
        this.setBackground(Color.RED);

        tableModel = new EventTableModel<AccumulatedProjectActivity>(this.report.getAccumulatedActivitiesByDay(), new AccumulatedActivitiesTableFormat());
        final JXTable table = new JXTable(tableModel);
        table.setHighlighters(GuiConstants.HIGHLIGHTERS);

        table.getColumn(0).setCellRenderer(new DefaultTableRenderer(new FormatStringValue(DateFormat.getDateInstance())));
        table.getColumn(2).setCellRenderer(new DefaultTableRenderer(new FormatStringValue(FormatUtils.getDurationFormat())));

        table.setAutoResizeMode(JXTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        JScrollPane table_scroll_pane = new JScrollPane(table);

        this.add(table_scroll_pane, BorderLayout.CENTER);
    }

    public void update(final Observable o, final Object arg) {
        if (o != null && o instanceof ObservingAccumulatedActivitiesReport) {
            tableModel.fireTableDataChanged();
        }
        
    }
}
