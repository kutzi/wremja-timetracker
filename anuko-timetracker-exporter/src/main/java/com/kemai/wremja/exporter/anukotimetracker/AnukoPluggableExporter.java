package com.kemai.wremja.exporter.anukotimetracker;

import java.awt.Frame;

import org.apache.commons.configuration.Configuration;

import com.kemai.wremja.gui.actions.AbstractWremjaAction;
import com.kemai.wremja.gui.model.PluggableExporter;
import com.kemai.wremja.gui.model.PresentationModel;

public class AnukoPluggableExporter implements PluggableExporter {

    private Configuration settings;
    
    @Override
    public AbstractWremjaAction getExportAction(Frame owner, PresentationModel model) {
        return new AnukoExporterAction(owner, model, settings);
    }

    @Override
    public void setConfiguration( Configuration configuration ) {
        this.settings = configuration;
    }

}
