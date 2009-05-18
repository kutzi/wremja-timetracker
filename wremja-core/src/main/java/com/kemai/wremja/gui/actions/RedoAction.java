package com.kemai.wremja.gui.actions;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;

import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

import com.kemai.util.TextResourceBundle;
import com.kemai.wremja.gui.model.edit.EditStack;

/**
 * Redoes the last edit activity using the {@link EditStack}.
 * @author remast
 */
@SuppressWarnings("serial")
public class RedoAction extends AbstractEditAction {

    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(RedoAction.class);

    public RedoAction(final EditStack editStack) {
        super(editStack);

        resetTooltip();
        putValue(NAME, textBundle.textFor("RedoAction.Name")); //$NON-NLS-1$
        putValue(SMALL_ICON, new ImageIcon(getClass().getResource("/icons/edit-redo.png"))); //$NON-NLS-1$
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke('Y', InputEvent.CTRL_MASK));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void actionPerformed(final ActionEvent e) {
        redo();
    }

    @Override
    public void resetTooltip() {
        putValue(SHORT_DESCRIPTION, textBundle.textFor("RedoAction.ShortDescription")); //$NON-NLS-1$
    }
}
