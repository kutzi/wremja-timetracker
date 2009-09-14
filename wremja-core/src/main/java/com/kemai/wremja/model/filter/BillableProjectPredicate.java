package com.kemai.wremja.model.filter;

import com.kemai.util.Predicate;
import com.kemai.wremja.logging.Logger;
import com.kemai.wremja.model.ProjectActivity;

/**
 * {@link Predicate} which evaluates to true for billable projects.
 *
 * @author kutzi
 */
public class BillableProjectPredicate implements Predicate<ProjectActivity> {

    private static final Logger LOGGER = Logger.getLogger(BillableProjectPredicate.class);
    
    @Override
    public boolean evaluate(ProjectActivity activity) {
        if (activity == null) {
            return false;
        } else if (activity.getProject() == null) {
            LOGGER.warn("Project is null for activity: " + activity);
            return false;
        }
        return activity.getProject().isBillable();
    }

}
