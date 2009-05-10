package com.kemai.wremja.gui.panels.report;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.text.DateFormat;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.renderer.DefaultTableRenderer;
import org.jdesktop.swingx.renderer.FormatStringValue;
import org.jdesktop.swingx.renderer.StringValue;

import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.swing.EventTableModel;

import com.kemai.wremja.FormatUtils;
import com.kemai.wremja.gui.GuiConstants;
import com.kemai.wremja.gui.model.report.HoursByPeriod;
import com.kemai.wremja.gui.model.report.HoursByPeriodReport;
import com.kemai.wremja.logging.Logger;

/**
 * Panel for displaying the report of working hours by a given period
 *
 * @param <P> The period type
 * @param <R> The report type
 *
 * @see HoursByPeriodReport
 * @author kutzi
 */
@SuppressWarnings("serial") //$NON-NLS-1$
abstract class HoursByPeriodPanel<P extends HoursByPeriod, R extends HoursByPeriodReport<P>> extends JXPanel implements Observer {

    private static final Logger log = Logger.getLogger(HoursByPeriodPanel.class);
    
    /**
     * The report displayed by this panel.
     */
    private final R report;
    
    /**
     * The table model.
     */
    private EventTableModel<P> tableModel;
    
    private final MyTableCellRenderer renderer2 = new MyTableCellRenderer(new FormatStringValue(FormatUtils.getDurationFormat()));
    
    /**
     * Creates a new panel for the given report of hours by day.
     * @param report the report with hours by day
     */
    protected HoursByPeriodPanel(final R report) {
        this.report = report;
        this.setLayout(new BorderLayout());
        
        this.report.addObserver(this);
        
        initialize();
    }

    
    protected abstract TableFormat<? super P> getTableFormat();
    
    /**
     * Set up GUI components.
     */
    private void initialize() {
        tableModel = new EventTableModel<P>(this.report.getHoursByPeriod(), getTableFormat());

        final JXTable table = new JXTable(tableModel);
        table.setHighlighters(GuiConstants.HIGHLIGHTERS);
        table.setAutoResizeMode(JXTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        
        table.getColumn(0).setCellRenderer(new DefaultTableRenderer(new FormatStringValue(DateFormat.getDateInstance())));
        table.getColumn(1).setCellRenderer(renderer2);
        
        JScrollPane table_scroll_pane = new JScrollPane(table);

        this.add(table_scroll_pane, BorderLayout.CENTER);
    }

    public void update(final Observable o, final Object arg) {
        if (o != null && o instanceof HoursByPeriodReport) {
            tableModel.fireTableDataChanged();
        } else {
            log.error("Unexpected Observable: " + o);
        }
    }
    
    private static class MyTableCellRenderer extends DefaultTableRenderer {

        public MyTableCellRenderer(StringValue converter) {
            super(converter);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table,
                Object value, boolean isSelected, boolean hasFocus, int row,
                int column) {
            if(value instanceof HoursByPeriod) {
                HoursByPeriod hoursByPeriod = (HoursByPeriod)value;
                Component comp = super.getTableCellRendererComponent(table, hoursByPeriod.getHours(),
                        isSelected, hasFocus,  row, column);
                Font f = comp.getFont();
                if( hoursByPeriod.isChanging() ) {
                    f = f.deriveFont(Font.ITALIC);
                } else {
                    f = f.deriveFont(Font.PLAIN);
                }
                comp.setFont(f);
                return comp;
            } else {
                return super.getTableCellRendererComponent(table, value,
                        isSelected, hasFocus,  row, column);
            }
        }
    }
}