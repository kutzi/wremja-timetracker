package com.kemai.swing.util;

import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Rectangle;

/**
 * @author remast
 * @author kutzi
 */
public abstract class AWTUtils {
    
    /** Hide constructor. */
    private AWTUtils() {}

    /**
     * Ensures that a component stays within the current screen's bounds
     * while changing the position of the window as little as possible.
     *
     * @param preferredLeftTop The preferred left-top location of the component
     * @param component The component
     */
    public static void keepInScreenBounds(final Point preferredLeftTop, final Component component) {
        Rectangle preferredBounds = new Rectangle(preferredLeftTop, component.getPreferredSize());
        component.setLocation(ScreenUtils.ensureOnScreen(preferredBounds).getLocation());
    }
    
    /**
     * Looks for the parent frame in the hierarchy of the given container.
     * @param container the container for whom to look for a frame
     * @return the frame that is the parent of the container or 
     * <code>null</code> if there is none
     */
    public static Frame getFrame(final Container container) {
        if (container == null) {
            return null;
        }

        if (container instanceof Frame) {
            return (Frame) container;
        }

        return getFrame(container.getParent());
    }

}
