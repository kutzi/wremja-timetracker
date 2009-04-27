/**
 * 
 */
package com.kemai.wremja.gui.panels.report;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;

import javax.swing.BorderFactory;
import javax.swing.border.TitledBorder;

import org.jdesktop.swingx.JXPanel;

import com.kemai.swing.text.TextEditor;
import com.kemai.wremja.gui.GuiConstants;
import com.kemai.wremja.gui.model.PresentationModel;
import com.kemai.wremja.model.ProjectActivity;

/**
 * Holds the editor for the description of a project activity.
 * @author remast
 */
@SuppressWarnings("serial")
public class DescriptionPanelEntry extends JXPanel {

    private ProjectActivity activity;

    private TextEditor editor;

    private TitledBorder titledBorder;
    
    private PresentationModel model;

    public DescriptionPanelEntry(final ProjectActivity activity, final PresentationModel model) {
        this.activity = activity;
        this.model = model;
        initialize();
    }

    private void initialize() {
        this.setLayout(new BorderLayout());
        titledBorder = BorderFactory.createTitledBorder(String.valueOf(activity));
        titledBorder.setTitleColor(GuiConstants.DARK_BLUE);
        this.setBorder(titledBorder);

        editor = new TextEditor();
        editor.setText(activity.getDescription());
        editor.setBorder(BorderFactory.createLineBorder(GuiConstants.VERY_LIGHT_GREY));
        this.add(editor, BorderLayout.CENTER);

        editor.addTextObserver(new TextEditor.TextChangeObserver() {

            public void onTextChange() {
                final String oldDescription = activity.getDescription();
                final String newDescription = editor.getText();
                
                activity.setDescription(newDescription);

                final PropertyChangeEvent propertyChangeEvent = new PropertyChangeEvent(activity, ProjectActivity.PROPERTY_DESCRIPTION, oldDescription, newDescription);
                model.fireProjectActivityChangedEvent(activity, propertyChangeEvent);
              }
        });
    }

    /**
     * Update internal state from the project activity.
     */
    public void update() {
        this.titledBorder.setTitle(String.valueOf(activity));
        updateUI();
    }

}
