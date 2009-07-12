package com.kemai.wremja.gui.actions;

import java.awt.Frame;
import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import com.kemai.util.TextResourceBundle;
import com.kemai.wremja.gui.model.PresentationModel;
import com.kemai.wremja.logging.Logger;

/**
 * Action to exit the application.
 * @author remast
 * @author kutzi
 */
@SuppressWarnings("serial") 
public class ExitAction extends AbstractWremjaAction {

    private static final Logger LOG = Logger.getLogger(ExitAction.class);
    
    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(ExitAction.class);

    /**
     * Constructor.
     *
     * @param owner The owner frame.
     * @param model The {@link PresentationModel}
     */
    public ExitAction(final Frame owner, final PresentationModel model) {
        super(owner, model);

        setName(textBundle.textFor("ExitAction.Name")); //$NON-NLS-1$
        setTooltip(textBundle.textFor("ExitAction.ShortDescription")); //$NON-NLS-1$
        putValue(SMALL_ICON, new ImageIcon(getClass().getResource("/icons/gtk-quit.png"))); //$NON-NLS-1$
        putValue(LONG_DESCRIPTION, textBundle.textFor("ExitAction.LongDescription")); //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value="DM_EXIT", justification="That's what this action is good for")
    public final void actionPerformed(final ActionEvent event) {
        boolean quit = true;

        // If activity is running, then double check with user.
        if (getModel().isActive()) {
            final int dialogResult = JOptionPane.showConfirmDialog(
                    getOwner(), 
                    textBundle.textFor("ExitConfirmDialog.Message"),  //$NON-NLS-1$
                    textBundle.textFor("ExitConfirmDialog.Title"),  //$NON-NLS-1$
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.INFORMATION_MESSAGE
            );
            
            switch(dialogResult) {
                case JOptionPane.CANCEL_OPTION: // intentional fall-through
                case JOptionPane.CLOSED_OPTION: quit = false; break;
                case JOptionPane.YES_OPTION :
                    quit = true;
                    getModel().setStopActivityOnShutdown(true);
                    break;
                case JOptionPane.NO_OPTION:
                    quit = true;
                    getModel().setStopActivityOnShutdown(false);
                    break;
                default:
                    LOG.error("Invalid dialogResult " + dialogResult );
                    quit = false;
            }
        } 

        if (quit) {
            LOG.info("Shutting down");
            System.exit(0);
        }
    }

}
