package com.kemai.wremja.gui.settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import com.kemai.util.IOUtils;

/**
 * A {@link Configuration} based on java.util.Properties
 * 
 * @author kutzi
 */
public class JUPropertiesConfiguration implements Configuration {

    private final File file;
    private final String name;
    private final Properties props = new Properties();

    public JUPropertiesConfiguration( File propertiesFile, String name ) throws IOException {
        this.file = propertiesFile;
        this.name = name;
        if( file.exists() ) {
            
            InputStream in = null;
            try {
                in = new FileInputStream(file);
                props.load(in);
            } finally {
                IOUtils.closeQuietly(in);
            }
        }
    }
    
    @Override
    public int getIntegerProperty(String key, int defaultValue) {
        String value = this.props.getProperty(key);
        if( value != null ) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        } else {
            return defaultValue;
        }
    }

    @Override
    public long getLongProperty(String key, long defaultValue) {
        String value = this.props.getProperty(key);
        if( value != null ) {
            try {
                return Long.parseLong(value);
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
        OutputStream out = null;
        try {
            // note that Properties internally wraps the outputstream
            // in a BufferedWriter, so we don't need to buffer it here
            out = new FileOutputStream(this.file);
            this.props.store(out, this.name );
        } catch (IOException e) {
            throw new RuntimeException( "Saving failed", e );
        } finally {
            IOUtils.closeQuietly(out);
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
