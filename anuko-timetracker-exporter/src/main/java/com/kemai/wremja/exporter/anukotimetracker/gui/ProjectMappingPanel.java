package com.kemai.wremja.exporter.anukotimetracker.gui;

import info.clearthought.layout.TableLayout;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;

import org.apache.commons.lang.StringUtils;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXPanel;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.swing.EventComboBoxModel;

import com.kemai.swing.util.LabeledItem;
import com.kemai.wremja.exporter.anukotimetracker.model.AnukoActivity;
import com.kemai.wremja.exporter.anukotimetracker.model.AnukoInfo;
import com.kemai.wremja.exporter.anukotimetracker.model.AnukoProject;
import com.kemai.wremja.exporter.anukotimetracker.model.Mapping;
import com.kemai.wremja.model.Project;
import com.kemai.wremja.model.ProjectActivity;
import com.kemai.wremja.model.ReadableRepository;
import com.kemai.wremja.model.filter.Filter;

/**
 * A panel which helps matching the wremja {@link Project}s to
 * the Anuko activities.
 */
public class ProjectMappingPanel extends JXPanel {

    private static final long serialVersionUID = 1L;

    private final AnukoInfo info;
    private final ReadableRepository wremjaData;
    private final Filter filter;
    private final JButton exportButton;
    private SortedSet<Project> wremjaProjects;
    
    private final Mapping mappings;

    
    public ProjectMappingPanel(AnukoInfo info, ReadableRepository wremjaData,
            Filter filter, String mappings, JButton exportButton) {
        super();
        this.info = info;
        this.wremjaData = wremjaData;
        this.filter = filter;
        this.exportButton = exportButton;
        this.exportButton.setEnabled(true);
        
        if(StringUtils.isNotBlank(mappings)) {
        	this.mappings = Mapping.fromString(mappings, wremjaData, info);
        } else {
        	this.mappings = new Mapping();
        }
        
        initialize();
    }

    private void initialize() {
        final double borderBig = 8;
        final double border = 3;
        
        int numberOfRows = getWremjaProjects().size();
        double[] rowLayout = new double[numberOfRows*2 + 3];
        for( int i=0; i < numberOfRows; i++ ) {
            int row = i*2;
            rowLayout[row] = border;
            rowLayout[row+1] = TableLayout.PREFERRED;
        }
        
        if( rowLayout.length >= 2 ) {
            rowLayout[rowLayout.length-2] = TableLayout.FILL;
            rowLayout[rowLayout.length-1] = border;
        }
        
        final double size[][] = {
                { border, TableLayout.PREFERRED, borderBig, TableLayout.FILL}, // Columns
                rowLayout };
        this.setLayout(new TableLayout(size));
        
        JXLabel label = new JXLabel("Wremja");
        label.setFont(label.getFont().deriveFont(Font.BOLD));
        this.add(label, "1, 1");
        
        label = new JXLabel("Anuko");
        label.setFont(label.getFont().deriveFont(Font.BOLD));
        this.add(label, "3, 1");
        
        int row = 3;
        for( Project wremjaProject : getWremjaProjects() ) {
            label = new JXLabel(wremjaProject.getTitle());
            label.setToolTipText(wremjaProject.getDescription());
            this.add(label, "1, " + row); //$NON-NLS-1$
            this.add(getProjectSelector(wremjaProject, info.getActivities()), "3, " + row); //$NON-NLS-1$
            row = row + 2;
        }
    }
    
    private SortedSet<Project> getWremjaProjects() {
        if( this.wremjaProjects == null ) {
            if( filter == null ) {
                this.wremjaProjects = new TreeSet<Project>(this.wremjaData.getProjects());
            } else {
                // return only those projects which have matching activities
                List<ProjectActivity> activities = this.wremjaData.getActivities();
                activities = filter.applyFilters(activities);
                SortedSet<Project> projects = new TreeSet<Project>();
                for( ProjectActivity activity : activities ) {
                    projects.add(activity.getProject());
                }
                this.wremjaProjects = projects;
            }
        }
        return this.wremjaProjects;
    }
    
    private static final AnukoActivity NO_SELECTION = new AnukoActivity(-4711, "*"); //$NON-NLS-1$

    private static final LabeledItem<AnukoActivity> NO_SELECTION_ITEM = new LabeledItem<AnukoActivity>(NO_SELECTION, " "); //$NON-NLS-1$
    
    
    private JComboBox getProjectSelector(final Project p, Collection<AnukoActivity> activities) {
        
    	AnukoActivity selectedActivity = this.mappings.get(p);
    	
        EventList<LabeledItem<AnukoActivity>> eventList =
            new BasicEventList<LabeledItem<AnukoActivity>>();
        eventList.add(NO_SELECTION_ITEM);
        
        int selectionIndex = 0;
        int i = 1;
        for( AnukoActivity activity : activities ) {
            eventList.add( new LabeledItem<AnukoActivity>(activity, activity.getName()) );
            if(activity.equals(selectedActivity)) {
            	selectionIndex = i;
            }
            i++;
        }
        SortedList<LabeledItem<AnukoActivity>> projectList = new SortedList<LabeledItem<AnukoActivity>>(eventList);
            
        
        JComboBox projectFilterSelector = new JComboBox(
                new EventComboBoxModel<LabeledItem<AnukoActivity>>(projectList)
        );
        
        projectFilterSelector.setRenderer(new ComboTooltipRenderer() );

        projectFilterSelector.setSelectedIndex(selectionIndex);
        
        if(selectionIndex == 0) {
        	this.exportButton.setEnabled(false);
        }

        projectFilterSelector.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
            	@SuppressWarnings("unchecked")
                LabeledItem<AnukoActivity> item = (LabeledItem<AnukoActivity>)
                    ((JComboBox)e.getSource()).getSelectedItem();
                if( item == NO_SELECTION_ITEM ) {
                    removeMapping(p);
                } else {
                    addMapping(p, item.getItem());
                }
            }
            
        });
        return projectFilterSelector;
    }
    
    private void addMapping( Project p, AnukoActivity a) {
        this.mappings.add(p, a);
        if( mappings.size() == getWremjaProjects().size() ) {
            this.exportButton.setEnabled(true);
        }
    }
    
    private void removeMapping( Project p ) {
        this.mappings.remove(p);
        this.exportButton.setEnabled(false);
    }
    
    private static class ComboTooltipRenderer extends DefaultListCellRenderer {

        private static final long serialVersionUID = 1L;

        @Override
        public Component getListCellRendererComponent(JList list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {
            JComponent comp = (JComponent) super.getListCellRendererComponent(
                    list, value, index, isSelected, cellHasFocus);

            StringBuilder toolTipText = null;
            
            if (value != null) {
                @SuppressWarnings("unchecked")
                LabeledItem<AnukoActivity> label = (LabeledItem<AnukoActivity>)value;
                AnukoActivity activity = label.getItem();
                if( activity.getProjects().size() > 0) {
                    toolTipText = new StringBuilder("Project(s): ");
                    for(AnukoProject p : activity.getProjects()) {
                        toolTipText.append( p.getName() ).append( ", ");
                    }
                    toolTipText.delete(toolTipText.length()-2, toolTipText.length());
                }
            }
            comp.setToolTipText(toolTipText != null ? toolTipText.toString() : null);
            return comp;
        }
    }
    
    public Mapping getMappings() {
        return this.mappings;
    }
}
