package com.kemai.wremja.gui.panels.report;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.renderer.DefaultTableRenderer;
import org.jdesktop.swingx.renderer.FormatStringValue;
import org.jdesktop.swingx.renderer.StringValue;
import org.joda.time.ReadableInstant;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import ca.odell.glazedlists.swing.DefaultEventTableModel;

import com.kemai.swing.util.AWTUtils;
import com.kemai.swing.util.WTable;
import com.kemai.util.TextResourceBundle;
import com.kemai.wremja.FormatUtils;
import com.kemai.wremja.gui.GuiConstants;
import com.kemai.wremja.gui.dialogs.DescriptionsDialog;
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
        
        initPopupMenu(table);
        
        JScrollPane table_scroll_pane = new JScrollPane(table);

        this.add(table_scroll_pane, BorderLayout.CENTER);
    }

    private void initPopupMenu(JXTable table) {
    	final JPopupMenu menu = new JPopupMenu();
        final AbstractAction editAction = new TablePopupAction(table, tableModel, "Actions.ShowDescriptions", "/icons/text-x-generic.png") {

			@Override
			protected void actionPerformed(
					List<AccumulatedProjectActivity> selectedActivities) {
				
				DescriptionsDialog dialog = new DescriptionsDialog(
						AWTUtils.getFrame(AccummulatedActitvitiesPanel.this), selectedActivities.get(0));
                dialog.pack();
                dialog.setLocationRelativeTo(AWTUtils.getFrame(menu));
                dialog.setVisible(true);
			}
        };
        
        menu.add(editAction);
        
        // attach menu to table cells
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(final MouseEvent e) {
                checkForPopup(e);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                checkForPopup(e);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                checkForPopup(e);
            }
            
            private void checkForPopup(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    JTable table = (JTable) e.getSource();
                    int[] selectionIndices = table.getSelectedRows();
                    if(selectionIndices.length == 0) {
                        // select cell under mouse
                        int row = table.rowAtPoint(e.getPoint());
                        int column = table.columnAtPoint(e.getPoint());
                        table.changeSelection(row, column, false, false);
                    }
                    
                    if(selectionIndices.length > 1) {
                        // edit action works only on a single cell
                        editAction.setEnabled(false);
                    } else {
                        editAction.setEnabled(true);
                    }
                    menu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
		
	}
    
    private static abstract class TablePopupAction extends AbstractAction {
    	private final JXTable table;
    	private final DefaultEventTableModel<AccumulatedProjectActivity> tableModel;
    	
    	public TablePopupAction(JXTable table, DefaultEventTableModel<AccumulatedProjectActivity> tableModel, String resourceKey, String iconUrl) {
    		super(TEXT_BUNDLE.textFor(resourceKey), new ImageIcon(AllActitvitiesPanel.class.getResource(iconUrl)));
			this.table = table;
			this.tableModel = tableModel;
		}

		public void actionPerformed(final ActionEvent event) {
    		// 1. Get selected activities
            int[] selectionIndices = table.getSelectedRows();
            
            List<AccumulatedProjectActivity> selectedActivities = new ArrayList<>(selectionIndices.length);
            for(int index : selectionIndices) {
                int modelIndex = table.convertRowIndexToModel(index);
                selectedActivities.add(tableModel.getElementAt(modelIndex));
            }
            
            actionPerformed(selectedActivities);
    	}

		protected abstract void actionPerformed(List<AccumulatedProjectActivity> selectedActivities);
    }

	public void update(final Observable o, final Object arg) {
        if (o != null && o instanceof ObservingAccumulatedActivitiesReport) {
            tableModel.fireTableDataChanged();
        }
        
    }
}
