package com.kemai.wremja.gui.actions;

import java.awt.Frame;

import javax.swing.ImageIcon;
import javax.swing.filechooser.FileFilter;

import com.kemai.swing.util.FileFilters;
import com.kemai.util.TextResourceBundle;
import com.kemai.wremja.gui.model.PresentationModel;
import com.kemai.wremja.gui.settings.UserSettings;
import com.kemai.wremja.model.export.ExcelExporter;
import com.kemai.wremja.model.export.Exporter;

/**
 * Exports all accumulated activities and all project activities 
 * into a Microsoft Excel file.
 * @author remast
 */
@SuppressWarnings("serial") //$NON-NLS-1$
public final class ExportExcelAction extends AbstractExportAction {

    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(ExportExcelAction.class);

    /** File extension of MS Excel files. */
    private static final String EXCEL_FILE_EXTENSION = ".xls";
    
    /** File filter for MS Excel files. */
    private static final FileFilter EXCEL_FILE_FILTER = new FileFilters.ExcelFileFilter();


    public ExportExcelAction(final Frame owner, final PresentationModel model) {
        super(owner, model);

        putValue(NAME, textBundle.textFor("ExcelExportAction.Name")); //$NON-NLS-1$
        putValue(SHORT_DESCRIPTION, textBundle.textFor("ExcelExportAction.ShortDescription")); //$NON-NLS-1$
        putValue(SMALL_ICON, new ImageIcon(getClass().getResource("/icons/gnome-mime-application-vnd.ms-excel.png"))); //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Exporter createExporter() {
        return new ExcelExporter();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getFileExtension() {
        return EXCEL_FILE_EXTENSION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected FileFilter getFileFilter() {
        return EXCEL_FILE_FILTER;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getLastExportLocation() {
        return UserSettings.instance().getLastExcelExportLocation();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setLastExportLocation(final String lastExportLocation) {
        UserSettings.instance().setLastExcelExportLocation(lastExportLocation);
    }

}
