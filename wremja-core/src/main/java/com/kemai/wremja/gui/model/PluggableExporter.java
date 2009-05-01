package com.kemai.wremja.gui.model;

import java.awt.Frame;

import com.kemai.wremja.gui.actions.AbstractWremjaAction;
import com.kemai.wremja.gui.settings.Configuration;

public interface PluggableExporter {
    public AbstractWremjaAction getExportAction(Frame owner, PresentationModel model);
    public void setConfiguration( Configuration configuration );
}
