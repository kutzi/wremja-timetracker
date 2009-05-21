package com.kemai.util;

import org.jdesktop.swingx.util.OS;

/**
 * Utilities from OS specific operations. E.g. detecting the current OS.
 * 
 * Partly based on jDownloader https://www.syncom.org/svn/jdownloader/trunk/src/jd/nutils/OSDetector.java
 * @author kutzi
 *
 */
public class OSUtils {

	private static final boolean KDE;
	private static final boolean GNOME;
	
	static {
		boolean kde = false;
		boolean gnome = false;
		
		if(OS.isLinux()) {
	        String gdmSession = System.getenv("GDMSESSION") != null ? System.getenv("GDMSESSION").toLowerCase() : "";
	        if (gdmSession.contains("gnome")) {
	        	gnome = true;
        	} else if(gdmSession.contains("kde")) {
        		kde = true;
        	} else {
        		String desktopSession = System.getenv("DESKTOP_SESSION") != null ? System.getenv("DESKTOP_SESSION").toLowerCase() : "";
        		if (desktopSession.contains("gnome")) {
        			gnome = true;
    			} else if(desktopSession.contains("gnome")) {
    				kde = true;
    			} else {
    		        // check gnome desktop id
    				/*
    				 * Note that GNOME_DESKTOP_SESSION_ID is deprecated, though there isn't any alternative at the moment.
    				 * See: https://bugs.launchpad.net/ubuntu/+source/hardinfo/+bug/285445
    				 */
    		        String gnomeDesktopSessionId = System.getenv("GNOME_DESKTOP_SESSION_ID");
    		        if (gnomeDesktopSessionId != null && gnomeDesktopSessionId.trim().length() > 0) {
    		        	gnome = true;
    		        }
    		        
    		        // check window manager
    		        String windowManager = System.getenv("WINDOW_MANAGER");
    		        if (windowManager != null && windowManager.trim().toLowerCase().endsWith("kde")) {
    		        	kde = true;
		        	}
    			}
        	}
		}
		
		KDE = kde;
		GNOME = gnome;
	}
	
	public static boolean isWindows() {
		return OS.isWindows();
	}
	
	public static boolean isLinux() {
		return OS.isLinux();
	}
	
	public static boolean isMaxOSX() {
		return OS.isMacOSX();
	}
	
	/**
     * Returns if OS is Linux/Gnome
     */
    public static boolean isGnome() {
        return GNOME;
    }

    /**
     * erkennt KDE.
     */
    public static boolean isKDE() {
        return KDE;
    }
}
