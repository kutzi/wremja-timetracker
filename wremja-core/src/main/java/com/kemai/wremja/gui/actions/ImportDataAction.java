package com.kemai.wremja.gui.actions;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import com.kemai.swing.util.FileFilters;
import com.kemai.util.TextResourceBundle;
import com.kemai.wremja.gui.model.PresentationModel;
import com.kemai.wremja.logging.Logger;
import com.kemai.wremja.model.ActivityRepository;
import com.kemai.wremja.model.io.ProTrackReader;

/**
 * Action to import data from a data file.
 * @author remast
 */
@SuppressWarnings("serial") 
public class ImportDataAction extends AbstractWremjaAction {

    /** The logger. */
    private static final Logger log = Logger.getLogger(ImportDataAction.class);

    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(ImportDataAction.class);

    public ImportDataAction(final Frame owner, final PresentationModel model) {
        super(owner, model);

        setName(textBundle.textFor("ImportDataAction.Name")); //$NON-NLS-1$
        setTooltip(textBundle.textFor("ImportDataAction.ShortDescription")); //$NON-NLS-1$
        putValue(SMALL_ICON, new ImageIcon(getClass().getResource("/icons/package-x-generic.png"))); //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void actionPerformed(final ActionEvent event) {
        final JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileFilters.DataImportFileFilter());

        int returnVal = chooser.showOpenDialog(getOwner());
        if (JFileChooser.APPROVE_OPTION == returnVal) {
            final File file = chooser.getSelectedFile();
            final ProTrackReader reader = new ProTrackReader();
            try {
                final ActivityRepository data = reader.read(file);

                boolean doImport = true;
                final int dialogResult = JOptionPane.showConfirmDialog(
                        getOwner(), 
                        textBundle.textFor("ImportDataAction.Message"),  //$NON-NLS-1$
                        textBundle.textFor("ImportDataAction.Title"),  //$NON-NLS-1$
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.INFORMATION_MESSAGE
                );
                doImport = JOptionPane.YES_OPTION == dialogResult;

                if (doImport) {
                    getModel().setData(data);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, textBundle.textFor("ImportDataAction.IOException.Message", file.getAbsolutePath()), textBundle.textFor("ImportDataAction.IOException.Heading"), //$NON-NLS-1$ //$NON-NLS-2$ 
                        JOptionPane.ERROR_MESSAGE);
                log.error(e, e);
            }
        }
    }

}
