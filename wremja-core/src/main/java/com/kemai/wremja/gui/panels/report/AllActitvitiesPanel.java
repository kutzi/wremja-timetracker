package com.kemai.wremja.gui.panels.report;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Observable;
import java.util.Observer;

import javax.swing.AbstractAction;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
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

import ca.odell.glazedlists.swing.EventComboBoxModel;
import ca.odell.glazedlists.swing.EventListJXTableSorting;
import ca.odell.glazedlists.swing.EventTableModel;

import com.kemai.util.TextResourceBundle;
import com.kemai.wremja.FormatUtils;
import com.kemai.wremja.gui.GuiConstants;
import com.kemai.wremja.gui.events.WremjaEvent;
import com.kemai.wremja.gui.model.PresentationModel;
import com.kemai.wremja.gui.panels.table.AllActivitiesTableFormat;
import com.kemai.wremja.logging.Logger;
import com.kemai.wremja.model.Project;
import com.kemai.wremja.model.ProjectActivity;

/**
 * @author remast
 */
@SuppressWarnings("serial")//$NON-NLS-1$
public class AllActitvitiesPanel extends JXPanel implements Observer {

    private static final Logger log = Logger.getLogger(AllActitvitiesPanel.class);

    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(GuiConstants.class);

    /** The model. */
    private final PresentationModel model;

    private EventTableModel<ProjectActivity> tableModel;


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
        tableModel = new EventTableModel<ProjectActivity>(model.getActivitiesList(),
                new AllActivitiesTableFormat(model));
        final JXTable table = new JXTable(tableModel);

        // Fix sorting
        EventListJXTableSorting.install(table, model.getActivitiesList());

        table.getColumn(1).setCellRenderer(new DefaultTableRenderer(new FormatStringValue(DateFormat.getDateInstance())));
        table.getColumn(1).setCellEditor(new DatePickerCellEditor());

        table.getColumn(2).setCellRenderer(new DefaultTableRenderer(new FormatStringValue(FormatUtils.getTimeFormat())));
        table.getColumn(2).setCellEditor( new MaskCellEditor() );
        table.getColumn(3).setCellRenderer(new DefaultTableRenderer(new FormatStringValue(FormatUtils.getTimeFormat())));
        table.getColumn(3).setCellEditor( new MaskCellEditor() );
        table.getColumn(4).setCellRenderer(new DefaultTableRenderer(new FormatStringValue(FormatUtils.getDurationFormat())));

        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(final ListSelectionEvent e) {
                if (table.getSelectedRows() == null) {
                    table.setToolTipText(null);
                }

                double duration = 0;
                
                for (int i : table.getSelectedRows()) {
                    duration += model.getActivitiesList().get(i).getDuration();
                }

                table.setToolTipText(textBundle.textFor("AllActivitiesPanel.tooltipDuration", FormatUtils.getDurationFormat().format(duration))); //$NON-NLS-1$
            }

        });

        final JPopupMenu menu = new JPopupMenu();
        menu.add(new AbstractAction(textBundle.textFor("AllActitvitiesPanel.Delete"), new ImageIcon(getClass().getResource("/icons/gtk-delete.png"))) { //$NON-NLS-1$

            public void actionPerformed(final ActionEvent event) {
                // 1. Get selected activities
                int[] selectionIndices = table.getSelectedRows();

                // 2. Remove all selected activities
                for (int selectionIndex : selectionIndices) {
                    model.removeActivity(model.getActivitiesList().get(selectionIndex), this);
                }
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
                    JTable source = (JTable) e.getSource();
                    int row = source.rowAtPoint(e.getPoint());
                    int column = source.columnAtPoint(e.getPoint());
                    source.changeSelection(row, column, false, false);
                    menu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
        table.setPreferredScrollableViewportSize(table.getPreferredSize());

        table.setHighlighters(GuiConstants.HIGHLIGHTERS);
        table.setCellEditor(new JXTable.GenericEditor());

        final TableColumn projectColumn = table.getColumn(0);
        final TableCellEditor cellEditor = new ComboBoxCellEditor(new JComboBox(new EventComboBoxModel<Project>(model
                .getProjectList())));
        projectColumn.setCellEditor(cellEditor);

        JScrollPane table_scroll_pane = new JScrollPane(table);
        this.add(table_scroll_pane);
    }


    /**
     * {@inheritDoc}
     */
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
                            if (false) { // userSaysRevert()) { //reverted
                                tf.postActionEvent(); // inform the editor
                            }
                        } else
                            try { // The text is valid,
                                tf.commitEdit(); // so use it.
                                tf.postActionEvent(); // stop editing
                            } catch (java.text.ParseException exc) {
                            }
                    }
                });

            } catch (ParseException e) {
                log.error(e, e);
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
