package com.kemai.wremja.gui.panels;

import info.clearthought.layout.TableLayout;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import org.apache.commons.lang.StringUtils;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTitledSeparator;
import org.joda.time.DateTime;
import org.joda.time.Period;

import ca.odell.glazedlists.swing.DefaultEventComboBoxModel;

import com.kemai.swing.text.TextEditor;
import com.kemai.util.DateUtils;
import com.kemai.util.TextResourceBundle;
import com.kemai.wremja.FormatUtils;
import com.kemai.wremja.gui.GuiConstants;
import com.kemai.wremja.gui.MainFrame;
import com.kemai.wremja.gui.actions.StartAction;
import com.kemai.wremja.gui.actions.StopAction;
import com.kemai.wremja.gui.events.WremjaEvent;
import com.kemai.wremja.gui.model.PresentationModel;
import com.kemai.wremja.gui.settings.IUserSettings;
import com.kemai.wremja.logging.Logger;
import com.kemai.wremja.model.Project;
import com.kemai.wremja.model.ProjectActivity;

/**
 * Panel for capturing new activities.
 *
 * @author remast
 * @author kutzi
 */
@SuppressWarnings("serial")
public class ActivityPanel extends JPanel implements Observer {

    private static final Logger LOG = Logger.getLogger(ActivityPanel.class);
    
    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(MainFrame.class);

    /** Big font for labels. */
    private static final Font FONT_BIG = new Font("Sans Serif", Font.PLAIN, 14);

    /** Big bold font for labels. */
    private static final Font FONT_BIG_BOLD = FONT_BIG.deriveFont(Font.BOLD);
    
    /** Color for highlighted time and duration. */
    private static final Color HIGHLIGHT_COLOR = new Color(51, 0, 102);

    /** Text for inactive start field. */
    private static final String START_INACTIVE = "--:--";

    /** Text for inactive duration. */
    private static final String DURATION_INACTIVE = "-:-- h";
    
    /** Format for minutes.
     * 
     * Note: access needs not to be synchronized as this should only be used from within the EDT.
     */
    private static final NumberFormat MINUTE_FORMAT = new DecimalFormat("##00");

    /** The model. */
    private final PresentationModel model;
    
    private final IUserSettings settings;

    /** Starts/stops the active project. */
    private final JButton startStopButton;

    /** The list of projects. The selected project is the currently active project. */
    private JComboBox<Project> projectSelector;

    /** The description editor. */
    private TextEditor descriptionEditor;

    /** Displays the duration of the running activity. */
    private JLabel duration;

    /** Displays the start time of the running activity. */
    private JFormattedTextField start;

    /**
     * Create a new panel for the given model.
     * @param model the model
     */
    public ActivityPanel(final PresentationModel model, IUserSettings settings) {
        this.model = model;
        this.model.addObserver(this);
        this.settings = settings;
        
        startStopButton = new JButton(new StartAction(null, this.model));

        initialize();
    }

    /**
     * Set up GUI components.
     */
    private void initialize() {
        // 1. Init start-/stop-Buttons
        if (this.model.isActive()) {
            startStopButton.setAction(new StopAction(this.model));
        } else {
            startStopButton.setAction(new StartAction(null, this.model));
        }

        int preferredElementHeight = startStopButton.getPreferredSize().height;
        createProjectSelector(preferredElementHeight);
        // 2. Restore selected project if set.
        if (this.model.getData().getActiveProject() != null) {
            this.projectSelector.setSelectedItem(
                    this.model.getData().getActiveProject()
            );
        } else {
            // If not set initially select first project
            if (!this.model.getProjectList().isEmpty()) {
                this.projectSelector.setSelectedItem(
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

        descriptionEditor = new TextEditor(true, false);
        descriptionEditor.setBorder(
                BorderFactory.createLineBorder(GuiConstants.VERY_LIGHT_GREY)
        );
        descriptionEditor.setPreferredSize(new Dimension(200, 100));
        descriptionEditor.addTextObserver(new TextEditor.TextChangeObserver() {

            public void onTextChange() {
                final String description = descriptionEditor.getText();

                // Store in model
                model.setDescription(description);

                // Save description in settings.
                settings.setLastDescription(description);
            }
        });

        descriptionEditor.setText(model.getDescription());
        descriptionEditor.setEditable(model.isActive());

        final JXPanel buttonPanel = new JXPanel();

        final double buttonPanelSize [][] = {
                { border, TableLayout.FILL, border, TableLayout.FILL, border }, // Columns
                { 0, TableLayout.PREFERRED, border, TableLayout.PREFERRED, border,
                    TableLayout.PREFERRED,
                    TableLayout.FILL, border * 2 } // Rows
        };

        buttonPanel.setLayout(new TableLayout(buttonPanelSize));

        buttonPanel.add(startStopButton, "1, 1, 3, 1"); //$NON-NLS-1$
        buttonPanel.add(this.projectSelector, "1, 3, 3, 3"); //$NON-NLS-1$

        start = new JFormattedTextField(FormatUtils.getTimeFormat()) {

			@Override
			protected void invalidEdit() {
				super.invalidEdit();
				setForeground(Color.RED);
			}
        	
        };
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
        startPanel.setPreferredSize(new Dimension(
                startPanel.getPreferredSize().width,
                preferredElementHeight));
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
        timerPanel.setPreferredSize(new Dimension(
                timerPanel.getPreferredSize().width,
                preferredElementHeight));

        buttonPanel.add(timerPanel, "3, 5"); //$NON-NLS-1$
        
        // add a dummy panel to take up additional height, when JSplitPane is resized
        buttonPanel.add(new JPanel(), "1, 6, 3, 6"); //$NON-NLS-1$

        this.add(new JXTitledSeparator(textBundle.textFor("ActivityPanel.ActivityLabel")), "1, 1, 3, 1"); //$NON-NLS-1$ $NON-NLS-2$
        this.add(buttonPanel, "1, 3"); //$NON-NLS-1$
        this.add(descriptionEditor, "3, 3"); //$NON-NLS-1$

        addKeyAccelerator();
    }

    /**
     * Assigns CTRL+ENTER as a keyboard accelerator for the start/stop button.
     */
	private void addKeyAccelerator() {
		KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, KeyEvent.CTRL_MASK);
        Action action = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				startStopButton.getAction().actionPerformed(e);
			}
        };
        
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(stroke, "doStartOrStop");
        getActionMap().put("doStartOrStop", action);
	}

    /**
     * Initializes the projectSelector.
     */
    private void createProjectSelector(int preferredHeight) {
        projectSelector = new JComboBox<>();
        projectSelector.setToolTipText(textBundle.textFor("ProjectSelector.ToolTipText")); //$NON-NLS-1$
        projectSelector.setModel(getVisibleProjectsModel());

        /* Handling of selection events: */
        projectSelector.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                // 1. Set current project to the just selected project.
                final Project selectedProject = (Project) projectSelector.getSelectedItem();
                ActivityPanel.this.model.startNewProject(selectedProject);

                // 2. Clear the description.
                if (descriptionEditor != null) {
                    descriptionEditor.setText("");
                }
            }
        });
        Dimension preferredSize = projectSelector.getPreferredSize();
        preferredSize.setSize(preferredSize.width, preferredHeight);
        projectSelector.setPreferredSize(preferredSize);
//        final ListCellRenderer<? super Project> renderer = projectSelector.getRenderer();
//        projectSelector.setRenderer(new ListCellRenderer<Project>() {
//            @Override
//            public Component getListCellRendererComponent(JList<? extends Project> list, Project value,
//                    int index, boolean isSelected, boolean cellHasFocus) {
//                Component comp = renderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
//                if (comp instanceof JLabel) {
//                    JLabel label = (JLabel) comp;
//                    label.setText(label.getText());
//                }
//                return comp;
//            }
//        });
    }

	@SuppressWarnings("unchecked")
	private ComboBoxModel<Project> getVisibleProjectsModel() {
		return new DefaultEventComboBoxModel<Project>(this.model.getVisibleProjects());
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
	
	        case DURATION_CHANGED:
	            updateDuration();
	            break;
	            
			case DATA_CHANGED:
				break;
			case FILTER_CHANGED:
				break;
			case PROJECT_ACTIVITY_ADDED:
				break;
			case PROJECT_ACTIVITY_CHANGED:
				break;
			case PROJECT_ACTIVITY_REMOVED:
				break;
        }
    }

    /**
     * Executed on project changed event.
     * @param event the event of the project change
     */
    private void updateProjectChanged(final WremjaEvent event) {
        this.projectSelector.setSelectedItem(event.getData());

        if (model.isActive()) {
            start.setValue(this.model.getStart().toDate());
            updateDuration();
        }
    }

    /**
     * Executed on start event.
     */
    private void updateStart() {
        descriptionEditor.setText("");
        descriptionEditor.setEditable(true);

        // Clear description in settings.
        this.settings.setLastDescription("");

        // Change button from start to stop
        startStopButton.setAction(new StopAction(this.model));

        start.setValue(this.model.getStart().toDate());
        start.setEnabled(true);

        updateDuration();
        duration.setEnabled(true);
    }

    /**
     * Executed on stop event.
     */
    private void updateStop() {
        descriptionEditor.setText("");
        descriptionEditor.setEditable(false);

        // Clear description in settings.
        this.settings.setLastDescription("");

        startStopButton.setAction(new StartAction(null, this.model));

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
            LOG.error(e, e);
        }
    }

    /**
     * Changes the start time to the time entered by the user manually.
     * The start time is validated so that it is before the current time.
     */
    private void changeStartTime() {
    	start.setForeground(HIGHLIGHT_COLOR);
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
            	// check for overlap
        		ProjectActivity tmp = new ProjectActivity(newStart, DateUtils.getNow(), null);
        		List<ProjectActivity> overlappingActivities = model.getOverlappingActivities(tmp, null);
				if(!overlappingActivities.isEmpty()) {
					if (overlappingActivities.size() == 1) {
						
						// TODO: only do this, if new end time > start time of overlapping activity,
						// so we don't create empty activities this way!
	        			Object[] options = { "Yes", "No" };
	        			int choice = JOptionPane.showOptionDialog(
	        					ActivityPanel.this,
	        					"Overlaps with an existing activity. Adjust end time of previous activity?",
	        					"Overlap",
	        					JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE,
	        					null, options, options[0]);
	        			
	        			if (choice == 0) {
	        				overlappingActivities.get(0).setEnd(newStart);
	        			} else {
	        				start.setText(FormatUtils.formatTime(model.getStart()));
	        				return;
	        			}
	        		} else {
	        			JOptionPane.showMessageDialog(
	                             ActivityPanel.this, 
	                             "Overlaps with existing activities!",
	                             "Overlap",
	                             JOptionPane.ERROR_MESSAGE
	                     );
	         			start.setText(FormatUtils.formatTime(model.getStart()));
	         			return;
	        		}
				}
        		
                model.setStart(newStart);
                start.setText(FormatUtils.formatTime(model.getStart()));
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
        	// shouldn't come here, as any parse errors should already be caught by JFormattedTextField's parser
        	start.setForeground(Color.RED);
            return;
        }
    }

}
