package com.kemai.wremja.gui.actions;

import java.awt.Frame;
import java.awt.event.ActionEvent;

import com.kemai.util.TextResourceBundle;
import com.kemai.wremja.gui.dialogs.SettingsDialog;
import com.kemai.wremja.gui.model.PresentationModel;
import com.kemai.wremja.gui.settings.UserSettings;

/**
 * Shows the settings dialog of the application.
 * @author remast
 */
@SuppressWarnings("serial")
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
        putValue(SMALL_ICON, SettingsDialog.ICON);
        putValue(SHORT_DESCRIPTION, textBundle.textFor("SettingsAction.ShortDescription")); //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void actionPerformed(final ActionEvent event) {
        // Display the settings dialog
        final SettingsDialog settingsDialog = new SettingsDialog(getOwner(), UserSettings.instance());
        settingsDialog.pack();
        settingsDialog.setLocationRelativeTo(getOwner());
        settingsDialog.setVisible(true);
    }

}
