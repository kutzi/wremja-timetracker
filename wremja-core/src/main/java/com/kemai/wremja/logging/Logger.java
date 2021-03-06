package com.kemai.wremja.logging;


/**
 * Our simple wrapper for logging.
 * 
 * @author kutzi
 */
public class Logger {
    public enum Level {
        DEBUG, INFO, WARN, ERROR, FATAL;
    }
    
    /**
     * Returns a new {@link Logger} for the given class.
     */
    public static Logger getLogger( Class<?> clazz ) {
        Destination destination = new JULDestination( clazz );
        return new Logger(destination);
    }

    /**
     * Returns a new {@link Logger} with the given name.
     */
    public static Logger getLogger( String name ) {
        Destination destination = new JULDestination( name );
        return new Logger(destination);
    }
    
    private final Destination destination;
    
    private Logger( Destination destination ) {
        this.destination = destination;
    }
    
    static interface Destination {
        public void log(Level level, Object msg, Throwable t);
    }

    public void error(Object msg, Throwable t) {
        this.destination.log(Level.ERROR, msg, t);
    }
    
    public void error(String msg) {
        this.destination.log(Level.ERROR, msg, null);
    }

    public void warn(Object msg, Throwable t) {
        this.destination.log(Level.WARN, msg, t);
    }
    
    public void warn(String msg) {
        this.destination.log(Level.WARN, msg, null);
    }

    public void debug(String msg) {
        this.destination.log(Level.DEBUG, msg, null);
    }

    public void info(String msg) {
        this.destination.log(Level.INFO, msg, null);
    }
}
