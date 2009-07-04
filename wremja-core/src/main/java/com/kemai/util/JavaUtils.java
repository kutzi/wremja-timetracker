package com.kemai.util;

import org.apache.commons.lang.SystemUtils;

/**
 * Utility class for checking the Java runtime version and various methods to check
 * for bugs in the runtime.
 *
 * @author kutzi
 *
 */
public class JavaUtils {
	
	private static final String VERSION;
	
	private static final int VERSION_INT;
	
	private static final int MAINTENANCE_VERSION;
	
	static {
		VERSION = SystemUtils.JAVA_VERSION_TRIMMED;
		VERSION_INT = SystemUtils.JAVA_VERSION_INT;
		
		int maintenanceNr = 0;
		int index = VERSION.indexOf('_');
		if(index != -1 && index + 1 < VERSION.length()) {
			String maintenanceStr = VERSION.substring(index + 1, VERSION.length());
			try {
				maintenanceNr = Integer.parseInt(maintenanceStr);
			} catch(NumberFormatException nfe ) {
				System.err.println("Couldn't determine Java maintenance number from " + VERSION);
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
		if(OSUtils.isWindows()) {
			return false;
		}
		// not sure about MacOSX, but I think it's nearer to Linux than Windows
		
		if(isGreaterOrEqual(160, 0) && isLesser(160, 14)) {
			return true;
		}
		return false;
	}
	
	private JavaUtils() {
		// no instances
	}
	
	private static int compareTo(int mainNr, int maintenanceNr) {
		if(VERSION_INT > mainNr) {
			return 1;
		}
		if(VERSION_INT < mainNr) {
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
//	public static void main(String[] args) {
//		System.out.println(VERSION);
//		System.out.println(hasJPasswordFieldBug());
//	}
}
