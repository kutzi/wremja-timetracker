package com.kemai.wremja.gui.settings;

/**
 * Simple configuration interface
 * 
 * @author kutzi
 */
public interface Configuration {

    public void setProperty( String key, String value);
    
    public void setProperty( String key, int value);
    
    public void setProperty( String key, long value);
    
    public void setProperty( String key, boolean value);
    
    public String getStringProperty( String key );
    
    public String getStringProperty( String key, String defaultValue );
    
    public int getIntegerProperty( String key, int defaultValue );
    
    public long getLongProperty( String key, long defaultValue );
    
    public boolean getBooleanProperty( String key, boolean defaultValue );
}


