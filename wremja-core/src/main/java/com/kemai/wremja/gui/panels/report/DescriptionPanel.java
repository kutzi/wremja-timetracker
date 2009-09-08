package com.kemai.wremja.gui.panels.report;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Map.Entry;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jdesktop.swingx.JXPanel;

import ca.odell.glazedlists.SortedList;

import com.kemai.wremja.gui.GuiConstants;
import com.kemai.wremja.gui.events.WremjaEvent;
import com.kemai.wremja.gui.model.PresentationModel;
import com.kemai.wremja.logging.Logger;
import com.kemai.wremja.model.ProjectActivity;

/**
 * Display and edit the descriptions of all project activities.
 * @author remast
 */
@SuppressWarnings("serial")
public class DescriptionPanel extends JXPanel implements Observer {

    private static final Logger LOGGER = Logger.getLogger(DescriptionPanel.class);
    
    /** The model. */
    private final PresentationModel model;

    /** Cache for all entries by activity. */
    private final Map<ProjectActivity, DescriptionPanelEntry> entriesByActivity;

    private JPanel container;

    public DescriptionPanel(final PresentationModel model) {
        super();
        this.setLayout(new BorderLayout());
        this.model = model;
        this.entriesByActivity = new HashMap<ProjectActivity, DescriptionPanelEntry>();
        this.model.addObserver(this);
        initialize();
    }

    private void initialize() {
        container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(container);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        this.add(scrollPane, BorderLayout.CENTER);

        initializeDescriptionEntries();
    }

    private void initializeDescriptionEntries() {
        // clear filtered activities
        entriesByActivity.clear();

        // Remove old description panels.
        container.removeAll();

        SortedList<ProjectActivity> activitiesList = this.model.getActivitiesList();
        for (final ProjectActivity activity : activitiesList) {
            final DescriptionPanelEntry descriptionPanelEntry = new DescriptionPanelEntry(activity, this.model);

            // Alternate background color
            if (this.model.getActivitiesList().indexOf(activity) % 2 == 0) {
                descriptionPanelEntry.setBackground(Color.WHITE);
            } else {
                descriptionPanelEntry.setBackground(GuiConstants.BEIGE);
            }

            // Save entry
            entriesByActivity.put(activity, descriptionPanelEntry);

            // Display entry
            container.add(descriptionPanelEntry);
        }

        // prevent display glitches, if all entries were removed:
        if (activitiesList.isEmpty()) {
            updateUI();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void update(final Observable source, final Object eventObject) {
        if (!(eventObject instanceof WremjaEvent)) {
            LOGGER.warn("Expected WremjaEvent but got: " +
                    (eventObject != null ? eventObject.getClass() : "null"));
                    
            return;
        }

        final WremjaEvent event = (WremjaEvent) eventObject;
        ProjectActivity activity;
        
        switch (event.getType()) {

            case PROJECT_ACTIVITY_ADDED:
                activity = (ProjectActivity) event.getData();
                DescriptionPanelEntry newEntryPanel = new DescriptionPanelEntry(activity, this.model);
                entriesByActivity.put(activity, newEntryPanel);
                this.container.add(newEntryPanel);

                // Set color
                if (Math.abs(entriesByActivity.size()) % 2 == 1) {
                    newEntryPanel.setBackground(Color.WHITE);
                } else {
                    newEntryPanel.setBackground(GuiConstants.BEIGE);
                }
                break;

            case PROJECT_ACTIVITY_CHANGED:
                activity = (ProjectActivity) event.getData();
                if (entriesByActivity.containsKey(activity)) {
                    entriesByActivity.get(activity).update();
                }
                break;

            case PROJECT_ACTIVITY_REMOVED:
                @SuppressWarnings("unchecked")
                Collection<ProjectActivity> activities = (Collection<ProjectActivity>)event.getDataCollection();
                for(ProjectActivity activity2 : activities) {
                    if (entriesByActivity.containsKey(activity2)) {
                        final DescriptionPanelEntry entryPanel = entriesByActivity.get(activity2);
                        this.container.remove(entryPanel);
                    }
                }
                break;

            case PROJECT_CHANGED:
                for (Entry<ProjectActivity, DescriptionPanelEntry> entry : entriesByActivity.entrySet()) {
                    entry.getValue().update();
                }
                break;

            case FILTER_CHANGED:
                initializeDescriptionEntries();
                break;
        }
    }
}
