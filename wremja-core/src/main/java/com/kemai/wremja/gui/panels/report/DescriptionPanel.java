package com.kemai.wremja.gui.panels.report;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Map.Entry;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jdesktop.swingx.JXPanel;

import com.kemai.swing.util.GuiConstants;
import com.kemai.wremja.gui.events.WremjaEvent;
import com.kemai.wremja.gui.model.PresentationModel;
import com.kemai.wremja.model.ProjectActivity;
import com.kemai.wremja.model.filter.Filter;

/**
 * Display and edit the descriptions of all project activities.
 * @author remast
 */
@SuppressWarnings("serial")
public class DescriptionPanel extends JXPanel implements Observer {

    /** The model. */
    private final PresentationModel model;

    /** Cache for all entries by activity. */
    private final Map<ProjectActivity, DescriptionPanelEntry> entriesByActivity;

    /** The applied filter. */
    private Filter filter;

    private JPanel container;

    public DescriptionPanel(final PresentationModel model) {
        super();
        this.setLayout(new BorderLayout());
        this.model = model;
        this.entriesByActivity = new HashMap<ProjectActivity, DescriptionPanelEntry>();
        this.model.addObserver(this);
        this.filter = model.getFilter();

        initialize();
    }

    private void initialize() {
        container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

        this.add(new JScrollPane(container), BorderLayout.CENTER);

        applyFilter();
    }

    private void applyFilter() {
        // clear filtered activities
        entriesByActivity.clear();

        // Remove old description panels.
        container.removeAll();

        for (final ProjectActivity activity : this.model.getActivitiesList()) {
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
    }

    /**
     * {@inheritDoc}
     */
    public void update(final Observable source, final Object eventObject) {
        if (eventObject == null || !(eventObject instanceof WremjaEvent)) {
            return;
        }

        final WremjaEvent event = (WremjaEvent) eventObject;
        ProjectActivity activity;

        switch (event.getType()) {

            case WremjaEvent.PROJECT_ACTIVITY_ADDED:
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

            case WremjaEvent.PROJECT_ACTIVITY_CHANGED:
                activity = (ProjectActivity) event.getData();
                if (entriesByActivity.containsKey(activity)) {
                    entriesByActivity.get(activity).update();
                }
                break;

            case WremjaEvent.PROJECT_ACTIVITY_REMOVED:
                activity = (ProjectActivity) event.getData();
                if (entriesByActivity.containsKey(activity)) {
                    final DescriptionPanelEntry entryPanel = entriesByActivity.get(activity);
                    this.container.remove(entryPanel);
                }
                break;

            case WremjaEvent.PROJECT_CHANGED:
                for (Entry<ProjectActivity, DescriptionPanelEntry> entry : entriesByActivity.entrySet()) {
                    entry.getValue().update();
                }
                break;

            case WremjaEvent.FILTER_CHANGED:
                final Filter newFilter = (Filter) event.getData();
                setFilter(newFilter);
                break;
        }
    }

    private void setFilter(final Filter filter) {
        this.filter = filter;
        applyFilter();
    }
}
