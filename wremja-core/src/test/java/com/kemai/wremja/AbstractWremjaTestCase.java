package com.kemai.wremja;

import org.junit.BeforeClass;

import com.kemai.wremja.gui.settings.ApplicationSettings;
import com.kemai.wremja.gui.settings.Configuration;
import com.kemai.wremja.gui.settings.UserSettings;

public abstract class AbstractWremjaTestCase {

	/**
	 * Mocks the application and user settings to make sure that they don't interfere with the tests.
	 */
    @BeforeClass
    @SuppressWarnings("deprecation")
    public static void setupClass() {
    	Configuration mockConfig = new Configuration() {
			
			@Override
			public void setProperty(String key, boolean value) {
			}
			
			@Override
			public void setProperty(String key, long value) {
			}
			
			@Override
			public void setProperty(String key, int value) {
			}
			
			@Override
			public void setProperty(String key, String value) {
			}
			
			@Override
			public String getStringProperty(String key, String defaultValue) {
				return defaultValue;
			}
			
			@Override
			public String getStringProperty(String key) {
				return "";
			}
			
			@Override
			public long getLongProperty(String key, long defaultValue) {
				return defaultValue;
			}
			
			@Override
			public int getIntegerProperty(String key, int defaultValue) {
				return defaultValue;
			}
			
			@Override
			public boolean getBooleanProperty(String key, boolean defaultValue) {
				return defaultValue;
			}
		};
		UserSettings.instance().setConfig(mockConfig);
		ApplicationSettings.instance().setConfig(mockConfig);
    }
}
