package com.kemai.wremja.gui.model.report;

import java.util.Observable;
import java.util.Observer;

import com.kemai.wremja.gui.events.WremjaEvent;
import com.kemai.wremja.gui.model.PresentationModel;
import com.kemai.wremja.model.ProjectActivity;
import com.kemai.wremja.model.filter.Filter;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.SortedList;

/**
 * Report for the working hours by period.
 * @author kutzi
 */
public abstract class HoursByPeriodReport<E extends HoursByPeriod> extends Observable implements Observer  {

    /** The model. */
    private final PresentationModel model;

    private final EventList<E> hoursByPeriodList = new SortedList<E>(new BasicEventList<E>());

    private Filter filter;
    
    HoursByPeriodReport(final PresentationModel model) {
        this.model = model;
        this.filter = model.getFilter();
        this.model.addObserver(this);

        calculateHours();
    }
    
    
    public EventList<E> getHoursByPeriod() {
        return this.hoursByPeriodList;
    }
    
    /**
     * @param filter the filter to set
     */
    private void setFilter(final Filter filter) {
        this.filter = filter;

        calculateHours();
    }

    private void calculateHours() {
        this.hoursByPeriodList.clear();

        for (ProjectActivity activity : this.model.getActivitiesList()) {
            addHours(activity);
        }
        
        if( this.model.isActive() && this.filter.matchesNow() ) {
            // add hours of current activity
            addCurrentHours();
        } else {
            // TODO: this could be done more efficiently !?
            for( E hoursByPeriod : this.hoursByPeriodList ) {
                hoursByPeriod.setChanging(false);
            }
        }
    }

    protected abstract void addCurrentHours();

    protected abstract void addHours(final ProjectActivity activity);
    
    protected void addHoursByPeriod( E newHoursByPeriod ) {
        if (this.hoursByPeriodList.contains(newHoursByPeriod)) {
            E oldHoursByPeriod = this.hoursByPeriodList.get(hoursByPeriodList.indexOf(newHoursByPeriod));
            oldHoursByPeriod.addHours(newHoursByPeriod);
        } else {
            this.hoursByPeriodList.add(newHoursByPeriod);
        }
    }
    
    protected PresentationModel getModel() {
        return this.model;
    }

    protected boolean filterMatches( ProjectActivity activity ) {
        if (filter != null && !filter.matchesCriteria(activity)) {
            return false;
        }
        return true;
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
