package de.kutzi.javautils.system;

import java.awt.Toolkit;

import javax.swing.UIManager;

/**
 * Utilities from OS specific operations. E.g. detecting the current OS.
 * 
 * Partly based on jDownloader https://www.syncom.org/svn/jdownloader/trunk/src/jd/nutils/OSDetector.java
 * @author kutzi
 *
 */
public class OSUtil {

    private static final boolean osIsMacOsX;
    private static final boolean osIsWindows;
    private static final boolean osIsWindowsXP;
    private static final boolean osIsWindows2003;
    private static final boolean osIsWindowsVista;
    private static final boolean osIsLinux;

    static {
      String os = System.getProperty("os.name").toLowerCase();

      osIsMacOsX = "mac os x".equals(os);
      osIsWindows = os != null && os.indexOf("windows") != -1;
      osIsWindowsXP = "windows xp".equals(os);
      osIsWindows2003 = "windows 2003".equals(os);
      osIsWindowsVista = "windows vista".equals(os);
      osIsLinux = os != null && os.indexOf("linux") != -1;
    }

    /**
     * @return true if this VM is running on Mac OS X
     */
    public static boolean isMacOSX() {
      return osIsMacOsX;
    }

    /**
     * @return true if this VM is running on Windows
     */
    public static boolean isWindows() {
      return osIsWindows;
    }

    /**
     * @return true if this VM is running on Windows XP
     */
    public static boolean isWindowsXP() {
      return osIsWindowsXP;
    }

    /**
     * @return true if this VM is running on Windows 2003
     */
    public static boolean isWindows2003() {
      return osIsWindows2003;
    }

    /**
     * @return true if this VM is running on Windows Vista
     */
    public static boolean isWindowsVista() {
      return osIsWindowsVista;
    }
    
    /**
     * @return true if this VM is running on a Linux distribution
     */
    public static boolean isLinux() {
      return osIsLinux;
    }
    
    /**
     * @return true if the VM is running Windows and the Java
     *         application is rendered using XP Visual Styles.
     */
    public static boolean isUsingWindowsVisualStyles() {
      if (!isWindows()) {
        return false;
      }

      boolean xpthemeActive = Boolean.TRUE.equals(Toolkit.getDefaultToolkit()
          .getDesktopProperty("win.xpstyle.themeActive"));
      if (!xpthemeActive) {
        return false;
      } else {
        try {
          return System.getProperty("swing.noxp") == null;
        } catch (RuntimeException e) {
          return true;
        }
      }
    }

    /**
     * Returns the name of the current Windows visual style.
     * <ul>
     * <li>it looks for a property name "win.xpstyle.name" in UIManager and if not found
     * <li>it queries the win.xpstyle.colorName desktop property ({@link Toolkit#getDesktopProperty(java.lang.String)})
     * </ul>
     * 
     * @return the name of the current Windows visual style if any. 
     */
    public static String getWindowsVisualStyle() {
      String style = UIManager.getString("win.xpstyle.name");
      if (style == null) {
        // guess the name of the current XPStyle
        // (win.xpstyle.colorName property found in awt_DesktopProperties.cpp in
        // JDK source)
        style = (String)Toolkit.getDefaultToolkit().getDesktopProperty(
          "win.xpstyle.colorName");
      }
      return style;
    }
    
    private static final boolean KDE;
    private static final boolean GNOME;
    
    static {
        boolean kde = false;
        boolean gnome = false;
        
        if(isLinux()) {
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
