package com.kemai.wremja.logging;


/**
 * Our (very) simple wrapper for logging.
 * 
 * @author kutzi
 */
public class Logger {
    public enum Level {
        DEBUG, INFO, WARN, ERROR, FATAL;
    }
    
    public static Logger getLogger( Class<?> clazz ) {
        Destination destination = new Log4jDestination( clazz );
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

    public void debug(String msg) {
        this.destination.log(Level.DEBUG, msg, null);
    }

    public void info(String msg) {
        this.destination.log(Level.INFO, msg, null);
    }
}
