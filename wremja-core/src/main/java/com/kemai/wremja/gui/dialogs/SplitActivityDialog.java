package com.kemai.wremja.gui.dialogs;

import static com.kemai.wremja.FormatUtils.formatTime;
import static com.kemai.wremja.FormatUtils.parseTime;
import info.clearthought.layout.TableLayout;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.ParseException;

import javax.swing.ComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DateFormatter;
import javax.swing.text.Document;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

import ca.odell.glazedlists.swing.DefaultEventComboBoxModel;

import com.kemai.swing.dialog.EscapeDialog;
import com.kemai.util.DateUtils;
import com.kemai.util.TextResourceBundle;
import com.kemai.wremja.FormatUtils;
import com.kemai.wremja.gui.model.PresentationModel;
import com.kemai.wremja.model.OverlappingActivitiesException;
import com.kemai.wremja.model.Project;
import com.kemai.wremja.model.ProjectActivity;

/**
 * Dialog for splitting an activity into 2 activities.
 * @author kutzi
 */
@SuppressWarnings("serial")
public class SplitActivityDialog extends EscapeDialog {

    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(SplitActivityDialog.class);

    private final JLabel startLabel = new JLabel(textBundle.textFor("Dialogs.StartLabel"));
    
    private final JLabel midLabel = new JLabel(textBundle.textFor("SplitActivityDialog.MidTimeLabel"));

    private final JLabel endLabel = new JLabel(textBundle.textFor("Dialogs.EndLabel"));

    private final JLabel project1Label = new JLabel(textBundle.textFor("SplitActivityDialog.Project", "1"));
    
    private final JLabel project2Label = new JLabel(textBundle.textFor("SplitActivityDialog.Project", "2"));

    private JComboBox<Project> project1Selector;
    
	private JComboBox<Project> project2Selector;

    private JButton submitActivityButton;

    private JFormattedTextField startField;

    private JFormattedTextField midField;
    
    private JFormattedTextField endField;

    private PresentationModel model;

    private boolean submitted = false;

    // ------------------------------------------------
    // Edit components
    // ------------------------------------------------

    private final ProjectActivity oldActivity;

    private DateTime start;
    private DateTime mid;
    private DateTime end;

    public SplitActivityDialog(Frame owner, PresentationModel model, ProjectActivity oldActivity) {
        super(owner);
        this.model = model;
        this.oldActivity = oldActivity;

        initialize();
    }

    /**
     * Set up GUI components.
     */
    private void initialize() {
        setLocationRelativeTo(getOwner());

        this.setIconImage(new ImageIcon(getClass().getResource("/icons/edit-cut.png")).getImage());
        this.setTitle(textBundle.textFor("SplitActivityDialog.DialogTitle"));

        this.setModal(true);

        initializeLayout();

        this.getRootPane().setDefaultButton(submitActivityButton);
    }

    /**
     * This method initializes activityPanel.
     */
    private void initializeLayout() {
        final double border = 7;
        final double size[][] = {
                { border, TableLayout.PREFERRED, border, TableLayout.FILL, border }, // Columns
                { border, TableLayout.PREFERRED,
                	border, TableLayout.PREFERRED,
                	border, TableLayout.PREFERRED,
                    border, TableLayout.PREFERRED,
                	border, border,
                    border, TableLayout.PREFERRED,
                    border, TableLayout.FILL,
                    border, border,
                    border, TableLayout.PREFERRED, border} }; // Rows

        final TableLayout tableLayout = new TableLayout(size);
        this.setLayout(tableLayout);

        this.project1Selector = new JComboBox<Project>(newProjectListComboModel());
        project1Selector.setSelectedItem(oldActivity.getProject());
        this.project1Selector.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getItem() != null && e.getStateChange() == ItemEvent.SELECTED) {
                    submitActivityButton.setEnabled(true);
                }
            }
        });
        this.add(project1Label, "1, 1");
        this.add(this.project1Selector, "3, 1");

        DocumentListener validDateListener = new DocumentListener() {

            @Override
            public void changedUpdate(DocumentEvent e) {
                // noop                    
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                checkIfDocumentContainsValidDate(e.getDocument());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                checkIfDocumentContainsValidDate(e.getDocument());
            }
            
            private void checkIfDocumentContainsValidDate(Document document) {
                try {
                    String text = document.getText(0, document.getLength());
                    if(validateTime(text)) {
                        submitActivityButton.setEnabled(true);
                        return;
                    }
                } catch (BadLocationException e) {
                    // fall through
                }
                submitActivityButton.setEnabled(false);
            }
        };
        
        add(startLabel, "1, 5");
        JFormattedTextField startField = getStartField();
        startField.setText(formatTime(oldActivity.getStart()));
        startField.setEditable(false);
        add(startField, "3, 5");

        this.add(midLabel, "1, 7");
        JFormattedTextField midField = getMidField();
        midField.setText(formatTime(getMiddleTime()));
        midField.getDocument().addDocumentListener(validDateListener);
		this.add(midField, "3, 7");

        project2Selector = new JComboBox<Project>(newProjectListComboModel());
        project2Selector.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getItem() != null && e.getStateChange() == ItemEvent.SELECTED) {
                    submitActivityButton.setEnabled(true);
                }
            }
        });
        this.add(project2Label, "1, 11");
        this.add(project2Selector, "3, 11");
        
        add(endLabel, "1, 13");
        JFormattedTextField endField = getEndField();
        endField.setText(formatTime(oldActivity.getEnd()));
        endField.setEditable(false);
		add(endField, "3, 13");
        
        this.add(getSubmitActivityButton(), "1, 17, 3, 17");
    }

	private DateTime getMiddleTime() {
		long duration = oldActivity.getEnd().getMillis() - oldActivity.getStart().getMillis();
		
		return oldActivity.getStart().plus(duration / 2);
	}

	@SuppressWarnings({ "unchecked" })
	private ComboBoxModel<Project> newProjectListComboModel() {
		return new DefaultEventComboBoxModel<Project>(model.getVisibleProjects());
	}

    private JButton getSubmitActivityButton() {
        if (submitActivityButton == null) {
            submitActivityButton = new JButton();

            submitActivityButton.setText(textBundle.textFor("Dialogs.SaveLabel")); //$NON-NLS-1$
            submitActivityButton.setIcon(new ImageIcon(getClass().getResource("/icons/gtk-save.png"))); //$NON-NLS-1$

            // Confirm with 'Enter' key
            submitActivityButton.setMnemonic(KeyEvent.VK_ENTER);
            
            submitActivityButton.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					// there's a strange bug on Linux/Gnome (of course)
					// that the action listener is not called on the 1st mouse click
					// (button only gets the focus, but ActionListener is not called)
					// Therefore this here
					submitActivity();
				}
			});

            submitActivityButton.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent event) {
                	submitActivity();
                }
            });
        }
        return submitActivityButton;
    }
    
    private void submitActivity() {
    	if (submitted) {
    		return;
    	}
    	
        // Validate
        if (!validateFields()) {
            return;
        }

        ProjectActivity activity1 = new ProjectActivity(
                start, 
                mid, 
                (Project)project1Selector.getSelectedItem(),
                oldActivity.getDescription()
        );
        
        ProjectActivity activity2 = new ProjectActivity(
                mid, 
                end, 
                (Project)project2Selector.getSelectedItem(),
                ""
        );

        try {
//            final ProjectActivity oldActivity = SplitActivityDialog.this.oldActivity;
//
//            final boolean activitiesEqual = activity1.getStart().equals(oldActivity.getStart()) 
//            && activity1.getEnd().equals(oldActivity.getEnd())
//            && activity1.getProject().equals(oldActivity.getProject())
//            && activity1.getDescription().equals(oldActivity.getDescription());
            
            //if (!activitiesEqual) {
            model.replaceActivity(oldActivity, activity1, SplitActivityDialog.this);
            model.addActivity(activity2, SplitActivityDialog.this);
            
            submitted = true;
            SplitActivityDialog.this.dispose();
        } catch(OverlappingActivitiesException e) {
            final String title = textBundle.textFor("Dialogs.OverlapError.Title");
            final String message = textBundle.textFor("Dialogs.OverlapError.Text");

            JOptionPane.showMessageDialog(
                    SplitActivityDialog.this, 
                    message,
                    title, 
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * This method initializes startField.
     * @return javax.swing.JTextField
     */
    private JFormattedTextField getStartField() {
        if (startField == null) {
            final DateFormatter dateFormatter = new DateFormatter(FormatUtils.getTimeFormat());
            startField = new JFormattedTextField(dateFormatter);
            dateFormatter.install(startField);
        }
        return startField;
    }

    private JFormattedTextField getMidField() {
        if (midField == null) {
            final DateFormatter dateFormatter = new DateFormatter(FormatUtils.getTimeFormat());
            midField = new JFormattedTextField(dateFormatter);
            dateFormatter.install(midField);
        }
        return midField;
    }
    
    private JFormattedTextField getEndField() {
        if (endField == null) {
            final DateFormatter dateFormatter = new DateFormatter(FormatUtils.getTimeFormat());
            endField = new JFormattedTextField(dateFormatter);
            dateFormatter.install(endField);
        }
        return endField;
    }

    /**
     * Validates the field to ensure that the entered data is valid.
     * @return
     */
    private boolean validateFields() {
        if (this.project1Selector.getSelectedItem() == null || project2Selector.getSelectedItem() == null) {
            return false;
        }

        try {
            start = parseTime(startField.getText());
            mid = parseTime(midField.getText());
            end = parseTime(endField.getText());
        } catch (ParseException e) {
            // On parse error one of the dates is not valid
            return false;
        }

        correctDates();

        try {
            new ProjectActivity(start, mid, (Project)project1Selector.getSelectedItem());
            new ProjectActivity(mid, end, (Project)project2Selector.getSelectedItem());
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(
                    SplitActivityDialog.this, 
                    i18nText("Dialogs.Error.InvalidStartEnd"),
                    i18nText("Dialogs.Error.Title"), 
                    JOptionPane.ERROR_MESSAGE
            );

            // invalid start and end time
            return false;
        }

        // All tests passed so dialog contains valid data
        return true;
    }

    /**
     * Validates that text contains a valid time value (i.e. 'HH:mm')
     */
    private boolean validateTime(String text) {
        if (StringUtils.isBlank(text)) {
            return false;
        }

        try {
            FormatUtils.parseTime(text);
            return true;
        } catch(ParseException e) {
            return false;
        }
    }
    
    /** 
     * Correct the start and end date so that they are on the same day in year.
     */
    private void correctDates() {
    	DateTime day = oldActivity.getDay();
        start = DateUtils.adjustToSameDay(day, start, false);
        mid = DateUtils.adjustToSameDay(day, mid, true);
        end = DateUtils.adjustToSameDay(day, end, true);
    }
    
    private String i18nText(String key) {
    	return textBundle.textFor(key);
    }
}
