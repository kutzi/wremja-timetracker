package de.kutzi.javautils.system;

/**
 * Utility class for checking the Java runtime version and various methods to check
 * for bugs in the runtime.
 *
 * @author kutzi
 *
 */
public class JavaUtil {
    
    /**
     * <p>The <code>java.version</code> System Property. Java version number.</p>
     *
     * <p>Defaults to <code>null</code> if the runtime does not have
     * security access to read this property or the property does not exist.</p>
     * 
     * <p>
     * This value is initialized when the class is loaded. If {@link System#setProperty(String,String)}
     * or {@link System#setProperties(java.util.Properties)} is called after this class is loaded, the value
     * will be out of sync with that System property.
     * </p>
     */
    public static final String JAVA_VERSION = getSystemProperty("java.version");
    
    public static final String JAVA_VERSION_TRIMMED;
    
    public static final int JAVA_VERSION_INT;
    
    private static final int MAINTENANCE_VERSION;
    
    static {
        JAVA_VERSION_TRIMMED = getJavaVersionTrimmed();
        JAVA_VERSION_INT = getJavaVersionAsInt();
        
        int maintenanceNr = 0;
        int index = JAVA_VERSION_TRIMMED.indexOf('_');
        if(index != -1 && index + 1 < JAVA_VERSION_TRIMMED.length()) {
            String maintenanceStr = JAVA_VERSION_TRIMMED.substring(index + 1, JAVA_VERSION_TRIMMED.length());
            try {
                maintenanceNr = Integer.parseInt(maintenanceStr);
            } catch(NumberFormatException nfe ) {
                System.err.println("Couldn't determine Java maintenance number from " + JAVA_VERSION_TRIMMED);
            }
        }
        MAINTENANCE_VERSION = maintenanceNr;
    }
    
    public static boolean isGreaterOrEqual(int mainNr, int maintenanceNr) {
        return compareTo(mainNr, maintenanceNr) >= 0;
    }
    
    public static boolean isLesser(int mainNr, int maintenanceNr) {
        return compareTo(mainNr, maintenanceNr) < 0;
    }
    
    /**
     * Checks if this VM has the JPasswordField bug described in:
     * http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6801620
     */
    public static boolean hasJPasswordFieldBug() {
        if(OSUtil.isWindows()) {
            return false;
        }
        // not sure about MacOSX, but I think it's nearer to Linux than Windows
        
        if(isGreaterOrEqual(160, 0) && isLesser(160, 14)) {
            return true;
        }
        return false;
    }
    
    private JavaUtil() {
        // no instances
    }
    
    private static int compareTo(int mainNr, int maintenanceNr) {
        if(JAVA_VERSION_INT > mainNr) {
            return 1;
        }
        if(JAVA_VERSION_INT < mainNr) {
            return -1;
        }
        
        if(MAINTENANCE_VERSION > maintenanceNr) {
            return 1;
        }
        if(MAINTENANCE_VERSION < maintenanceNr) {
            return -1;
        }
        return 0;
    }
    
    /**
     * <p>Gets a System property, defaulting to <code>null</code> if the property
     * cannot be read.</p>
     *
     * <p>If a <code>SecurityException</code> is caught, the return
     * value is <code>null</code> and a message is written to <code>System.err</code>.</p>
     * 
     * @param property the system property name
     * @return the system property value or <code>null</code> if a security problem occurs
     */
    private static String getSystemProperty(String property) {
        try {
            return System.getProperty(property);
        } catch (SecurityException ex) {
            // we are not allowed to look at this property
            System.err.println(
                "Caught a SecurityException reading the system property '" + property 
                + "'; the SystemUtils property value will default to null."
            );
            return null;
        }
    }
    
    /**
     * Trims the text of the java version to start with numbers.
     * 
     * @return the trimmed java version
     */
    private static String getJavaVersionTrimmed() {
        if (JAVA_VERSION != null) {
            for (int i = 0; i < JAVA_VERSION.length(); i++) {
                char ch = JAVA_VERSION.charAt(i);
                if (ch >= '0' && ch <= '9') {
                    return JAVA_VERSION.substring(i);
                }
            }
        }
        return null;
    }
    
    /**
     * <p>Gets the Java version number as an <code>int</code>.</p>
     *
     * <p>Example return values:</p>
     * <ul>
     *  <li><code>120</code> for JDK 1.2
     *  <li><code>131</code> for JDK 1.3.1
     * </ul>
     * 
     * <p>Patch releases are not reported.
     * Zero is returned if {@link #JAVA_VERSION_TRIMMED} is <code>null</code>.</p>
     * 
     * @return the version, for example 131 for JDK 1.3.1
     */
    private static int getJavaVersionAsInt() {
        if (JAVA_VERSION_TRIMMED == null) {
            return 0;
        }
        String str = JAVA_VERSION_TRIMMED.substring(0, 1);
        str = str + JAVA_VERSION_TRIMMED.substring(2, 3);
        if (JAVA_VERSION_TRIMMED.length() >= 5) {
            str = str + JAVA_VERSION_TRIMMED.substring(4, 5);
        } else {
            str = str + "0";
        }
        try {
            return Integer.parseInt(str);
        } catch (Exception ex) {
            return 0;
        }
    }
}

