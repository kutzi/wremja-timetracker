package com.kemai.wremja.gui.panels;

import info.clearthought.layout.TableLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTitledSeparator;
import org.joda.time.DateTime;

import com.kemai.swing.util.LabeledItem;
import com.kemai.util.DateUtils;
import com.kemai.util.TextResourceBundle;
import com.kemai.wremja.gui.Launcher;
import com.kemai.wremja.gui.lists.MonthFilterList;
import com.kemai.wremja.gui.lists.ProjectFilterList;
import com.kemai.wremja.gui.lists.WeekOfYearFilterList;
import com.kemai.wremja.gui.lists.YearFilterList;
import com.kemai.wremja.gui.model.PresentationModel;
import com.kemai.wremja.gui.settings.UserSettings;
import com.kemai.wremja.model.Project;
import com.kemai.wremja.model.filter.Filter;

import ca.odell.glazedlists.swing.EventComboBoxModel;

/**
 * Displays the reports generated from the project activities.
 * @author remast
 */
@SuppressWarnings("serial") //$NON-NLS-1$
public class ReportPanel extends JXPanel implements ActionListener {

    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(Launcher.class);

    /** The logger. */
    private static final Log log = LogFactory.getLog(ReportPanel.class);

    /** The model. */
    private PresentationModel model;

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

    public ReportPanel(final PresentationModel model) {
        this.model = model;

        initialize();
    }

    /**
     * Set up GUI components.
     */
    private void initialize() {
        // Obtain a reusable constraints object to place components in the grid.
        filteredActivitiesPane = new FilteredActivitiesPane(model);

        final double borderBig = 8;
        final double border = 3;
        final double size[][] = {
                { border, TableLayout.PREFERRED, border, TableLayout.FILL, borderBig, TableLayout.PREFERRED, border,
                    TableLayout.FILL, borderBig, TableLayout.PREFERRED, border, TableLayout.FILL, borderBig, TableLayout.PREFERRED, border, TableLayout.FILL, border}, // Columns
                    { border, TableLayout.PREFERRED, border, TableLayout.PREFERRED, borderBig, TableLayout.PREFERRED, 0,
                        TableLayout.FILL, border } }; // Rows
        this.setLayout(new TableLayout(size));

        JXTitledSeparator filterSep = new JXTitledSeparator(textBundle.textFor("ReportPanel.FiltersLabel")); //$NON-NLS-1$
        this.add(filterSep, "1, 1, 15, 1"); //$NON-NLS-1$

        this.add(new JXLabel(textBundle.textFor("ReportPanel.ProjectLabel")), "1, 3"); //$NON-NLS-1$ //$NON-NLS-2$
        this.add(getProjectFilterSelector(), "3, 3"); //$NON-NLS-1$

        this.add(new JXLabel(textBundle.textFor("ReportPanel.YearLabel")), "5, 3"); //$NON-NLS-1$ //$NON-NLS-2$
        this.add(getYearFilterSelector(), "7, 3"); //$NON-NLS-1$

        this.add(new JXLabel(textBundle.textFor("ReportPanel.MonthLabel")), "9, 3"); //$NON-NLS-1$ //$NON-NLS-2$
        this.add(getMonthFilterSelector(), "11, 3"); //$NON-NLS-1$

        this.add(new JXLabel(textBundle.textFor("ReportPanel.WeekLabel")), "13, 3"); //$NON-NLS-1$ //$NON-NLS-2$
        this.add(getWeekOfYearFilterSelector(), "15, 3"); //$NON-NLS-1$

        JXTitledSeparator sep = new JXTitledSeparator(textBundle.textFor("ReportPanel.DataLabel")); //$NON-NLS-1$
        this.add(sep, "1, 5, 15, 1"); //$NON-NLS-1$

        this.add(filteredActivitiesPane, "1, 7, 15, 7"); //$NON-NLS-1$
    }

    /**
     * @return the monthFilterSelector
     */
    private JComboBox getMonthFilterSelector() {
        if (monthFilterSelector == null) {
            monthFilterList = model.getMonthFilterList();
            monthFilterSelector = new JComboBox(
                    new EventComboBoxModel<LabeledItem<Integer>>(monthFilterList.getMonthList())
            );
            monthFilterSelector.setToolTipText(textBundle.textFor("MonthFilterSelector.ToolTipText")); //$NON-NLS-1$

            // Select first entry
            if (!monthFilterList.getMonthList().isEmpty()) {
                monthFilterSelector.setSelectedIndex(0);
            }

            // Read from Settings.
            final Integer selectedMonth = UserSettings.instance().getFilterSelectedMonth();
            if (selectedMonth != null) {
                for (LabeledItem<Integer> item : monthFilterList.getMonthList()) {
                    if (item.getItem().equals(selectedMonth)) {
                        monthFilterSelector.setSelectedItem(item);
                        break;
                    }
                }
            }

            monthFilterSelector.addActionListener(this);
        }
        return monthFilterSelector;
    }

    /**
     * @return the weekFilterSelector
     */
    private JComboBox getWeekOfYearFilterSelector() {
        if (weekFilterSelector == null) {
            weekOfYearFilterList = model.getWeekFilterList();
            weekFilterSelector = new JComboBox(new EventComboBoxModel<LabeledItem<Integer>>(weekOfYearFilterList
                    .getWeekList()));
            weekFilterSelector.setToolTipText(textBundle.textFor("WeekOfYearFilterSelector.ToolTipText")); //$NON-NLS-1$

            // Select first entry
            if (!weekOfYearFilterList.getWeekList().isEmpty()) {
                weekFilterSelector.setSelectedIndex(0);
            }

            // Read from Settings.
            final Integer selectedWeek = UserSettings.instance().getFilterSelectedWeekOfYear();
            if (selectedWeek != null) {
                for (LabeledItem<Integer> item : weekOfYearFilterList.getWeekList()) {
                    if (item.getItem().equals(selectedWeek)) {
                        weekFilterSelector.setSelectedItem(item);
                        break;
                    }
                }
            }

            weekFilterSelector.addActionListener(this);
        }
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

            // Select first entry
            if (!projectFilterList.getProjectList().isEmpty()) {
                projectFilterSelector.setSelectedIndex(0);
            }

            // Read from Settings.
            final Long selectedProjectId = UserSettings.instance().getFilterSelectedProjectId();
            if (selectedProjectId != null) {
                for (LabeledItem<Project> item : projectFilterList.getProjectList()) {
                    if (ObjectUtils.equals(item.getItem().getId(), selectedProjectId.longValue())) {
                        projectFilterSelector.setSelectedItem(item);
                        break;
                    }
                }
            }

            projectFilterSelector.addActionListener(this);
        }
        return projectFilterSelector;
    }

    /**
     * @return the yearFilterSelector
     */
    private JComboBox getYearFilterSelector() {
        if (yearFilterSelector == null) {
            yearFilterList = model.getYearFilterList();
            yearFilterSelector = new JComboBox(
                    new EventComboBoxModel<LabeledItem<Integer>>(yearFilterList.getYearList())
            );
            yearFilterSelector.setToolTipText(textBundle.textFor("YearFilterSelector.ToolTipText")); //$NON-NLS-1$

            // Select first entry
            if (!CollectionUtils.isEmpty(yearFilterList.getYearList())) {
                yearFilterSelector.setSelectedIndex(0);
            }

            // Read from Settings.
            final Integer selectedYear = UserSettings.instance().getFilterSelectedYear();
            if (selectedYear != null) {
                for (LabeledItem<Integer> item : yearFilterList.getYearList()) {
                    if (item.getItem().equals(selectedYear)) {
                        yearFilterSelector.setSelectedItem(item);
                        break;
                    }
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

        // Filter for month
        LabeledItem<Integer> filterItem = (LabeledItem<Integer>) getMonthFilterSelector().getSelectedItem();
        final int selectedMonth = filterItem.getItem();

        switch (selectedMonth) {
        case MonthFilterList.CURRENT_MONTH_DUMMY:
            filter.setMonth(DateUtils.getNow());
            break;

        case MonthFilterList.ALL_MONTHS_DUMMY:
            // No filtering by month
            break;

        default:
            try {
                final DateTime month = MonthFilterList.MONTH_FORMAT.parseDateTime(
                        String.valueOf(selectedMonth)
                );
                filter.setMonth(month);
            } catch (IllegalArgumentException e) {
                log.error(e, e);
            }
            break;
        }

        // Filter for week of year
        filterItem = (LabeledItem<Integer>) getWeekOfYearFilterSelector().getSelectedItem();
        final int selectedWeekOfYear= filterItem.getItem();

        switch (selectedWeekOfYear) {
        case WeekOfYearFilterList.CURRENT_WEEK_OF_YEAR_DUMMY:
            filter.setWeekOfYear(DateUtils.getNow());
            break;

        case WeekOfYearFilterList.ALL_WEEKS_OF_YEAR_DUMMY:
            // No filtering by week of year
            break;

        default:
            try {
                final DateTime weekOfYear = WeekOfYearFilterList.WEEK_OF_YEAR_FORMAT.parseDateTime(
                        String.valueOf(selectedWeekOfYear)
                );
                filter.setWeekOfYear(weekOfYear);
            } catch (IllegalArgumentException e) {
                log.error(e, e);
            }
            break;
        }

        // Filter for year
        filterItem = (LabeledItem<Integer>) getYearFilterSelector().getSelectedItem();
        final int selectedYear = filterItem.getItem();


        switch (selectedYear) {
        case YearFilterList.CURRENT_YEAR_DUMMY:
            filter.setYear(DateUtils.getNow());
            break;

        case YearFilterList.ALL_YEARS_DUMMY:
            // No filtering by year
            break;

        default:
            try {
                final DateTime year = YearFilterList.YEAR_FORMAT.parseDateTime(
                        String.valueOf(selectedYear)
                );
                filter.setYear(year);
            } catch (IllegalArgumentException e) {
                log.error(e, e);
            }
            break;
        }

        // Filter for project
        final LabeledItem<Project> projectFilterItem = (LabeledItem<Project>) getProjectFilterSelector().getSelectedItem();
        final Project project = projectFilterItem.getItem();
        if (!ProjectFilterList.ALL_PROJECTS_DUMMY.equals(project)) {
            filter.setProject(project);
        }
        return filter;
    }

    /**
     * Stores the filter in the user settings.
     */
    @SuppressWarnings("unchecked")
    private void storeFilterInSettings() {
        // Store filter by month
        LabeledItem<Integer> filterItem = (LabeledItem<Integer>) getMonthFilterSelector().getSelectedItem();
        final int selectedMonth = filterItem.getItem();
        UserSettings.instance().setFilterSelectedMonth(selectedMonth);

        // Store filter by year
        filterItem = (LabeledItem<Integer>) getYearFilterSelector().getSelectedItem();
        final int selectedYear = filterItem.getItem();
        UserSettings.instance().setFilterSelectedYear(selectedYear);

        // Store filter by week of year
        filterItem = (LabeledItem<Integer>) getWeekOfYearFilterSelector().getSelectedItem();
        final int selectedWeekOfYear = filterItem.getItem();
        UserSettings.instance().setFilterSelectedWeekOfYear(selectedWeekOfYear);

        // Store filter by project
        final LabeledItem<Project> projectFilterItem = (LabeledItem<Project>) getProjectFilterSelector().getSelectedItem();
        final Project project = projectFilterItem.getItem();
        if (!ProjectFilterList.ALL_PROJECTS_DUMMY.equals(project)) {
            long projectId = project.getId();
            UserSettings.instance().setFilterSelectedProjectId(projectId);
        } else {
            UserSettings.instance().setFilterSelectedProjectId(
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
