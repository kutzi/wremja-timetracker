package com.kemai.wremja.exporter.anukotimetracker;

import static org.apache.commons.lang.StringUtils.isBlank;

import java.awt.Frame;
import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileFilter;

import com.kemai.wremja.exporter.anukotimetracker.gui.ExportDialog;
import com.kemai.wremja.gui.actions.AbstractExportAction;
import com.kemai.wremja.gui.model.PresentationModel;
import com.kemai.wremja.gui.settings.IUserSettings;
import com.kemai.wremja.model.export.Exporter;

@SuppressWarnings("serial")
public class AnukoExporterAction extends AbstractExportAction {

    public AnukoExporterAction(Frame owner, PresentationModel model,
            IUserSettings settings) {
        super(owner, model, settings);
        
        setName("Anuko Exporter");
        setTooltip("Exports projects to Anuko Timetracker");
        setIcon(new ImageIcon(AnukoExporterAction.class.getResource("/icons/anuko_icon.gif"))); //$NON-NLS-1$
    }

    @Override
    public Exporter createExporter() {
    	throw new UnsupportedOperationException();
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
    	IUserSettings settings = getSettings();
    	
    	
    	if(isBlank(settings.getAnukoLogin())
    			|| isBlank(settings.getAnukoPassword())
    			|| isBlank(settings.getAnukoUrl())) {
    		JOptionPane.showMessageDialog(getOwner(),
    				"Please specify Anuko login, password and URL in the settings!", 
    				"Error", JOptionPane.ERROR_MESSAGE);
    		return;
    	}
    	
    	
    	
        ExportDialog dialog = new ExportDialog(getOwner(),
        		settings,
                settings.getAnukoUrl(),
                settings.getAnukoLogin(),
                settings.getAnukoPassword(),
                getModel().getData(), getModel().getFilter() );
        dialog.setLocationByPlatform(true);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.pack();
        dialog.setVisible(true);
        
        if(dialog.isExport()) {
	        Exporter exporter = new AnukoExporter(getSettings().getAnukoUrl(),
	        		getSettings().getAnukoLogin(), getSettings().getAnukoPassword(),
	        		dialog.getMappings());
	        ExportWorker exportWorker = new ExportWorker(
	                getModel(), 
	                exporter, 
	                getOwner(), null);
	        exportWorker.execute();
        }
    }
}
