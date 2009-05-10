package com.kemai.wremja.gui.dialogs;

import info.clearthought.layout.TableLayout;

import java.awt.Color;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import org.jdesktop.swingx.JXHeader;

import com.kemai.swing.dialog.EscapeDialog;
import com.kemai.util.TextResourceBundle;
import com.kemai.wremja.gui.settings.UserSettings;
import com.kemai.wremja.logging.Logger;
import com.kemai.wremja.util.validator.UrlValidator;

/**
 * The settings dialog for editing both application and user settings.
 *
 * @author kutzi
 * @author remast
 */
@SuppressWarnings("serial")
public class SettingsDialog extends EscapeDialog {

    private static final Logger log = Logger.getLogger(SettingsDialog.class);
    
    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(SettingsDialog.class);

    /** The model. */
    private final UserSettings settings;
    
    /** Component to edit setting to remember window size and location. */
    private JCheckBox rememberWindowSizeLocation;

    private final JTextField urlField = new JFormattedTextField();
    {
        urlField.getDocument().addDocumentListener(new UrlDocumentListener(urlField));
    }
    private final JTextField loginField = new JTextField();
    private final JPasswordField passwordField = new JPasswordField();

    private JButton saveButton;
    
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
                    TableLayout.PREFERRED, border,
                    TableLayout.PREFERRED, border,
                    TableLayout.PREFERRED, TableLayout.FILL, TableLayout.PREFERRED, border   }  // Rows
        };

        final TableLayout tableLayout = new TableLayout(size);
        this.setLayout(tableLayout);

        this.setSize(320, 200);

        this.setName("SettingsDialog"); //$NON-NLS-1$
        this.setTitle(textBundle.textFor("SettingsDialog.Title")); //$NON-NLS-1$
        this.add(new JXHeader(textBundle.textFor("SettingsDialog.ApplicationSettingsTitle"), null), "0, 0, 3, 1"); //$NON-NLS-1$ //$NON-NLS-2$

        
        rememberWindowSizeLocation = new JCheckBox(textBundle.textFor("SettingsDialog.Setting.RememberWindowSizeLocation.Title"));
        rememberWindowSizeLocation.setToolTipText(textBundle.textFor("SettingsDialog.Setting.RememberWindowSizeLocation.ToolTipText"));
        //rememberWindowSizeLocation.addActionListener(this);
        this.add(rememberWindowSizeLocation, "1, 3, 3, 3"); //$NON-NLS-1$
        
        JLabel urlLabel = new JLabel( "Timetracker base URL" );
        this.add(urlLabel, "1, 5"); //$NON-NLS-1$
        this.add(urlField, "3, 5"); //$NON-NLS-1$
        
        JLabel loginLabel = new JLabel( "Timetracker login" );
        this.add(loginLabel, "1, 7"); //$NON-NLS-1$
        this.add( loginField, "3, 7"); //$NON-NLS-1$
        JLabel passwordLabel = new JLabel( "Timetracker password" );
        this.add(passwordLabel, "1, 9");
        this.add(passwordField, "3, 9");

        saveButton = new JButton();
        saveButton.setText(textBundle.textFor("SettingsDialog.SaveLabel")); //$NON-NLS-1$
        saveButton.setIcon(new ImageIcon(getClass().getResource("/icons/gtk-save.png"))); //$NON-NLS-1$

        // Confirm with 'Alt' (Platform dependent) + 'Enter' key
        saveButton.setMnemonic(KeyEvent.VK_ENTER);

        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent event) {
                settings.setRememberWindowSizeLocation(rememberWindowSizeLocation.isSelected());
                settings.setAnukoUrl(urlField.getText());
                settings.setAnukoLogin(loginField.getText());
                settings.setAnukoPassword(String.valueOf(passwordField.getPassword()));
                SettingsDialog.this.dispose();
            }
        });

        saveButton.setDefaultCapable(true);
        
        this.add(saveButton, "1, 11, 3, 11");
        
        readFromSettings();
    }

    /**
     * Reads the data displayed in the dialog from the settings.
     */
    private void readFromSettings() {
        this.rememberWindowSizeLocation.setSelected(this.settings.isRememberWindowSizeLocation());
        this.urlField.setText(this.settings.getAnukoUrl());
        this.loginField.setText(this.settings.getAnukoLogin());
        this.passwordField.setText(this.settings.getAnukoPassword());
    }

    private class UrlDocumentListener implements DocumentListener {

        private final JTextField tf;
        private final Color defaultColor;
        private final UrlValidator validator;
        
        public UrlDocumentListener( JTextField tf ) {
            this.tf = tf;
            this.defaultColor = tf.getForeground();
            this.validator = new UrlValidator();
        }
        
        @Override
        public void changedUpdate(DocumentEvent e) {
            // noop
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            validate( e.getDocument() );
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            validate( e.getDocument() );
        }
        
        private void validate(Document document) {
            try {
                String text = document.getText(0, document.getLength());
                if( validator.validate(text)) {
                    tf.setForeground(defaultColor);
                    saveButton.setEnabled(true);
                } else {
                    tf.setForeground(Color.RED);
                    saveButton.setEnabled(false);
                }
            } catch (BadLocationException e) {
                log.error(e, e);
            }
        }
        
    }
}
