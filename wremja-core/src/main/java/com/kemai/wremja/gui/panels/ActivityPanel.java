package com.kemai.wremja.gui.panels;

import info.clearthought.layout.TableLayout;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTitledSeparator;
import org.joda.time.DateTime;
import org.joda.time.Period;

import ca.odell.glazedlists.swing.EventComboBoxModel;

import com.kemai.swing.text.TextEditor;
import com.kemai.swing.util.GuiConstants;
import com.kemai.util.DateUtils;
import com.kemai.util.TextResourceBundle;
import com.kemai.wremja.FormatUtils;
import com.kemai.wremja.gui.MainFrame;
import com.kemai.wremja.gui.actions.StartAction;
import com.kemai.wremja.gui.actions.StopAction;
import com.kemai.wremja.gui.events.WremjaEvent;
import com.kemai.wremja.gui.model.PresentationModel;
import com.kemai.wremja.gui.settings.UserSettings;
import com.kemai.wremja.model.Project;

/**
 * Panel for capturing new activities.
 * @author remast
 */
@SuppressWarnings("serial")
public class ActivityPanel extends JPanel implements Observer {

    private static final Log log = LogFactory.getLog(ActivityPanel.class);
    
    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(MainFrame.class);

    /** Big font for labels. */
    private static final Font FONT_BIG = new Font("Sans Serif", Font.PLAIN, 14);

    /** Color for highlighted time and duration. */
    private static final Color HIGHLIGHT_COLOR = new Color(51, 0, 102);

    /** Big bold font for labels. */
    private static final Font FONT_BIG_BOLD = new Font("Sans Serif", Font.BOLD, 14);

    /** Text for inactive start field. */
    private static final String START_INACTIVE = "--:--";

    /** Text for inactive duration. */
    private static final String DURATION_INACTIVE = "-:-- h";

    /** The model. */
    private final PresentationModel model;

    /** Starts/stops the active project. */
    private JButton startStopButton = null;

    /** The list of projects. The selected project is the currently active project. */
    private JComboBox projectSelector = null;

    /** The description editor. */
    private TextEditor descriptionEditor;

    /** Timer for the time passed since activity was started. */
    private Timer timer;

    /** Displays the duration of the running activity. */
    private JLabel duration;

    /** Displays the start time of the running activity. */
    private JFormattedTextField start;

    /** Format for minutes. */
    private static NumberFormat MINUTE_FORMAT = new DecimalFormat("##00");

    /**
     * Create a new panel for the given model.
     * @param model the model
     */
    public ActivityPanel(final PresentationModel model) {
        this.model = model;
        this.model.addObserver(this);

        // Fire timer event every minute
        this.timer = new Timer(1000 * 60, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateDuration();
            }
            
        });

        initialize();
    }

    /**
     * Set up GUI components.
     */
    private void initialize() {
        // 1. Init start-/stop-Buttons
        if (this.model.isActive()) {
            getStartStopButton().setAction(new StopAction(this.model));
        } else {
            getStartStopButton().setAction(new StartAction(null, this.model));
        }

        // 2. Restore selected project if set.
        if (this.model.getData().getActiveProject() != null) {
            this.getProjectSelector().setSelectedItem(
                    this.model.getData().getActiveProject()
            );
        } else {
            // If not set initially select first project
            if (!this.model.getProjectList().isEmpty()) {
                getProjectSelector().setSelectedItem(
                        this.model.getProjectList().get(0)
                );
            }
        }

        final double border = 5;
        final double size[][] = {
                { border, 0.45, border, 0.55, border }, // Columns
                { border, TableLayout.PREFERRED, border, TableLayout.FILL, border }  // Rows
        };

        this.setLayout(new TableLayout(size));

        descriptionEditor = new TextEditor(true);
        descriptionEditor.setBorder(
                BorderFactory.createLineBorder(GuiConstants.VERY_LIGHT_GREY)
        );
        descriptionEditor.setPreferredSize(new Dimension(200, 100));
        descriptionEditor.setCollapseEditToolbar(false);
        descriptionEditor.addTextObserver(new TextEditor.TextChangeObserver() {

            public void onTextChange() {
                final String description = descriptionEditor.getText();

                // Store in model
                model.setDescription(description);

                // Save description in settings.
                UserSettings.instance().setLastDescription(description);
            }
        });

        descriptionEditor.setText(model.getDescription());
        descriptionEditor.setEditable(model.isActive());

        final JXPanel buttonPanel = new JXPanel();

        final double buttonPanelSize [][] = {
                { border, TableLayout.FILL, border, TableLayout.FILL, border }, // Columns
                { 0, TableLayout.FILL, border, TableLayout.FILL, border, TableLayout.FILL, border * 2 } // Rows
        };

        buttonPanel.setLayout(new TableLayout(buttonPanelSize));

        buttonPanel.add(getStartStopButton(), "1, 1, 3, 1"); //$NON-NLS-1$
        buttonPanel.add(getProjectSelector(), "1, 3, 3, 3"); //$NON-NLS-1$

        start = new JFormattedTextField(FormatUtils.getTimeFormat());
        start.setToolTipText(textBundle.textFor("ActivityPanel.Start.ToolTipText"));
        start.setBorder(BorderFactory.createEmptyBorder());
        start.setFont(FONT_BIG_BOLD);
        start.setForeground(HIGHLIGHT_COLOR);

        // Restore current activity
        if (model.isActive()) {
            start.setEnabled(true);
            start.setValue(this.model.getStart().toDate());
        } else {
            start.setText(START_INACTIVE);
            start.setEnabled(false);
        }

        start.setEditable(true);

        start.addPropertyChangeListener("value", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                changeStartTime();
            }
        });
        
        final int borderSmall = 3;

        final JXPanel startPanel = new JXPanel();
        final double startPanelSize [][] = new double [][] {
                { TableLayout.PREFERRED, borderSmall, TableLayout.FILL }, // Columns
                { TableLayout.FILL } // Rows
        };
        startPanel.setLayout(new TableLayout(startPanelSize));

        final JLabel startLabel = new JLabel(textBundle.textFor("ActivityPanel.StartLabel"));
        startLabel.setFont(FONT_BIG);
        startLabel.setToolTipText(textBundle.textFor("ActivityPanel.Start.ToolTipText"));

        startPanel.add(startLabel, "0, 0"); //$NON-NLS-1$
        startPanel.add(start, "2, 0"); //$NON-NLS-1$

        buttonPanel.add(startPanel, "1, 5"); //$NON-NLS-1$

        duration = new JLabel();
        duration.setFont(FONT_BIG_BOLD);
        duration.setForeground(HIGHLIGHT_COLOR);

        // Restore current activity
        if (model.isActive()) {
            duration.setEnabled(true);
            updateDuration();
        } else {
            duration.setEnabled(false);
            duration.setText(DURATION_INACTIVE);
        }
        duration.setToolTipText(textBundle.textFor("ActivityPanel.Duration.ToolTipText"));

        final JXPanel timerPanel = new JXPanel();
        final double [][] doublePanelSize = new double [][] {
                { TableLayout.PREFERRED, borderSmall, TableLayout.FILL }, // Columns
                { TableLayout.FILL } // Rows
        };
        timerPanel.setLayout(new TableLayout(doublePanelSize));

        final JLabel durationLabel = new JLabel(textBundle.textFor("ActivityPanel.DurationLabel")); //$NON-NLS-1$
        durationLabel.setFont(FONT_BIG);
        durationLabel.setForeground(Color.DARK_GRAY);
        durationLabel.setToolTipText(textBundle.textFor("ActivityPanel.Duration.ToolTipText")); //$NON-NLS-1$

        timerPanel.add(durationLabel, "0, 0"); //$NON-NLS-1$
        timerPanel.add(duration, "2, 0"); //$NON-NLS-1$

        buttonPanel.add(timerPanel, "3, 5"); //$NON-NLS-1$

        this.add(new JXTitledSeparator(textBundle.textFor("ActivityPanel.ActivityLabel")), "1, 1, 3, 1"); //$NON-NLS-1$ $NON-NLS-2$
        this.add(buttonPanel, "1, 3"); //$NON-NLS-1$
        this.add(descriptionEditor, "3, 3"); //$NON-NLS-1$
    }

    /**
     * This method initializes startStopButton.
     * @return javax.swing.JButton
     */
    private JButton getStartStopButton() {
        if (startStopButton == null) {
            startStopButton = new JButton(new StartAction(null, this.model));
        }
        return startStopButton;
    }

    /**
     * This method initializes projectSelector.
     * @return javax.swing.JComboBox
     */
    private JComboBox getProjectSelector() {
        if (projectSelector == null) {
            projectSelector = new JComboBox();
            projectSelector.setToolTipText(textBundle.textFor("ProjectSelector.ToolTipText")); //$NON-NLS-1$
            projectSelector.setModel(new EventComboBoxModel<Project>(this.model.getProjectList()));

            /* Handling of selection events: */
            projectSelector.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    // 1. Set current project to the just selected project.
                    final Project selectedProject = (Project) projectSelector.getSelectedItem();
                    ActivityPanel.this.model.changeProject(selectedProject);

                    // 2. Clear the description.
                    if (descriptionEditor != null) {
                        descriptionEditor.setText("");
                    }
                }
            });
        }
        return projectSelector;
    }

    /**
     * {@inheritDoc}
     */
    public final void update(final Observable source, final Object eventObject) {
        if (eventObject == null || !(eventObject instanceof WremjaEvent)) {
            return;
        }

        final WremjaEvent event = (WremjaEvent) eventObject;

        switch (event.getType()) {

        case PROJECT_ACTIVITY_STARTED:
            this.updateStart();
            break;

        case PROJECT_ACTIVITY_STOPPED:
            this.updateStop();
            break;

        case PROJECT_CHANGED:
            this.updateProjectChanged(event);
            break;

        case PROJECT_ADDED:
            break;

        case PROJECT_REMOVED:
            break;

        case START_CHANGED:
            updateDuration();
            break;
        }
    }

    /**
     * Executed on project changed event.
     * @param event the event of the project change
     */
    private void updateProjectChanged(final WremjaEvent event) {
        getProjectSelector().setSelectedItem((Project) event.getData());

        if (model.isActive()) {
            start.setValue(this.model.getStart().toDate());
            updateDuration();
        }
    }

    /**
     * Executed on start event.
     */
    private void updateStart() {
        timer.start();

        descriptionEditor.setText("");
        descriptionEditor.setEditable(true);

        // Clear description in settings.
        UserSettings.instance().setLastDescription("");

        // Change button from start to stop
        getStartStopButton().setAction(new StopAction(this.model));

        start.setValue(this.model.getStart().toDate());
        start.setEnabled(true);

        updateDuration();
        duration.setEnabled(true);
    }

    /**
     * Executed on stop event.
     */
    private void updateStop() {
        timer.stop();

        descriptionEditor.setText("");
        descriptionEditor.setEditable(false);

        // Clear description in settings.
        UserSettings.instance().setLastDescription("");

        getStartStopButton().setAction(new StartAction(null, this.model));

        // Reset start time
        start.setValue(null);
        start.setText(START_INACTIVE);
        start.setEnabled(false);

        // Reset duration
        duration.setText(DURATION_INACTIVE);
        duration.setEnabled(false);
    }

    /**
     * Updates the GUI with the current duration.
     */
    private void updateDuration() {
        try {
            final Period period = new Period(
                    this.model.getStart(), 
                    DateUtils.getNow()
            );
            final String durationPrint = period.getHours() + ":" + MINUTE_FORMAT.format(period.getMinutes()) + " h";

            // Display duration
            duration.setText(durationPrint);
        } catch (Exception e) {
            log.error(e, e);
        }
    }

    /**
     * Changes the start time to the time entered by the user manually.
     * The start time is validated so that it is before the current time.
     */
    private void changeStartTime() {
        if (StringUtils.isEmpty(start.getText())) {
            return;
        }

        // If new start time is equal to current start time there's nothing to do
        if (StringUtils.equals(start.getText(), FormatUtils.formatTime(model.getStart()))) {
            return;
        }

        // New start time must be before the current time.
        try {
            DateTime newStart = FormatUtils.parseTime(start.getText());
            newStart = DateUtils.adjustToSameDay(
                    DateUtils.getNow(), 
                    new DateTime(newStart), 
                    false
            );

            final boolean correct = DateUtils.isBeforeOrEqual(
                    newStart, 
                    DateUtils.getNow()
            );

            if (correct) {
                model.setStart(newStart);
            } else {
                JOptionPane.showMessageDialog(
                        ActivityPanel.this, 
                        textBundle.textFor("ActivityPanel.StartTimeError.Message"),  //$NON-NLS-1$
                        textBundle.textFor("ActivityPanel.StartTimeError.Title"),  //$NON-NLS-1$
                        JOptionPane.ERROR_MESSAGE
                );

                start.setText(FormatUtils.formatTime(model.getStart()));
            }
        } catch (ParseException e) {
            return;
        }
    }

}
