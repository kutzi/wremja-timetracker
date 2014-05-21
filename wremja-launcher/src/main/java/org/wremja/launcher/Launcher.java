/* 
 * $Id$
 *
 * This file is part of Wremja.
 * 
 * Copyright (C) 2009 Christoph Kutzinski.
 * 
 * Wremja is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 2.1 of the License.
 * 
 * Wremja is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * Wremja. If not, see <http://www.gnu.org/licenses/>.
 */
package org.wremja.launcher;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Frame;
import java.awt.SystemTray;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;

import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.HyperlinkEvent.EventType;

import org.jdesktop.swingx.plaf.LookAndFeelAddons;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.xmlpull.mxp1.MXParser;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.jgoodies.looks.plastic.Plastic3DLookAndFeel;
import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.kemai.util.CollectionUtils;
import com.kemai.util.TextResourceBundle;
import com.kemai.wremja.gui.GuiConstants;
import com.kemai.wremja.gui.MainFrame;
import com.kemai.wremja.gui.model.PresentationModel;
import com.kemai.wremja.gui.model.ProjectActivityStateException;
import com.kemai.wremja.gui.model.io.DataBackup;
import com.kemai.wremja.gui.model.io.SaveDaemon;
import com.kemai.wremja.gui.settings.ApplicationSettings;
import com.kemai.wremja.gui.settings.IUserSettings;
import com.kemai.wremja.gui.settings.UserSettings;
import com.kemai.wremja.logging.BetterFormatter;
import com.kemai.wremja.logging.Logger;
import com.kemai.wremja.model.ActivityRepository;
import com.kemai.wremja.model.io.ProTrackReader;

import de.kutzi.javautils.misc.MavenVersion;

/**
 * Controls the lifecycle of the application.
 * 
 * @author remast
 * @author kutzi
 * @author $Author$ (last change)
 */
public final class Launcher {

    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(Launcher.class);

    /** The logger. */
    private static volatile Logger LOG;

    //------------------------------------------------
    // Command line options
    //------------------------------------------------

    /** Property for command line option minimized (-m). */
    private Boolean minimized;

    private boolean debug = false;

	private boolean updateCheck;

    private MainFrame mainFrame;

	private static boolean firstStart;

    /** The daemon to periodically save to disk. */
    private static SaveDaemon saveDaemon;
    
    /** The interval in ms in which the data is saved (at least) to the disk. */
    private static final long SAVE_TIMER_INTERVAL = TimeUnit.MINUTES.toMillis(2);

    //------------------------------------------------
    // Application resources
    //------------------------------------------------

    /** The lock file to avoid multiple instances of the application. */
    private static File lockFile;
    private static FileLock lock;
    
    /** Changing the last-modified date on a locked file doesn't work (at least on Windows).
     * Therefore we need an additional file to lockFile.
     */
    private static File lastTouchFile;

    /** The absolute path name of the log file. */
    private static String logFileName;
    
    /** Set to true if the previous shutdown was likely a 'clean' one. I.e. the lock file was deleted. */
    private static boolean cleanShutdown = false;
    
    /** last modified time of the previous run if it was a 'dirty' shutdown. Only set if cleanShutdown == false */
    private static long lastModified;

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
            
            mainInstance.initDir();

            mainInstance.initLogger();
            
            initUncaughtExceptionHandler();

            initLookAndFeel();

            initLockFile();

            @SuppressWarnings("deprecation")
            IUserSettings settings = UserSettings.instance();
            
            TimeZone tz = TimeZone.getTimeZone(settings.getTimeZone());
            LOG.info("Using timezone: " + tz);
            TimeZone.setDefault(tz);
            DateTimeZone.setDefault(DateTimeZone.forTimeZone(tz));
            
            final PresentationModel model = initModel(settings);

            mainInstance.initMainFrame(model, settings);
            
            initTimer(model);

            initShutdownHook(model);
            
            mainInstance.checkForUpdates();
        } catch (Throwable t) {
            LOG.error(t, t);
            JOptionPane.showMessageDialog(
                    null, 
                    textBundle.textFor("Launcher.FatalError.Message", logFileName),  //$NON-NLS-1$
                    textBundle.textFor("Launcher.FatalError.Title"),  //$NON-NLS-1$
                    JOptionPane.ERROR_MESSAGE
            );
            System.exit(-1);
        }
    }

	private void initDir() {
    	File wremjaDataDir = ApplicationSettings.instance().getApplicationDataDirectory();
    	if(!wremjaDataDir.exists()) {
    		firstStart = true;
    		if(!wremjaDataDir.mkdir()) {
    			throw new IllegalStateException("Couldn't create data directory " + wremjaDataDir);
    		}
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
            if (argument.startsWith("--minimized")) {
                this.minimized = Boolean.TRUE;
            } else if (argument.startsWith("--debug")) {
                this.debug  = true;
            } else if (argument.startsWith("--updateCheck")) {
            	this.updateCheck = true;
            }
        }

    }

    /**
     * Initializes the main frame.
     * @param model the model to be displayed
     * @param mainInstance the main instance
     * @return the initialized main frame
     */
    private MainFrame initMainFrame(PresentationModel model, IUserSettings settings) throws Exception {
        LOG.debug("Initializing main frame ...");
        this.mainFrame = new MainFrame(model, settings);
        
        boolean minimized = false;
        if( this.minimized != null ) {
        	// command line overrides preferences
        	minimized = this.minimized.booleanValue();
        } else {
        	if( settings.isRememberWindowSizeLocation() ) {
        		minimized = settings.isWindowMinimized();
        	}
        }
        
        if( !cleanShutdown && model.isActive()) {
            mainFrame.handleUnfinishedActivityOnStartup(lastModified);
        }
        
        if (settings.isUseTrayIcon() && SystemTray.isSupported()) {
        	mainFrame.setVisible(true);
        	mainFrame.setExtendedState(minimized ? Frame.ICONIFIED : Frame.NORMAL);
        } else {
        	// tray icon disabled or not supported
        	// remembering minimized state doesn't make much sense in that scenario
        	// ergo ignore it
        	mainFrame.setVisible(true);
        }
        

        return mainFrame;
    }

    /**
     * Initializes the lock file.
     */
    private static void initLockFile() {
        LOG.debug("Initializing lock file ...");
        if (!tryLock()) {
            JOptionPane.showMessageDialog(
                    null, 
                    textBundle.textFor("Launcher.ErrorAlreadyRunning.Message"),  //$NON-NLS-1$
                    textBundle.textFor("Launcher.ErrorAlreadyRunning.Title"),  //$NON-NLS-1$
                    JOptionPane.ERROR_MESSAGE
            );
            LOG.info(textBundle.textFor("Launcher.ErrorAlreadyRunning.Message")); //$NON-NLS-1$
            System.exit(0);
        }
    }

    /**
     * Initializes the shutdown hook process.
     * @param model
     */
    private static void initShutdownHook(final PresentationModel model) {
        LOG.debug("Initializing shutdown hook ...");

        // Note: that logging in the shutdown hook doesn't work, as
        // probably the LogManager is already destroyed
        Runtime.getRuntime().addShutdownHook(
                new Thread("Wremja shutdown ...") {

                    @Override
                    public void run() {
                        // TODO: decouple this from the PresentationModel
                        // (always causes problems, because some GUI components which
                        // should be notified are already gone!)
                        
                        // prevent dirty saves on shutdown
                        saveDaemon.requestStop();
                        
                        // stop current activity (if necessary)
                        if (model.isActive() && model.isStopActivityOnShutdown()) {
                            // remove all observers to prevent side effect, because of
                            // already dead observers
                            model.deleteObservers();
                            try {
                                model.stop();
                            } catch (ProjectActivityStateException e) {
                                // ignore
                            }
                        }

                        // save model
                        try {
                            model.save();
                        } catch (Throwable t) {
                            System.err.println(t);
                        } finally {
                            // Release file lock
                            releaseLock();
                        }
                    }
                }
        );
    }

    /**
     * Initializes the model from the stored data file or creates a new one.
     * @param settings 
     * @return the model
     */
    private static PresentationModel initModel(IUserSettings settings) {
        LOG.debug("Initializing model...");

        final PresentationModel model = new PresentationModel(settings, lastTouchFile);
        if(firstStart) {
        	//look if there is a Baralga file
            File baralgaDir = new File(System.getProperty("user.home"), ".ProTrack");
            File baralgaData = new File(baralgaDir, "ProTrack.ptd");
            if(baralgaData.isFile()) {
                int result = JOptionPane.showConfirmDialog(null, 
                        textBundle.textFor("Launcher.DataLoading.FromBaralga.Text", baralgaData.getAbsolutePath()), //$NON-NLS-1$
                        textBundle.textFor("Launcher.DataLoading.FromBaralga.Title"), //$NON-NLS-1$
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );
                
                if(result == JOptionPane.YES_OPTION) {
                    try {
	                    ActivityRepository data = readData(baralgaData);
	                    LOG.info("Importing data from Baralga file " + baralgaData +
	                            ".\n" + data.getProjects().size() + " projects and " + data.getActivities().size() + " activities.");

	                    // Reading data file was successful.
	                    model.setData(data);
                    } catch (IOException e) {
                    	LOG.error("Error reading Baralga file", e);
                        JOptionPane.showMessageDialog(null, 
                                textBundle.textFor("Launcher.DataLoading.ErrorTextGeneric"), //$NON-NLS-1$
                                textBundle.textFor("Launcher.DataLoading.ErrorTitle"), 
                                JOptionPane.ERROR_MESSAGE
                        );
                    }
                }
            }
        }

        final String dataFileLocation = settings.getDataFileLocation();
        final File file = new File(dataFileLocation);

        try {
            if (file.exists()) {
                final ActivityRepository data = readData(file);
                LOG.info("Loaded data from file " + file +
                        ".\n" + data.getProjects().size() + " projects and " + data.getActivities().size() + " activities.");

                // Reading data file was successful.
                model.setData(data);
            } else {
            	// look if there are backup files
                final List<File> backupFiles = DataBackup.getBackupFiles(file);

                if (CollectionUtils.isNotEmpty(backupFiles)) {
                    loadFromBackup(model, backupFiles);
                }
            }
        } catch (IOException dataFileIOException) {
        	LOG.error("Data file corrupt, trying to read from backup", dataFileIOException);
            // Make a backup copy of the corrupt file
            DataBackup.saveCorruptDataFile(file);

            // Reading data file was not successful so we try the backup files. 
            final List<File> backupFiles = DataBackup.getBackupFiles(file);

            if (CollectionUtils.isNotEmpty(backupFiles)) {
                loadFromBackup(model, backupFiles);
            } else {
                // Data corrupt and no backup file found
                JOptionPane.showMessageDialog(null, 
                        textBundle.textFor("Launcher.DataLoading.ErrorTextNoBackup"), //$NON-NLS-1$
                        textBundle.textFor("Launcher.DataLoading.ErrorTitle"), 
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
        return model;
    }

	private static void loadFromBackup(final PresentationModel model, final List<File> backupFiles) {
	    for (File backupFile : backupFiles) {
	        try {
	            final ActivityRepository data = readData(backupFile);
	            model.setData(data);
	            LOG.info("Loaded data from backup file " + backupFile +
	                    ".\n" + data.getProjects().size() + " projects and " + data.getActivities().size() + " activities.");

	            final DateTime backupDate = DataBackup.getDateOfBackup(backupFile);
	            String backupDateString = backupFile.getName();
	            if (backupDate != null)  {
	                backupDateString = DateTimeFormat.mediumDateTime().print(backupDate);
	            }

	            JOptionPane.showMessageDialog(null, 
	                    textBundle.textFor("Launcher.DataLoading.ErrorText", backupDateString), //$NON-NLS-1$
	                    textBundle.textFor("Launcher.DataLoading.ErrorTitle"), 
	                    JOptionPane.WARNING_MESSAGE
	            );

	            break;
	        } catch (IOException backupFileIOException) {
	            LOG.error(backupFileIOException, backupFileIOException);
	        }
	    }
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
                return reader.read(file);
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
    private void initLogger() throws IOException {
        String logDir = ApplicationSettings.instance().getApplicationDataDirectory().getAbsolutePath() + File.separator + "log";
        File logDirF = new File(logDir);
        if( !logDirF.isDirectory() ) {
            if( !logDirF.mkdirs() ) {
                throw new IOException("Couldn't create log directory: " + logDir );
            }
        }

        logFileName =  logDir + File.separator + "wremja.log";

        LogManager.getLogManager().reset();
        
        FileHandler fileHandler = new FileHandler(logFileName + ".%g", 50000, 3, true );
        fileHandler.setFormatter(new BetterFormatter());
        fileHandler.setLevel(Level.INFO);
        
        ConsoleHandler consoleHandler = new ConsoleHandler() {
        	{
        		setOutputStream(System.out);
        	}
        };
        consoleHandler.setLevel(Level.WARNING);
        consoleHandler.setFormatter(new BetterFormatter());
        
        java.util.logging.Logger rootLogger = java.util.logging.Logger.getLogger("");
        rootLogger.addHandler(fileHandler);
        rootLogger.addHandler(consoleHandler);
        rootLogger.setLevel(Level.INFO);
        
        java.util.logging.Logger appLogger = java.util.logging.Logger.getLogger("com.kemai");
        appLogger.setLevel(Level.INFO);
        
        if(this.debug) {
            consoleHandler.setLevel(Level.FINEST);
            appLogger.setLevel(Level.FINEST);
        }
        
        LOG = Logger.getLogger(Launcher.class);
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
        LOG.debug("Initializing look and feel ...");
        
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
                LOG.error(ex, ex);
            }
        }
        String s = LookAndFeelAddons.getBestMatchAddonClassName();
        try {
            LookAndFeelAddons.setAddon(s);
        } catch (Exception e) {
            LOG.warn(e, e);
        }
    }

    /**
     * Initialize the timer to automatically save the model.
     * @see #SAVE_TIMER_INTERVAL
     * @param model the model to be saved
     */
    private static void initTimer(final PresentationModel model) {
        LOG.debug("Initializing save timer ...");
        saveDaemon = new SaveDaemon(model, SAVE_TIMER_INTERVAL, TimeUnit.MILLISECONDS);
        Thread t = new Thread(saveDaemon, "Wremja save daemon");
        t.setDaemon(true);
        t.start();
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
            lockFile = new File(UserSettings.getLockFileLocation());
            lastTouchFile = new File(lockFile.getParentFile(), "lastTouch");
            if (!lockFile.exists()) {
                lockFile.createNewFile();
                cleanShutdown = true;
            } else {
                cleanShutdown = false;
                lastModified = lastTouchFile.lastModified();
            }
            
            if(!lastTouchFile.exists()) {
                lastTouchFile.createNewFile();
            }

            final FileChannel channel = new RandomAccessFile(lockFile, "rw").getChannel(); //$NON-NLS-1$
            lock = channel.tryLock();

            return lock != null;
        } catch (Exception e) {
            final String error = textBundle.textFor("Launcher.LockFileError.Message"); //$NON-NLS-1$
            LOG.error(error, e);
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

        try {
            lock.release();
        } catch (IOException e) {
            LOG.error(e, e);
        } finally {
            try {
                lock.channel().close();
            } catch (Exception e) {
                // ignore
            }
        }
        
        final boolean deleteSuccessfull = lockFile.delete();
        if (!deleteSuccessfull) {
            LOG.warn("Could not delete lock file at " + lockFile.getAbsolutePath() + ". Please delete manually.");
        }
    }
    
    private static final URL UPDATE_URL;
    private static final String DOWNLOAD_URL = "http://kenai.com/projects/wremja/downloads";
    static {
    	URL url;
    	try {
	        //url = new URL("http://oss.sonatype.org/content/repositories/kenai-snapshots/com/kenai/wremja/wremja-installer/maven-metadata.xml");
	        url = new URL("http://oss.sonatype.org/content/repositories/kenai-releases/com/kenai/wremja/wremja-installer/maven-metadata.xml");
        } catch (MalformedURLException e) {
	        LOG.error("", e);
	        url = null;
        }
        UPDATE_URL = url;
    }
    
    private void checkForUpdates() {
    	
    	if (!this.updateCheck) {
    		return;
    	}
    	
    	SwingWorker<String, Object> checker = new SwingWorker<String, Object>() {
    		
    		private String newerVersion;
    		
			@Override
			public String doInBackground() {
		    	try {
		    		TimeUnit.SECONDS.sleep(3);
			        boolean hasInternet = false;
			        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
			        while (interfaces.hasMoreElements()) {
			          NetworkInterface interf = interfaces.nextElement();
			          if (interf.isUp()) {
			        	  if (!interf.isLoopback()) {
			        		hasInternet = true;
			        		break;
			        	  }
			          }
			        }
			        
			        if (!hasInternet) {
			        	//System.out.println("No Internet connection found");
			        	return null;
			        } else {
			        	//System.out.println("Internet connection found!");
			        	MavenVersion currentVersion
			        		= MavenVersion.fromString(GuiConstants.WREMJA_VERSION);
			        	
			        	URLConnection conn = UPDATE_URL.openConnection();
			        	
			        	XmlPullParser parser = new MXParser();
			        	parser.setInput(conn.getInputStream(), null);

			        	while (true) {
			        		int eventType = parser.next();
			        		
			        		if(eventType == XmlPullParser.END_DOCUMENT) {
			        			break;
			        		}
			        		
			        		if (eventType == XmlPullParser.START_TAG) {
			        			if ("versioning".equals(parser.getName())) {
			        				return parseVersioningElement(parser, currentVersion);
			        			}
			        		}
			        	}
			        }
		        } catch (Exception e) {
			        LOG.warn("", e);
		        }
		        return null;
			}

			private String parseVersioningElement(XmlPullParser parser, MavenVersion currentVersion) throws XmlPullParserException, IOException {
				while (true) {
					int eventType = parser.next();
	
					// seems like <release> is not necessarily what we want:
					// http://www.sonatype.com/people/2009/12/maven-dependency-resolution-a-repository-perspective/?utm_source=feedburner&utm_medium=feed&utm_campaign=Feed%3A+sonatype+%28Sonatype+Blogs%29&utm_content=Google+Reader
					
//					if(eventType == XmlPullParser.START_TAG
//							&& "release".equals(parser.getName())) {
//						String versionStr = parser.nextText();
//            			if (versionStr != null) {
//            				MavenVersion version = MavenVersion.fromString(versionStr.trim());
//            				if (version.isGreaterThan(currentVersion)) {
//            					this.newerVersion = versionStr.trim();
//            					return versionStr;
//            				}
//            			}
//					}
					
    				if (eventType == XmlPullParser.START_TAG && "versions".equals(parser.getName())) {
    					this.newerVersion = getNewerVersion(parser, currentVersion);
    					return newerVersion;
    				}
					
					if (eventType == XmlPullParser.END_TAG
							&& "versioning".equals(parser.getName())) {
						return null;
					}
				}
            }

			private String getNewerVersion(XmlPullParser parser,
                    MavenVersion currentVersion) throws XmlPullParserException, IOException {
	            while (true) {
	            	int eventType = parser.next();
	            	
	            	if (eventType == XmlPullParser.END_TAG) {
	            		if ("versions".equals(parser.getName())) {
	            			return null;
	            		}
	            	}
	            	
	            	if (eventType == XmlPullParser.START_TAG) {
	            		if ("version".equals(parser.getName())) {
	            			String versionStr = parser.nextText();
	            			if (versionStr != null) {
	            				MavenVersion version = MavenVersion.fromString(versionStr.trim());
	            				if (version.isGreaterThan(currentVersion)) {
	            					return versionStr;
	            				}
	            			}
	            		}
	            	}
	            }
            }

			@Override
            protected void done() {
				if (newerVersion != null) {
					JEditorPane msg = new JEditorPane();
					
					Color bg = (Color) UIManager.get("OptionPane.background", Locale.getDefault());
					msg.setBackground(bg);
					msg.setEditable(false);
					msg.setContentType("text/html");
					msg.setText("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">" +
							"<html>" +
							"<head>" +
							"<style>\n" + 
							"    a {text-decoration: underline; color: blue}\n" + 
							"</style>\n" + 
							"</head><body>A newer version (" +
							newerVersion + ") of Wremja is available.<br>Please download it from"
									+ " <a href=\"" + DOWNLOAD_URL + "\">here</a>.</body></html>!");
					msg.addHyperlinkListener(new HyperlinkListener() {
						@Override
						public void hyperlinkUpdate(HyperlinkEvent e) {
							if (e.getEventType().equals(EventType.ACTIVATED)) {
								try {
		                            Desktop.getDesktop().browse(e.getURL().toURI());
	                            } catch (Exception e1) {
		                            LOG.error("", e1);
	                            }
							}
						}
					});
					JOptionPane.showMessageDialog(mainFrame, msg);
				}
            }
		};
		checker.execute();
    }
}
