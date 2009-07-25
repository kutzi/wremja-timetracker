package com.kemai.wremja.gui.dialogs;

import info.clearthought.layout.TableLayout;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import org.jdesktop.swingx.JXHeader;

import com.kemai.swing.dialog.EscapeDialog;
import com.kemai.swing.util.WPasswordField;
import com.kemai.util.OSUtils;
import com.kemai.util.TextResourceBundle;
import com.kemai.util.UiUtilities;
import com.kemai.wremja.gui.actions.SettingsAction;
import com.kemai.wremja.gui.settings.IUserSettings;
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

    private static final Logger LOG = Logger.getLogger(SettingsDialog.class);
    
    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(SettingsDialog.class);
    
    /** The model. */
    private final IUserSettings settings;
    
    /** Component to edit setting to remember window size and location. */
    private final JCheckBox rememberWindowSizeLocation = new JCheckBox(textBundle.textFor("SettingsDialog.Setting.RememberWindowSizeLocation.Title")); //$NON-NLS-1$
    {
        rememberWindowSizeLocation.setToolTipText(textBundle.textFor("SettingsDialog.Setting.RememberWindowSizeLocation.ToolTipText")); //$NON-NLS-1$
    }

    private final JCheckBox purgeEmptyActivities = new JCheckBox(textBundle.textFor("SettingsDialog.Setting.DiscardEmptyActivities.Title")); //$NON-NLS-1$
    {
        purgeEmptyActivities.setToolTipText(textBundle.textFor("SettingsDialog.Setting.DiscardEmptyActivities.ToolTipText")); //$NON-NLS-1$
    }
    
    private final JCheckBox allowOverlappingActivities = new JCheckBox(textBundle.textFor("SettingsDialog.Setting.AllowOverlappingActivities.Title")); //$NON-NLS-1$
    {
    	allowOverlappingActivities.setToolTipText(textBundle.textFor("SettingsDialog.Setting.AllowOverlappingActivities.ToolTipText")); //$NON-NLS-1$
    }
    
    private final JCheckBox useTrayicon = new JCheckBox(textBundle.textFor("SettingsDialog.Setting.UseTrayIcon.Title")); //$NON-NLS-1$
    {
    	String toolTip = textBundle.textFor("SettingsDialog.Setting.UseTrayIcon.ToolTipText"); //$NON-NLS-1$
    	if(OSUtils.isGnome()) {
    		toolTip = "<html>" + toolTip + //$NON-NLS-1$
    		"<br>" + //$NON-NLS-1$
    		textBundle.textFor("SettingsDialog.Setting.UseTrayIcon.ToolTipGnomeWarning") + //$NON-NLS-1$
    		"</html>"; //$NON-NLS-1$
    	}
    	useTrayicon.setToolTipText(toolTip);
    }

    private static final Map<String, String> durationTexts = new HashMap<String, String>() {
	    {
	        put("#0.00", textBundle.textFor("SettingsDialog.Setting.DurationFormat.HoursAndFractions")); //$NON-NLS-1$ //$NON-NLS-2$
	        put("#0:00", textBundle.textFor("SettingsDialog.Setting.DurationFormat.HoursAndMinutes")); //$NON-NLS-1$ //$NON-NLS-2$
	    }
    };
    
    private final JComboBox durationFormat = new JComboBox(new Object[] {"#0.00", "#0:00"}); //$NON-NLS-1$
    {
        ListCellRenderer renderer = new ListCellRenderer() {
            
            private final JLabel label = new JLabel();
            
            @Override
            public Component getListCellRendererComponent(JList list,
                    Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {

                if (isSelected) {
                    label.setBackground(list.getSelectionBackground());
                    label.setForeground(list.getSelectionForeground());
                } else {
                    label.setBackground(list.getBackground());
                    label.setForeground(list.getForeground());
                }

                String description = durationTexts.get(value);
                label.setText(description != null ? description : "");
                label.setFont(list.getFont());

                return label;
            }
        };
        durationFormat.setRenderer(renderer);
    }
    
    private final JTextField urlField = new JFormattedTextField();
    {
        urlField.setColumns(30);
        urlField.getDocument().addDocumentListener(new UrlDocumentListener(urlField));
    }
    private final JTextField loginField = new JTextField();
    private final JPasswordField passwordField = new WPasswordField();

    private final JButton saveButton = new JButton();
    {
        saveButton.setText(textBundle.textFor("SettingsDialog.SaveLabel")); //$NON-NLS-1$
        saveButton.setIcon(new ImageIcon(getClass().getResource("/icons/gtk-save.png"))); //$NON-NLS-1$

        // Confirm with 'Alt' (Platform dependent) + 'Enter' key
        saveButton.setMnemonic(KeyEvent.VK_ENTER);
        saveButton.setEnabled(false);
    }
    
    /**
     * Creates a new settings dialog.
     * @param owner the owning frame
     * @param model the model
     */
    public SettingsDialog(final Frame owner, final IUserSettings settings) {
        super(owner);
        this.settings = settings;

        initialize();
    }

    /**
     * Set up GUI components.
     */
    private void initialize() {
        final double border = 5;
        double[][] size = {
                { border, TableLayout.PREFERRED, border,
                	TableLayout.PREFERRED, border
                	 }, // Columns
                { border, TableLayout.PREFERRED, border, TableLayout.FILL,
                        border, TableLayout.PREFERRED, border } // Rows
        };

        TableLayout tableLayout = new TableLayout(size);

        this.setName("SettingsDialog"); //$NON-NLS-1$
        this.setTitle(textBundle.textFor("SettingsDialog.Title")); //$NON-NLS-1$
        this.setLayout(tableLayout);
        this.add(
                new JXHeader(textBundle
                        .textFor("SettingsDialog.ApplicationSettingsTitle"),
                        null, SettingsAction.ICON), "0, 0, 3, 1"); //$NON-NLS-1$

        JTabbedPane tabbedPane = new JTabbedPane();
        this.add(tabbedPane, "1, 3, 3, 3");

        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent event) {
                settings.setRememberWindowSizeLocation(rememberWindowSizeLocation
                                .isSelected());
                settings.setDiscardEmptyActivities(purgeEmptyActivities.isSelected());
                settings.setUseTrayIcon(useTrayicon.isSelected());
                settings.setAllowOverlappingActivities(allowOverlappingActivities.isSelected());
                settings.setDurationFormat(durationFormat.getSelectedItem().toString());
                settings.setAnukoUrl(urlField.getText());
                settings.setAnukoLogin(loginField.getText());
                settings.setAnukoPassword(String.valueOf(passwordField
                        .getPassword()));
                
                JOptionPane.showMessageDialog(SettingsDialog.this, textBundle.textFor("SettingsDialog.SaveInfo.Message"),
                		textBundle.textFor("SettingsDialog.SaveInfo.Title"), JOptionPane.INFORMATION_MESSAGE);
                
                SettingsDialog.this.dispose();
            }
        });

        saveButton.setDefaultCapable(true);

        this.add(saveButton, "1, 5, 1, 5");
        
        JButton cancelButton = new JButton();
        cancelButton.setText(UIManager.getString("OptionPane.cancelButtonText", Locale.getDefault())); //$NON-NLS-1$
        cancelButton.setMnemonic(UiUtilities.getMnemonic("OptionPane.cancelButtonMnemonic", Locale.getDefault())); //$NON-NLS-1$
        cancelButton.setIcon(new ImageIcon(getClass().getResource("/icons/dialog-cancel.png"))); //$NON-NLS-1$

        cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SettingsDialog.this.dispose();
			}
        });
        this.add(cancelButton, "3, 5, 3, 5");

        // add general pane
        JPanel generalPanel = new JPanel();
        tabbedPane.addTab(textBundle.textFor("SettingsDialog.GeneralTab.Title"), null,
                generalPanel, textBundle.textFor("SettingsDialog.GeneralTab.ToolTipText"));

        size = new double[][] {
                { border, TableLayout.PREFERRED, border, TableLayout.FILL,
                        border }, // Columns
                { border, TableLayout.PREFERRED, border, TableLayout.PREFERRED,
                        border, TableLayout.PREFERRED, border,
                        TableLayout.PREFERRED, border,
                        TableLayout.PREFERRED, border,
                        TableLayout.PREFERRED, border } // Rows
        };
        tableLayout = new TableLayout(size);
        generalPanel.setLayout(tableLayout);

        generalPanel.add(rememberWindowSizeLocation, "1, 1, 3, 1"); //$NON-NLS-1$
        generalPanel.add(purgeEmptyActivities, "1, 3, 3, 3"); //$NON-NLS-1$
        generalPanel.add(allowOverlappingActivities, "1, 5, 3, 5"); //$NON-NLS-1$
        generalPanel.add(useTrayicon, "1, 7, 3, 7"); //$NON-NLS-1$
        JPanel durationFormatPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        durationFormatPanel.add(new JLabel("Duration Format:"));
        durationFormatPanel.add(durationFormat);
        
        generalPanel.add(durationFormatPanel, "1, 9, 3, 9"); //$NON-NLS-1$

        //getRootPane().setDefaultButton(saveButton);

        // add anuko pane
        JPanel anukoPanel = new JPanel();
        tabbedPane.addTab("Anuko", null, anukoPanel, "Anuko exporter is currently not usable");
        int index = tabbedPane.getTabCount() - 1;
        tabbedPane.setEnabledAt(index, true);
        tableLayout = new TableLayout(size);
        anukoPanel.setLayout(tableLayout);

        JLabel urlLabel = new JLabel("Timetracker base URL");
        anukoPanel.add(urlLabel, "1, 1"); //$NON-NLS-1$
        anukoPanel.add(urlField, "3, 1"); //$NON-NLS-1$

        JLabel loginLabel = new JLabel("Timetracker login");
        anukoPanel.add(loginLabel, "1, 3"); //$NON-NLS-1$
        anukoPanel.add(loginField, "3, 3"); //$NON-NLS-1$
        JLabel passwordLabel = new JLabel("Timetracker password");
        anukoPanel.add(passwordLabel, "1, 5");
        anukoPanel.add(passwordField, "3, 5");

        readFromSettings();
        
        addSaveEnabler();
        
    	
    	allowOverlappingActivities.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!allowOverlappingActivities.isSelected()) {
					JOptionPane.showMessageDialog(SettingsDialog.this,
	                		"Note that disabling this option should be considered experimental!\nPress 'Cancel' if you're not sure.",
	                		 "Warning", JOptionPane.WARNING_MESSAGE);
				}
			}
		});

    }

	private void addSaveEnabler() {
		ActionListener saveEnabler = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveButton.setEnabled(true);
			}
		};
        
        rememberWindowSizeLocation.addActionListener(saveEnabler);
        purgeEmptyActivities.addActionListener(saveEnabler);
        allowOverlappingActivities.addActionListener(saveEnabler);
        useTrayicon.addActionListener(saveEnabler);
        durationFormat.addActionListener(saveEnabler);
        
        urlField.addActionListener(saveEnabler);
        loginField.addActionListener(saveEnabler);
        passwordField.addActionListener(saveEnabler);
	}

    /**
     * Reads the data displayed in the dialog from the settings.
     */
    private void readFromSettings() {
        this.rememberWindowSizeLocation.setSelected(this.settings.isRememberWindowSizeLocation());
        this.purgeEmptyActivities.setSelected(this.settings.isDiscardEmptyActivities());
        this.allowOverlappingActivities.setSelected(this.settings.isAllowOverlappingActivities());
        this.useTrayicon.setSelected(this.settings.isUseTrayIcon());
        this.durationFormat.setSelectedItem(this.settings.getDurationFormat());
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
                LOG.error(e, e);
            }
        }
        
    }
}
