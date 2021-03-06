/**
 * 
 */
package com.kemai.wremja.gui.model.edit;

import java.util.Collection;
import java.util.Observable;
import java.util.Observer;
import java.util.Stack;

import com.kemai.util.CollectionUtils;
import com.kemai.wremja.gui.actions.RedoAction;
import com.kemai.wremja.gui.actions.UndoAction;
import com.kemai.wremja.gui.events.WremjaEvent;
import com.kemai.wremja.gui.model.PresentationModel;
import com.kemai.wremja.logging.Logger;
import com.kemai.wremja.model.OverlappingActivitiesException;
import com.kemai.wremja.model.ProjectActivity;

/**
 * Edit stack for undoing and redoing edit actions. The stack observes
 * the model and keeps track of undoable and redoable events.
 * @author remast
 */
// TODO: use Swing's built-in undo/redo support instead?
public class EditStack implements Observer {

	private static final Logger LOG = Logger.getLogger(EditStack.class);

	/**
	 * The action for undoing an edit activity.
	 */
	private final UndoAction undoAction;

	/**
	 * The action for redoing an edit activity.
	 */
	private final RedoAction redoAction;

	/**
	 * The undoable edit events.
	 */
	private final Stack<WremjaEvent> undoStack = new Stack<WremjaEvent>();

	/**
	 * The redoable edit events.
	 */
	private final Stack<WremjaEvent> redoStack = new Stack<WremjaEvent>();

	/** The model. */
	private final PresentationModel model;

	/**
	 * Creates a new edit stack for the given model.
	 * @param model the edited model to create stack for
	 */
	public EditStack(final PresentationModel model) {
		this.model = model;
		this.undoAction = new UndoAction(this);
		this.redoAction = new RedoAction(this);

		updateActions();
	}

    /**
     * {@inheritDoc}
     */
	public void update(final Observable source, final Object eventObject) {
		if (eventObject == null || !(eventObject instanceof WremjaEvent)) {
			return;
		}

		final WremjaEvent event = (WremjaEvent) eventObject;

		// Ignore our own events
		if (this == event.getSource()) {
			return;
		}

		if (event.canBeUndone()) {
			undoStack.push(event);
			updateActions();
		}
	}

	/**
	 * Enable or disable actions.
	 * @param event 
	 */
	private void updateActions() {
        if (CollectionUtils.isNotEmpty(undoStack)) {
            undoAction.setEnabled(true);
            undoAction.setTooltip(undoStack.peek().getUndoText());
        } else {
            undoAction.setEnabled(false);
            undoAction.resetTooltip();
        }
        
        if (CollectionUtils.isNotEmpty(redoStack)) {
            redoAction.setEnabled(true);
            redoAction.setTooltip(redoStack.peek().getRedoText());
        } else {
            redoAction.setEnabled(false);
            redoAction.resetTooltip();
        }
	}

	/**
	 * @return the undoAction
	 */
	public UndoAction getUndoAction() {
		return undoAction;
	}

	/**
	 * @return the redoAction
	 */
	public RedoAction getRedoAction() {
		return redoAction;
	}

	/**
	 * Undo last edit action.
	 */
	public final void undo() {
		if (CollectionUtils.isEmpty(undoStack)) {
			return;
		}

		final WremjaEvent event = undoStack.pop();
		redoStack.push(event);

		executeUndo(event);

		updateActions();
	}

	/**
	 * Redo last edit action.
	 */
	public final void redo() {
		if (CollectionUtils.isEmpty(redoStack)) {
			return;
		}

		final WremjaEvent event = redoStack.pop();
		undoStack.push(event);

		executeRedo(event);

		updateActions();
	}

    /**
     * Undoes the given event.
     * @param event the event to undo
     */
	@SuppressWarnings("unchecked")
    private void executeUndo(final WremjaEvent event) {
	    switch( event.getType() ) {
	        case PROJECT_ACTIVITY_REMOVED:
	            Collection<ProjectActivity> activities = (Collection<ProjectActivity>)event.getDataCollection();
	            for(ProjectActivity activity : activities) {
	                try {
	                    model.addActivity(activity, this);
                    } catch (OverlappingActivitiesException e) {
	                    LOG.error("Overlapping activities on undo", e);
                    }
	            }
	            break;
	        case PROJECT_ACTIVITY_ADDED: model.removeActivity((ProjectActivity) event.getData(), this);
	            break;
	    }
	}

	/**
	 * Redoes the given event.
	 * @param event the event to redo
	 */
	@SuppressWarnings("unchecked")
    private void executeRedo(final WremjaEvent event) {
	    switch( event.getType() ) {
            case PROJECT_ACTIVITY_REMOVED:
                model.removeActivities((Collection<ProjectActivity>)event.getDataCollection(), this);
                break;
            case PROJECT_ACTIVITY_ADDED:
            	try {
	            	model.addActivity((ProjectActivity) event.getData(), this);
	            } catch (OverlappingActivitiesException e) {
	            	LOG.error("Overlapping activities on redo", e);
	            }
                break;
	    }
    }

}
