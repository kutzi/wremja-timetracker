package com.kemai.wremja.gui.settings;

import java.awt.Dimension;
import java.awt.Point;

import com.kemai.wremja.model.filter.Filter;

/**
 * Empty implementation of {@link IUserSettings}.
 *
 * @author kutzi
 */
// TODO: this is a test support class and should therefore be moved into a test module
public class UserSettingsAdapter implements IUserSettings {

    @Override
    public String getAnukoLogin() {
        return null;
    }

    @Override
    public String getAnukoPassword() {
        return null;
    }

    @Override
    public String getAnukoUrl() {
        return null;
    }
    
    @Override
    public String getAnukoMappings() {
    	return null;
    }

    @Override
    public String getDataFileLocation() {
        return null;
    }

    @Override
    public String getDurationFormat() {
        return null;
    }

    @Override
    public int getFilterSelectedMonth(int defaultValue) {
        return defaultValue;
    }

    @Override
    public long getFilterSelectedProjectId(long defaultValue) {
        return defaultValue;
    }

    @Override
    public int getFilterSelectedWeekOfYear(int defaultValue) {
        return defaultValue;
    }

    @Override
    public int getFilterSelectedYear(int defaultValue) {
        return defaultValue;
    }

    @Override
    public String getLastCsvExportLocation() {
        return null;
    }

    @Override
    public String getLastDataExportLocation() {
        return null;
    }

    @Override
    public String getLastDescription() {
        return null;
    }

    @Override
    public String getLastExcelExportLocation() {
        return null;
    }

    @Override
    public String getShownCategory() {
        return null;
    }

    @Override
    public Point getWindowLocation() {
        return null;
    }

    @Override
    public Dimension getWindowSize() {
        return null;
    }

    @Override
    public boolean isAllowOverlappingActivities() {
        return false;
    }

    @Override
    public boolean isDiscardEmptyActivities() {
        return false;
    }

    @Override
    public boolean isRememberWindowSizeLocation() {
        return false;
    }

    @Override
    public boolean isUseTrayIcon() {
        return false;
    }

    @Override
    public boolean isWindowMinimized() {
        return false;
    }

    @Override
    public Filter restoreFromSettings() {
        return new Filter();
    }

    @Override
    public void setAllowOverlappingActivities(boolean allow) {
    }

    @Override
    public void setAnukoLogin(String login) {
    }

    @Override
    public void setAnukoPassword(String password) {
    }

    @Override
    public void setAnukoUrl(String url) {
    }
    
    @Override
    public void setAnukoMappings(String s) {
    }

    @Override
    public void setDiscardEmptyActivities(boolean b) {
    }

    @Override
    public void setDurationFormat(String format) {
    }

    @Override
    public void setFilterSelectedMonth(int month) {
    }

    @Override
    public void setFilterSelectedProjectId(long projectId) {
    }

    @Override
    public void setFilterSelectedWeekOfYear(int weekOfYear) {
    }

    @Override
    public void setFilterSelectedYear(int year) {
    }

    @Override
    public void setLastCsvExportLocation(String csvExportLocation) {
    }

    @Override
    public void setLastDataExportLocation(String dataExportLocation) {
    }

    @Override
    public void setLastDescription(String lastDescription) {
    }

    @Override
    public void setLastExcelExportLocation(String excelExportLocation) {
    }

    @Override
    public void setRememberWindowSizeLocation(boolean rememberWindowSizeLocation) {
    }

    @Override
    public void setShownCategory(String shownCategory) {
    }

    @Override
    public void setUseTrayIcon(boolean b) {
    }

    @Override
    public void setWindowLocation(Point location) {
    }

    @Override
    public void setWindowMinimized(boolean minimized) {
    }

    @Override
    public void setWindowSize(Dimension size) {
    }

    @Override
    public int[] getMainTabpaneOrder() {
        return null;
    }

    @Override
    public void setMainTabpaneOrder(int[] order) {
    }

    @Override
    public int getFilterSelectedDayOfWeek(int defaultValue) {
        return defaultValue;
    }

    @Override
    public void setFilterSelectedDayOfWeek(int selectedDayOfWeek) {
    }

    @Override
    public boolean getBooleanProperty(String key, boolean defaultValue) {
        return false;
    }

    @Override
    public void setBooleanProperty(String key, boolean value) {
    }
}
