package com.kemai.wremja.gui.actions;

import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.kemai.swing.util.AWTUtils;
import com.kemai.util.TextResourceBundle;
import com.kemai.wremja.gui.model.PresentationModel;
import com.kemai.wremja.gui.model.ProjectActivityStateException;
import com.kemai.wremja.model.Project;

/**
 * Action to change the active project in the systray.
 */
@SuppressWarnings("serial") //$NON-NLS-1$
public class ChangeProjectAction extends AbstractWremjaAction {

    private static final Log log = LogFactory.getLog(ChangeProjectAction.class);
    
    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(ChangeProjectAction.class);

    private static final boolean CONFIRMATION_OVERRIDE = true;
    
    /**
     * The project to be activated when the action is performed.
     */
    private Project newProject;

    public ChangeProjectAction(final PresentationModel model, final Project newProject) {
        super(model);
        this.newProject = newProject;

        // Highlight the currently selected project
        String projectName = String.valueOf(newProject);
        if (model.getSelectedProject() != null && model.getSelectedProject().equals(newProject)) {
            projectName = "* " + projectName;
        }

        putValue(NAME, projectName);
        putValue(SHORT_DESCRIPTION, textBundle.textFor("ChangeProjectAction.ShortDescription") + String.valueOf(newProject) + "."); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void actionPerformed(final ActionEvent event) {
        boolean projectHasChanged = false;
        // Check if the new project is different from the old one
        if (! ObjectUtils.equals(getModel().getSelectedProject(), newProject)) {
            getModel().changeProject(newProject);
            projectHasChanged = true;
        }

        try {
            if (!getModel().isActive() && isStartConfirmed()) {
                getModel().start();
            } else if( getModel().isActive() && isStopConfirmed()) {
                if( !projectHasChanged ) {
                    getModel().stop();
                }
            }
        } catch (ProjectActivityStateException e) {
            log.warn(e, e);
        }
    }

    /**
     * Checks with user wether newly selected project should be started or not.
     * @return <code>true</code> if project shall be started else <code>false</code>
     */
    private boolean isStartConfirmed() {
        if( CONFIRMATION_OVERRIDE ) {
            return true;
        }
        
        // Unfortunately the systray gives no hint where it is located, so we have to guess
        // by getting the current mouse location.
        final Point currentMousePosition = MouseInfo.getPointerInfo().getLocation();

        final JOptionPane pane = new JOptionPane(
                textBundle.textFor("StartActivityConfirmDialog.Message"), //$NON-NLS-1$
                JOptionPane.QUESTION_MESSAGE, 
                JOptionPane.YES_NO_OPTION
                );

        final JDialog dialog = pane.createDialog(textBundle.textFor("StartActivityConfirmDialog.Title")); //$NON-NLS-1$
        dialog.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icons/Baralga-Tray.gif"))); //$NON-NLS-1$ 
        
        Dimension d = dialog.getPreferredSize();
        final Point preferredLeftTop = new Point(currentMousePosition.x - d.width/2, currentMousePosition.y - d.height/2);
        AWTUtils.keepInScreenBounds(preferredLeftTop, dialog);

        dialog.setVisible(true);
        dialog.dispose();

        final Object selectedValue = pane.getValue();

        return (selectedValue instanceof Integer)
        && (((Integer) selectedValue).intValue() == JOptionPane.YES_OPTION);
    }
    
    private boolean isStopConfirmed() {
        if( CONFIRMATION_OVERRIDE ) { 
            return true;
        } else {
            // TODO
            return false;
        }
    }
}
