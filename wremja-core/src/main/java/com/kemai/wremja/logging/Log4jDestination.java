package com.kemai.wremja.logging;

import com.kemai.wremja.logging.Logger.Destination;
import com.kemai.wremja.logging.Logger.Level;

class Log4jDestination implements Destination {

//    private final Logger delegate;
//
//    public Log4jDestination(Class<?> clazz) {
//        this.delegate = null;//Logger.getLogger(clazz);
//    }
    
    @Override
    public void log(Level level, Object message, Throwable throwable) {
//        try {
//            this.delegate.log(level2Log4jLevel(level), message, throwable);
//        } catch (IllegalArgumentException e) {
//            this.delegate.log(org.apache.log4j.Level.ERROR, "Invalid log level " + level + " used");
//            this.delegate.log(org.apache.log4j.Level.ERROR, message, throwable);
//        }
    }
    
//    private static org.apache.log4j.Level level2Log4jLevel( Level level ) {
//        switch( level ) {
//            case DEBUG : return org.apache.log4j.Level.DEBUG;
//            case INFO : return org.apache.log4j.Level.INFO;
//            case WARN : return org.apache.log4j.Level.WARN;
//            case ERROR : return org.apache.log4j.Level.ERROR;
//            case FATAL : return org.apache.log4j.Level.FATAL;
//            default : throw new IllegalArgumentException( level.name() );
//        }
//    }
}
