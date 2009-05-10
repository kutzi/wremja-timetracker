package com.kemai.wremja.gui.actions;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;

import javax.swing.ImageIcon;
import javax.swing.KeyStroke;


import com.kemai.util.TextResourceBundle;
import com.kemai.wremja.gui.dialogs.AddOrEditActivityDialog;
import com.kemai.wremja.gui.model.PresentationModel;

/**
 * Displays the dialog to add a new project activity.
 * @author remast
 */
@SuppressWarnings("serial") //$NON-NLS-1$
public class AddActivityAction extends AbstractWremjaAction {

    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(AddActivityAction.class);

    public AddActivityAction(final Frame owner, final PresentationModel model) {
        super(owner, model);
        putValue(SMALL_ICON, new ImageIcon(getClass().getResource("/icons/gtk-add.png"))); //$NON-NLS-1$
        putValue(NAME, textBundle.textFor("AddActivityAction.Name")); //$NON-NLS-1$
        putValue(SHORT_DESCRIPTION, textBundle.textFor("AddActivityAction.ShortDescription")); //$NON-NLS-1$
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke('N', InputEvent.CTRL_MASK));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void actionPerformed(final ActionEvent event) {
        // Display dialog to add activity
        final AddOrEditActivityDialog addActivityDialog = new AddOrEditActivityDialog(getOwner(), getModel());
        addActivityDialog.pack();
        addActivityDialog.setLocationRelativeTo(getOwner());
        addActivityDialog.setVisible(true);
    }

}
