package com.kemai.wremja.gui.dialogs;

import static com.kemai.wremja.gui.settings.SettingsConstants.DAY_OF_WEEK_FILTER_ENABLED;
import static com.kemai.wremja.gui.settings.SettingsConstants.MONTH_FILTER_ENABLED;
import static com.kemai.wremja.gui.settings.SettingsConstants.PROJECT_FILTER_ENABLED;
import static com.kemai.wremja.gui.settings.SettingsConstants.WEEK_OF_YEAR_FILTER_ENABLED;
import static com.kemai.wremja.gui.settings.SettingsConstants.YEAR_FILTER_ENABLED;
import info.clearthought.layout.TableLayout;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.swing.ImageIcon;
import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.java.balloontip.BalloonTip;
import net.java.balloontip.styles.BalloonTipStyle;
import net.java.balloontip.styles.RoundedBalloonStyle;
import net.java.balloontip.utils.ToolTipUtils;

import org.jdesktop.swingx.JXHeader;
import org.jdesktop.swingx.JXTextField;

import com.kemai.swing.dialog.EscapeDialog;
import com.kemai.util.TextResourceBundle;
import com.kemai.util.UiUtilities;
import com.kemai.wremja.gui.actions.SettingsAction;
import com.kemai.wremja.gui.settings.IUserSettings;

/**
 * The settings dialog for editing both application and user settings.
 *
 * @author kutzi
 * @author remast
 */
public class SettingsDialog extends EscapeDialog {

    private static final long serialVersionUID = 1L;

    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(SettingsDialog.class);
    
    private static String text(String key) {
        return textBundle.textFor(key);
    }
    
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
    	useTrayicon.setToolTipText(textBundle.textFor("SettingsDialog.Setting.UseTrayIcon.ToolTipText")); //$NON-NLS-1$
    }

    @SuppressWarnings("serial")
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
    
    private JXTextField timeZone = new JXTextField("Time Zone");
    {
    	timeZone.setPreferredSize(new Dimension(250, 20));
    	timeZone.setInputVerifier(new InputVerifier() {
			
    		private TimeZone GMT = TimeZone.getTimeZone("GMT");
    		
			@Override
			public boolean verify(JComponent input) {
				JXTextField jTextField = (JXTextField)input;
				String text = jTextField.getText();
				if (text == null || text.isEmpty()) {
					return true;
				}
				
				TimeZone zone = TimeZone.getTimeZone(text);
				if (!zone.equals(GMT)) {
					return true;
				} else {
					boolean valid = text.equals("GMT");
					if (!valid) {
						saveButton.setEnabled(false);
						jTextField.setForeground(Color.RED);
						BalloonTipStyle style = new RoundedBalloonStyle(5,5,Color.WHITE, Color.RED);
						BalloonTip balloonTip = new BalloonTip(jTextField, "Unknown TimeZone '" + text + "'!", style, false);
						balloonTip.addDefaultMouseListener(true);
					}
					return valid;
				}
			}
		});
    }
    
    private final JCheckBox projectFilter = new JCheckBox(text("SettingsDialog.Setting.ProjectFilter.Title"));
    private final JCheckBox yearFilter = new JCheckBox(text("SettingsDialog.Setting.YearFilter.Title"));
    private final JCheckBox monthFilter = new JCheckBox(text("SettingsDialog.Setting.MonthFilter.Title"));
    private final JCheckBox weekFilter = new JCheckBox(text("SettingsDialog.Setting.WeekFilter.Title"));
    private final JCheckBox dayFilter = new JCheckBox(text("SettingsDialog.Setting.DayFilter.Title"));
    {
        projectFilter.setToolTipText(text("SettingsDialog.Setting.ProjectFilter.ToolTip"));
        yearFilter.setToolTipText(text("SettingsDialog.Setting.YearFilter.ToolTip"));
        monthFilter.setToolTipText(text("SettingsDialog.Setting.MonthFilter.ToolTip"));
        weekFilter.setToolTipText(text("SettingsDialog.Setting.WeekFilter.ToolTip"));
        dayFilter.setToolTipText(text("SettingsDialog.Setting.DayFilter.ToolTip"));
    }
    
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
     * @param settings the settings instance to use
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

        this.setName("SettingsDialog"); //$NON-NLS-1$
        this.setTitle(textBundle.textFor("SettingsDialog.Title")); //$NON-NLS-1$
        this.setLayout(new TableLayout(size));
        this.add(
                new JXHeader(textBundle
                        .textFor("SettingsDialog.ApplicationSettingsTitle"),
                        null, SettingsAction.ICON), "0, 0, 3, 1"); //$NON-NLS-1$

        JTabbedPane tabbedPane = new JTabbedPane();
        this.add(tabbedPane, "1, 3, 3, 3");

        saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (!saveButton.isEnabled()) {
					return;
				}
				
                settings.setRememberWindowSizeLocation(rememberWindowSizeLocation
                                .isSelected());
                settings.setDiscardEmptyActivities(purgeEmptyActivities.isSelected());
                settings.setUseTrayIcon(useTrayicon.isSelected());
                settings.setAllowOverlappingActivities(allowOverlappingActivities.isSelected());
                settings.setDurationFormat(durationFormat.getSelectedItem().toString());
                settings.setTimeZone(timeZone.getText());
                
                settings.setBooleanProperty(PROJECT_FILTER_ENABLED, projectFilter.isSelected());
                settings.setBooleanProperty(YEAR_FILTER_ENABLED, yearFilter.isSelected());
                settings.setBooleanProperty(MONTH_FILTER_ENABLED, monthFilter.isSelected());
                settings.setBooleanProperty(WEEK_OF_YEAR_FILTER_ENABLED, weekFilter.isSelected());
                settings.setBooleanProperty(DAY_OF_WEEK_FILTER_ENABLED, dayFilter.isSelected());
                
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
        generalPanel.setLayout(new TableLayout(size));

        generalPanel.add(rememberWindowSizeLocation, "1, 1, 3, 1"); //$NON-NLS-1$
        generalPanel.add(purgeEmptyActivities, "1, 3, 3, 3"); //$NON-NLS-1$
        generalPanel.add(allowOverlappingActivities, "1, 5, 3, 5"); //$NON-NLS-1$
        generalPanel.add(useTrayicon, "1, 7, 3, 7"); //$NON-NLS-1$
        
        JPanel timezonePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        timezonePanel.add(new JLabel("Time Zone"));
        timezonePanel.add(timeZone);
        generalPanel.add(timezonePanel, "1, 9, 3, 9");
        
        
        
        JPanel durationFormatPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        durationFormatPanel.add(new JLabel(text("SettingsDialog.Setting.DurationFormat.Title")));
        durationFormatPanel.add(durationFormat);
        
        generalPanel.add(durationFormatPanel, "1, 11, 3, 11"); //$NON-NLS-1$

        
        JPanel filterPanel = new JPanel();
        tabbedPane.addTab("Filter", null, filterPanel, "Filter settings");
        size = new double[][] {
                { border, TableLayout.PREFERRED, border,
                    TableLayout.PREFERRED, border
                     }, // Columns
                { border, TableLayout.PREFERRED, border,
                         TableLayout.PREFERRED, border,
                         TableLayout.PREFERRED, border,
                         TableLayout.PREFERRED, border,
                         TableLayout.PREFERRED, border,
                } // Rows
        };
        filterPanel.setLayout(new TableLayout(size));
        filterPanel.add(this.projectFilter, "1, 1, 3, 1");
        filterPanel.add(this.yearFilter, "1, 3, 3, 3");
        filterPanel.add(this.monthFilter, "1, 5, 3, 5");
        filterPanel.add(this.weekFilter, "1, 7, 3, 7");
        filterPanel.add(this.dayFilter, "1, 9, 3, 9");
        
        readFromSettings();
        
        addSaveEnabler();
    }

	private void addSaveEnabler() {
		ActionListener saveEnabler = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveButton.setEnabled(true);
			}
		};
		
		DocumentListener docListener = new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				timeZone.setForeground(Color.BLACK);
				saveButton.setEnabled(true);
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				timeZone.setForeground(Color.BLACK);
				saveButton.setEnabled(true);
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				timeZone.setForeground(Color.BLACK);
				saveButton.setEnabled(true);
			}
		};
        
        rememberWindowSizeLocation.addActionListener(saveEnabler);
        purgeEmptyActivities.addActionListener(saveEnabler);
        allowOverlappingActivities.addActionListener(saveEnabler);
        useTrayicon.addActionListener(saveEnabler);
        durationFormat.addActionListener(saveEnabler);
        timeZone.getDocument().addDocumentListener(docListener);
        
        this.projectFilter.addActionListener(saveEnabler);
        this.yearFilter.addActionListener(saveEnabler);
        this.monthFilter.addActionListener(saveEnabler);
        this.weekFilter.addActionListener(saveEnabler);
        this.dayFilter.addActionListener(saveEnabler);
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
        this.timeZone.setText(this.settings.getTimeZone());
        
        this.projectFilter.setSelected(this.settings.getBooleanProperty(PROJECT_FILTER_ENABLED, true));
        this.yearFilter.setSelected(this.settings.getBooleanProperty(YEAR_FILTER_ENABLED, true));
        this.monthFilter.setSelected(this.settings.getBooleanProperty(MONTH_FILTER_ENABLED, true));
        this.weekFilter.setSelected(this.settings.getBooleanProperty(WEEK_OF_YEAR_FILTER_ENABLED, true));
        this.dayFilter.setSelected(this.settings.getBooleanProperty(DAY_OF_WEEK_FILTER_ENABLED, true));
    }
}
