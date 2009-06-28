package com.kemai.wremja.gui.actions;

import java.awt.Frame;
import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import com.kemai.util.TextResourceBundle;
import com.kemai.wremja.gui.model.PresentationModel;
import com.kemai.wremja.gui.model.ProjectActivityStateException;

/**
 * Starts a new project activity.
 * @author remast
 */
@SuppressWarnings("serial") //$NON-NLS-1$
public class StartAction extends AbstractWremjaAction {

    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(StartAction.class);

    public StartAction(final Frame owner, final PresentationModel model) {
        super(owner, model);

        setName(textBundle.textFor("StartAction.Name")); //$NON-NLS-1$
        setTooltip(textBundle.textFor("StartAction.ShortDescription")); //$NON-NLS-1$
        putValue(SMALL_ICON, new ImageIcon(getClass().getResource("/icons/gtk-ok.png"))); //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void actionPerformed(final ActionEvent event) {
        try {
            getModel().start();
        } catch (ProjectActivityStateException exception) {
            JOptionPane.showConfirmDialog(
                    getOwner(),
                    exception.getLocalizedMessage(),
                    textBundle.textFor("StartAction.ErrorDialog.Title"),  //$NON-NLS-1$
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.ERROR_MESSAGE
            );        
        }
    }

}
