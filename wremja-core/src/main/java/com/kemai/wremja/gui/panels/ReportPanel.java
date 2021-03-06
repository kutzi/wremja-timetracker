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

import ca.odell.glazedlists.swing.DefaultEventComboBoxModel;

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
    private JComboBox<Project> projectFilterSelector;

    /** Filter by selected year. */
    private JComboBox<Integer> yearFilterSelector;

    /** Filter by selected month. */
    private JComboBox<Integer> monthFilterSelector;

    /** Filter by selected week. */
    private JComboBox<Integer> weekFilterSelector;
    
    /** Filter by selected day. */
    private JComboBox<Integer> dayFilterSelector;
    
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

        final double borderBig = 8;
        final double border = 3;
        final double size[][] = new double[2] [];

        int nrOfFilters = getNumberOfEnabledFilters();
        int nrOfColumns = Math.max(1, nrOfFilters);
        double[] columns = new double[nrOfColumns * 4 + 1];
        columns[0] = border;
        for (int i=1; i <= nrOfColumns; i++) {
        	int base = (i-1) * 4;
        	columns[base+1] = TableLayout.PREFERRED;
        	columns[base+2] = border;
        	columns[base+3] = TableLayout.FILL;
        	columns[base+4] = borderBig;
        }
        columns[columns.length - 1] = border;
        size[0] = columns;
        
        double[] rows =  { border, TableLayout.PREFERRED, border, TableLayout.PREFERRED, borderBig, TableLayout.PREFERRED, 0,
            TableLayout.FILL, border };
        size[1] = rows;
        this.setLayout(new TableLayout(size));

        int lastColumn = columns.length - 2; // excluding the last border column
        
        JXTitledSeparator filterSep = new JXTitledSeparator(TEXT_BUNDLE.textFor("ReportPanel.FiltersLabel")); //$NON-NLS-1$
        this.add(filterSep, "1, 1, " + lastColumn + ", 1"); //$NON-NLS-1$

        initSelectorComponents();
        
        int column = 1;
        if (settings.getBooleanProperty(SettingsConstants.PROJECT_FILTER_ENABLED, true)) {
        	add(new JXLabel(TEXT_BUNDLE.textFor("ReportPanel.ProjectLabel")), column + ", 3");
        	column += 2;
        	add(this.projectFilterSelector, column + ", 3");
        	column += 2;
        }

        if (settings.getBooleanProperty(SettingsConstants.YEAR_FILTER_ENABLED, true)) {
        	add(new JXLabel(TEXT_BUNDLE.textFor("ReportPanel.YearLabel")), column + ", 3");
        	column += 2;
        	add(initYearFilterSelector(), column + ", 3");
        	column += 2;
        }

        if (settings.getBooleanProperty(SettingsConstants.MONTH_FILTER_ENABLED, true)) {
        	add(new JXLabel(TEXT_BUNDLE.textFor("ReportPanel.MonthLabel")), column + ", 3");
        	column += 2;
        	add(initMonthFilterSelector(), column + ", 3");
        	column += 2;
        }

        if (settings.getBooleanProperty(SettingsConstants.WEEK_OF_YEAR_FILTER_ENABLED, true)) {
        	add(new JXLabel(TEXT_BUNDLE.textFor("ReportPanel.WeekLabel")), column + ", 3");
        	column += 2;
        	add(initWeekOfYearFilterSelector(), column + ", 3");
        	column += 2;
        }        
        if (settings.getBooleanProperty(SettingsConstants.DAY_OF_WEEK_FILTER_ENABLED, true)) {
        	add(new JXLabel(TEXT_BUNDLE.textFor("ReportPanel.DayLabel")), column + ", 3");
        	column += 2;
        	add(initDayOfWeekFilterSelector(), column + ", 3");
        	column += 2;
        }
        
        JXTitledSeparator sep = new JXTitledSeparator(TEXT_BUNDLE.textFor("ReportPanel.DataLabel")); //$NON-NLS-1$
        this.add(sep, "1, 5, " + lastColumn + ", 1"); //$NON-NLS-1$

        this.add(filteredActivitiesPane, "1, 7, " + lastColumn + ", 7"); 
        
        int selectedMonth = this.settings.getFilterSelectedMonth(SettingsConstants.ALL_ITEMS_FILTER_DUMMY);
    	int selectedWeekOfYear = this.settings.getFilterSelectedWeekOfYear(SettingsConstants.ALL_ITEMS_FILTER_DUMMY);
    	int selectedDayOfWeek = this.settings.getFilterSelectedDayOfWeek(SettingsConstants.ALL_ITEMS_FILTER_DUMMY);
        disableSelectBoxesIfNeeded(selectedMonth, selectedWeekOfYear, selectedDayOfWeek);
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

    private void initSelectorComponents() {
        getProjectFilterSelector(); 
        initYearFilterSelector(); 
        initMonthFilterSelector(); 
        initWeekOfYearFilterSelector(); 
        initDayOfWeekFilterSelector(); 
    }
    
    /**
     * @return the monthFilterSelector
     */
    @SuppressWarnings("unchecked")
	private JComboBox<Integer> initMonthFilterSelector() {
        MonthFilterList monthFilterList = new MonthFilterList(model);
        monthFilterSelector = new JComboBox<>(
                new DefaultEventComboBoxModel<LabeledItem<Integer>>(monthFilterList.getMonthList())
        );
        monthFilterSelector.setToolTipText(TEXT_BUNDLE.textFor("MonthFilterSelector.ToolTipText")); 

        int selectedMonth = this.settings.getFilterSelectedMonth(ALL_ITEMS_FILTER_DUMMY);
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
    @SuppressWarnings("unchecked")
	private JComboBox<Integer> initWeekOfYearFilterSelector() {
        WeekOfYearFilterList weekOfYearFilterList = new WeekOfYearFilterList(model);
        weekFilterSelector = new JComboBox<>(new DefaultEventComboBoxModel<LabeledItem<Integer>>(weekOfYearFilterList
                .getWeekList()));
        weekFilterSelector.setToolTipText(TEXT_BUNDLE.textFor("WeekOfYearFilterSelector.ToolTipText")); 

        int selectedWeek = this.settings.getFilterSelectedWeekOfYear(ALL_ITEMS_FILTER_DUMMY);
        for (LabeledItem<Integer> item : weekOfYearFilterList.getWeekList()) {
            if (item.getItem().intValue() == selectedWeek) {
                weekFilterSelector.setSelectedItem(item);
                break;
            }
        }

        weekFilterSelector.addActionListener(this);
        return weekFilterSelector;
    }
    
    @SuppressWarnings("unchecked")
	private JComboBox<Integer> initDayOfWeekFilterSelector() {
        DayOfWeekFilterList dayOfWeekFilterList = new DayOfWeekFilterList();
        dayFilterSelector = new JComboBox<>(new DefaultEventComboBoxModel<LabeledItem<Integer>>(dayOfWeekFilterList
                .getDayList()));
        dayFilterSelector.setToolTipText(TEXT_BUNDLE.textFor("DayOfWeekFilterSelector.ToolTipText")); 

        int selectedDay = this.settings.getFilterSelectedDayOfWeek(ALL_ITEMS_FILTER_DUMMY);
        for (LabeledItem<Integer> item : dayOfWeekFilterList.getDayList()) {
            if (item.getItem().intValue() == selectedDay) {
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
    @SuppressWarnings("unchecked")
	private JComboBox<Project> getProjectFilterSelector() {
        if (projectFilterSelector == null) {
            ProjectFilterList projectFilterList = new ProjectFilterList(model);
            projectFilterSelector = new JComboBox<>(
                    new DefaultEventComboBoxModel<LabeledItem<Project>>(projectFilterList.getProjectList())
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
     * @return the yearFilterSelector
     */
    @SuppressWarnings("unchecked")
	private JComboBox<Integer> initYearFilterSelector() {
        if (yearFilterSelector == null) {
            YearFilterList yearFilterList = new YearFilterList(model);
            yearFilterSelector = new JComboBox<>(
                    new DefaultEventComboBoxModel<LabeledItem<Integer>>(yearFilterList.getYearList())
            );
            yearFilterSelector.setToolTipText(TEXT_BUNDLE.textFor("YearFilterSelector.ToolTipText")); 

            int selectedYear = this.settings.getFilterSelectedYear(ALL_ITEMS_FILTER_DUMMY);
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
