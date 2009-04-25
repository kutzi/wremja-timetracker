package com.kemai.wremja.gui.panels.report;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.util.Observable;
import java.util.Observer;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.autocomplete.ComboBoxCellEditor;
import org.jdesktop.swingx.renderer.DefaultTableRenderer;
import org.jdesktop.swingx.renderer.FormatStringValue;
import org.jdesktop.swingx.table.DatePickerCellEditor;

import com.kemai.swing.util.GuiConstants;
import com.kemai.util.TextResourceBundle;
import com.kemai.wremja.FormatUtils;
import com.kemai.wremja.gui.Launcher;
import com.kemai.wremja.gui.events.BaralgaEvent;
import com.kemai.wremja.gui.model.PresentationModel;
import com.kemai.wremja.gui.panels.table.AllActivitiesTableFormat;
import com.kemai.wremja.model.Project;
import com.kemai.wremja.model.ProjectActivity;

import ca.odell.glazedlists.swing.EventComboBoxModel;
import ca.odell.glazedlists.swing.EventListJXTableSorting;
import ca.odell.glazedlists.swing.EventTableModel;

/**
 * @author remast
 */
@SuppressWarnings("serial")//$NON-NLS-1$
public class AllActitvitiesPanel extends JXPanel implements Observer {

    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(Launcher.class);

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
        table.getColumn(3).setCellRenderer(new DefaultTableRenderer(new FormatStringValue(FormatUtils.getTimeFormat())));
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
            public void mouseReleased(final MouseEvent e) {
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
        if (source == null || !(eventObject instanceof BaralgaEvent)) {
            return;
        }

        final BaralgaEvent event = (BaralgaEvent) eventObject;

        switch (event.getType()) {
            case BaralgaEvent.PROJECT_ACTIVITY_CHANGED:
                tableModel.fireTableDataChanged();
                break;
            
            case BaralgaEvent.PROJECT_ACTIVITY_ADDED:
            case BaralgaEvent.PROJECT_ACTIVITY_REMOVED:
                tableModel.fireTableDataChanged();
                break;
    
            case BaralgaEvent.PROJECT_CHANGED:
                tableModel.fireTableDataChanged();
                break;
        }
    }

}
