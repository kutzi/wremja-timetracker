package com.kemai.wremja.gui.settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * A {@link Configuration} based on java.util.Properties
 * 
 * @author kutzi
 */
public class JUPropertiesConfiguration implements Configuration {

    private final File file;
    private final Properties props = new Properties();

    public JUPropertiesConfiguration( File propertiesFile ) throws IOException {
        this.file = propertiesFile;
        if( file.exists() ) {
            props.load(new FileInputStream(file));
        }
    }
    
    @Override
    public Integer getIntegerProperty(String key, Integer defaultValue) {
        String value = this.props.getProperty(key);
        if( value != null ) {
            try {
                return Integer.valueOf(value);
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        } else {
            return defaultValue;
        }
    }

    @Override
    public Long getLongProperty(String key, Long defaultValue) {
        String value = this.props.getProperty(key);
        if( value != null ) {
            try {
                return Long.valueOf(value);
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        } else {
            return defaultValue;
        }
    }
    
    @Override
    public boolean getBooleanProperty(String key, boolean defaultValue) {
        String value = this.props.getProperty(key);
        if( value != null ) {
            return Boolean.parseBoolean(value);
        } else {
            return defaultValue;
        }
    }

    @Override
    public String getStringProperty(String key) {
        return getStringProperty(key, null);
    }
    
    @Override
    public String getStringProperty(String key, String defaultValue) {
        String value = this.props.getProperty(key);
        if( value != null ) {
            return value;
        } else {
            return defaultValue;
        }
    }

    @Override
    public void setProperty(String key, String value) {
        this.props.setProperty(key, value);
        save();
    }

    private void save() {
        try {
            this.props.store(new FileOutputStream(this.file), null);
        } catch (IOException e) {
            throw new RuntimeException( "Saving failed", e );
        }
    }

    @Override
    public void setProperty(String key, int value) {
        setProperty(key, Integer.toString(value));
    }

    @Override
    public void setProperty(String key, long value) {
        setProperty(key, Long.toString(value));
    }

    @Override
    public void setProperty(String key, boolean value) {
        setProperty(key, Boolean.toString(value));
    }
}
