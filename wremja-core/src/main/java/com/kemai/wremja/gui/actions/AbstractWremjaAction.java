package com.kemai.wremja.gui.actions;

import java.awt.Frame;

import javax.swing.AbstractAction;

import com.kemai.wremja.gui.model.PresentationModel;

/**
 * Abstract base class for all Wremja actions.
 * @author remast
 * @author kutzi
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

    
    @Override
    public void putValue(String key, Object newValue) {
        super.putValue(key, newValue);
        
        if( NAME.equals(key) ) {
            // set up mnemonic key if there is no other, yet
            if( getValue(MNEMONIC_KEY) == null ) {
                final String name = (String) getValue(NAME);
                try {
                    putValue(MNEMONIC_KEY, Integer.valueOf(name.codePointAt(0)) );
                } catch (StringIndexOutOfBoundsException e) {
                    // Ignore
                }
            }
        }
    }
}
