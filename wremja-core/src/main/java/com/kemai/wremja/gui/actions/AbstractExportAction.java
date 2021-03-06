package com.kemai.wremja.gui.actions;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileFilter;

import com.kemai.util.TextResourceBundle;
import com.kemai.wremja.gui.model.PresentationModel;
import com.kemai.wremja.gui.settings.IUserSettings;
import com.kemai.wremja.logging.Logger;
import com.kemai.wremja.model.export.Exporter;

/**
 * Base action for all data exports.
 * @author remast
 */
@SuppressWarnings("serial")
public abstract class AbstractExportAction extends AbstractWremjaAction {

    private static final Logger log = Logger.getLogger(AbstractExportAction.class);

    private final IUserSettings settings;
    
    public AbstractExportAction(Frame owner, PresentationModel model, IUserSettings settings) {
        super(owner, model);
        this.settings = settings;
    }

    /**
     * Creates a new instance of the exporter.
     * @return the new exporter instance
     */
    public abstract Exporter createExporter();

    protected IUserSettings getSettings() {
    	return this.settings;
    }
    
    /**
     * Getter for the last export location.
     * @return the location of the last export or <code>null</code>
     */
    protected abstract String getLastExportLocation();

    /**
     * Setter for the last export location
     * @param lastExportLocation the last export location to set
     */
    protected abstract void setLastExportLocation(final String lastExportLocation);

    /**
     * Getter for the file filter to be used for the export.
     * @return the file filter to be used for the export
     */
    protected abstract FileFilter getFileFilter();

    /**
     * Getter for the file extension to be used for the export.
     * @return the file extension to be used for the export
     */
    protected abstract String getFileExtension();

    /**
     * {@inheritDoc}
     */
    @Override
    public void actionPerformed(final ActionEvent event) {
        final JFileChooser chooser = new JFileChooser();

        // Set selection to last export location
        String lastExportLocation = getLastExportLocation();
        if(lastExportLocation != null) {
            chooser.setSelectedFile(new File(lastExportLocation));
        } else {
            chooser.setCurrentDirectory(null);
        }

        chooser.setFileFilter(getFileFilter());

        int returnVal = chooser.showSaveDialog(getOwner());
        if (JFileChooser.APPROVE_OPTION == returnVal) {
            File file = chooser.getSelectedFile();
            if (!file.getAbsolutePath().endsWith(getFileExtension())) {
                file = new File(file.getAbsolutePath() + getFileExtension());
            }

            final ExportWorker exportWorker = new ExportWorker(
                    getModel(), 
                    createExporter(), 
                    getFileFilter(),
                    getOwner(),
                    file);
            exportWorker.execute();
        }

    }
    
    private OutputStream getFileOutputStream(File f, FileFilter fileFilter, Frame owner) {
        try {
            return new BufferedOutputStream(new FileOutputStream(f));
        } catch (FileNotFoundException e) {
            log.error(e, e);
            TextResourceBundle bundle = TextResourceBundle.getBundle(getClass());
            JOptionPane.showMessageDialog(
                    owner, 
                    bundle.textFor("AbstractExportAction.IOException.Message", f.getAbsolutePath(), e.getLocalizedMessage()), //$NON-NLS-1$
                    bundle.textFor("AbstractExportAction.IOException.Heading", fileFilter.getDescription()), //$NON-NLS-1$
                    JOptionPane.ERROR_MESSAGE
            );
            return null;
        }
    }

    /**
     * Worker thread to perform the actual export in the background.
     * @author remast
     */
    protected class ExportWorker extends SwingWorker<String, Object> {
        
        private PresentationModel model;
        private File file;
        private Exporter exporter;
        private Frame owner;
        private FileFilter fileFilter;

        public ExportWorker(final PresentationModel model, final Exporter exporter, final FileFilter fileFilter, final Frame owner, final File file) {
            this.model = model;
            this.exporter = exporter;
            this.owner = owner;
            this.fileFilter = fileFilter;
            this.file = file;
        }
        
        @Override
        public String doInBackground() {

            try {
            	try (OutputStream out = getFileOutputStream(file, fileFilter, owner)) {

	                synchronized ( model.getData() ) {
	                    exporter.export(
	                            model.getData(),
	                            model.getFilter(),
	                            out
	                    );
	                }
	                
	                // Make sure everything is written.
	                out.flush();
	
	                // Store export location in settings
	                setLastExportLocation(file.getAbsolutePath());
        		}
            } catch (Exception e) {
                log.error(e, e);
                TextResourceBundle bundle = TextResourceBundle.getBundle(getClass());
                JOptionPane.showMessageDialog(
                        owner, 
                        bundle.textFor("AbstractExportAction.IOException.Message", file.getAbsolutePath(), e.getLocalizedMessage()), //$NON-NLS-1$
                        bundle.textFor("AbstractExportAction.IOException.Heading", fileFilter.getDescription()), //$NON-NLS-1$
                        JOptionPane.ERROR_MESSAGE
                );
            }
            return null;
        }
    }

}
