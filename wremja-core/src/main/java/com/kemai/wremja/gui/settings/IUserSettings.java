package com.kemai.wremja.gui.settings;

import java.awt.Dimension;
import java.awt.Point;

import com.kemai.wremja.model.filter.Filter;

public interface IUserSettings {

    /**
     * Get the location of the data file.
     * @return the path of the data file
     */
    public String getDataFileLocation();

    /**
     * Gets the location of the last Excel export.
     * @return the location of the last Excel export
     */
    public String getLastExcelExportLocation();

    /**
     * Sets the location of the last Excel export.
     * @param excelExportLocation the location of the last Excel export to set
     */
    public void setLastExcelExportLocation(String excelExportLocation);

    /**
     * Gets the location of the last Data export.
     * @return the location of the last Data export
     */
    public String getLastDataExportLocation();

    /**
     * Sets the location of the last Data export.
     * @param dataExportLocation the location of the last data export to set
     */
    public void setLastDataExportLocation(String dataExportLocation);

    /**
     * Gets the location of the last Csv export.
     * @return the location of the last Csv export
     */
    public String getLastCsvExportLocation();

    /**
     * Sets the location of the last Csv export.
     * @param csvExportLocation the location of the last Csv export to set
     */
    public void setLastCsvExportLocation(String csvExportLocation);

    public String getLastDescription();

    public void setLastDescription(String lastDescription);

    public int getFilterSelectedMonth(int defaultValue);

    public void setFilterSelectedMonth(int month);

    public int getFilterSelectedWeekOfYear(int defaultValue);

    public void setFilterSelectedWeekOfYear(int weekOfYear);
    
    public int getFilterSelectedDayOfWeek(int defaultValue);

    public void setFilterSelectedDayOfWeek(int selectedDayOfWeek);

    public int getFilterSelectedYear(int defaultValue);

    public void setFilterSelectedYear(int year);

    public long getFilterSelectedProjectId(long defaultValue);

    public void setFilterSelectedProjectId(long projectId);

    /**
     * @deprecated There's no such thing anymore
     */
    public String getShownCategory();

    /**
     * @deprecated There's no such thing anymore
     */
    public void setShownCategory(String shownCategory);

    public String getAnukoLogin();

    public void setAnukoLogin(String login);

    public String getAnukoPassword();

    public void setAnukoPassword(String password);

    public String getAnukoUrl();

    public void setAnukoUrl(String url);
    
    /**
     * Returns the mappings from wremja to anuko projects.
     */
    public String getAnukoMappings();
    
    public void setAnukoMappings(String s);

    public boolean isRememberWindowSizeLocation();

    public void setRememberWindowSizeLocation(boolean rememberWindowSizeLocation);

    public Dimension getWindowSize();

    public void setWindowSize(Dimension size);

    public Point getWindowLocation();

    public void setWindowLocation(Point location);

    public void setWindowMinimized(boolean minimized);

    public boolean isWindowMinimized();

    public boolean isDiscardEmptyActivities();

    public void setDiscardEmptyActivities(boolean b);

    public boolean isUseTrayIcon();

    public void setUseTrayIcon(boolean b);

    public String getDurationFormat();

    public void setDurationFormat(String format);

    public boolean isAllowOverlappingActivities();

    public void setAllowOverlappingActivities(boolean allow);

    /**
     * Restore the current filter from the user settings.
     * @return the restored filter
     */
    public Filter restoreFromSettings();

    /**
     * Returns the order of the tabs in the main tabbed pane.
     */
    int[] getMainTabpaneOrder();

    /**
     * Sets the order of the tabs in the main tabbed pane.
     */
    void setMainTabpaneOrder(int[] order);

    boolean getBooleanProperty(String key, boolean defaultValue);

    void setBooleanProperty(String key, boolean value);
}