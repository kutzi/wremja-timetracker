package com.kemai.wremja.gui.actions;

import java.awt.Frame;
import java.awt.event.ActionEvent;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.kemai.util.TextResourceBundle;
import com.kemai.wremja.gui.dialogs.SettingsDialog;
import com.kemai.wremja.gui.model.PresentationModel;
import com.kemai.wremja.gui.settings.IUserSettings;

/**
 * Shows the settings dialog of the application.
 * @author remast
 */
public class SettingsAction extends AbstractWremjaAction {

    private static final long serialVersionUID = 1L;

	public static final Icon ICON = new ImageIcon(SettingsDialog.class.getResource("/icons/stock_folder-properties.png")); //$NON-NLS-1$

	/** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(SettingsAction.class);

	private final IUserSettings settings;

    /**
     * Creates a new settings action.
     * @param owner the owning frame
     */
    public SettingsAction(Frame owner, PresentationModel model, IUserSettings settings) {
        super(owner, model);
		this.settings = settings;

        setName(textBundle.textFor("SettingsAction.Name")); //$NON-NLS-1$
        setTooltip(textBundle.textFor("SettingsAction.ShortDescription")); //$NON-NLS-1$
        setIcon(ICON);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void actionPerformed(final ActionEvent event) {
        // Display the settings dialog
        final SettingsDialog settingsDialog = new SettingsDialog(getOwner(), this.settings);
        settingsDialog.pack();
        settingsDialog.setLocationRelativeTo(getOwner());
        settingsDialog.setVisible(true);
    }

}
