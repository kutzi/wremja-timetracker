package com.kemai.wremja.gui.model.report;

import org.joda.time.DateTime;

import com.kemai.wremja.gui.model.PresentationModel;
import com.kemai.wremja.gui.settings.SettingsConstants;
import com.kemai.wremja.model.ProjectActivity;
import com.kemai.wremja.model.filter.Filter;

/**
 * Report for the working hours by week.
 * @author remast
 * @author kutzi
 */
public class HoursByWeekReport extends HoursByPeriodReport<HoursByWeek>  {

    public HoursByWeekReport(final PresentationModel model) {
        super(model);
    }
    
    @Override
    protected void addCurrentHours() {
        final HoursByWeek newHoursByWeek = new HoursByWeek(getModel().getStart().getWeekOfWeekyear(),
                getModel().getCurrentDuration());
        newHoursByWeek.setChanging(true);
        addHoursByPeriod(newHoursByWeek);
    }

    @Override
    public void addHours(final ProjectActivity activity) {
        final DateTime dateTime = activity.getStart();

        final HoursByWeek newHoursByWeek = new HoursByWeek(dateTime.getWeekOfWeekyear(), activity.getDuration());
        
        addHoursByPeriod(newHoursByWeek);
    }
    
    @Override
    protected void setFilter(final Filter filter) {
        Filter copy = filter.copy();
        // we mustn't filter by day for this report!
        copy.setDayOfWeek(SettingsConstants.ALL_ITEMS_FILTER_DUMMY);
        super.setFilter(copy);
    }
}
