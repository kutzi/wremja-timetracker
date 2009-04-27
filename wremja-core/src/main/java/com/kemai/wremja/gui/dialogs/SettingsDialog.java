package com.kemai.wremja.gui.dialogs;

import info.clearthought.layout.TableLayout;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.jdesktop.swingx.JXHeader;

import com.kemai.swing.dialog.EscapeDialog;
import com.kemai.util.TextResourceBundle;
import com.kemai.wremja.gui.settings.UserSettings;

/**
 * The settings dialog for editing both application and user settings.
 * @author remast
 */
@SuppressWarnings("serial")
public class SettingsDialog extends EscapeDialog {

    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(SettingsDialog.class);

    /** The model. */
    private final UserSettings settings;

    private JTextField loginField = new JTextField();
    private JPasswordField passwordField = new JPasswordField();
    
    /**
     * Creates a new settings dialog.
     * @param owner the owning frame
     * @param model the model
     */
    public SettingsDialog(final Frame owner, final UserSettings settings) {
        super(owner);
        this.settings = settings;

        initialize();
    }

    /**
     * Set up GUI components.
     */
    private void initialize() {
        setLocationRelativeTo(getOwner());

        final double border = 5;
        final double size[][] = {
                { border, TableLayout.PREFERRED, border, TableLayout.FILL, border }, // Columns
                { border, TableLayout.PREFERRED, border, TableLayout.PREFERRED, border,
                    TableLayout.PREFERRED, TableLayout.FILL, TableLayout.PREFERRED, border   }  // Rows
        };

        final TableLayout tableLayout = new TableLayout(size);
        this.setLayout(tableLayout);

        this.setSize(300, 180);

        this.setName("SettingsDialog"); //$NON-NLS-1$
        this.setTitle(textBundle.textFor("SettingsDialog.Title")); //$NON-NLS-1$
        this.add(new JXHeader(textBundle.textFor("SettingsDialog.ApplicationSettingsTitle"), null), "0, 0, 3, 1"); //$NON-NLS-1$ //$NON-NLS-2$

        JLabel loginLabel = new JLabel( "Timetracker login" );
        this.add(loginLabel, "1, 3"); //$NON-NLS-1$
        this.add( loginField, "3, 3"); //$NON-NLS-1$
        JLabel passwordLabel = new JLabel( "Timetracker password" );
        this.add(passwordLabel, "1, 5");
        this.add(passwordField, "3, 5");

        JButton saveButton = new JButton();
        saveButton.setText(textBundle.textFor("SettingsDialog.SaveLabel")); //$NON-NLS-1$
        saveButton.setIcon(new ImageIcon(getClass().getResource("/icons/gtk-save.png"))); //$NON-NLS-1$

        // Confirm with 'Enter' key
        saveButton.setMnemonic(KeyEvent.VK_ENTER);

        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent event) {
                settings.setAnukoLogin(loginField.getText());
                settings.setAnukoPassword(String.valueOf(passwordField.getPassword()));
                SettingsDialog.this.dispose();
            }
        });

        saveButton.setDefaultCapable(true);
        
        this.add(saveButton, "1, 7, 3, 7");
        
        readFromSettings();
    }

    /**
     * Reads the data displayed in the dialog from the settings.
     */
    private void readFromSettings() {
        this.loginField.setText(this.settings.getAnukoLogin());
        this.passwordField.setText(this.settings.getAnukoPassword());
    }

}
