package com.kemai.wremja.exporter.anukotimetracker;

import java.awt.Frame;
import java.awt.event.ActionEvent;

import javax.swing.WindowConstants;
import javax.swing.filechooser.FileFilter;

import org.apache.commons.configuration.Configuration;

import com.kemai.wremja.exporter.anukotimetracker.gui.ExportDialog;
import com.kemai.wremja.gui.actions.AbstractExportAction;
import com.kemai.wremja.gui.model.PresentationModel;
import com.kemai.wremja.model.export.Exporter;

@SuppressWarnings("serial")
public class AnukoExporterAction extends AbstractExportAction {

    private static final String LAST_URL = "LAST.URL";
    
    private final Configuration settings;
    
    public AnukoExporterAction(Frame owner, PresentationModel model,
            Configuration settings) {
        super(owner, model);
        this.settings = settings;
    }

    public Exporter createExporter() {
        return new AnukoExporter();
    }

    protected String getFileExtension() {
        // not needed here
        return null;
    }

    protected FileFilter getFileFilter() {
        // not needed here
        return null;
    }

    protected String getLastExportLocation() {
        return this.settings.getString(LAST_URL);
    }

    protected void setLastExportLocation(String lastExportLocation) {
        this.settings.setProperty(LAST_URL, lastExportLocation);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void actionPerformed(final ActionEvent event) {
        ExportDialog dialog = new ExportDialog(getOwner(), getLastExportLocation(),
                getModel().getData(), getModel().getFilter() );
        dialog.setLocationByPlatform(true);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.pack();
        dialog.setVisible(true);
        
        setLastExportLocation(dialog.getLastUrl());
    }

}
