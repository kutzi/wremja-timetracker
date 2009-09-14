package com.kemai.wremja.gui.dialogs.table;

import java.beans.PropertyChangeEvent;

import ca.odell.glazedlists.gui.WritableTableFormat;

import com.kemai.wremja.gui.model.PresentationModel;
import com.kemai.wremja.model.Project;

/**
 * Format for table containing the projects.
 * @author remast
 */
public class ProjectListTableFormat implements WritableTableFormat<Project> {

    /** The model. */
    private final PresentationModel model;

    public ProjectListTableFormat(final PresentationModel model) {
        this.model = model;
    }

    public boolean isEditable(final Project project, final int column) {
        return true;
    }

    public Project setColumnValue(final Project project, final Object value,
            final int column) {
        switch (column) {
        case 0:
            final String oldTitle = project.getTitle();
            final String newTitle = ((String) value).trim();

            if(Project.validateProjectName(newTitle)) {
                project.setTitle(newTitle);
    
                // Fire event
                final PropertyChangeEvent propertyChangeEvent = new PropertyChangeEvent(project, Project.PROPERTY_TITLE, oldTitle, newTitle);
                model.fireProjectChangedEvent(project, propertyChangeEvent);
            }
            break;
        case 1:
        {
            Boolean oldValue = project.getBillable();
            Boolean newValue = (Boolean)value;
            project.setBillable(newValue);
            model.fireProjectChangedEvent(project,
                    new PropertyChangeEvent(project, Project.PROPERTY_BILLABLE,
                            oldValue, newValue));
            break;
        }
        case 2:
        {
            Boolean oldValue = project.getEnabled();
            Boolean newValue = (Boolean)value;
            project.setEnabled(newValue);
            model.fireProjectChangedEvent(project,
                    new PropertyChangeEvent(project, Project.PROPERTY_VISIBLE,
                            oldValue, newValue));
            break;
        }
        default:
            throw new IllegalArgumentException("Illegal column: " + column);
        }
        
        return project;
    }

    public int getColumnCount() {
        return 3;
    }

    public String getColumnName(final int column) {
        switch (column) {
        case 0: return "Name";
        case 1: return "Billable";
        case 2: return "Enabled";
        default: return null;
        }
    }

    public Object getColumnValue(final Project project, final int column) {
        switch (column) {
        case 0:
            return project.getTitle();
        case 1:
            return project.getBillable();
        case 2:
            return project.getEnabled();
        default:
            throw new IllegalArgumentException("Illegal column: " + column);
        }
    }

}
