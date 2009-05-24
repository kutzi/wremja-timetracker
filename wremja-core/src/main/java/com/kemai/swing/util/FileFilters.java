package com.kemai.swing.util;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import com.kemai.util.TextResourceBundle;

/**
 * Container of misc {@link FileFilter}s for different file formats.
 * @author remast
 */
public abstract class FileFilters {

    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(FileFilters.class);

	/**
	 * Filter for Microsoft Excel files.
	 */
    public static final class ExcelFileFilter extends FileFilter {

        @Override
        public boolean accept(final File file) {
            return file.isDirectory() || file.getName().endsWith(".xls"); //$NON-NLS-1$
        }

        @Override
        public String getDescription() {
            return textBundle.textFor("FileFilters.MicrosoftExcelFile"); //$NON-NLS-1$
        }

    }
    
    /**
     * Filter for Comma Separated Value (CSV) files.
     */
    public static final class CsvFileFilter extends FileFilter {

        @Override
        public boolean accept(final File file) {
            return file.isDirectory() || file.getName().endsWith(".csv"); //$NON-NLS-1$
        }

        @Override
        public String getDescription() {
            return textBundle.textFor("FileFilters.CommaSeparatedValuesFile"); //$NON-NLS-1$
        }

    }

    /**
     * Export filter for data files.
     */
    public static class DataExportFileFilter extends FileFilter {

        @Override
        public boolean accept(final File file) {
            return file.isDirectory() || file.getName().endsWith(".ptd.xml"); //$NON-NLS-1$
        }

        @Override
        public String getDescription() {
            return textBundle.textFor("FileFilters.DataFile", "*.ptd.xml"); //$NON-NLS-1$
        }
    }
    
    /**
     * Import filter for data files.
     */
    public static final class DataImportFileFilter extends DataExportFileFilter {

        @Override
        public boolean accept(final File file) {
            if(super.accept(file)) {
                return true;
            }
            return file.getName().endsWith(".ptd"); //$NON-NLS-1$
        }
        
        @Override
        public String getDescription() {
            return textBundle.textFor("FileFilters.DataFile", "*.ptd.xml, *.ptd"); //$NON-NLS-1$
        }
    }

}
