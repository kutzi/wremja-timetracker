package com.kemai.wremja.exporter.anukotimetracker;

import java.awt.Frame;
import java.awt.event.ActionEvent;

import javax.swing.WindowConstants;
import javax.swing.filechooser.FileFilter;

import com.kemai.wremja.exporter.anukotimetracker.gui.ExportDialog;
import com.kemai.wremja.gui.actions.AbstractExportAction;
import com.kemai.wremja.gui.model.PresentationModel;
import com.kemai.wremja.gui.settings.IUserSettings;
import com.kemai.wremja.model.export.Exporter;

@SuppressWarnings("serial")
public class AnukoExporterAction extends AbstractExportAction {

    private static final String LAST_URL = "LAST.URL";
    
    private final IUserSettings settings;
    
    public AnukoExporterAction(Frame owner, PresentationModel model,
            IUserSettings settings) {
        super(owner, model);
        this.settings = settings;
    }

    @Override
    public Exporter createExporter() {
        return new AnukoExporter();
    }

    @Override
    protected String getFileExtension() {
        // not needed here
        return null;
    }

    @Override
    protected FileFilter getFileFilter() {
        // not needed here
        return null;
    }

    @Override
    protected String getLastExportLocation() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void setLastExportLocation(String lastExportLocation) {
        throw new UnsupportedOperationException();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void actionPerformed(final ActionEvent event) {
        ExportDialog dialog = new ExportDialog(getOwner(),
                this.settings.getAnukoUrl(),
                this.settings.getAnukoLogin(),
                this.settings.getAnukoPassword(),
                getModel().getData(), getModel().getFilter() );
        dialog.setLocationByPlatform(true);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.pack();
        dialog.setVisible(true);
    }
}
