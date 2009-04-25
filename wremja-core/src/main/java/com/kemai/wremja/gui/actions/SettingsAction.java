package com.kemai.wremja.gui.actions;

import java.awt.Frame;
import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;


import com.kemai.util.TextResourceBundle;
import com.kemai.wremja.gui.dialogs.SettingsDialog;
import com.kemai.wremja.gui.model.PresentationModel;

/**
 * Shows the settings dialog of the application.
 * @author remast
 */
@SuppressWarnings("serial")//$NON-NLS-1$
public class SettingsAction extends AbstractWremjaAction {

    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(SettingsAction.class);

    /**
     * Creates a new settings action.
     * @param owner the owning frame
     */
    public SettingsAction(final Frame owner, final PresentationModel model) {
        super(owner, model);

        putValue(NAME, textBundle.textFor("SettingsAction.Name")); //$NON-NLS-1$
        putValue(SMALL_ICON, new ImageIcon(getClass().getResource("/icons/stock_folder-properties.png"))); //$NON-NLS-1$
        putValue(SHORT_DESCRIPTION, textBundle.textFor("SettingsAction.ShortDescription")); //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void actionPerformed(final ActionEvent event) {
        // Display the settings dialog
        final SettingsDialog settingsDialog = new SettingsDialog(getOwner(), getModel());
        settingsDialog.setVisible(true);
    }

}
