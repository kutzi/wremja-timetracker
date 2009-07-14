package com.kemai.wremja.gui.model.io;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.kemai.wremja.gui.events.WremjaEvent;
import com.kemai.wremja.gui.model.PresentationModel;
import com.kemai.wremja.logging.Logger;

import edu.umd.cs.findbugs.annotations.SuppressWarnings;

/**
 * A daemon to save the model.
 * 
 * This daemon saves the data
 * a) when a change is detected
 * b) periodically at configurable periods to prevent the problem of maybe forgotten change events 
 * 
 * @author kutzi
 * @author remast
 */
public class SaveDaemon implements Runnable, Observer {
    
    /** The logger. */
    private static final Logger LOG = Logger.getLogger(SaveDaemon.class);

    /** The model. */
    private final PresentationModel model;
    
    private final long saveInterval;
    private final TimeUnit timeUnit;
    
    private long lastSaveTime;
    private volatile boolean stopRequested = false;
    
    private final BlockingQueue<WremjaEvent> queue = new LinkedBlockingQueue<WremjaEvent>();

    /**
     * Create a time which periodically saves the model.
     * @param model the model
     */
    public SaveDaemon(final PresentationModel model, long saveInterval, TimeUnit timeUnit) {
        this.model = model;
        this.model.addObserver(this);
        this.saveInterval = saveInterval;
        this.timeUnit = timeUnit;
    }

    @Override
    @SuppressWarnings("RV_RETURN_VALUE_IGNORED")
    public void run() {
        while(!stopRequested) {
            try {
            	queue.poll(saveInterval, timeUnit);
                try {
                    // prevent too frequent saves:
                    // TODO: that could be probably handled more sophisticated
                    if(System.currentTimeMillis() - this.lastSaveTime >= 1000) {
                        this.model.save();
                        this.lastSaveTime = System.currentTimeMillis();
                    } else {
                        LOG.debug("Not saved because of to small interval");
                    }
                } catch (Exception e) {
                    LOG.error(e, e);
                }
            } catch (InterruptedException e) {
                // ignore
            }
        }
    }

    @Override
    public void update(Observable o, Object eventObject) {
        if (eventObject == null || !(eventObject instanceof WremjaEvent)) {
            LOG.error("Unexpected event object " + eventObject);
            return;
        }
        final WremjaEvent event = (WremjaEvent) eventObject;
        switch(event.getType()) {
        case DATA_CHANGED :
        case PROJECT_ACTIVITY_ADDED:
        case PROJECT_ACTIVITY_CHANGED:
        case PROJECT_ACTIVITY_REMOVED:
        case PROJECT_ACTIVITY_STARTED:
        case PROJECT_ACTIVITY_STOPPED:
        case PROJECT_ADDED:
        case PROJECT_CHANGED:
        case PROJECT_REMOVED:
        case START_CHANGED:
            try {
	            queue.put(event);
            } catch (InterruptedException e) {
	            // restore interrupted state
            	Thread.currentThread().interrupt();
	            break;
            }
            break;
        case DURATION_CHANGED:
        case FILTER_CHANGED:
            // no save needed
        }
    }
    
    public void requestStop() {
        this.stopRequested = true;
    }
}
