package com.kemai.wremja.gui.panels.report;

import java.awt.BorderLayout;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JScrollPane;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.renderer.DefaultTableRenderer;
import org.jdesktop.swingx.renderer.FormatStringValue;
import org.jdesktop.swingx.renderer.StringValue;
import org.joda.time.ReadableInstant;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import ca.odell.glazedlists.swing.DefaultEventTableModel;

import com.kemai.swing.util.WTable;
import com.kemai.util.TextResourceBundle;
import com.kemai.wremja.FormatUtils;
import com.kemai.wremja.gui.GuiConstants;
import com.kemai.wremja.gui.model.report.ObservingAccumulatedActivitiesReport;
import com.kemai.wremja.gui.panels.table.AccumulatedActivitiesTableFormat;
import com.kemai.wremja.model.report.AccumulatedActivitiesReport;
import com.kemai.wremja.model.report.AccumulatedProjectActivity;

/**
 * Panel containing the accumulated hours spent on each project on one day.
 * @author remast
 * @author kutzi
 */
@SuppressWarnings("serial") 
public class AccummulatedActitvitiesPanel extends JXPanel implements Observer {

	private static final TextResourceBundle TEXT_BUNDLE = TextResourceBundle.getBundle(GuiConstants.class);
	
    private final AccumulatedActivitiesReport report;
    
    private DefaultEventTableModel<AccumulatedProjectActivity> tableModel;

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
        tableModel = new DefaultEventTableModel<AccumulatedProjectActivity>(this.report.getAccumulatedActivitiesByDay(), new AccumulatedActivitiesTableFormat());
        final JXTable table = new WTable(tableModel) {
            @Override
            public String getToolTipText() {
                if (getSelectedRows().length == 0) {
                    return "";
                }

                double duration = 0;
                
                for (int i : getSelectedRows()) {
                	int modelIndex = convertRowIndexToModel(i);
                    duration += tableModel.getElementAt(modelIndex).getTime();
                }

                return TEXT_BUNDLE.textFor("AllActivitiesPanel.tooltipDuration", FormatUtils.getDurationFormat().format(duration)); //$NON-NLS-1$
            }
        };
        table.setHighlighters(GuiConstants.HIGHLIGHTERS);

        StringValue dateTimeConverter = new StringValue() {
            private final DateTimeFormatter dtf = DateTimeFormat.mediumDate();
            @Override
            public String getString(Object value) {
                ReadableInstant instant = (ReadableInstant)value;
                return dtf.print(instant);
            }
        };
        table.getColumn(0).setCellRenderer(new DefaultTableRenderer(dateTimeConverter));
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
