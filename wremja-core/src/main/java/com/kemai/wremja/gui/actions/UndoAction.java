package com.kemai.wremja.gui.actions;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;

import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

import com.kemai.util.TextResourceBundle;
import com.kemai.wremja.gui.model.edit.EditStack;

/**
 * Undoes the last edit activity using the {@link EditStack}.
 * @author remast
 */
@SuppressWarnings("serial") 
 public class UndoAction extends AbstractEditAction {

     /** The bundle for internationalized texts. */
     private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(UndoAction.class);

     public UndoAction(final EditStack editStack) {
         super(editStack);

         putValue(NAME, textBundle.textFor("UndoAction.Name"));
         putValue(SHORT_DESCRIPTION, textBundle.textFor("UndoAction.ShortDescription")); //$NON-NLS-1$
         putValue(SMALL_ICON, new ImageIcon(getClass().getResource("/icons/edit-undo.png"))); //$NON-NLS-1$
         putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke('Z', InputEvent.CTRL_MASK));
     }

     /**
      * {@inheritDoc}
      */
     @Override
     public final void actionPerformed(final ActionEvent e) {
         undo();
     }

     @Override
     public void resetTooltip() {
         putValue(SHORT_DESCRIPTION, textBundle.textFor("UndoAction.ShortDescription")); //$NON-NLS-1$
     }
 }
