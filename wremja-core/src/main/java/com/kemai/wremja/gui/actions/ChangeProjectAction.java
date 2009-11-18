package com.kemai.wremja.gui.actions;

import java.awt.event.ActionEvent;

import org.apache.commons.lang.ObjectUtils;

import com.kemai.util.TextResourceBundle;
import com.kemai.wremja.gui.model.PresentationModel;
import com.kemai.wremja.gui.model.ProjectActivityStateException;
import com.kemai.wremja.logging.Logger;
import com.kemai.wremja.model.Project;

/**
 * Action to change the active project in the systray.
 */
@SuppressWarnings("serial") 
public class ChangeProjectAction extends AbstractWremjaAction {

    private static final Logger LOG = Logger.getLogger(ChangeProjectAction.class);
    
    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(ChangeProjectAction.class);

    /**
     * The project to be activated when the action is performed.
     */
    private final Project newProject;

    public ChangeProjectAction(final PresentationModel model, final Project newProject) {
        super(model);
        if( newProject == null ) {
        	throw new NullPointerException("newProject must not be null!");
        }
        this.newProject = newProject;

        // Highlight the currently selected project
        String projectName = newProject.getTitle();
        if (model.getSelectedProject() != null && model.getSelectedProject().equals(newProject)) {
            projectName = "* " + projectName;
        }

        setName(projectName);
        putValue(SHORT_DESCRIPTION, textBundle.textFor("ChangeProjectAction.ShortDescription", String.valueOf(newProject))); //$NON-NLS-1$
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
            if (!getModel().isActive()) {
                getModel().start();
            } else if( getModel().isActive()) {
                if( !projectHasChanged ) {
                    getModel().stop();
                }
            }
        } catch (ProjectActivityStateException e) {
            LOG.warn(e, e);
        }
    }
}
