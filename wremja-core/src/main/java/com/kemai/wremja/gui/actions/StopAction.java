/**
 * 
 */
package com.kemai.wremja.gui.actions;

import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;

import com.kemai.util.TextResourceBundle;
import com.kemai.wremja.gui.model.PresentationModel;
import com.kemai.wremja.gui.model.ProjectActivityStateException;
import com.kemai.wremja.logging.Logger;

/**
 * Stops the currently running project activity.
 * @author remast
 */
@SuppressWarnings("serial") //$NON-NLS-1$
public class StopAction extends AbstractWremjaAction {

    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(StopAction.class);

    /** The logger. */
    private static final Logger log = Logger.getLogger(StopAction.class);
    
    public StopAction(final PresentationModel model) {
        super(model);
        
        setName(textBundle.textFor("StopAction.Name")); //$NON-NLS-1$
        setTooltip(textBundle.textFor("StopAction.ShortDescription")); //$NON-NLS-1$
        putValue(SMALL_ICON, new ImageIcon(getClass().getResource("/icons/gtk-stop.png"))); //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void actionPerformed(final ActionEvent event) {
    	System.out.println("Stop performed");
        try {
            getModel().stop();
        } catch (ProjectActivityStateException e) {
            log.error(e, e);
        }
    }

}
