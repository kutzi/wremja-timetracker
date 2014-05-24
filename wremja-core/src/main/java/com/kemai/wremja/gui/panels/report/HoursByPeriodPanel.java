package com.kemai.wremja.gui.panels.report;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.renderer.DefaultTableRenderer;
import org.jdesktop.swingx.renderer.FormatStringValue;
import org.jdesktop.swingx.renderer.StringValue;

import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.swing.DefaultEventTableModel;

import com.kemai.swing.util.WTable;
import com.kemai.wremja.FormatUtils;
import com.kemai.wremja.gui.GuiConstants;
import com.kemai.wremja.gui.model.report.HoursPer;
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
@SuppressWarnings("serial") 
abstract class HoursByPeriodPanel<P extends HoursPer, R extends HoursByPeriodReport<P>> extends JXPanel implements Observer {

    private static final Logger log = Logger.getLogger(HoursByPeriodPanel.class);
    
    /**
     * The report displayed by this panel.
     */
    private final R report;
    
    /**
     * The table model.
     */
    private DefaultEventTableModel<P> tableModel;
    
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
    
    // TODO: find a better name for this method ;-)
    protected abstract StringValue getValueConverterFor1stColumn();
    
    /**
     * Set up GUI components.
     */
    private void initialize() {
        tableModel = new DefaultEventTableModel<P>(this.report.getHoursByPeriod(), getTableFormat());

        final JXTable table = new WTable(tableModel);
        table.setHighlighters(GuiConstants.HIGHLIGHTERS);
        table.setAutoResizeMode(JXTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        
        table.getColumn(0).setCellRenderer(new DefaultTableRenderer(getValueConverterFor1stColumn()));
        table.getColumn(1).setCellRenderer(new MyTableCellRenderer(new FormatStringValue(FormatUtils.getDurationFormat())));
        
        JScrollPane table_scroll_pane = new JScrollPane(table);

        this.add(table_scroll_pane, BorderLayout.CENTER);
    }

    public void update(final Observable o, final Object arg) {
        if (o != null && o instanceof HoursByPeriodReport<?>) {
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
            if(value instanceof HoursPer) {
                HoursPer hoursByPeriod = (HoursPer)value;
                JComponent comp = (JComponent) super.getTableCellRendererComponent(table,
                        Double.valueOf(hoursByPeriod.getHours()),
                        isSelected, hasFocus,  row, column);
                comp.setBorder(new EmptyBorder(0, 5, 0, 5));
                
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
