/**
 * 
 */
package com.kemai.wremja.gui.actions;

import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.kemai.util.TextResourceBundle;
import com.kemai.wremja.gui.model.PresentationModel;
import com.kemai.wremja.gui.model.ProjectActivityStateException;

/**
 * Stops the currently running project activity.
 * @author remast
 */
@SuppressWarnings("serial") //$NON-NLS-1$
public class StopAction extends AbstractWremjaAction {

    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(StopAction.class);

    /** The logger. */
    private static final Log log = LogFactory.getLog(StopAction.class);
    
    public StopAction(final PresentationModel model) {
        super(model);
        
        putValue(NAME, textBundle.textFor("StopAction.Name")); //$NON-NLS-1$
        putValue(SHORT_DESCRIPTION, textBundle.textFor("StopAction.ShortDescription")); //$NON-NLS-1$
        putValue(SMALL_ICON, new ImageIcon(getClass().getResource("/icons/gtk-stop.png"))); //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void actionPerformed(final ActionEvent event) {
        try {
            getModel().stop();
        } catch (ProjectActivityStateException e) {
            log.error(e, e);
        }
    }

}
