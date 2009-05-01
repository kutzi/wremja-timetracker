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
    
    public Integer getIntegerProperty( String key, Integer defaultValue );
    
    public Long getLongProperty( String key, Long defaultValue );
    
    public boolean getBooleanProperty( String key, boolean defaultValue );
}


