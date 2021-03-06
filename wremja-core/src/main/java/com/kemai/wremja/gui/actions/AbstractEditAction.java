package com.kemai.wremja.gui.actions;

import java.awt.Frame;

import com.kemai.wremja.gui.model.edit.EditStack;

/**
 * Base class for edit actions that can be redone and undone.
 * @author remast
 * @author kutzi
 */
@SuppressWarnings("serial")
public abstract class AbstractEditAction extends AbstractWremjaAction {

    /** The stack to manage edit actions. */
    private final EditStack editStack;

    /**
     * Creates an AbstractEditAction for undoing and redoing edit actions.
     * @param editStack the actions to be undone and redone
     */
    public AbstractEditAction(final EditStack editStack) {
    	super((Frame)null);
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
    
    protected abstract void resetTooltip();
}
