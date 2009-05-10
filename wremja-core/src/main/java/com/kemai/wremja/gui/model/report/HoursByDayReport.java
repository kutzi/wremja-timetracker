package com.kemai.wremja.gui.model.report;

import com.kemai.wremja.gui.model.PresentationModel;
import com.kemai.wremja.model.ProjectActivity;

/**
 * Report for the working hours by day.
 * @author remast
 * @author kutzi
 */
public class HoursByDayReport extends HoursByPeriodReport<HoursByDay>  {

    public HoursByDayReport(final PresentationModel model) {
        super(model);
    }
    
    @Override
    protected void addCurrentHours() {
        final HoursByDay newHoursByDay = new HoursByDay(getModel().getStart(),
                getModel().getCurrentDuration());
        newHoursByDay.setChanging(true);
        addHoursByPeriod(newHoursByDay);
    }

    @Override
    public void addHours(final ProjectActivity activity) {
        if( !filterMatches(activity)) {
            return;
        }

        final HoursByDay newHoursByDay = new HoursByDay(activity.getStart(), activity.getDuration());
        addHoursByPeriod(newHoursByDay);
    }
}
