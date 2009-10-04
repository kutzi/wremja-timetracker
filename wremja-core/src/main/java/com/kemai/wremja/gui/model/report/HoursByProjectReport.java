package com.kemai.wremja.gui.model.report;

import com.kemai.wremja.gui.model.PresentationModel;
import com.kemai.wremja.model.ProjectActivity;

/**
 * Report for the working hours by project.
 * @author remast
 */
public class HoursByProjectReport extends HoursByPeriodReport<HoursByProject> {

    public HoursByProjectReport(final PresentationModel model) {
        super(model);
    }
    
    @Override
    protected void addCurrentHours() {
        final HoursByProject newHours = new HoursByProject(getModel().getSelectedProject(),
                getModel().getCurrentDuration());
        newHours.setChanging(true);
        addHoursByPeriod(newHours);
    }

    @Override
    public void addHours(final ProjectActivity activity) {
        if( !filterMatches(activity)) {
            return;
        }

        final HoursByProject newHours = new HoursByProject(activity.getProject(), activity.getDuration());
        addHoursByPeriod(newHours);
    }
}
