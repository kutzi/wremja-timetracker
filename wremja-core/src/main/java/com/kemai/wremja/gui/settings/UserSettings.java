package com.kemai.wremja.gui.settings;

import java.awt.Dimension;
import java.awt.Point;
import java.io.File;
import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

import com.kemai.wremja.gui.lists.MonthFilterList;
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
    private static final Logger LOG = Logger.getLogger(UserSettings.class);

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
    private final Configuration userConfig;

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
            userConfig = new JUPropertiesConfiguration(userConfigFile, "Wremja user settings");
        } catch (IOException e) {
            LOG.error(e, e);
            throw new IllegalStateException("User settings couldn't be initialized", e);
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
        if (StringUtils.equals("*", selectedMonthObject)) {
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
        if (StringUtils.equals("*", selectedYearObject)) {
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

    private static final String EXPORTER_ANUKO_URL = "exporter.anuko.url";
    
    public String getAnukoUrl() {
        return doGetString(EXPORTER_ANUKO_URL, "");
    }
    
    public void setAnukoUrl(String url) {
        userConfig.setProperty(EXPORTER_ANUKO_URL, url);
    }
    
    
    //------------------------------------------------
    // Remember window size and location
    //------------------------------------------------

    /** The key for remembering window size and location. */
    public static final String REMEMBER_WINDOWSIZE_LOCATION = "settings.rememberWindowSizeLocation"; //$NON-NLS-1$

    public boolean isRememberWindowSizeLocation() {
        return doGetBoolean(REMEMBER_WINDOWSIZE_LOCATION, true);
    }

    public void setRememberWindowSizeLocation(final boolean rememberWindowSizeLocation) {
        userConfig.setProperty(REMEMBER_WINDOWSIZE_LOCATION, rememberWindowSizeLocation);
    }
    
    //------------------------------------------------
    // Window size
    //------------------------------------------------

    /** The key for the window size. */
    public static final String WINDOW_SIZE= "settings.windowSize"; //$NON-NLS-1$

    public Dimension getWindowSize() {
        final String encodedSize = doGetString(WINDOW_SIZE, "530.0|720.0");
        final String[] sizeValues = StringUtils.split(encodedSize, '|');
        
        final Dimension size = new Dimension(Double.valueOf(sizeValues[0]).intValue(), Double.valueOf(sizeValues[1]).intValue());
        return size;
    }

    public void setWindowSize(final Dimension size) {
        final String encodedSize = size.getWidth() + "|" + size.getHeight();
        userConfig.setProperty(WINDOW_SIZE, encodedSize);
    }
    
    //------------------------------------------------
    // Window location
    //------------------------------------------------

    /** The key for the window location */
    private static final String WINDOW_LOCATION = "settings.windowLocation"; //$NON-NLS-1$

    public Point getWindowLocation() {
        final String encodedLocation = doGetString(WINDOW_LOCATION, "0.0|0.0");
        final String[] locationCoordinates = StringUtils.split(encodedLocation, '|');
        
        final Point location = new Point(Double.valueOf(locationCoordinates[0]).intValue(), Double.valueOf(locationCoordinates[1]).intValue());
        return location;
    }

    public void setWindowLocation(final Point location) {
        final String encodedLocation = location.getX() + "|" + location.getY();
        userConfig.setProperty(WINDOW_LOCATION, encodedLocation);
    }
    
    private static final String WINDOW_MINIMIZED = "settings.windowMinimized"; //$NON-NLS-1$

	public void setWindowMinimized(boolean minimized) {
		userConfig.setProperty(WINDOW_MINIMIZED, minimized);
	}

	public boolean isWindowMinimized() {
		return doGetBoolean(WINDOW_MINIMIZED, false);
	}
	
    private static final String LAST_TOUCH_TIMESTAMP = "lastTouch"; //$NON-NLS-1$
    
    private static final String DISCARD_EMPTY_ACTIVITIES = "discard.emptyActivities";
    public boolean isDiscardEmptyActivities() {
        return doGetBoolean(DISCARD_EMPTY_ACTIVITIES, false);
    }
    public void setDiscardEmptyActivities(boolean b) {
        userConfig.setProperty(DISCARD_EMPTY_ACTIVITIES, b);
    }
    
    /**
     * Sets the last touch timestamp.
     */
    public void setLastTouchTimestamp(DateTime lastTouch) {
        userConfig.setProperty(LAST_TOUCH_TIMESTAMP, lastTouch.getMillis());
    }

    /**
     * Gets the last touch timestamp.
     */

    public DateTime getLastTouchTimestamp() {
        long timestamp = userConfig.getLongProperty(LAST_TOUCH_TIMESTAMP, System.currentTimeMillis());
        return new DateTime(timestamp);
    }
    
    /**
     * Restore the current filter from the user settings.
     * @return the restored filter
     */
    public Filter restoreFromSettings() {
        final Filter filter = new Filter();

        // Restore the month
        restoreMonthFilter(filter);

        // Restore the year
        restoreYearFilter(filter);
        
        // Restore the week of the year
        restoreWeekOfYearFilter(filter);

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

        filter.setYear(selectedYear.intValue());
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

        filter.setMonth(selectedMonth.intValue());
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
        
        filter.setWeekOfYear(selectedWeekOfYear.intValue());
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
            LOG.error(e, e);
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
            LOG.error(e, e);
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
            LOG.error(e, e);
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
    private boolean doGetBoolean(final String key, final boolean defaultValue) {
        try {
            return userConfig.getBooleanProperty(key, defaultValue);
        } catch (Exception e) {
            LOG.error(e, e);
            return defaultValue;
        }
    }
}
