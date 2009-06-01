package com.kemai.wremja.gui.actions;

import java.awt.Frame;

import javax.swing.ImageIcon;
import javax.swing.filechooser.FileFilter;

import com.kemai.swing.util.FileFilters;
import com.kemai.util.TextResourceBundle;
import com.kemai.wremja.gui.model.PresentationModel;
import com.kemai.wremja.gui.settings.UserSettings;
import com.kemai.wremja.model.export.Exporter;
import com.kemai.wremja.model.export.RawDataExporter;

/**
 * Action to export data to a data file.
 * @author remast
 */
@SuppressWarnings("serial") 
public class ExportDataAction extends AbstractExportAction {
    
    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(ExportDataAction.class);
    
    /** File extension of data files. */
    private static final String DATA_FILE_EXTENSION = ".ptd.xml";
    
    /** File filter for data files. */
    private static final FileFilter DATA_FILE_FILTER = new FileFilters.DataExportFileFilter();


    public ExportDataAction(final Frame owner, final PresentationModel model) {
        super(owner, model);

        setName(textBundle.textFor("ExportDataAction.Name")); //$NON-NLS-1$
        setTooltip(textBundle.textFor("ExportDataAction.ShortDescription")); //$NON-NLS-1$
        putValue(SMALL_ICON, new ImageIcon(getClass().getResource("/icons/package-x-generic.png"))); //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Exporter createExporter() {
        return new RawDataExporter();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getFileExtension() {
        return DATA_FILE_EXTENSION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected FileFilter getFileFilter() {
        return DATA_FILE_FILTER;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getLastExportLocation() {
        return UserSettings.instance().getLastDataExportLocation();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setLastExportLocation(final String lastExportLocation) {
        UserSettings.instance().setLastDataExportLocation(lastExportLocation);
    }
}
