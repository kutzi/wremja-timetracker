package com.kemai.wremja.gui.settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.ObjectUtils;

import com.kemai.wremja.gui.model.io.DataBackup;

import de.kutzi.javautils.io.IOUtils;

/**
 * A {@link Configuration} based on java.util.Properties
 * 
 * @author kutzi
 */
public class JUPropertiesConfiguration implements Configuration {
	
	private static final boolean DEBUG = false;
	
    private final File file;
    private final String name;
    private final Properties props = new Properties();
    
    private volatile long lastBackupSaved = 0;

    public JUPropertiesConfiguration( File propertiesFile, String name ) throws IOException {
        this.file = propertiesFile;
        this.name = name;
        
        InputStream in = null;
        if(this.file.exists() ) {
        	in = new FileInputStream(this.file);
        } else {
            // There is a small time window in save() where a backup file is created, but the
            // new configuration isn't saved yet.
            // Check if that maybe has happened
        	List<File> backupFiles = DataBackup.getBackupFiles(this.file);
        	if (!backupFiles.isEmpty()) {
        		in = new FileInputStream(backupFiles.get(0));
        	}
        }
        
        
        if(in != null) {
	        try {
	            props.load(in);
	        } finally {
	            IOUtils.close(in);
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
    public void setProperty(String key, String newValue) {
        Object oldValue = this.props.setProperty(key, newValue);
        if (!ObjectUtils.equals(oldValue, newValue)) {
        	save();
        }
    }

    private void save() {

        OutputStream out = null;
        try {
            if (System.currentTimeMillis() > lastBackupSaved + 5000) {
                // create backup at most every 5 seconds
                DataBackup.toBackup(this.file);
                lastBackupSaved = System.currentTimeMillis();
                if (DEBUG) {
                    System.out.println("Backup created");
                }
            }
            // note that Properties internally wraps the output stream
            // in a BufferedWriter, so we don't need to buffer it here
            out = new FileOutputStream(this.file);
            this.props.store(out, this.name );
            out.close();
            if (DEBUG) {
                System.out.println("Properties saved");
            }
        } catch (IOException e) {
            throw new RuntimeException( "Saving failed", e );
        } finally {
            IOUtils.close(out);
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
