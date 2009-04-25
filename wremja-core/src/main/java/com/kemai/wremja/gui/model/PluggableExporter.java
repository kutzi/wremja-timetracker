package com.kemai.wremja.gui.model;

import java.awt.Frame;

import org.apache.commons.configuration.Configuration;

import com.kemai.wremja.gui.actions.AbstractWremjaAction;

public interface PluggableExporter {
    public AbstractWremjaAction getExportAction(Frame owner, PresentationModel model);
    public void setConfiguration( Configuration configuration );
}
