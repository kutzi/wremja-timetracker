package com.kemai.wremja.gui.model.report;

import java.util.Observable;
import java.util.Observer;

import com.kemai.wremja.gui.events.WremjaEvent;
import com.kemai.wremja.gui.model.PresentationModel;
import com.kemai.wremja.model.ProjectActivity;
import com.kemai.wremja.model.filter.Filter;
import com.kemai.wremja.model.report.AccumulatedActivitiesReport;

public class ObservingAccumulatedActivitiesReport extends AccumulatedActivitiesReport implements Observer {

    /** The model. */
    private final PresentationModel model;

    public ObservingAccumulatedActivitiesReport(final PresentationModel model) {
        super(model.getData());

        this.filter = model.getFilter();
        this.model = model;
        this.model.addObserver(this);

        this.accumulate();
    }

    /**
     * {@inheritDoc}
     */
    public void update(final Observable source, final Object eventObject) {
        if (eventObject == null || !(eventObject instanceof WremjaEvent)) {
            return;
        }

        final WremjaEvent event = (WremjaEvent) eventObject;
        switch (event.getType()) {

        case PROJECT_ACTIVITY_ADDED:
            ProjectActivity activity = (ProjectActivity) event.getData();
            this.acummulateActivity(activity);
            break;

        case PROJECT_ACTIVITY_REMOVED:
            this.accumulate();
            break;

        case PROJECT_ACTIVITY_CHANGED:
            this.accumulate();
            break;
            
        case FILTER_CHANGED:
            final Filter newFilter = (Filter) event.getData();
            setFilter(newFilter);
            break;

        case DATA_CHANGED:
            setData(model.getData());
            break;
        }
        
        setChanged();
        notifyObservers();
    }

}
