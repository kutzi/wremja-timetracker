package com.kemai.wremja.gui.panels;

import static com.kemai.wremja.gui.settings.SettingsConstants.ALL_ITEMS_FILTER_DUMMY;
import static com.kemai.wremja.gui.settings.SettingsConstants.CURRENT_ITEM_FILTER_DUMMY;
import info.clearthought.layout.TableLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;

import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTitledSeparator;

import ca.odell.glazedlists.swing.EventComboBoxModel;

import com.kemai.swing.util.LabeledItem;
import com.kemai.util.TextResourceBundle;
import com.kemai.wremja.gui.GuiConstants;
import com.kemai.wremja.gui.lists.DayOfWeekFilterList;
import com.kemai.wremja.gui.lists.MonthFilterList;
import com.kemai.wremja.gui.lists.ProjectFilterList;
import com.kemai.wremja.gui.lists.WeekOfYearFilterList;
import com.kemai.wremja.gui.lists.YearFilterList;
import com.kemai.wremja.gui.model.PresentationModel;
import com.kemai.wremja.gui.settings.IUserSettings;
import com.kemai.wremja.gui.settings.SettingsConstants;
import com.kemai.wremja.model.Project;
import com.kemai.wremja.model.filter.Filter;

/**
 * Displays the reports generated from the project activities.
 * @author remast
 * @author kutzi
 */
@SuppressWarnings("serial") 
public class ReportPanel extends JXPanel implements ActionListener {

    /** The bundle for internationalized texts. */
    private static final TextResourceBundle TEXT_BUNDLE = TextResourceBundle.getBundle(GuiConstants.class);

    /** The model. */
    private final PresentationModel model;
    
    private final IUserSettings settings;

    /** Filter by selected project. */
    private JComboBox projectFilterSelector;

    /** Filter by selected year. */
    private JComboBox yearFilterSelector;

    /** Filter by selected month. */
    private JComboBox monthFilterSelector;

    /** Filter by selected week. */
    private JComboBox weekFilterSelector;
    
    /** Filter by selected day. */
    private JComboBox dayFilterSelector;
    
    /** The panel that actually displays the filtered activities. */
    private FilteredActivitiesPane filteredActivitiesPane;

    public ReportPanel(final PresentationModel model, IUserSettings settings) {
        this.model = model;
        this.settings = settings;

        initialize();
    }

    /**
     * Set up GUI components.
     */
    private void initialize() {
        this.filteredActivitiesPane = new FilteredActivitiesPane(this.model, this.settings);

        // TODO: init filter selectors based on settings!
        int nrOfFilters = getNumberOfEnabledFilters();
        
        
        final double borderBig = 8;
        final double border = 3;
        final double size[][] = {
                { border, TableLayout.PREFERRED, border, TableLayout.FILL, borderBig,
                    TableLayout.PREFERRED, border, TableLayout.FILL, borderBig,
                    TableLayout.PREFERRED, border, TableLayout.FILL, borderBig,
                    TableLayout.PREFERRED, border, TableLayout.FILL, borderBig,
                    TableLayout.PREFERRED, border, TableLayout.FILL, border}, // Columns
                    { border, TableLayout.PREFERRED, border, TableLayout.PREFERRED, borderBig, TableLayout.PREFERRED, 0,
                        TableLayout.FILL, border } }; // Rows
        this.setLayout(new TableLayout(size));

        int selectedYear = this.settings.getFilterSelectedYear(ALL_ITEMS_FILTER_DUMMY);
        int selectedMonth = this.settings.getFilterSelectedMonth(ALL_ITEMS_FILTER_DUMMY);
        int selectedWeek = this.settings.getFilterSelectedWeekOfYear(ALL_ITEMS_FILTER_DUMMY);
        int selectedDay = this.settings.getFilterSelectedDayOfWeek(ALL_ITEMS_FILTER_DUMMY);
        
        JXTitledSeparator filterSep = new JXTitledSeparator(TEXT_BUNDLE.textFor("ReportPanel.FiltersLabel")); //$NON-NLS-1$
        this.add(filterSep, "1, 1, 19, 1"); //$NON-NLS-1$

        this.add(new JXLabel(TEXT_BUNDLE.textFor("ReportPanel.ProjectLabel")), "1, 3"); 
        this.add(getProjectFilterSelector(), "3, 3"); 

        this.add(new JXLabel(TEXT_BUNDLE.textFor("ReportPanel.YearLabel")), "5, 3"); 
        this.add(initYearFilterSelector(selectedYear), "7, 3"); 

        this.add(new JXLabel(TEXT_BUNDLE.textFor("ReportPanel.MonthLabel")), "9, 3"); 
        this.add(initMonthFilterSelector(selectedMonth), "11, 3"); 

        this.add(new JXLabel(TEXT_BUNDLE.textFor("ReportPanel.WeekLabel")), "13, 3"); 
        this.add(initWeekOfYearFilterSelector(selectedWeek), "15, 3"); 
        
        this.add(new JXLabel(TEXT_BUNDLE.textFor("ReportPanel.DayLabel")), "17, 3"); 
        this.add(initDayOfWeekFilterSelector(selectedDay), "19, 3"); 

        
        JXTitledSeparator sep = new JXTitledSeparator(TEXT_BUNDLE.textFor("ReportPanel.DataLabel")); //$NON-NLS-1$
        this.add(sep, "1, 5, 19, 1"); //$NON-NLS-1$

        this.add(filteredActivitiesPane, "1, 7, 19, 7"); 
        
        disableSelectBoxesIfNeeded(selectedMonth, selectedWeek, selectedDay);
    }

    private int getNumberOfEnabledFilters() {
        int nr = 0;
        if (this.settings.getBooleanProperty(SettingsConstants.PROJECT_FILTER_ENABLED, true)) {
            nr++;
        }
        if (this.settings.getBooleanProperty(SettingsConstants.YEAR_FILTER_ENABLED, true)) {
            nr++;
        }
        if (this.settings.getBooleanProperty(SettingsConstants.MONTH_FILTER_ENABLED, true)) {
            nr++;
        }
        if (this.settings.getBooleanProperty(SettingsConstants.WEEK_OF_YEAR_FILTER_ENABLED, true)) {
            nr++;
        }
        if (this.settings.getBooleanProperty(SettingsConstants.DAY_OF_WEEK_FILTER_ENABLED, true)) {
            nr++;
        }
        return nr;
    }

    /**
     * @param selectedMonth 
     * @return the monthFilterSelector
     */
    private JComboBox initMonthFilterSelector(int selectedMonth) {
        MonthFilterList monthFilterList = new MonthFilterList(model);
        monthFilterSelector = new JComboBox(
                new EventComboBoxModel<LabeledItem<Integer>>(monthFilterList.getMonthList())
        );
        monthFilterSelector.setToolTipText(TEXT_BUNDLE.textFor("MonthFilterSelector.ToolTipText")); 

        for (LabeledItem<Integer> item : monthFilterList.getMonthList()) {
            if (item.getItem().intValue() == selectedMonth) {
                monthFilterSelector.setSelectedItem(item);
                break;
            }
        }

        monthFilterSelector.addActionListener(this);
        return monthFilterSelector;
    }

    /**
     * @return the weekFilterSelector
     */
    private JComboBox initWeekOfYearFilterSelector(int selectedWeek) {
        WeekOfYearFilterList weekOfYearFilterList = new WeekOfYearFilterList(model);
        weekFilterSelector = new JComboBox(new EventComboBoxModel<LabeledItem<Integer>>(weekOfYearFilterList
                .getWeekList()));
        weekFilterSelector.setToolTipText(TEXT_BUNDLE.textFor("WeekOfYearFilterSelector.ToolTipText")); 

        for (LabeledItem<Integer> item : weekOfYearFilterList.getWeekList()) {
            if (item.getItem().intValue() == selectedWeek) {
                weekFilterSelector.setSelectedItem(item);
                break;
            }
        }

        weekFilterSelector.addActionListener(this);
        return weekFilterSelector;
    }
    
    private JComboBox initDayOfWeekFilterSelector(int selectedWeek) {
        DayOfWeekFilterList dayOfWeekFilterList = new DayOfWeekFilterList();
        dayFilterSelector = new JComboBox(new EventComboBoxModel<LabeledItem<Integer>>(dayOfWeekFilterList
                .getDayList()));
        dayFilterSelector.setToolTipText(TEXT_BUNDLE.textFor("DayOfWeekFilterSelector.ToolTipText")); 

        for (LabeledItem<Integer> item : dayOfWeekFilterList.getDayList()) {
            if (item.getItem().intValue() == selectedWeek) {
                dayFilterSelector.setSelectedItem(item);
                break;
            }
        }

        dayFilterSelector.addActionListener(this);
        return dayFilterSelector;
    }

    /**
     * @return the projectFilterSelector
     */
    private JComboBox getProjectFilterSelector() {
        if (projectFilterSelector == null) {
            ProjectFilterList projectFilterList = new ProjectFilterList(model);
            projectFilterSelector = new JComboBox(
                    new EventComboBoxModel<LabeledItem<Project>>(projectFilterList.getProjectList())
            );
            projectFilterSelector.setToolTipText(TEXT_BUNDLE.textFor("ProjectFilterSelector.ToolTipText")); 

            final long selectedProjectId = this.settings.getFilterSelectedProjectId(ALL_ITEMS_FILTER_DUMMY);
            for (LabeledItem<Project> item : projectFilterList.getProjectList()) {
                if (item.getItem().getId() == selectedProjectId) {
                    projectFilterSelector.setSelectedItem(item);
                    break;
                }
            }

            projectFilterSelector.addActionListener(this);
        }
        return projectFilterSelector;
    }

    /**
     * @param selectedYear 
     * @return the yearFilterSelector
     */
    private JComboBox initYearFilterSelector(int selectedYear) {
        if (yearFilterSelector == null) {
            YearFilterList yearFilterList = new YearFilterList(model);
            yearFilterSelector = new JComboBox(
                    new EventComboBoxModel<LabeledItem<Integer>>(yearFilterList.getYearList())
            );
            yearFilterSelector.setToolTipText(TEXT_BUNDLE.textFor("YearFilterSelector.ToolTipText")); 

            for (LabeledItem<Integer> item : yearFilterList.getYearList()) {
                if (item.getItem().intValue() == selectedYear) {
                    yearFilterSelector.setSelectedItem(item);
                    break;
                }
            }

            yearFilterSelector.addActionListener(this);
        }
        return yearFilterSelector;
    }
    
    /**
     * Create filter from selection in this panel.
     * @return the filter for the selection
     */
    @SuppressWarnings("unchecked")
    public Filter createFilter() {
        final Filter filter = new Filter();

        // Filter for year
        {
            LabeledItem<Integer> filterItem = (LabeledItem<Integer>) this.yearFilterSelector.getSelectedItem();
            final int selectedYear = filterItem.getItem().intValue();
    
            filter.setYear(selectedYear);
        }
        
        final int selectedMonth;
        // Filter for month
        {
            LabeledItem<Integer> filterItem = (LabeledItem<Integer>) this.monthFilterSelector.getSelectedItem();
            selectedMonth = filterItem.getItem().intValue();
            filter.setMonth(selectedMonth);
        }

        final int selectedWeekOfYear;
        // Filter for week of year
        {
            LabeledItem<Integer> filterItem = (LabeledItem<Integer>) this.weekFilterSelector.getSelectedItem();
            selectedWeekOfYear= filterItem.getItem().intValue();
            filter.setWeekOfYear(selectedWeekOfYear);
        }
        
        final int selectedDayOfWeek;
        // Filter for day of week
        {
            LabeledItem<Integer> filterItem = (LabeledItem<Integer>) this.dayFilterSelector.getSelectedItem();
            selectedDayOfWeek = filterItem.getItem().intValue();
            filter.setDayOfWeek(selectedDayOfWeek);
        }
        
        disableSelectBoxesIfNeeded(selectedMonth, selectedWeekOfYear, selectedDayOfWeek);
        
        // Filter for project
        final LabeledItem<Project> projectFilterItem = (LabeledItem<Project>) getProjectFilterSelector().getSelectedItem();
        final Project project = projectFilterItem.getItem();
        if (ProjectFilterList.ALL_PROJECTS_DUMMY.equals(project)) {
            // dont't filter
        } else if (ProjectFilterList.BILLABLE_PROJECTS_DUMMY.equals(project)) {
                filter.setOnlyBillable(true);
        } else {
                filter.setProject(project);
        }
        return filter;
    }
    
    // TODO: there must be a more intelligent way to implement this:
    private void disableSelectBoxesIfNeeded(int selectedMonth, int selectedWeekOfYear, int selectedDayOfWeek) {
        if (selectedMonth == CURRENT_ITEM_FILTER_DUMMY
            || selectedWeekOfYear ==  CURRENT_ITEM_FILTER_DUMMY
            || selectedDayOfWeek ==  CURRENT_ITEM_FILTER_DUMMY) {
            this.yearFilterSelector.setEnabled(false);
        } else {
            this.yearFilterSelector.setEnabled(true);
        }

        if (selectedWeekOfYear ==  CURRENT_ITEM_FILTER_DUMMY
            || selectedDayOfWeek ==  CURRENT_ITEM_FILTER_DUMMY) {
            this.monthFilterSelector.setEnabled(false);
        } else {
            this.monthFilterSelector.setEnabled(true);
        }
        
        if ( selectedDayOfWeek ==  CURRENT_ITEM_FILTER_DUMMY) {
            this.weekFilterSelector.setEnabled(false);
        } else {
            this.weekFilterSelector.setEnabled(true);
        }
    }

    /**
     * Stores the filter in the user settings.
     */
    @SuppressWarnings("unchecked")
    private void storeFilterInSettings() {
        // Store filter by month
        LabeledItem<Integer> filterItem = (LabeledItem<Integer>) this.monthFilterSelector.getSelectedItem();
        final int selectedMonth = filterItem.getItem().intValue();
        this.settings.setFilterSelectedMonth(selectedMonth);

        // Store filter by year
        filterItem = (LabeledItem<Integer>) this.yearFilterSelector.getSelectedItem();
        final int selectedYear = filterItem.getItem().intValue();
        this.settings.setFilterSelectedYear(selectedYear);

        // Store filter by week of year
        filterItem = (LabeledItem<Integer>) this.weekFilterSelector.getSelectedItem();
        final int selectedWeekOfYear = filterItem.getItem().intValue();
        this.settings.setFilterSelectedWeekOfYear(selectedWeekOfYear);
        
        // Store filter by day of week
        filterItem = (LabeledItem<Integer>) this.dayFilterSelector.getSelectedItem();
        final int selectedDayOfWeek = filterItem.getItem().intValue();
        this.settings.setFilterSelectedDayOfWeek(selectedDayOfWeek);

        // Store filter by project
        final LabeledItem<Project> projectFilterItem = (LabeledItem<Project>) getProjectFilterSelector().getSelectedItem();
        final Project project = projectFilterItem.getItem();
        this.settings.setFilterSelectedProjectId(project.getId());
    }

    /**
     * One of the filter criteria changed. So we create and apply the filter.
     */
    public final void actionPerformed(final ActionEvent event) {
        // 1. Create filter from selection.
        final Filter filter = createFilter();

        // 2. Save selection to settings.
        storeFilterInSettings();

        // 3. Save to model
        model.setFilter(filter, this);
    }
}
