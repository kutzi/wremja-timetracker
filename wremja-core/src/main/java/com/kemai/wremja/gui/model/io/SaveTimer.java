package com.kemai.wremja.gui.model.io;

import java.util.TimerTask;

import com.kemai.wremja.gui.model.PresentationModel;
import com.kemai.wremja.logging.Logger;

/**
 * A timer to periodically save the model.
 * @author remast
 */
public class SaveTimer extends TimerTask {
    
    /** The logger. */
    private static final Logger log = Logger.getLogger(SaveTimer.class);

    /** The model. */
    private final PresentationModel model;

    /**
     * Create a time which periodically saves the model.
     * @param model the model
     */
    public SaveTimer(final PresentationModel model) {
        this.model = model;
    }

    @Override
    public void run() {
        try {
            this.model.save();
        } catch (Exception e) {
            log.error(e, e);
        }
    }

}
