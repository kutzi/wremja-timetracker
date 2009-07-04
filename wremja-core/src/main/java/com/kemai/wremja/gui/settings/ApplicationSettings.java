package com.kemai.wremja.gui.settings;

import java.io.File;
import java.net.URL;

import com.kemai.wremja.logging.Logger;

/**
 * Stores and reads all settings specific to the whole application.
 * @author remast
 * @author kutzi
 */
public final class ApplicationSettings {

    /** The logger. */
    private static final Logger LOG = Logger.getLogger(ApplicationSettings.class);

    /** Key for the name of the application properties file. */
    private static String APPLICATION_PROPERTIES_FILENAME = "application.properties"; //$NON-NLS-1$
    
    /** Node for Wremja application preferences. */
    private Configuration applicationConfig;

    //------------------------------------------------
    // Data locations
    //------------------------------------------------

    /** Default data directory. */
    public static final File dataDirectoryDefault = new File(System.getProperty("user.home"), ".wremja"); //$NON-NLS-1$ //$NON-NLS-2$
    
    /** The singleton instance. */
    private static final ApplicationSettings instance = new ApplicationSettings();

    /** Data directory relative to application installation. */
    public final File dataDirectoryApplicationRelative;

    /**
     * Getter for singleton instance.
     * @return the settings singleton
     */
    public static ApplicationSettings instance() {
        return instance;
    }

    private ApplicationSettings() {
        try {
            // TRICKY: Obtaining relative path outside of executable JAR with the following.
            String url = ApplicationSettings.class.getResource("/" + ApplicationSettings.class.getName().replaceAll("\\.", "/") + ".class").toString();
            url = url.substring(4).replaceFirst("/[^/]+\\.jar!.*$", "/");
            
            File rootDirectory = null;
            
            // TRICKY: Here's a hack to make it work for production environment, jUnit tests and development.
            // So we check if the url is a file 
            //   -> production 
            //   else -> development
            if (url.startsWith("file")) {
                // production environment (packaging as jar)
                rootDirectory = new File(new URL(url).toURI());
            } else {
                // development environment (compiled classes)
                rootDirectory = new File(ApplicationSettings.class.getResource("/").toURI()); //$NON-NLS-1$
            }

            dataDirectoryApplicationRelative = new File(
                    rootDirectory,
                    "data" //$NON-NLS-1$
            );

            final File dataDir = new File(
                    rootDirectory,
                    "data" //$NON-NLS-1$
            );

            if (!dataDir.exists()) {
                final boolean dataDirCreated = dataDir.mkdir();
                if (!dataDirCreated) {
                    throw new RuntimeException("Could not create directory at " + dataDir.getAbsolutePath() + ".");
                }
            }

            final File file = new File(
                    dataDir,
                    APPLICATION_PROPERTIES_FILENAME
            );

            applicationConfig = new JUPropertiesConfiguration(file, "Wremja application settings");
        } catch (Exception e) {
            LOG.error(e, e);
            throw new IllegalStateException("Application settings couldn't be initialized", e);
        }
    }
    
    /**
     * @deprecated use this only in tests!
     */
    public void setConfig(Configuration config) {
    	this.applicationConfig = config;
    }

    /** Key for storage mode. */
    private static final String STORE_DATA_IN_APPLICATION_DIRECTORY = "storeDataInApplicationDirectory"; //$NON-NLS-1$

    /**
     * Getter for storage mode. This can either be the default directory 
     * (user specific) or a directory relative to the application installation.
     * @return <code>true</code> if data is stored in application installation 
     * directory or <code>false</code> if data is stored in default directory
     */
    public boolean isStoreDataInApplicationDirectory() {
        return applicationConfig.getBooleanProperty(STORE_DATA_IN_APPLICATION_DIRECTORY, false);
    }

    /**
     * Sets the storage mode of the application.
     * @param storeDataInApplicationDirectory the new storage mode
     */
    public void setStoreDataInApplicationDirectory(final boolean storeDataInApplicationDirectory) {
        if (isStoreDataInApplicationDirectory() == storeDataInApplicationDirectory) {
            return;
        }

        applicationConfig.setProperty(STORE_DATA_IN_APPLICATION_DIRECTORY, storeDataInApplicationDirectory);
    }

    /**
     * Get the directory of the application in the profile of the user.
     * @return the directory for user settings
     */
    public File getApplicationDataDirectory()  {
        if (isStoreDataInApplicationDirectory()) {
            return dataDirectoryApplicationRelative;
        } else {
            return dataDirectoryDefault;
        }
    }
}
