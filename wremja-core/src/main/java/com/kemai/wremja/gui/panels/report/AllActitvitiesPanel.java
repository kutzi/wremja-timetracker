package com.kemai.wremja.gui.panels.report;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.AbstractAction;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.MaskFormatter;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.autocomplete.ComboBoxCellEditor;
import org.jdesktop.swingx.renderer.DefaultTableRenderer;
import org.jdesktop.swingx.renderer.FormatStringValue;
import org.jdesktop.swingx.table.DatePickerCellEditor;

import ca.odell.glazedlists.swing.DefaultEventComboBoxModel;
import ca.odell.glazedlists.swing.DefaultEventTableModel;

import com.kemai.swing.util.AWTUtils;
import com.kemai.swing.util.WTable;
import com.kemai.util.TextResourceBundle;
import com.kemai.wremja.FormatUtils;
import com.kemai.wremja.gui.GuiConstants;
import com.kemai.wremja.gui.dialogs.AddOrEditActivityDialog;
import com.kemai.wremja.gui.dialogs.SplitActivityDialog;
import com.kemai.wremja.gui.events.WremjaEvent;
import com.kemai.wremja.gui.model.PresentationModel;
import com.kemai.wremja.gui.panels.table.AllActivitiesTableFormat;
import com.kemai.wremja.logging.Logger;
import com.kemai.wremja.model.Project;
import com.kemai.wremja.model.ProjectActivity;

/**
 * @author remast
 */
@SuppressWarnings("serial")
public class AllActitvitiesPanel extends JXPanel implements Observer {

    private static final Logger LOG = Logger.getLogger(AllActitvitiesPanel.class);

    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(GuiConstants.class);

    /** The model. */
    private final PresentationModel model;

    private DefaultEventTableModel<ProjectActivity> tableModel;


    /**
     * Create a panel showing all activities of the given model.
     * 
     * @param model
     *            the model
     */
    public AllActitvitiesPanel(final PresentationModel model) {
        this.model = model;
        this.model.addObserver(this);

        this.setLayout(new BorderLayout());

        initialize();
    }


    /**
     * Set up GUI components.
     */
    private void initialize() {
        tableModel = new DefaultEventTableModel<ProjectActivity>(model.getActivitiesList(),
                new AllActivitiesTableFormat(model));
        final JXTable table = new WTable(tableModel) {
            @Override
            public String getToolTipText() {
                if (getSelectedRows().length == 0) {
                    return "";
                }

                double duration = 0;
                
                for (int i : getSelectedRows()) {
                	int modelIndex = convertRowIndexToModel(i);
                    duration += tableModel.getElementAt(modelIndex).getDuration();
                }

                return textBundle.textFor("AllActivitiesPanel.tooltipDuration", FormatUtils.getDurationFormat().format(duration)); //$NON-NLS-1$
            }
        };
        
        table.getColumn(1).setCellRenderer(new DefaultTableRenderer(new FormatStringValue(DateFormat.getDateInstance())));
        table.getColumn(1).setCellEditor(new DatePickerCellEditor());

        table.getColumn(2).setCellRenderer(new DefaultTableRenderer(new FormatStringValue(FormatUtils.getTimeFormat())));
        table.getColumn(2).setCellEditor( new MaskCellEditor() );
        table.getColumn(3).setCellRenderer(new DefaultTableRenderer(new FormatStringValue(FormatUtils.getTimeFormat())));
        table.getColumn(3).setCellEditor( new MaskCellEditor() );
        table.getColumn(4).setCellRenderer(new DefaultTableRenderer(new FormatStringValue(FormatUtils.getDurationFormat())));

        initPopupMenu(table);

        table.setPreferredScrollableViewportSize(table.getPreferredSize());

        table.setHighlighters(GuiConstants.HIGHLIGHTERS);
        table.setCellEditor(new JXTable.GenericEditor());

        final TableColumn projectColumn = table.getColumn(0);
        final TableCellEditor cellEditor = new ComboBoxCellEditor(new JComboBox<>(newVisibleProjectsModel()));
        projectColumn.setCellEditor(cellEditor);

        JScrollPane table_scroll_pane = new JScrollPane(table);
        this.add(table_scroll_pane);
    }


	@SuppressWarnings({"unchecked" })
	private ComboBoxModel<Project> newVisibleProjectsModel() {
		return new DefaultEventComboBoxModel<Project>(model.getVisibleProjects());
	}


    private void initPopupMenu(final JXTable table) {
        final JPopupMenu menu = new JPopupMenu();
        final AbstractAction editAction = new TablePopupAction(table, tableModel, "AllActitvitiesPanel.Edit", "/icons/edit-icon.png") {

			@Override
			protected void actionPerformed(
					List<ProjectActivity> selectedActivities) {
				// edit 1st selected activity
                final AddOrEditActivityDialog editActivityDialog = new AddOrEditActivityDialog(
                        AWTUtils.getFrame(AllActitvitiesPanel.this), 
                        model, 
                        selectedActivities.get(0)
                );
                editActivityDialog.pack();
                editActivityDialog.setLocationRelativeTo(AWTUtils.getFrame(menu));
                editActivityDialog.setVisible(true);
			}
        };
        
        menu.add(editAction);
        
        final AbstractAction splitAction = new TablePopupAction(table, tableModel, "AllActitvitiesPanel.Split", "/icons/edit-cut.png") {

			@Override
			protected void actionPerformed(
					List<ProjectActivity> selectedActivities) {
                SplitActivityDialog dialog = new SplitActivityDialog(
                        AWTUtils.getFrame(AllActitvitiesPanel.this), 
                        model, 
                        selectedActivities.get(0)
                );
                dialog.pack();
                dialog.setLocationRelativeTo(AWTUtils.getFrame(menu));
                dialog.setVisible(true);
			}
        };
        menu.add(splitAction);
        
        menu.add(new TablePopupAction(table, tableModel, "AllActitvitiesPanel.Delete", "/icons/gtk-delete.png") {

			@Override
			protected void actionPerformed(
					List<ProjectActivity> selectedActivities) {
				model.removeActivities(selectedActivities, this);
			}
        });

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
                        splitAction.setEnabled(false);
                    } else {
                        editAction.setEnabled(true);
                        splitAction.setEnabled(true);
                    }
                    menu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
    }


    private static abstract class TablePopupAction extends AbstractAction {
    	private final JXTable table;
    	private final DefaultEventTableModel<ProjectActivity> tableModel;
    	
    	public TablePopupAction(JXTable table, DefaultEventTableModel<ProjectActivity> tableModel, String resourceKey, String iconUrl) {
    		super(textBundle.textFor(resourceKey), new ImageIcon(AllActitvitiesPanel.class.getResource(iconUrl)));
			this.table = table;
			this.tableModel = tableModel;
		}

		public void actionPerformed(final ActionEvent event) {
    		// 1. Get selected activities
            int[] selectionIndices = table.getSelectedRows();
            
            List<ProjectActivity> selectedActivities = new ArrayList<ProjectActivity>(selectionIndices.length);
            for(int index : selectionIndices) {
                int modelIndex = table.convertRowIndexToModel(index);
                selectedActivities.add(tableModel.getElementAt(modelIndex));
            }
            
            actionPerformed(selectedActivities);
    	}

		protected abstract void actionPerformed(List<ProjectActivity> selectedActivities);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void update(final Observable source, final Object eventObject) {
        if (source == null || !(eventObject instanceof WremjaEvent)) {
            return;
        }

        final WremjaEvent event = (WremjaEvent) eventObject;

        switch (event.getType()) {
            case PROJECT_ACTIVITY_CHANGED:
                tableModel.fireTableDataChanged();
                break;
            
            case PROJECT_ACTIVITY_ADDED:
            case PROJECT_ACTIVITY_REMOVED:
                tableModel.fireTableDataChanged();
                break;
    
            case PROJECT_CHANGED:
                tableModel.fireTableDataChanged();
                break;
			default:
				break;
        }
    }

    /**
     * see:
     * http://java.sun.com/docs/books/tutorial/uiswing/examples/components/TableFTFEditDemoProject/src/components/IntegerEditor.java
     */
    private static class MaskCellEditor extends DefaultCellEditor {

        private final JFormattedTextField tf;
        
        public MaskCellEditor() {
            super(new JFormattedTextField());
            tf = (JFormattedTextField)getComponent();
            try {
                MaskFormatter formatter = new MaskFormatter("##:##");
                formatter.setPlaceholder("00:00");
                formatter.setPlaceholderCharacter('0');
                tf.setFormatterFactory(new DefaultFormatterFactory(formatter));

                tf.setHorizontalAlignment(JTextField.LEFT);
                tf.setFocusLostBehavior(JFormattedTextField.PERSIST);

                // React when the user presses Enter while the editor is
                // active. (Tab is handled as specified by
                // JFormattedTextField's focusLostBehavior property.)
                tf.getInputMap().put(
                        KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "check");
                tf.getActionMap().put("check", new AbstractAction() {
                    public void actionPerformed(ActionEvent e) {
                        if (!tf.isEditValid()) { // The text is invalid.
//                            if (false) { // userSaysRevert()) { //reverted
//                                tf.postActionEvent(); // inform the editor
//                            }
                        } else
                            try { // The text is valid,
                                tf.commitEdit(); // so use it.
                                tf.postActionEvent(); // stop editing
                            } catch (java.text.ParseException exc) {
                            }
                    }
                });

            } catch (ParseException e) {
                LOG.error(e, e);
            }
        }
        
        //Override to invoke setValue on the formatted text field.
        @Override
        public Component getTableCellEditorComponent(JTable table,
                Object value, boolean isSelected,
                int row, int column) {
            JFormattedTextField ftf =
                (JFormattedTextField)super.getTableCellEditorComponent(
                    table, value, isSelected, row, column);
            ftf.setValue(value);
            return ftf;
        }

        //Override to check whether the edit is valid,
        //setting the value if it is and complaining if
        //it isn't.  If it's OK for the editor to go
        //away, we need to invoke the superclass's version 
        //of this method so that everything gets cleaned up.
        @Override
        public boolean stopCellEditing() {
            JFormattedTextField ftf = (JFormattedTextField)getComponent();
            if (ftf.isEditValid()) {
                try {
                    ftf.commitEdit();
                } catch (java.text.ParseException exc) { }
            
            } else { //text is invalid
                return false; //don't let the editor go away
            }
            return super.stopCellEditing();
        }

        
    }
}
