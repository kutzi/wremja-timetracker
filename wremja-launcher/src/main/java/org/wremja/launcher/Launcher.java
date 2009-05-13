package org.wremja.launcher;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.TimeUnit;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;

import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.jdesktop.swingx.plaf.LookAndFeelAddons;

import com.jgoodies.looks.plastic.Plastic3DLookAndFeel;
import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.kemai.util.CollectionUtils;
import com.kemai.util.TextResourceBundle;
import com.kemai.wremja.gui.MainFrame;
import com.kemai.wremja.gui.model.PresentationModel;
import com.kemai.wremja.gui.model.ProjectActivityStateException;
import com.kemai.wremja.gui.model.io.DataBackup;
import com.kemai.wremja.gui.model.io.SaveTimer;
import com.kemai.wremja.gui.settings.ApplicationSettings;
import com.kemai.wremja.gui.settings.UserSettings;
import com.kemai.wremja.logging.BetterFormatter;
import com.kemai.wremja.logging.Logger;
import com.kemai.wremja.model.ActivityRepository;
import com.kemai.wremja.model.io.ProTrackReader;

/**
 * Controls the lifecycle of the application.
 * @author remast
 */
public final class Launcher {

    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(Launcher.class);

    /** The logger. */
    private static final Logger log = Logger.getLogger(Launcher.class);

    //------------------------------------------------
    // Command line options
    //------------------------------------------------

    /** Property for command line option minimized (-m). */
    private Boolean minimized;

    /** The interval in ms in which the data is saved to the disk. */
    private static final long SAVE_TIMER_INTERVAL = TimeUnit.MINUTES.toMillis(1);


    //------------------------------------------------
    // Application resources
    //------------------------------------------------

    /** The lock file to avoid multiple instances of the application. */
    private static FileLock lock;

    /** The timer to periodically save to disk. */
    private static Timer timer;

    /** The absolute path name of the log file. */
    private static String logFileName;

    /** Hide constructor. */
    private Launcher() { }

    /**
     * Main method that starts the application.
     * @param arguments the command line arguments
     */
    public static void main(final String[] arguments) {
        try {
            final Launcher mainInstance = new Launcher();
            mainInstance.parseCommandLineArguments(arguments);

            initLogger();
            
            initUncaughtExceptionHandler();

            initLookAndFeel();

            initLockFile();

            final PresentationModel model = initModel();

            initMainFrame(model, mainInstance);
            
            initTimer(model);

            initShutdownHook(model);
        } catch (Throwable t) {
            log.error(t, t);
            JOptionPane.showMessageDialog(
                    null, 
                    textBundle.textFor("Launcher.FatalError.Message", logFileName),  //$NON-NLS-1$
                    textBundle.textFor("Launcher.FatalError.Title"),  //$NON-NLS-1$
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Parses the parameters from the given command line arguments.
     * @param arguments the command line arguments to parse
     */
    private void parseCommandLineArguments(final String[] arguments) {
        if (arguments == null || arguments.length == 0) {
            return;
        }

        for (String argument : arguments) {
            if (argument.startsWith("-m=")) {
                this.minimized = Boolean.valueOf(argument.substring("-m=".length()));
            }
        }

    }

    /**
     * Initializes the main frame.
     * @param model the model to be displayed
     * @param mainInstance the main instance
     * @return the initialized main frame
     */
    private static MainFrame initMainFrame(final PresentationModel model,
            final Launcher mainInstance) throws Exception {
        log.debug("Initializing main frame ...");
        final MainFrame mainFrame = new MainFrame(model);
        
        boolean minimized = false;
        if( mainInstance.minimized != null ) {
        	// command line overrides preferences
        	minimized = mainInstance.minimized.booleanValue();
        } else {
        	if( UserSettings.instance().isRememberWindowSizeLocation() ) {
        		minimized = UserSettings.instance().isWindowMinimized();
        	}
        }
        
        if(model.isActive()) {
            mainFrame.handleUnfinishedActivityOnStartup();
        }
        
        mainFrame.setVisible(!minimized);
        if( minimized ) {
        	mainFrame.showTray(true);
        }
        

        return mainFrame;
    }

    /**
     * Initializes the lock file.
     */
    private static void initLockFile() {
        log.debug("Initializing lock file ...");
        if (!tryLock()) {
            JOptionPane.showMessageDialog(
                    null, 
                    textBundle.textFor("Launcher.ErrorAlreadyRunning.Message"),  //$NON-NLS-1$
                    textBundle.textFor("Launcher.ErrorAlreadyRunning.Title"),  //$NON-NLS-1$
                    JOptionPane.ERROR_MESSAGE
            );
            log.info(textBundle.textFor("Launcher.ErrorAlreadyRunning.Message")); //$NON-NLS-1$
            System.exit(0);
        }
    }

    /**
     * Initializes the shutdown hook process.
     * @param model
     */
    private static void initShutdownHook(final PresentationModel model) {
        log.debug("Initializing shutdown hook ...");

        Runtime.getRuntime().addShutdownHook(
                new Thread("Baralga shutdown ...") {

                    @Override
                    public void run() {
                        log.debug("Shutting down");
                        // 1. Stop current activity (if any)
                        if (model.isActive()) {
                            try {
                                model.stop(false);
                            } catch (ProjectActivityStateException e) {
                                // ignore
                            }
                        }

                        // 2. Save model
                        try {
                            model.save();
                        } catch (Exception e) {
                            log.error(e, e);
                        } catch (Throwable t) {
                            log.error(t, t);
                        } finally {
                            // 3. Release lock
                            releaseLock();
                            log.debug("Shutdown finished");
                        }
                    }

                }
        );
    }

    /**
     * Initializes the model from the stored data file or creates a new one.
     * @return the model
     */
    private static PresentationModel initModel() {
        log.debug("Initializing model...");

        // Initialize with new site
        final PresentationModel model = new PresentationModel();

        final String dataFileLocation = UserSettings.instance().getDataFileLocation();
        final File file = new File(dataFileLocation);

        try {
            if (file.exists()) {
                final ActivityRepository data = readData(file);

                // Reading data file was successful.
                model.setData(data);
            }
        } catch (IOException dataFileIOException) {
            // Make a backup copy of the corrupt file
            DataBackup.saveCorruptDataFile();

            // Reading data file was not successful so we try the backup files. 
            final List<File> backupFiles = DataBackup.getBackupFiles();

            if (CollectionUtils.isNotEmpty(backupFiles)) {
                for (File backupFile : backupFiles) {
                    try {
                        final ActivityRepository data = readData(backupFile);
                        model.setData(data);

                        final Date backupDate = DataBackup.getDateOfBackup(backupFile);
                        String backupDateString = backupFile.getName();
                        if (backupDate != null)  {
                            backupDateString = DateFormat.getDateTimeInstance().format(backupDate);
                        }

                        JOptionPane.showMessageDialog(null, 
                                textBundle.textFor("Launcher.DataLoading.ErrorText", backupDateString), //$NON-NLS-1$
                                textBundle.textFor("Launcher.DataLoading.ErrorTitle"), //$NON-NLS-1$
                                JOptionPane.WARNING_MESSAGE
                        );

                        break;
                    } catch (IOException backupFileIOException) {
                        log.error(backupFileIOException, backupFileIOException);
                    }
                }
            } else {
                // Data corrupt and no backup file found
                JOptionPane.showMessageDialog(null, 
                        textBundle.textFor("Launcher.DataLoading.ErrorTextNoBackup"), //$NON-NLS-1$
                        textBundle.textFor("Launcher.DataLoading.ErrorTitle"), //$NON-NLS-1$
                        JOptionPane.ERROR_MESSAGE
                );
            }

        }
        return model;
    }

    /**
     * Read ActivityRepository data from the given file.
     * @param file the file to be read
     * @return the data read from file or null if the file is null or doesn't exist
     * @throws IOException on error reading file
     */
    private static ActivityRepository readData(final File file) throws IOException {
        // Check for saved data
        if (file != null && file.exists()) {
            ProTrackReader reader;
            try {
                reader = new ProTrackReader();
                reader.read(file);
                return reader.getData();
            } catch (Exception e) {
                throw new IOException(e);
            }
        }
        return null;
    }

    /**
     * Initialize the logger of the application.
     * @throws IOException 
     */
    private static void initLogger() throws IOException {
        log.debug("Initializing logger ...");
        
        String logDir = ApplicationSettings.instance().getApplicationDataDirectory().getAbsolutePath() + File.separator + "log";
        File logDirF = new File(logDir);
        if( !logDirF.isDirectory() ) {
            if( !logDirF.mkdirs() ) {
                throw new IOException("Couldn't create log directory: " + logDir );
            }
        }

        logFileName =  logDir + File.separator + "wremja.log";
        
        FileHandler fileHandler = new FileHandler(logFileName + ".%g", 50000, 3, true );
        fileHandler.setFormatter(new BetterFormatter());
        fileHandler.setLevel(Level.INFO);
        
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.WARNING);
        consoleHandler.setFormatter(new BetterFormatter());
        
        java.util.logging.Logger rootLogger = java.util.logging.Logger.getLogger("");
        rootLogger.addHandler(fileHandler);
        rootLogger.addHandler(consoleHandler);
        rootLogger.setLevel(Level.INFO);
        
        java.util.logging.Logger appLogger = java.util.logging.Logger.getLogger("com.kemai");
        appLogger.setLevel(Level.FINEST);
    }

    private static void initUncaughtExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler( new Thread.UncaughtExceptionHandler() {
            
            private final Logger log = Logger.getLogger("UncaughtExceptionHandler");
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                this.log.error( "Caught exception from Thread " + t.getName(), e );
            }
        });
    }
    
    /**
     * Initialize the look & feel of the application.
     */
    private static void initLookAndFeel() {
        log.debug("Initializing look and feel ...");
        
        // enable antialiasing
        System.setProperty("swing.aatext", "true");

        // use Xrender pipeline on Linux, if available
        System.setProperty("sun.java2d.xrender", "true");

        try {
            // a) Try windows
            UIManager.setLookAndFeel(new Plastic3DLookAndFeel());
            Plastic3DLookAndFeel.setTabStyle(PlasticLookAndFeel.TAB_STYLE_METAL_VALUE);
            Plastic3DLookAndFeel.setHighContrastFocusColorsEnabled(true);
        } catch (UnsupportedLookAndFeelException e) {
            // b) Try system look & feel
            try {
                UIManager.setLookAndFeel(
                        UIManager.getSystemLookAndFeelClassName()
                );
            } catch (Exception ex) {
                log.error(ex, ex);
            }
        }
        String s = LookAndFeelAddons.getBestMatchAddonClassName();
        try {
            LookAndFeelAddons.setAddon(s);
        } catch (Exception e) {
            log.warn(e, e);
        }
    }

    /**
     * Initialize the timer to automatically save the model.
     * @see #SAVE_TIMER_INTERVAL
     * @param model the model to be saved
     */
    private static void initTimer(final PresentationModel model) {
        log.debug("Initializing timer ...");
        timer = new Timer();
        timer.scheduleAtFixedRate(new SaveTimer(model), SAVE_TIMER_INTERVAL, SAVE_TIMER_INTERVAL);
    }

    /**
     * Tries to create and lock a lock file at <code>${user.home}/.ProTrack/lock</code>.
     * @return <code>true</code> if the lock could be acquired. <code>false</code> if
     *   the lock is held by another program
     * @throws RuntimeException if an I/O error occurred
     */
    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value="RV_RETURN_VALUE_IGNORED_BAD_PRACTICE", justification="Its irrelevant if lock file already existed or not.")
    private static boolean tryLock() {
        try {
            checkOrCreateDataDir();
            final File lockFile = new File(UserSettings.getLockFileLocation());
            if (!lockFile.exists()) {
                lockFile.createNewFile();
            }

            final FileChannel channel = new RandomAccessFile(lockFile, "rw").getChannel(); //$NON-NLS-1$
            lock = channel.tryLock();

            return lock != null;
        } catch (Exception e) {
            final String error = textBundle.textFor("Launcher.LockFileError.Message"); //$NON-NLS-1$
            log.error(error, e);
            throw new RuntimeException(error);
        }
    }

    /**
     * Checks whether the data directory exists and creates it if necessary.
     */
    private static void checkOrCreateDataDir() {
        final File wremjaDir = ApplicationSettings.instance().getApplicationDataDirectory();
        if (!wremjaDir.exists()) {
            final boolean wremjaDirCreated = wremjaDir.mkdir();
            if (!wremjaDirCreated) {
                throw new RuntimeException("Could not create directory at " + wremjaDir.getAbsolutePath() + ".");
            }
        }
    }


    /**
     * Releases the lock file created with {@link #createLock()}.
     */
    private static void releaseLock() {
        if (lock == null) {
            return;
        }

//        final File lockFile = new File(UserSettings.getLockFileLocation());

        try {
            lock.release();
        } catch (IOException e) {
            log.error(e, e);
        } finally {
            try {
                lock.channel().close();
            } catch (Exception e) {
                // ignore
            }
        }
        
//        final boolean deleteSuccessfull = lockFile.delete();
//        if (!deleteSuccessfull) {
//            log.warn("Could not delete lock file at " + lockFile.getAbsolutePath() + ". Please delete manually.");
//        }
    }
}
