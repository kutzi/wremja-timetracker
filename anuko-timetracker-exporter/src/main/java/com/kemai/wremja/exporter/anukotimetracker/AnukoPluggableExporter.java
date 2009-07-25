package com.kemai.wremja.exporter.anukotimetracker;

import java.awt.Frame;

import com.kemai.wremja.gui.actions.AbstractWremjaAction;
import com.kemai.wremja.gui.model.PluggableExporter;
import com.kemai.wremja.gui.model.PresentationModel;
import com.kemai.wremja.gui.settings.Configuration;

public class AnukoPluggableExporter implements PluggableExporter {

    private Configuration settings;
    
    @Override
    public AbstractWremjaAction getExportAction(Frame owner, PresentationModel model) {
        //return new AnukoExporterAction(owner, model, settings);
        
        // FIXME
        return null;
    }

    @Override
    public void setConfiguration( Configuration configuration ) {
        this.settings = configuration;
    }

}
