package com.kemai.wremja.gui.actions;

import java.awt.Frame;

import javax.swing.AbstractAction;

import com.kemai.wremja.gui.model.PresentationModel;

/**
 * Abstract base class for all Wremja actions.
 * @author remast
 */
@SuppressWarnings("serial")
public abstract class AbstractWremjaAction extends AbstractAction {

    /** The model. */
    private final PresentationModel model;

    /** The owning frame. */
    private final Frame owner;

    /**
     * Creates a new action for the given model.
     * @param model the model to create action for
     */
    public AbstractWremjaAction(final PresentationModel model) {
        this(null, model);
    }

    /**
     * Create a new action for the given owning frame.
     * @param owner the owning frame
     */
    public AbstractWremjaAction(final Frame owner) {
        this(owner, null);
    }

    /**
     * Create a new action for the given owning frame and model.
     * @param owner the owning frame
     * @param model the model to create action for
     */
    public AbstractWremjaAction(final Frame owner, final PresentationModel model) {
        this.owner = owner;
        this.model = model;
    }

    /**
     * @return the model
     */
    protected PresentationModel getModel() {
        return model;
    }

    /**
     * Getter for the owning frame of this action.
     * @return the owning frame of this action
     */
    protected Frame getOwner() {
        return owner;
    }

    /**
     * Get the mnemonic key which is the first character of the actions name.
     * @return the mnemonic key character or '-' if the action has no name
     */
    public char getMnemonic() {
        if (getValue(NAME) != null) {
            final String name = (String) getValue(NAME);
            try {
                return name.charAt(0);
            } catch (StringIndexOutOfBoundsException e) {
                // Ignore
            }
        }

        return '-';
    }
}
