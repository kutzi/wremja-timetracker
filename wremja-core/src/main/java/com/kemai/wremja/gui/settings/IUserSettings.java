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
    public void setLastExcelExportLocation(final String excelExportLocation);

    /**
     * Gets the location of the last Data export.
     * @return the location of the last Data export
     */
    public String getLastDataExportLocation();

    /**
     * Sets the location of the last Data export.
     * @param dataExportLocation the location of the last data export to set
     */
    public void setLastDataExportLocation(final String dataExportLocation);

    /**
     * Gets the location of the last Csv export.
     * @return the location of the last Csv export
     */
    public String getLastCsvExportLocation();

    /**
     * Sets the location of the last Csv export.
     * @param csvExportLocation the location of the last Csv export to set
     */
    public void setLastCsvExportLocation(final String csvExportLocation);

    public String getLastDescription();

    public void setLastDescription(final String lastDescription);

    public int getFilterSelectedMonth(int defaultValue);

    public void setFilterSelectedMonth(final int month);

    public int getFilterSelectedWeekOfYear(int defaultValue);

    public void setFilterSelectedWeekOfYear(final int weekOfYear);

    public int getFilterSelectedYear(int defaultValue);

    public void setFilterSelectedYear(final int year);

    public long getFilterSelectedProjectId(long defaultValue);

    public void setFilterSelectedProjectId(final long projectId);

    public String getShownCategory();

    public void setShownCategory(final String shownCategory);

    public String getAnukoLogin();

    public void setAnukoLogin(String login);

    public String getAnukoPassword();

    public void setAnukoPassword(String password);

    public String getAnukoUrl();

    public void setAnukoUrl(String url);

    public boolean isRememberWindowSizeLocation();

    public void setRememberWindowSizeLocation(
            final boolean rememberWindowSizeLocation);

    public Dimension getWindowSize();

    public void setWindowSize(final Dimension size);

    public Point getWindowLocation();

    public void setWindowLocation(final Point location);

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

}