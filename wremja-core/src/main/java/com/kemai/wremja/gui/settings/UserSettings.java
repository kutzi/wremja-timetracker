package com.kemai.wremja.gui.settings;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

import com.kemai.util.DateUtils;
import com.kemai.wremja.gui.lists.MonthFilterList;
import com.kemai.wremja.gui.lists.WeekOfYearFilterList;
import com.kemai.wremja.gui.lists.YearFilterList;
import com.kemai.wremja.logging.Logger;
import com.kemai.wremja.model.filter.Filter;

/**
 * Stores and reads all settings specific to one user.
 * @author remast
 * @author kutzi
 */
public final class UserSettings {

    /** The logger. */
    private static final Logger log = Logger.getLogger(UserSettings.class);

    /** Default name of the ActivityRepository data file. */
    public static final String DEFAULT_FILE_NAME = "ProTrack.ptd"; //$NON-NLS-1$

    /**
     * Get the location of the data file.
     * @return the path of the data file
     */
    public String getDataFileLocation() {
        return ApplicationSettings.instance().getApplicationDataDirectory().getPath() + File.separator + DEFAULT_FILE_NAME;
    }

    /** Key for the name of the user properties file. */
    private static String USER_PROPERTIES_FILENAME = "wremja.properties";

    /** Node for Wremja user preferences. */
    private Configuration userConfig;

    /** The singleton instance. */
    private static final UserSettings instance = new UserSettings();

    /**
     * Getter for singleton instance.
     * @return the settings singleton
     */
    public static UserSettings instance() {
        return instance;
    }

    /**
     * Constructor for the settings.
     */
    private UserSettings() {
        final File userConfigFile = new File(ApplicationSettings.instance().getApplicationDataDirectory() + File.separator + USER_PROPERTIES_FILENAME);
        try {
            userConfig = new JUPropertiesConfiguration(userConfigFile);
        } catch (IOException e) {
            log.error(e, e);
        }
    }

    //------------------------------------------------
    // Lock file Location
    //------------------------------------------------

    /** Name of the lock file. */
    private static final String LOCK_FILE_NAME = "lock"; //$NON-NLS-1$

    /** 
     * Gets the location of the lock file.
     * @return the location of the lock file
     */
    public static String getLockFileLocation() {
        return ApplicationSettings.instance().getApplicationDataDirectory() + File.separator + LOCK_FILE_NAME;
    }

    //------------------------------------------------
    // Excel Export Location
    //------------------------------------------------

    /** Key for the location of last Excel export. */
    private static final String LAST_EXCEL_EXPORT_LOCATION = "export.excel"; //$NON-NLS-1$

    /**
     * Gets the location of the last Excel export.
     * @return the location of the last Excel export
     */
    public String getLastExcelExportLocation() {
        return doGetString(LAST_EXCEL_EXPORT_LOCATION, System.getProperty("user.home"));
    }

    /**
     * Sets the location of the last Excel export.
     * @param excelExportLocation the location of the last Excel export to set
     */
    public void setLastExcelExportLocation(final String excelExportLocation) {
        userConfig.setProperty(LAST_EXCEL_EXPORT_LOCATION, excelExportLocation);
    }

    //------------------------------------------------
    // Data Export Location
    //------------------------------------------------

    /** Key for the location of last Data export. */
    private static final String LAST_DATA_EXPORT_LOCATION = "export.data"; //$NON-NLS-1$

    /**
     * Gets the location of the last Data export.
     * @return the location of the last Data export
     */
    public String getLastDataExportLocation() {
        return doGetString(LAST_DATA_EXPORT_LOCATION, System.getProperty("user.home"));
    }

    /**
     * Sets the location of the last Data export.
     * @param dataExportLocation the location of the last data export to set
     */
    public void setLastDataExportLocation(final String dataExportLocation) {
        userConfig.setProperty(LAST_DATA_EXPORT_LOCATION, dataExportLocation);
    }

    //------------------------------------------------
    // Csv Export Location
    //------------------------------------------------

    /** Key for the location of last Csv export. */
    private static final String LAST_CSV_EXPORT_LOCATION = "export.csv"; //$NON-NLS-1$

    /**
     * Gets the location of the last Csv export.
     * @return the location of the last Csv export
     */
    public String getLastCsvExportLocation() {
        return doGetString(LAST_CSV_EXPORT_LOCATION, System.getProperty("user.home"));
    }

    /**
     * Sets the location of the last Csv export.
     * @param csvExportLocation the location of the last Csv export to set
     */
    public void setLastCsvExportLocation(final String csvExportLocation) {
        userConfig.setProperty(LAST_CSV_EXPORT_LOCATION, csvExportLocation);
    }

    //------------------------------------------------
    // Description
    //------------------------------------------------

    /** Last description. */
    private static final String LAST_DESCRIPTION = "description"; //$NON-NLS-1$

    public String getLastDescription() {
        return doGetString(LAST_DESCRIPTION, StringUtils.EMPTY);
    }

    public void setLastDescription(final String lastDescription) {
        userConfig.setProperty(LAST_DESCRIPTION, lastDescription);
    }

    //------------------------------------------------
    // Filter Settings
    //------------------------------------------------

    /** The key for the selected month of filter. */
    private static final String SELECTED_MONTH = "filter.month"; //$NON-NLS-1$

    public Integer getFilterSelectedMonth() {
        // Avoid ConversionException by checking the type of the property
        final String selectedMonthObject = userConfig.getStringProperty(SELECTED_MONTH);
        
        // -- 
        // :INFO: Migrate from < 1.3 where * was used as dummy value
        if (StringUtils.equals("*", (String) selectedMonthObject)) {
            setFilterSelectedMonth(MonthFilterList.ALL_MONTHS_DUMMY);
        }
        // --
        return doGetInteger(SELECTED_MONTH, null);
    }
    
    public void setFilterSelectedMonth(final Integer month) {
        userConfig.setProperty(SELECTED_MONTH, month);
    }

    /** The key for the selected week of filter. */
    private static final String SELECTED_WEEK_OF_YEAR = "filter.weekOfYear"; //$NON-NLS-1$

    public Integer getFilterSelectedWeekOfYear() {
        return doGetInteger(SELECTED_WEEK_OF_YEAR, null);
    }

    public void setFilterSelectedWeekOfYear(final Integer weekOfYear) {
        userConfig.setProperty(SELECTED_WEEK_OF_YEAR, weekOfYear);
    }

    /** The key for the selected year of filter. */
    private static final String SELECTED_YEAR = "filter.year"; //$NON-NLS-1$

    public Integer getFilterSelectedYear() {
        // Avoid ConversionException by checking the type of the property
        final String selectedYearObject = userConfig.getStringProperty(SELECTED_YEAR);
        
        // -- 
        // :INFO: Migrate from < 1.3 where * was used as dummy value
        if (StringUtils.equals("*", (String) selectedYearObject)) {
            setFilterSelectedYear(YearFilterList.ALL_YEARS_DUMMY);
        }
        // -- 
        return doGetInteger(SELECTED_YEAR, null);
    }

    public void setFilterSelectedYear(final Integer year) {
        userConfig.setProperty(SELECTED_YEAR, year);
    }

    /** The key for the selected project id of filter. */
    private static final String SELECTED_PROJECT_ID = "filter.projectId"; //$NON-NLS-1$

    public Long getFilterSelectedProjectId() {
        return doGetLong(SELECTED_PROJECT_ID, null);
    }

    public void setFilterSelectedProjectId(final long projectId) {
        userConfig.setProperty(SELECTED_PROJECT_ID, Long.valueOf(projectId));
    }

    //------------------------------------------------
    // Shown category
    //------------------------------------------------

    /** The key for the shown category. */
    public static final String SHOWN_CATEGORY = "shown.category"; //$NON-NLS-1$

    public String getShownCategory() {
        return doGetString(SHOWN_CATEGORY, "General");
    }

    public void setShownCategory(final String shownCategory) {
        userConfig.setProperty(SHOWN_CATEGORY, shownCategory);
    }
    
    private static final String EXPORTER_ANUKO_LOGIN = "exporter.anuko.login";
    
    public String getAnukoLogin() {
        return doGetString(EXPORTER_ANUKO_LOGIN, "");
    }
    
    public void setAnukoLogin(String login) {
        userConfig.setProperty(EXPORTER_ANUKO_LOGIN, login);
    }
    
    private static final String EXPORTER_ANUKO_PASSWORD = "exporter.anuko.password";
    
    public String getAnukoPassword() {
        return doGetString(EXPORTER_ANUKO_PASSWORD, "");
    }
    
    public void setAnukoPassword(String password) {
        userConfig.setProperty(EXPORTER_ANUKO_PASSWORD, password);
    }

    /**
     * Restore the current filter from the user settings.
     * @return the restored filter
     */
    public Filter restoreFromSettings() {
        final Filter filter = new Filter();

        // Restore the week of the year
        restoreWeekOfYearFilter(filter);

        // Restore the month
        restoreMonthFilter(filter);

        // Restore the year
        restoreYearFilter(filter);

        return filter;
    }

    /**
     * Restores the filter for the year.
     * @param filter the restored filter
     */
    private void restoreYearFilter(final Filter filter) {
        final Integer selectedYear = UserSettings.instance().getFilterSelectedYear();

        if (selectedYear == null) {
            return;
        }

        if (selectedYear == YearFilterList.CURRENT_YEAR_DUMMY) {
            filter.setYear(DateUtils.getNow());
            return;
        } 

        if (selectedYear != YearFilterList.ALL_YEARS_DUMMY) {
            try {
                DateTime year = new DateTime().withYear(selectedYear);
                filter.setYear(year);
            } catch (NumberFormatException e) {
                log.error(e, e);
            }
        }
    }

    /**
     * Restores the filter for the month.
     * @param filter the restored filter
     */
    private void restoreMonthFilter(final Filter filter) {
        final Integer selectedMonth = getFilterSelectedMonth();

        if (selectedMonth == null) {
            return;
        }

        if (selectedMonth == MonthFilterList.CURRENT_MONTH_DUMMY) {
            filter.setMonth(DateUtils.getNow());
            return;
        } 

        if (selectedMonth != MonthFilterList.ALL_MONTHS_DUMMY) {
            try {
                DateTime month = new DateTime().withMonthOfYear(selectedMonth);
                filter.setMonth(month);
            } catch (NumberFormatException e) {
                log.error(e, e);
            }
        }
    }

    /**
     * Restores the filter for the week of year.
     * @param filter the restored filter
     */
    private void restoreWeekOfYearFilter(final Filter filter) {
        final Integer selectedWeekOfYear = getFilterSelectedWeekOfYear();

        if (selectedWeekOfYear == null) {
            return;
        }

        if (selectedWeekOfYear == WeekOfYearFilterList.CURRENT_WEEK_OF_YEAR_DUMMY) {
            filter.setWeekOfYear(DateUtils.getNow());
            return;
        } 

        if (selectedWeekOfYear != WeekOfYearFilterList.ALL_WEEKS_OF_YEAR_DUMMY) {
            try {
                DateTime weekOfYear = new DateTime().withWeekOfWeekyear(selectedWeekOfYear);
                filter.setWeekOfYear(weekOfYear);
            } catch (NumberFormatException e) {
                log.error(e, e);
            }
        }
    }

    //------------------------------------------------
    // Helper methods
    //------------------------------------------------

    /**
     * Getter that handle errors gracefully meaning errors are logged 
     * but applications continues with the default value.
     * @param key the key of the property to get
     * @param defaultValue the default value of the property to get
     * @return the property value if set and correct otherwise the default value
     */
    private String doGetString(final String key, final String defaultValue) {
        try {
            return userConfig.getStringProperty(key, defaultValue);
        } catch (Exception e) {
            log.error(e, e);
            return defaultValue;
        }
    }

    /**
     * Getter that handle errors gracefully meaning errors are logged 
     * but applications continues with the default value.
     * @param key the key of the property to get
     * @param defaultValue the default value of the property to get
     * @return the property value if set and correct otherwise the default value
     */
    private Long doGetLong(final String key, final Long defaultValue) {
        try {
            return userConfig.getLongProperty(key, defaultValue);
        } catch (Exception e) {
            log.error(e, e);
            return defaultValue;
        }
    }

    /**
     * Getter that handle errors gracefully meaning errors are logged 
     * but applications continues with the default value.
     * @param key the key of the property to get
     * @param defaultValue the default value of the property to get
     * @return the property value if set and correct otherwise the default value
     */
    private Integer doGetInteger(final String key, final Integer defaultValue) {
        try {
            return userConfig.getIntegerProperty(key, defaultValue);
        } catch (Exception e) {
            log.error(e, e);
            return defaultValue;
        }
    }
}
