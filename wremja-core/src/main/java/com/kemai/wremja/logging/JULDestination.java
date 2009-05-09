package com.kemai.wremja.logging;

import java.util.logging.Logger;

import com.kemai.wremja.logging.Logger.Destination;
import com.kemai.wremja.logging.Logger.Level;

class JULDestination implements Destination {

    private final Logger delegate;
    
    JULDestination( Class<?> clazz ) {
        this( clazz.getName() );
    }
    
    JULDestination( String name ) {
        this.delegate = Logger.getLogger(name);
    }
    
    @Override
    public void log(Level level, Object msg, Throwable t) {
        this.delegate.log(level2JULLevel(level), String.valueOf(msg), t);
    }

    private static java.util.logging.Level level2JULLevel( Level level ) {
        switch( level ) {
            case DEBUG : return java.util.logging.Level.FINER;
            case INFO : return java.util.logging.Level.INFO;
            case WARN : return java.util.logging.Level.WARNING;
            case ERROR : return java.util.logging.Level.SEVERE;
            case FATAL : return java.util.logging.Level.SEVERE;
            default : throw new IllegalArgumentException( level.name() );
        }
    }
}
