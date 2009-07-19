package com.kemai.wremja.gui.panels;

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
import com.kemai.wremja.gui.lists.MonthFilterList;
import com.kemai.wremja.gui.lists.ProjectFilterList;
import com.kemai.wremja.gui.lists.WeekOfYearFilterList;
import com.kemai.wremja.gui.lists.YearFilterList;
import com.kemai.wremja.gui.model.PresentationModel;
import com.kemai.wremja.gui.settings.IUserSettings;
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
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(GuiConstants.class);

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

    /** List of years by which can be filtered. */
    private YearFilterList yearFilterList;

    /** List of months by which can be filtered. */
    private MonthFilterList monthFilterList;

    /** List of months by which can be filtered. */
    private WeekOfYearFilterList weekOfYearFilterList;

    /** List of projects by which can be filtered. */
    private ProjectFilterList projectFilterList;

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
        final double size[][] = {
                { border, TableLayout.PREFERRED, border, TableLayout.FILL, borderBig, TableLayout.PREFERRED, border,
                    TableLayout.FILL, borderBig, TableLayout.PREFERRED, border, TableLayout.FILL, borderBig, TableLayout.PREFERRED, border, TableLayout.FILL, border}, // Columns
                    { border, TableLayout.PREFERRED, border, TableLayout.PREFERRED, borderBig, TableLayout.PREFERRED, 0,
                        TableLayout.FILL, border } }; // Rows
        this.setLayout(new TableLayout(size));

        int selectedYear = this.settings.getFilterSelectedYear(YearFilterList.ALL_YEARS_DUMMY);
        int selectedMonth = this.settings.getFilterSelectedMonth(MonthFilterList.ALL_MONTHS_DUMMY);
        int selectedWeek = this.settings.getFilterSelectedWeekOfYear(WeekOfYearFilterList.ALL_WEEKS_OF_YEAR_DUMMY);
        
        JXTitledSeparator filterSep = new JXTitledSeparator(textBundle.textFor("ReportPanel.FiltersLabel")); //$NON-NLS-1$
        this.add(filterSep, "1, 1, 15, 1"); //$NON-NLS-1$

        this.add(new JXLabel(textBundle.textFor("ReportPanel.ProjectLabel")), "1, 3"); //$NON-NLS-1$ //$NON-NLS-2$
        this.add(getProjectFilterSelector(), "3, 3"); //$NON-NLS-1$

        this.add(new JXLabel(textBundle.textFor("ReportPanel.YearLabel")), "5, 3"); //$NON-NLS-1$ //$NON-NLS-2$
        this.add(initYearFilterSelector(selectedYear), "7, 3"); //$NON-NLS-1$

        this.add(new JXLabel(textBundle.textFor("ReportPanel.MonthLabel")), "9, 3"); //$NON-NLS-1$ //$NON-NLS-2$
        this.add(initMonthFilterSelector(selectedMonth), "11, 3"); //$NON-NLS-1$

        this.add(new JXLabel(textBundle.textFor("ReportPanel.WeekLabel")), "13, 3"); //$NON-NLS-1$ //$NON-NLS-2$
        this.add(initWeekOfYearFilterSelector(selectedWeek), "15, 3"); //$NON-NLS-1$

        JXTitledSeparator sep = new JXTitledSeparator(textBundle.textFor("ReportPanel.DataLabel")); //$NON-NLS-1$
        this.add(sep, "1, 5, 15, 1"); //$NON-NLS-1$

        this.add(filteredActivitiesPane, "1, 7, 15, 7"); //$NON-NLS-1$
        
        disableCheckBoxesIfNeeded(selectedMonth, selectedWeek);
    }

    /**
     * @param selectedMonth 
     * @return the monthFilterSelector
     */
    private JComboBox initMonthFilterSelector(int selectedMonth) {
        monthFilterList = model.getMonthFilterList();
        monthFilterSelector = new JComboBox(
                new EventComboBoxModel<LabeledItem<Integer>>(monthFilterList.getMonthList())
        );
        monthFilterSelector.setToolTipText(textBundle.textFor("MonthFilterSelector.ToolTipText")); //$NON-NLS-1$

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
        weekOfYearFilterList = model.getWeekFilterList();
        weekFilterSelector = new JComboBox(new EventComboBoxModel<LabeledItem<Integer>>(weekOfYearFilterList
                .getWeekList()));
        weekFilterSelector.setToolTipText(textBundle.textFor("WeekOfYearFilterSelector.ToolTipText")); //$NON-NLS-1$

        for (LabeledItem<Integer> item : weekOfYearFilterList.getWeekList()) {
            if (item.getItem().intValue() == selectedWeek) {
                weekFilterSelector.setSelectedItem(item);
                break;
            }
        }

        weekFilterSelector.addActionListener(this);
        return weekFilterSelector;
    }

    /**
     * @return the projectFilterSelector
     */
    private JComboBox getProjectFilterSelector() {
        if (projectFilterSelector == null) {
            projectFilterList = model.getProjectFilterList();
            projectFilterSelector = new JComboBox(
                    new EventComboBoxModel<LabeledItem<Project>>(projectFilterList.getProjectList())
            );
            projectFilterSelector.setToolTipText(textBundle.textFor("ProjectFilterSelector.ToolTipText")); //$NON-NLS-1$

            final long selectedProjectId = this.settings.getFilterSelectedProjectId(ProjectFilterList.ALL_PROJECTS_DUMMY_VALUE);
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
            yearFilterList = model.getYearFilterList();
            yearFilterSelector = new JComboBox(
                    new EventComboBoxModel<LabeledItem<Integer>>(yearFilterList.getYearList())
            );
            yearFilterSelector.setToolTipText(textBundle.textFor("YearFilterSelector.ToolTipText")); //$NON-NLS-1$

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
        
        disableCheckBoxesIfNeeded(selectedMonth, selectedWeekOfYear);
        
        // Filter for project
        final LabeledItem<Project> projectFilterItem = (LabeledItem<Project>) getProjectFilterSelector().getSelectedItem();
        final Project project = projectFilterItem.getItem();
        if (!ProjectFilterList.ALL_PROJECTS_DUMMY.equals(project)) {
            filter.setProject(project);
        }
        return filter;
    }
    
    private void disableCheckBoxesIfNeeded(int selectedMonth, int selectedWeekOfYear) {
        if(selectedMonth == MonthFilterList.CURRENT_MONTH_DUMMY
                || selectedWeekOfYear == WeekOfYearFilterList.CURRENT_WEEK_OF_YEAR_DUMMY) {
                 this.yearFilterSelector.setEnabled(false);
             } else {
                 this.yearFilterSelector.setEnabled(true);
             }
             
             if(selectedWeekOfYear == WeekOfYearFilterList.CURRENT_WEEK_OF_YEAR_DUMMY) {
                 this.monthFilterSelector.setEnabled(false);
             } else {
                 this.monthFilterSelector.setEnabled(true);
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

        // Store filter by project
        final LabeledItem<Project> projectFilterItem = (LabeledItem<Project>) getProjectFilterSelector().getSelectedItem();
        final Project project = projectFilterItem.getItem();
        if (!ProjectFilterList.ALL_PROJECTS_DUMMY.equals(project)) {
            long projectId = project.getId();
            this.settings.setFilterSelectedProjectId(projectId);
        } else {
            this.settings.setFilterSelectedProjectId(
                    ProjectFilterList.ALL_PROJECTS_DUMMY_VALUE
            );
        }
    }

    /**
     * One of the filter criteria changed. So we create and apply the filter.
     */
    public final void actionPerformed(final ActionEvent event) {
        // 1. Create filter from selection.
        final Filter filter = this.createFilter();

        // 2. Save selection to settings.
        storeFilterInSettings();

        // 3. Save to model
        model.setFilter(filter, this);
    }
}
