package com.kemai.wremja.gui.settings;

import java.awt.Dimension;
import java.awt.Point;
import java.io.File;
import java.io.IOException;

import org.apache.commons.lang.StringUtils;

import com.kemai.util.OSUtils;
import com.kemai.wremja.gui.lists.MonthFilterList;
import com.kemai.wremja.gui.lists.YearFilterList;
import com.kemai.wremja.logging.Logger;
import com.kemai.wremja.model.ActivityRepository;
import com.kemai.wremja.model.filter.Filter;

/**
 * Stores and reads all settings specific to one user.
 * @author remast
 * @author kutzi
 */
public final class UserSettings implements IUserSettings {

    private static final Logger LOG = Logger.getLogger(UserSettings.class);

    /** Default name of the {@link ActivityRepository} data file. */
    public static final String DEFAULT_FILE_NAME = "ProTrack.ptd"; //$NON-NLS-1$

    /* (non-Javadoc)
     * @see com.kemai.wremja.gui.settings.IUserSettings#getDataFileLocation()
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
     * @deprecated please use an injected {@link IUserSettings} instead!
     */
    @Deprecated
    public static UserSettings instance() {
        return instance;
    }

    /**
     * Constructor for the settings.
     */
    private UserSettings() {
        final File userConfigFile = new File(ApplicationSettings.instance().getApplicationDataDirectory(), USER_PROPERTIES_FILENAME);
        try {
            userConfig = new JUPropertiesConfiguration(userConfigFile, "Wremja user settings");
        } catch (IOException e) {
            LOG.error(e, e);
            throw new IllegalStateException("User settings couldn't be initialized", e);
        }
    }
    
    /**
     * @deprecated use this only in tests!
     */
    public void setConfig(Configuration config) {
    	this.userConfig = config;
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

    /* (non-Javadoc)
     * @see com.kemai.wremja.gui.settings.IUserSettings#getLastExcelExportLocation()
     */
    public String getLastExcelExportLocation() {
        return doGetString(LAST_EXCEL_EXPORT_LOCATION, null);
    }

    /* (non-Javadoc)
     * @see com.kemai.wremja.gui.settings.IUserSettings#setLastExcelExportLocation(java.lang.String)
     */
    public void setLastExcelExportLocation(final String excelExportLocation) {
        userConfig.setProperty(LAST_EXCEL_EXPORT_LOCATION, excelExportLocation);
    }

    //------------------------------------------------
    // Data Export Location
    //------------------------------------------------

    /** Key for the location of last Data export. */
    private static final String LAST_DATA_EXPORT_LOCATION = "export.data"; //$NON-NLS-1$

    /* (non-Javadoc)
     * @see com.kemai.wremja.gui.settings.IUserSettings#getLastDataExportLocation()
     */
    public String getLastDataExportLocation() {
        return doGetString(LAST_DATA_EXPORT_LOCATION, null);
    }

    /* (non-Javadoc)
     * @see com.kemai.wremja.gui.settings.IUserSettings#setLastDataExportLocation(java.lang.String)
     */
    public void setLastDataExportLocation(final String dataExportLocation) {
        userConfig.setProperty(LAST_DATA_EXPORT_LOCATION, dataExportLocation);
    }

    //------------------------------------------------
    // Csv Export Location
    //------------------------------------------------

    /** Key for the location of last Csv export. */
    private static final String LAST_CSV_EXPORT_LOCATION = "export.csv"; //$NON-NLS-1$

    /* (non-Javadoc)
     * @see com.kemai.wremja.gui.settings.IUserSettings#getLastCsvExportLocation()
     */
    public String getLastCsvExportLocation() {
        return doGetString(LAST_CSV_EXPORT_LOCATION, null);
    }

    /* (non-Javadoc)
     * @see com.kemai.wremja.gui.settings.IUserSettings#setLastCsvExportLocation(java.lang.String)
     */
    public void setLastCsvExportLocation(final String csvExportLocation) {
        userConfig.setProperty(LAST_CSV_EXPORT_LOCATION, csvExportLocation);
    }

    //------------------------------------------------
    // Description
    //------------------------------------------------

    /** Last description. */
    private static final String LAST_DESCRIPTION = "description"; //$NON-NLS-1$

    /* (non-Javadoc)
     * @see com.kemai.wremja.gui.settings.IUserSettings#getLastDescription()
     */
    public String getLastDescription() {
        return doGetString(LAST_DESCRIPTION, "");
    }

    /* (non-Javadoc)
     * @see com.kemai.wremja.gui.settings.IUserSettings#setLastDescription(java.lang.String)
     */
    public void setLastDescription(final String lastDescription) {
        userConfig.setProperty(LAST_DESCRIPTION, lastDescription);
    }

    //------------------------------------------------
    // Filter Settings
    //------------------------------------------------

    /** The key for the selected month of filter. */
    private static final String SELECTED_MONTH = "filter.month"; //$NON-NLS-1$

    /* (non-Javadoc)
     * @see com.kemai.wremja.gui.settings.IUserSettings#getFilterSelectedMonth(int)
     */
    public int getFilterSelectedMonth(int defaultValue) {
        // Avoid ConversionException by checking the type of the property
        final String selectedMonthObject = userConfig.getStringProperty(SELECTED_MONTH);
        
        // -- 
        // :INFO: Migrate from < Baralga 1.3 where * was used as dummy value
        if (StringUtils.equals("*", selectedMonthObject)) {
            setFilterSelectedMonth(MonthFilterList.ALL_MONTHS_DUMMY);
        }
        // --
        return doGetInteger(SELECTED_MONTH, defaultValue);
    }
    
    /* (non-Javadoc)
     * @see com.kemai.wremja.gui.settings.IUserSettings#setFilterSelectedMonth(int)
     */
    public void setFilterSelectedMonth(final int month) {
        userConfig.setProperty(SELECTED_MONTH, month);
    }

    /** The key for the selected week of filter. */
    private static final String SELECTED_WEEK_OF_YEAR = "filter.weekOfYear"; //$NON-NLS-1$

    /* (non-Javadoc)
     * @see com.kemai.wremja.gui.settings.IUserSettings#getFilterSelectedWeekOfYear(int)
     */
    public int getFilterSelectedWeekOfYear(int defaultValue) {
        return doGetInteger(SELECTED_WEEK_OF_YEAR, defaultValue);
    }

    /* (non-Javadoc)
     * @see com.kemai.wremja.gui.settings.IUserSettings#setFilterSelectedWeekOfYear(int)
     */
    public void setFilterSelectedWeekOfYear(final int weekOfYear) {
        userConfig.setProperty(SELECTED_WEEK_OF_YEAR, weekOfYear);
    }

    /** The key for the selected year of filter. */
    private static final String SELECTED_YEAR = "filter.year"; //$NON-NLS-1$

    /* (non-Javadoc)
     * @see com.kemai.wremja.gui.settings.IUserSettings#getFilterSelectedYear(int)
     */
    public int getFilterSelectedYear(int defaultValue) {
        // Avoid ConversionException by checking the type of the property
        final String selectedYearObject = userConfig.getStringProperty(SELECTED_YEAR);
        
        // -- 
        // :INFO: Migrate from < 1.3 where * was used as dummy value
        if (StringUtils.equals("*", selectedYearObject)) {
            setFilterSelectedYear(YearFilterList.ALL_YEARS_DUMMY);
        }
        // -- 
        return doGetInteger(SELECTED_YEAR, defaultValue);
    }

    /* (non-Javadoc)
     * @see com.kemai.wremja.gui.settings.IUserSettings#setFilterSelectedYear(int)
     */
    public void setFilterSelectedYear(final int year) {
        userConfig.setProperty(SELECTED_YEAR, year);
    }

    /** The key for the selected project id of filter. */
    private static final String SELECTED_PROJECT_ID = "filter.projectId"; //$NON-NLS-1$

    /* (non-Javadoc)
     * @see com.kemai.wremja.gui.settings.IUserSettings#getFilterSelectedProjectId(long)
     */
    public long getFilterSelectedProjectId(long defaultValue) {
        return doGetLong(SELECTED_PROJECT_ID, defaultValue);
    }

    /* (non-Javadoc)
     * @see com.kemai.wremja.gui.settings.IUserSettings#setFilterSelectedProjectId(long)
     */
    public void setFilterSelectedProjectId(final long projectId) {
        userConfig.setProperty(SELECTED_PROJECT_ID, projectId);
    }

    //------------------------------------------------
    // Shown category
    //------------------------------------------------

    /** The key for the shown category. */
    public static final String SHOWN_CATEGORY = "shown.category"; //$NON-NLS-1$

    /* (non-Javadoc)
     * @see com.kemai.wremja.gui.settings.IUserSettings#getShownCategory()
     */
    public String getShownCategory() {
        return doGetString(SHOWN_CATEGORY, "General");
    }

    /* (non-Javadoc)
     * @see com.kemai.wremja.gui.settings.IUserSettings#setShownCategory(java.lang.String)
     */
    public void setShownCategory(final String shownCategory) {
        userConfig.setProperty(SHOWN_CATEGORY, shownCategory);
    }
    
    private static final String EXPORTER_ANUKO_LOGIN = "exporter.anuko.login";
    
    /* (non-Javadoc)
     * @see com.kemai.wremja.gui.settings.IUserSettings#getAnukoLogin()
     */
    public String getAnukoLogin() {
        return doGetString(EXPORTER_ANUKO_LOGIN, "");
    }
    
    /* (non-Javadoc)
     * @see com.kemai.wremja.gui.settings.IUserSettings#setAnukoLogin(java.lang.String)
     */
    public void setAnukoLogin(String login) {
        userConfig.setProperty(EXPORTER_ANUKO_LOGIN, login);
    }
    
    private static final String EXPORTER_ANUKO_PASSWORD = "exporter.anuko.password";
    
    /* (non-Javadoc)
     * @see com.kemai.wremja.gui.settings.IUserSettings#getAnukoPassword()
     */
    public String getAnukoPassword() {
        return doGetString(EXPORTER_ANUKO_PASSWORD, "");
    }
    
    /* (non-Javadoc)
     * @see com.kemai.wremja.gui.settings.IUserSettings#setAnukoPassword(java.lang.String)
     */
    public void setAnukoPassword(String password) {
        userConfig.setProperty(EXPORTER_ANUKO_PASSWORD, password);
    }

    private static final String EXPORTER_ANUKO_URL = "exporter.anuko.url";
    
    /* (non-Javadoc)
     * @see com.kemai.wremja.gui.settings.IUserSettings#getAnukoUrl()
     */
    public String getAnukoUrl() {
        return doGetString(EXPORTER_ANUKO_URL, "");
    }
    
    /* (non-Javadoc)
     * @see com.kemai.wremja.gui.settings.IUserSettings#setAnukoUrl(java.lang.String)
     */
    public void setAnukoUrl(String url) {
        userConfig.setProperty(EXPORTER_ANUKO_URL, url);
    }

    private static final String EXPORTER_ANUKO_MAPPINGS = "exporter.anuko.mappings";
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getAnukoMappings() {
        return doGetString(EXPORTER_ANUKO_MAPPINGS, "");
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setAnukoMappings(String s) {
        userConfig.setProperty(EXPORTER_ANUKO_MAPPINGS, s);
    }
    
    //------------------------------------------------
    // Remember window size and location
    //------------------------------------------------

    /** The key for remembering window size and location. */
    public static final String REMEMBER_WINDOWSIZE_LOCATION = "settings.rememberWindowSizeLocation"; //$NON-NLS-1$

    /* (non-Javadoc)
     * @see com.kemai.wremja.gui.settings.IUserSettings#isRememberWindowSizeLocation()
     */
    public boolean isRememberWindowSizeLocation() {
        return doGetBoolean(REMEMBER_WINDOWSIZE_LOCATION, true);
    }

    /* (non-Javadoc)
     * @see com.kemai.wremja.gui.settings.IUserSettings#setRememberWindowSizeLocation(boolean)
     */
    public void setRememberWindowSizeLocation(final boolean rememberWindowSizeLocation) {
        userConfig.setProperty(REMEMBER_WINDOWSIZE_LOCATION, rememberWindowSizeLocation);
    }
    
    //------------------------------------------------
    // Window size
    //------------------------------------------------

    /** The key for the window size. */
    public static final String WINDOW_SIZE= "settings.windowSize"; //$NON-NLS-1$

    /* (non-Javadoc)
     * @see com.kemai.wremja.gui.settings.IUserSettings#getWindowSize()
     */
    public Dimension getWindowSize() {
        final String encodedSize = doGetString(WINDOW_SIZE, "530.0|720.0");
        final String[] sizeValues = StringUtils.split(encodedSize, '|');
        
        final Dimension size = new Dimension(Double.valueOf(sizeValues[0]).intValue(), Double.valueOf(sizeValues[1]).intValue());
        return size;
    }

    /* (non-Javadoc)
     * @see com.kemai.wremja.gui.settings.IUserSettings#setWindowSize(java.awt.Dimension)
     */
    public void setWindowSize(final Dimension size) {
        final String encodedSize = size.getWidth() + "|" + size.getHeight();
        userConfig.setProperty(WINDOW_SIZE, encodedSize);
    }
    
    //------------------------------------------------
    // Window location
    //------------------------------------------------

    /** The key for the window location */
    private static final String WINDOW_LOCATION = "settings.windowLocation"; //$NON-NLS-1$

    /* (non-Javadoc)
     * @see com.kemai.wremja.gui.settings.IUserSettings#getWindowLocation()
     */
    public Point getWindowLocation() {
        final String encodedLocation = doGetString(WINDOW_LOCATION, "0.0|0.0");
        final String[] locationCoordinates = StringUtils.split(encodedLocation, '|');
        
        final Point location = new Point(Double.valueOf(locationCoordinates[0]).intValue(), Double.valueOf(locationCoordinates[1]).intValue());
        return location;
    }

    /* (non-Javadoc)
     * @see com.kemai.wremja.gui.settings.IUserSettings#setWindowLocation(java.awt.Point)
     */
    public void setWindowLocation(final Point location) {
        final String encodedLocation = location.getX() + "|" + location.getY();
        userConfig.setProperty(WINDOW_LOCATION, encodedLocation);
    }
    
    private static final String WINDOW_MINIMIZED = "settings.windowMinimized"; //$NON-NLS-1$

	/* (non-Javadoc)
     * @see com.kemai.wremja.gui.settings.IUserSettings#setWindowMinimized(boolean)
     */
	public void setWindowMinimized(boolean minimized) {
		userConfig.setProperty(WINDOW_MINIMIZED, minimized);
	}

	/* (non-Javadoc)
     * @see com.kemai.wremja.gui.settings.IUserSettings#isWindowMinimized()
     */
	public boolean isWindowMinimized() {
		return doGetBoolean(WINDOW_MINIMIZED, false);
	}
	
    private static final String DISCARD_EMPTY_ACTIVITIES = "discard.emptyActivities";
    /* (non-Javadoc)
     * @see com.kemai.wremja.gui.settings.IUserSettings#isDiscardEmptyActivities()
     */
    public boolean isDiscardEmptyActivities() {
        return doGetBoolean(DISCARD_EMPTY_ACTIVITIES, false);
    }
    /* (non-Javadoc)
     * @see com.kemai.wremja.gui.settings.IUserSettings#setDiscardEmptyActivities(boolean)
     */
    public void setDiscardEmptyActivities(boolean b) {
        userConfig.setProperty(DISCARD_EMPTY_ACTIVITIES, b);
    }
    
    private static final String USE_TRAY_ICON = "gui.useTrayIcon";
    /* (non-Javadoc)
     * @see com.kemai.wremja.gui.settings.IUserSettings#isUseTrayIcon()
     */
    public boolean isUseTrayIcon() {
    	boolean def = !OSUtils.isGnome(); // tray icon issues under Gnome
    	return doGetBoolean(USE_TRAY_ICON, def);
    }
    
    /* (non-Javadoc)
     * @see com.kemai.wremja.gui.settings.IUserSettings#setUseTrayIcon(boolean)
     */
    public void setUseTrayIcon(boolean b) {
    	userConfig.setProperty(USE_TRAY_ICON, b);
    }
    
    private static final String DURATION_FORMAT = "durationFormat";
    
    /* (non-Javadoc)
     * @see com.kemai.wremja.gui.settings.IUserSettings#getDurationFormat()
     */
    public String getDurationFormat() {
        return doGetString(DURATION_FORMAT, "#0.00");
    }
    
    /* (non-Javadoc)
     * @see com.kemai.wremja.gui.settings.IUserSettings#setDurationFormat(java.lang.String)
     */
    public void setDurationFormat(String format) {
        userConfig.setProperty(DURATION_FORMAT, format);
    } 
    
    private static final String ALLOW_OVERLAPPING_ACTIVITIES = "allow.overlapping.activities";
    
    /* (non-Javadoc)
     * @see com.kemai.wremja.gui.settings.IUserSettings#isAllowOverlappingActivities()
     */
    @Override
    public boolean isAllowOverlappingActivities() {
    	return doGetBoolean(ALLOW_OVERLAPPING_ACTIVITIES, true);
    }
    
    /* (non-Javadoc)
     * @see com.kemai.wremja.gui.settings.IUserSettings#setAllowOverlappingActivities(boolean)
     */
    @Override
    public void setAllowOverlappingActivities(boolean allow) {
    	userConfig.setProperty(ALLOW_OVERLAPPING_ACTIVITIES, allow);
    }
    
    private static final String MAIN_TABPANE_ORDER = "main.tabpane.order";
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int[] getMainTabpaneOrder() {
        String order = doGetString(MAIN_TABPANE_ORDER, "");
        if (StringUtils.isBlank(order)) {
            return new int[0];
        }

        String[] split = order.split(",");
        int[] result = new int[split.length];
        
        try {
            for (int i = 0; i < split.length; i++) {
                result[i] = Integer.parseInt(split[i]);
            }
            return result;
        } catch (NumberFormatException e) {
            LOG.warn("", e);
            return new int[0];
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setMainTabpaneOrder(int[] order) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (int i : order) {
            if (!first) {
                sb.append(",");
            } else {
                first = false;
            }
            sb.append(i);
        }
        userConfig.setProperty(MAIN_TABPANE_ORDER, sb.toString());
    }
    
    /* (non-Javadoc)
     * @see com.kemai.wremja.gui.settings.IUserSettings#restoreFromSettings()
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
        final int selectedYear = getFilterSelectedYear(YearFilterList.ALL_YEARS_DUMMY);

        filter.setYear(selectedYear);
    }

    /**
     * Restores the filter for the month.
     * @param filter the restored filter
     */
    private void restoreMonthFilter(final Filter filter) {
        final int selectedMonth = getFilterSelectedMonth(MonthFilterList.ALL_MONTHS_DUMMY);

        filter.setMonth(selectedMonth);
    }

    /**
     * Restores the filter for the week of year.
     * @param filter the restored filter
     */
    private void restoreWeekOfYearFilter(final Filter filter) {
        final int selectedWeekOfYear = getFilterSelectedWeekOfYear(YearFilterList.ALL_YEARS_DUMMY);

        filter.setWeekOfYear(selectedWeekOfYear);
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
    private long doGetLong(final String key, final long defaultValue) {
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
    private int doGetInteger(final String key, final int defaultValue) {
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
