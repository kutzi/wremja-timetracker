package com.kemai.wremja.gui.model.io;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import com.kemai.wremja.gui.events.WremjaEvent;
import com.kemai.wremja.gui.model.PresentationModel;
import com.kemai.wremja.logging.Logger;

/**
 * A daemon to save the model.
 * 
 * This daemon saves the data
 * a) when a change is detected
 * b) periodically at a configurable period to prevent the problem of maybe forgotten change events 
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
    
    private final Semaphore semaphore = new Semaphore(0);

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
    public void run() {
        while(!stopRequested) {
            try {
            	waitForNextSaveableChange();
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
    
    private void waitForNextSaveableChange() throws InterruptedException {
        this.semaphore.tryAcquire(this.saveInterval, this.timeUnit);
        this.semaphore.drainPermits();
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
            this.semaphore.release();
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
