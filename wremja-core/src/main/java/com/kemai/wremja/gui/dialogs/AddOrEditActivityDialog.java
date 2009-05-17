package com.kemai.wremja.gui.dialogs;

import info.clearthought.layout.TableLayout;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.text.ParseException;

import javax.swing.BorderFactory;
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

import ca.odell.glazedlists.swing.EventComboBoxModel;

import com.kemai.swing.dialog.EscapeDialog;
import com.kemai.swing.text.TextEditor;
import com.kemai.swing.util.JDateTimePicker;
import com.kemai.util.CollectionUtils;
import com.kemai.util.DateUtils;
import com.kemai.util.TextResourceBundle;
import com.kemai.wremja.FormatUtils;
import com.kemai.wremja.gui.GuiConstants;
import com.kemai.wremja.gui.model.PresentationModel;
import com.kemai.wremja.model.Project;
import com.kemai.wremja.model.ProjectActivity;

/**
 * Dialog for manually adding a project activity.
 * @author remast
 * @author kutzi
 */
@SuppressWarnings("serial")
public class AddOrEditActivityDialog extends EscapeDialog {

    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(AddOrEditActivityDialog.class);

    // ------------------------------------------------
    // Labels
    // ------------------------------------------------

    /** Label for activity start time. */
    private final JLabel startLabel = new JLabel(textBundle.textFor("AddOrEditActivityDialog.StartLabel")); //$NON-NLS-1$

    /** Label for activity end time. */
    private final JLabel endLabel = new JLabel(textBundle.textFor("AddOrEditActivityDialog.EndLabel")); //$NON-NLS-1$;

    /** Label for description. */
    private final JLabel descriptionLabel = new JLabel(textBundle.textFor("AddOrEditActivityDialog.DescriptionLabel")); //$NON-NLS-1$

    /** Label for project. */
    private final JLabel projectLabel = new JLabel(textBundle.textFor("AddOrEditActivityDialog.ProjectLabel")); //$NON-NLS-1$

    /** Label for date. */
    private final JLabel dateLabel = new JLabel(textBundle.textFor("AddOrEditActivityDialog.DateLabel")); //$NON-NLS-1$

    // ------------------------------------------------
    // Edit components
    // ------------------------------------------------

    /** Selects the project of the activity. */
    private JComboBox projectSelector = null;

    /** Button to submit the edited activity. */
    private JButton submitActivityButton = null;

    /** Start of the project activity. */
    private JFormattedTextField startField = null;

    /** End of the project activity. */
    private JFormattedTextField endField = null;

    /** The model. */
    private PresentationModel model;

    /** Selects the date of the activity. */
    private final JDateTimePicker datePicker = new JDateTimePicker(new DateTime());;

    /** The description of the activity. */
    private final TextEditor descriptionEditor = new TextEditor(true, false);
    {
        descriptionEditor.setBorder(BorderFactory.createLineBorder(GuiConstants.VERY_LIGHT_GREY));
    }

    // ------------------------------------------------
    // Edit components
    // ------------------------------------------------

    /** Activity before being edited. */
    private ProjectActivity oldActivity = null;

    // ------------------------------------------------
    // Fields for project activity
    // ------------------------------------------------

    /** The project of the activity. */
    private Project project;

    /** The start time of the activity. */
    private DateTime start;

    /** The end time of the activity. */
    private DateTime end;

    /** The day of the activity. */
    private DateTime day;

    /**
     * Create a new dialog.
     * @param owner
     * @param model
     */
    public AddOrEditActivityDialog(final Frame owner, final PresentationModel model) {
        this(owner, model, null);
    }

    public AddOrEditActivityDialog(final Frame owner, final PresentationModel model, final ProjectActivity oldActivity) {
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

        if (oldActivity == null) {
            this.setIconImage(new ImageIcon(getClass().getResource("/icons/gtk-add.png")).getImage()); //$NON-NLS-1$
            this.setTitle(textBundle.textFor("AddOrEditActivityDialog.AddActivityLabel")); //$NON-NLS-1$
        } else {
            this.setIconImage(new ImageIcon(getClass().getResource("/icons/gtk-edit.png")).getImage()); //$NON-NLS-1$
            this.setTitle(textBundle.textFor("AddOrEditActivityDialog.EditActivityLabel")); //$NON-NLS-1$
        }

        this.setModal(true);

        initializeLayout();

        this.getRootPane().setDefaultButton(submitActivityButton);

        readFromModel();
    }

    private void readFromModel() {
        if (oldActivity == null) {
            // Initialize selected project
            // a) If no project selected take first project
            if (model.getSelectedProject() == null) {
                // Select first entry
                if (!CollectionUtils.isEmpty(model.getProjectList())) {
                    final Project project = model.getProjectList().get(0);
                    projectSelector.setSelectedItem(project);
                } else {
                    this.submitActivityButton.setEnabled(false);
                }
            } else {
                // b) Take selected project
                projectSelector.setSelectedItem(model.getSelectedProject());
            }

            // Initialize start and end time with current time
            final String now = FormatUtils.formatTime(new DateTime());
            this.startField.setText(now);
            this.endField.setText(now);
        } else {
            projectSelector.setSelectedItem(oldActivity.getProject());
            this.day = oldActivity.getStart();
            this.datePicker.setDateTime(day);
            this.startField.setText(FormatUtils.formatTime(oldActivity.getStart()));
            this.endField.setText(FormatUtils.formatTime(oldActivity.getEnd()));
            this.descriptionEditor.setText(oldActivity.getDescription());
        }

    }

    /**
     * This method initializes activityPanel.
     */
    private void initializeLayout() {
        final double border = 5;
        final double size[][] = {
                { border, TableLayout.PREFERRED, border, TableLayout.FILL, border }, // Columns
                { border, TableLayout.PREFERRED, border, TableLayout.PREFERRED, border, TableLayout.PREFERRED,
                    border, TableLayout.PREFERRED, border, TableLayout.FILL, border, TableLayout.PREFERRED, border} }; // Rows

        final TableLayout tableLayout = new TableLayout(size);
        this.setLayout(tableLayout);

        this.projectSelector = new JComboBox(new EventComboBoxModel<Project>(model.getProjectList()));
        this.projectSelector.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getItem() != null && e.getStateChange() == ItemEvent.SELECTED) {
                    submitActivityButton.setEnabled(true);
                }
            }
        });
        this.add(projectLabel, "1, 1");
        this.add(this.projectSelector, "3, 1");

        this.add(dateLabel, "1, 3");
        this.add(this.datePicker, "3, 3");

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
        
        this.add(startLabel, "1, 5");
        this.add(getStartField(validDateListener), "3, 5");

        this.add(endLabel, "1, 7");
        this.add(getEndField(validDateListener), "3, 7");

        this.add(descriptionLabel, "1, 9");
        this.add(descriptionEditor, "3, 9");

        this.add(getSubmitActivityButton(), "1, 11, 3, 11");
    }

    /**
     * This method initializes addActivityButton.
     * @return javax.swing.JButton
     */
    private JButton getSubmitActivityButton() {
        if (submitActivityButton == null) {
            submitActivityButton = new JButton();

            if (oldActivity == null) {
                submitActivityButton.setText(textBundle.textFor("AddOrEditActivityDialog.AddLabel")); //$NON-NLS-1$
                submitActivityButton.setIcon(new ImageIcon(getClass().getResource("/icons/gtk-add.png"))); //$NON-NLS-1$
            } else {
                submitActivityButton.setText(textBundle.textFor("AddOrEditActivityDialog.SaveLabel")); //$NON-NLS-1$
                submitActivityButton.setIcon(new ImageIcon(getClass().getResource("/icons/gtk-save.png"))); //$NON-NLS-1$
            }

            // Confirm with 'Enter' key
            submitActivityButton.setMnemonic(KeyEvent.VK_ENTER);

            submitActivityButton.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent event) {
                    // Validate
                    if (!AddOrEditActivityDialog.this.validateFields()) {
                        return;
                    }

                    final ProjectActivity activity = new ProjectActivity(
                            start, 
                            end, 
                            project,
                            descriptionEditor.getText()
                    );

                    // Check if we're in edit or add mode
                    if (oldActivity == null) {
                        model.addActivity(activity, this);
                    } else {
                        final ProjectActivity oldActivity = AddOrEditActivityDialog.this.oldActivity;

                        final boolean activitiesEqual = activity.getStart().equals(oldActivity.getStart()) 
                        && activity.getEnd().equals(oldActivity.getEnd())
                        && activity.getProject().equals(oldActivity.getProject())
                        && activity.getDescription().equals(oldActivity.getDescription());
                        
                        if (!activitiesEqual) {
                            // Delete old activity and add new one (easier than changing everything)
                            model.removeActivity(oldActivity, AddOrEditActivityDialog.this);
                            model.addActivity(activity, AddOrEditActivityDialog.this);
                        }
                    }

                    AddOrEditActivityDialog.this.dispose();
                }
            });

            submitActivityButton.setDefaultCapable(true);
        }
        return submitActivityButton;
    }

    /**
     * This method initializes startField.
     * @return javax.swing.JTextField
     */
    private JFormattedTextField getStartField(DocumentListener docListener) {
        if (startField == null) {
            final DateFormatter dateFormatter = new DateFormatter(FormatUtils.getTimeFormat());
            startField = new JFormattedTextField(dateFormatter);
            dateFormatter.install(startField);
            startField.getDocument().addDocumentListener(docListener);
        }
        return startField;
    }

    /**
     * This method initializes endField.
     * @return javax.swing.JFormattedTextField
     */
    private JFormattedTextField getEndField(DocumentListener docListener) {
        if (endField == null) {
            final DateFormatter dateFormatter = new DateFormatter(FormatUtils.getTimeFormat());
            endField = new JFormattedTextField(dateFormatter);
            dateFormatter.install(endField);
            endField.getDocument().addDocumentListener(docListener);
        }
        return endField;
    }

    /**
     * Validates the field to ensure that the entered data is valid.
     * @return
     */
    private boolean validateFields() {
        if (this.projectSelector.getSelectedItem() == null) {
            return false;
        }

        validateTime(this.startField.getText());
        validateTime(this.endField.getText());

        try {
            // TODO: this parsing is redundant
            start = FormatUtils.parseTime(this.startField.getText());
            end = FormatUtils.parseTime(this.endField.getText());
        } catch (ParseException e) {
            // On parse error one of the dates is not valid
            return false;
        }

        day = this.datePicker.getDateTime();
        correctDates();
        project = (Project) this.projectSelector.getSelectedItem();

        try {
            new ProjectActivity(start, end, project);
        } catch (IllegalArgumentException e) {
            final String title = textBundle.textFor("AddOrEditActivityDialog.Error.Title");
            final String message = textBundle.textFor("AddOrEditActivityDialog.Error.InvalidStartEnd");

            JOptionPane.showMessageDialog(
                    AddOrEditActivityDialog.this, 
                    message,
                    title, 
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
        start = DateUtils.adjustToSameDay(day, start, false);
        end = DateUtils.adjustToSameDay(day, end, true);
    }

}
