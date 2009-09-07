package com.kemai.wremja.gui.actions;

import java.awt.Frame;

import javax.swing.ImageIcon;
import javax.swing.filechooser.FileFilter;

import com.kemai.swing.util.FileFilters;
import com.kemai.util.TextResourceBundle;
import com.kemai.wremja.gui.model.PresentationModel;
import com.kemai.wremja.gui.settings.IUserSettings;
import com.kemai.wremja.model.export.CsvExporter;
import com.kemai.wremja.model.export.Exporter;

/**
 * Exports all project activities into a CSV (Comma Separated Value) file.
 * @author kutzi
 * @author remast
 */
@SuppressWarnings("serial") 
public final class ExportCsvAction extends AbstractExportAction {

    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(ExportCsvAction.class);

    /** File extension of CSV files. */
    private static final String CSV_FILE_EXTENSION = ".csv";
    
    /** File filter for CSV files. */
    private static final FileFilter CSV_FILE_FILTER = new FileFilters.CsvFileFilter();

    public ExportCsvAction(Frame owner, PresentationModel model, IUserSettings settings) {
        super(owner, model, settings);

        setName(textBundle.textFor("CsvExportAction.Name")); //$NON-NLS-1$
        setTooltip(textBundle.textFor("CsvExportAction.ShortDescription")); //$NON-NLS-1$
        setIcon(new ImageIcon(getClass().getResource("/icons/csv-file.png"))); //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Exporter createExporter() {
        return new CsvExporter();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getFileExtension() {
        return CSV_FILE_EXTENSION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected FileFilter getFileFilter() {
        return CSV_FILE_FILTER;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getLastExportLocation() {
        return getSettings().getLastCsvExportLocation();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setLastExportLocation(final String lastExportLocation) {
        getSettings().setLastCsvExportLocation(lastExportLocation);
    }

}
