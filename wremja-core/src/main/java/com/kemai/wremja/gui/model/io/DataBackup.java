package com.kemai.wremja.gui.model.io;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;

import com.kemai.util.IOUtils;
import com.kemai.wremja.gui.settings.ApplicationSettings;
import com.kemai.wremja.gui.settings.UserSettings;
import com.kemai.wremja.logging.Logger;

/**
 * Misc utility methods for creating and reading backups.
 * @author remast
 */
public class DataBackup {

    /** The logger. */
    private static final Logger LOG = Logger.getLogger(DataBackup.class);

    /** The date format for dates used in the names of backup files. */
    private static final SimpleDateFormat BACKUP_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd_HHmmss");

    /** The full path of the backed up corrupt data file. */
    private static final String ERROR_FILE_PATH = UserSettings.instance().getDataFileLocation() + ".Error";

    /** The name of the backed up corrupt data file. */
    private static final String ERROR_FILE_NAME = UserSettings.DEFAULT_FILE_NAME + ".Error";

    /** The number of backup files to keep. */
    private static final int NUMBER_OF_BACKUPS = 5;

    /**
     * Create a backup from given file.
     * @param toBackup the file to be backed up
     */
//    public static void createBackup(final File toBackup) {
//        if (toBackup == null || !toBackup.exists()) {
//            return;
//        }
//
//        try {
//            IOUtils.copyFile(
//                    toBackup, 
//                    new File(UserSettings.instance().getDataFileLocation() + "." + BACKUP_DATE_FORMAT.format(new Date())),
//                    true
//            );
//            cleanupBackupFiles();
//        } catch (Exception e) {
//            LOG.error(e, e);
//        }
//    }
    
    /**
     * Renames the argument to a backup file.
     * 
     * @param file the file
     */
    public static void toBackup(File file) {
    	if (file == null || !file.exists()) {
            return;
        }
    	
    	File backupFile = new File(UserSettings.instance().getDataFileLocation() + "." + BACKUP_DATE_FORMAT.format(new Date()));
    	if(file.renameTo(backupFile)) {
    		cleanupBackupFiles();
    	} else {
    		LOG.error("Couldn't rename to " + backupFile.getAbsolutePath());
    	}
    }

    /**
     * Cleans up old backup files so that not more backup files than <code>NUMBER_OF_BACKUPS</code> exist.
     */
    private static void cleanupBackupFiles() {
        final List<File> backupFiles = getBackupFiles();
        if (backupFiles != null && backupFiles.size() > NUMBER_OF_BACKUPS) {
            final int numberOfFilesToDelete = backupFiles.size() - NUMBER_OF_BACKUPS;

            for (int i = 1; i <= numberOfFilesToDelete; i++) {
                final File toDelete = backupFiles.get(backupFiles.size() - i);
                final boolean successfull = toDelete.delete();
                if (!successfull) {
                    LOG.error("Could not delete file " + toDelete.getAbsolutePath() + ".");
                }
            }
        }
    }

    /**
     * Get a list of all backup files in order of the backup date (with the latest backup as first). If 
     * there there are no backups <code>Collections.EMPTY_LIST</code> is returned.
     * @return the list of backup files
     */
    public static List<File> getBackupFiles()  {
        final SortedMap<Date, File> sortedBackupFiles = new TreeMap<Date, File>();

        final File dir = ApplicationSettings.instance().getApplicationDataDirectory();
        final String [] backupFiles = dir.list(new FilenameFilter() {

            public boolean accept(final File dir, final String name) {
                if (!StringUtils.equals(ERROR_FILE_NAME, name) 
                        && !StringUtils.equals(UserSettings.DEFAULT_FILE_NAME, name) 
                        && name.startsWith(UserSettings.DEFAULT_FILE_NAME)) {
                    return true;
                }

                return false;
            }

        });

        if (backupFiles == null) {
            return Collections.emptyList();
        }

        for (String backupFile : backupFiles) {
            try {
            	final Date backupDate = BACKUP_DATE_FORMAT.parse(backupFile.substring(UserSettings.DEFAULT_FILE_NAME.length()+1));
                sortedBackupFiles.put(backupDate, new File(ApplicationSettings.instance().getApplicationDataDirectory() + File.separator + backupFile));
            } catch (ParseException e) {
                // ignore
            }
        }

        // Order the list by the date of the backup with the latest backup at front.
        final List<File> backupFileList = new ArrayList<File>(sortedBackupFiles.size());
        final int numberOfBackups = sortedBackupFiles.size();
        for (int i = 0; i < numberOfBackups; i++) {
            final Date backupDate = sortedBackupFiles.lastKey();

            backupFileList.add(sortedBackupFiles.get(backupDate));
            sortedBackupFiles.remove(backupDate);
        }

        return backupFileList;
    }

    /**
     * Get the date on which the backup file has been created. 
     * @param backupFile the backup file to get date for
     * @return The date on which the backup file has been created. If no date could be inferred <code>null</code> is returned.
     */
    public static Date getDateOfBackup(final File backupFile) {
        try {
            return BACKUP_DATE_FORMAT.parse(backupFile.getName().substring(UserSettings.DEFAULT_FILE_NAME.length()+1));
        } catch (Exception e) {
            LOG.error(e, e);
            return null;
        }
    }

    /**
     * Make a backup copy of the corrupt data file.
     */
    public static void saveCorruptDataFile() {
        try {
            IOUtils.copyFile(new File(UserSettings.instance().getDataFileLocation()), new File(ERROR_FILE_PATH), true);
        } catch (IOException e) {
            LOG.error(e, e);
        }
    }
}
