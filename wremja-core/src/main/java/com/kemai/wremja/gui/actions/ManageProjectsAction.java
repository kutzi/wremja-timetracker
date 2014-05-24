package com.kemai.wremja.gui.actions;

import java.awt.Frame;
import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;

import com.kemai.util.TextResourceBundle;
import com.kemai.wremja.gui.dialogs.ManageProjectsDialog;
import com.kemai.wremja.gui.model.PresentationModel;

/**
 * Displays the dialog to manage the projects.
 * @author remast
 */
@SuppressWarnings("serial") //$NON-NLS-1$
public class ManageProjectsAction extends AbstractWremjaAction {

    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(ManageProjectsAction.class);

    public ManageProjectsAction(final Frame owner, final PresentationModel model) {
        super(owner, model);
        setName(textBundle.textFor("ManageProjectsAction.Name")); //$NON-NLS-1$
        setTooltip(textBundle.textFor("ManageProjectsAction.ShortDescription")); //$NON-NLS-1$
        putValue(SMALL_ICON, new ImageIcon(getClass().getResource("/icons/folder-open.png"))); //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void actionPerformed(final ActionEvent event) {
        // Display dialog to manage projects
        final ManageProjectsDialog manageProjectsDialog = new ManageProjectsDialog(getOwner(), getModel());
        manageProjectsDialog.pack();
        manageProjectsDialog.setLocationRelativeTo(getOwner());
        manageProjectsDialog.setVisible(true);
    }

}
