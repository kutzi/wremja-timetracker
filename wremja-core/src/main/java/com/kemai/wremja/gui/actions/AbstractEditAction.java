package com.kemai.wremja.gui.actions;

import javax.swing.AbstractAction;

import com.kemai.wremja.gui.model.edit.EditStack;

/**
 * Base class for edit actions that can be redone and undone.
 * @author remast
 */
@SuppressWarnings("serial")
public abstract class AbstractEditAction extends AbstractAction {

    /** The stack to manage edit actions. */
    private final EditStack editStack;

    /**
     * Creates an AbstractEditAction for undoing and redoing edit actions.
     * @param editStack the actions to be undone and redone
     */
    public AbstractEditAction(final EditStack editStack) {
        this.editStack = editStack;
    }

    /**
     * Undo the last edit action.
     */
    protected final void undo() {
        this.editStack.undo();
    }
    
    /**
     * Redo the last edit action.
     */
    protected final void redo() {
        this.editStack.redo();
    }
    
    public void setText(final String name) {
        putValue(NAME, name);
        putValue(SHORT_DESCRIPTION, name);
    }
    
    protected abstract void resetText();
}
