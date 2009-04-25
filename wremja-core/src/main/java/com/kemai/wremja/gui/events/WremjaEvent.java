package com.kemai.wremja.gui.events;

import java.beans.PropertyChangeEvent;

/**
 * Events of Wremja.
 * @author remast
 */
public class WremjaEvent {

    //------------------------------------------------
    // Constants for ActivityRepository Events
    //------------------------------------------------
    public enum Type {
        /** A project has been changed. I.e. a new project is active now. */
        PROJECT_CHANGED,

        /** A project activity has been started. */
        PROJECT_ACTIVITY_STARTED,

        /** A project activity has been stopped. */
        PROJECT_ACTIVITY_STOPPED,

        /** A project has been added. */
        PROJECT_ADDED,

        /** A project has been removed. */
        PROJECT_REMOVED,

        /** A project activity has been added. */
        PROJECT_ACTIVITY_ADDED,

        /** A project activity has been removed. */
        PROJECT_ACTIVITY_REMOVED,
    
        /** A project activity has been changed. */
        PROJECT_ACTIVITY_CHANGED,

        /** The filter has been changed. */
        FILTER_CHANGED,

        /** The data has changed. */
        DATA_CHANGED,

        /** The start time has changed. */
        START_CHANGED
    }

    /** The type of the event. */
    private final Type type;

    /** The data of the event. */
    private Object data;

    /** A property hint of the event. */
    private PropertyChangeEvent propertyChangeEvent;

    /** The source that fired the event. */
    private Object source;

    /**
     * Constructor for a new event.
     * @param type the type of the event.
     */
    public WremjaEvent(final Type type) {
        this.type = type;
    }

    /**
     * Constructor for a new event.
     * @param type the type of the event.
     * @param source the source that fired the event
     */
    public WremjaEvent(final Type type, final Object source) {
        this.type = type;
        this.source = source;
    }

    /**
     * Checks whether the event can be undone.
     * @return <code>true</code> if undoing the event is possible else <code>false</code>
     */
    public final boolean canBeUndone() {
        // INFO: For now only adding / removing activities can be undone.
        return this.type == Type.PROJECT_ACTIVITY_REMOVED 
        || this.type == Type.PROJECT_ACTIVITY_ADDED;
    }

    /**
     * Getter for the data.
     * @return the data
     */
    public Object getData() {
        return data;
    }

    /**
     * Setter for the data.
     * @param data the data to set
     */
    public void setData(final Object data) {
        this.data = data;
    }

    /**
     * @return the type
     */
    public Type getType() {
        return type;
    }

    /**
     * @return the propertyHint
     */
    public PropertyChangeEvent getPropertyChangeEvent() {
        return propertyChangeEvent;
    }

    /**
     * @param propertyHint the propertyHint to set
     */
    public void setPropertyChangeEvent(final PropertyChangeEvent propertyHint) {
        this.propertyChangeEvent = propertyHint;
    }

    /**
     * @return the source
     */
    public Object getSource() {
        return source;
    }

    /**
     * @param source the source to set
     */
    public void setSource(final Object source) {
        this.source = source;
    }

}
