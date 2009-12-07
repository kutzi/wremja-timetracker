package com.kemai.wremja.gui.model.report;

import java.util.Observable;
import java.util.Observer;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.SortedList;

import com.kemai.wremja.gui.events.WremjaEvent;
import com.kemai.wremja.gui.model.PresentationModel;
import com.kemai.wremja.model.ProjectActivity;
import com.kemai.wremja.model.filter.Filter;

/**
 * Report for the working hours by period.
 * @author kutzi
 */
public abstract class HoursByPeriodReport<E extends HoursPer> extends Observable implements Observer  {

    /** The model. */
    private final PresentationModel model;

    private final EventList<E> hoursByPeriodList = new SortedList<E>(new BasicEventList<E>());

    private Filter filter;
    
    protected HoursByPeriodReport(final PresentationModel model) {
        this(model, model.getFilter());
    }
    
    protected HoursByPeriodReport(PresentationModel model, Filter filter) {
    	this.model = model;
        this.filter = filter;
        this.model.addObserver(this);

        calculateHours();
    }

	public EventList<E> getHoursByPeriod() {
        return this.hoursByPeriodList;
    }
    
    /**
     * @param filter the filter to set
     */
    protected void setFilter(final Filter filter) {
        this.filter = filter;

        calculateHours();
    }

    private void calculateHours() {
        this.hoursByPeriodList.clear();

        for (ProjectActivity activity : this.model.getUnfilteredActivities()) {
            if (this.filter.matchesCriteria(activity)) {
                addHours(activity);
            }
        }
        
        if( this.model.isActive()
            && this.filter.matchesNow()
            && this.filter.matchesProject(this.model.getSelectedProject())) {
            // add hours of current activity
            addCurrentHours();
        } else {
            // TODO: this could be done more efficiently !?
            for( E hoursByPeriod : this.hoursByPeriodList ) {
                hoursByPeriod.setChanging(false);
            }
        }
    }

    /**
     * Add the already elapsed hours of the current activity to the report.
     */
    protected abstract void addCurrentHours();

    /**
     * Add the hours of the given activity to the report.
     */
    protected abstract void addHours(final ProjectActivity activity);
    
    protected final void addHoursByPeriod( E newHoursByPeriod ) {
        if (this.hoursByPeriodList.contains(newHoursByPeriod)) {
            E oldHoursByPeriod = this.hoursByPeriodList.get(hoursByPeriodList.indexOf(newHoursByPeriod));
            oldHoursByPeriod.addHours(newHoursByPeriod);
        } else {
            this.hoursByPeriodList.add(newHoursByPeriod);
        }
    }
    
    protected final PresentationModel getModel() {
        return this.model;
    }

    protected final boolean filterMatches( ProjectActivity activity ) {
        if (filter != null && !filter.matchesCriteria(activity)) {
            return false;
        }
        return true;
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

            case PROJECT_ACTIVITY_ADDED:
                calculateHours();
                break;

            case PROJECT_ACTIVITY_REMOVED:
                calculateHours();
                break;

            case PROJECT_ACTIVITY_CHANGED:
                calculateHours();
                break;
                
            case FILTER_CHANGED:
                final Filter newFilter = (Filter) event.getData();
                setFilter(newFilter);
                break;
            
            case DURATION_CHANGED: // intentional fall-through
            case START_CHANGED:
                calculateHours();
                break;
        }

        setChanged();
        notifyObservers();
    }

}
