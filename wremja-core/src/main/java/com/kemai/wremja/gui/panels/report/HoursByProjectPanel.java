package com.kemai.wremja.gui.panels.report;

import java.awt.BorderLayout;
import java.text.DateFormat;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JScrollPane;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.renderer.DefaultTableRenderer;
import org.jdesktop.swingx.renderer.FormatStringValue;

import ca.odell.glazedlists.swing.EventTableModel;

import com.kemai.util.TextResourceBundle;
import com.kemai.wremja.FormatUtils;
import com.kemai.wremja.gui.GuiConstants;
import com.kemai.wremja.gui.model.report.HoursByProject;
import com.kemai.wremja.gui.model.report.HoursByProjectReport;
import com.kemai.wremja.gui.panels.table.HoursByProjectTableFormat;

/**
 * Panel for displaying the report of working hours by project.
 * @see HoursByProjectReport
 * @author remast
 * @author kutzi
 */
@SuppressWarnings("serial") //$NON-NLS-1$
public class HoursByProjectPanel extends JXPanel implements Observer {
	
	private static final TextResourceBundle TEXT_BUNDLE = TextResourceBundle.getBundle(GuiConstants.class);

    /**
     * The report displayed by this panel.
     */
    private final HoursByProjectReport report;
    
    /**
     * The table model.
     */
    private EventTableModel<HoursByProject> tableModel;
    
    /**
     * Creates a new panel for the given report of hours by project.
     * @param report the report with hours by project
     */
    public HoursByProjectPanel(final HoursByProjectReport report) {
        this.report = report;
        this.setLayout(new BorderLayout());
        
        this.report.addObserver(this);
        
        initialize();
    }

    /**
     * Set up GUI components.
     */
    private void initialize() {
        tableModel = new EventTableModel<HoursByProject>(this.report.getHoursByProject(), new HoursByProjectTableFormat());

        final JXTable table = new JXTable(tableModel) {
            @Override
            public String getToolTipText() {
                if (getSelectedRows().length == 0) {
                    return "";
                }

                double duration = 0;
                
                for (int i : getSelectedRows()) {
                	int modelIndex = convertRowIndexToModel(i);
                    duration += tableModel.getElementAt(modelIndex).getHours();
                }

                return TEXT_BUNDLE.textFor("AllActivitiesPanel.tooltipDuration", FormatUtils.getDurationFormat().format(duration)); //$NON-NLS-1$
            }
        };
        table.setHighlighters(GuiConstants.HIGHLIGHTERS);
        table.setAutoResizeMode(JXTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        
        table.getColumn(0).setCellRenderer(new DefaultTableRenderer(new FormatStringValue(DateFormat.getDateInstance())));
        table.getColumn(1).setCellRenderer(new DefaultTableRenderer(new FormatStringValue(FormatUtils.getDurationFormat())));
        
        JScrollPane table_scroll_pane = new JScrollPane(table);

        this.add(table_scroll_pane, BorderLayout.CENTER);
    }

    public void update(final Observable o, final Object arg) {
        if (o != null && o instanceof HoursByProjectReport) {
            tableModel.fireTableDataChanged();
        }
    }

}
