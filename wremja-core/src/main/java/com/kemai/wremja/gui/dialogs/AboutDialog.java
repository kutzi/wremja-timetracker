package com.kemai.wremja.gui.dialogs;

import info.clearthought.layout.TableLayout;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.net.MalformedURLException;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JLabel;

import org.jdesktop.swingx.JXHyperlink;
import org.jdesktop.swingx.JXImagePanel;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.action.OpenBrowserAction;

import com.kemai.swing.dialog.EscapeDialog;
import com.kemai.util.TextResourceBundle;
import com.kemai.wremja.gui.GuiConstants;
import com.kemai.wremja.logging.Logger;

/**
 * Displays information about the application like version and homepage.
 * @author remast
 * @author kutzi
 */
@SuppressWarnings("serial") 
public class AboutDialog extends EscapeDialog {
	
	private static final Logger LOGGER = Logger.getLogger(AboutDialog.class);

    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(AboutDialog.class);

    /**
     * Creates a new dialog.
     * @param owner the owning frame
     */
    public AboutDialog(final Frame owner) {
        super(owner);
        
        setName("aboutDialog"); //$NON-NLS-1$
        setTitle(textBundle.textFor("AboutDialog.Title")); //$NON-NLS-1$
        setAlwaysOnTop(true);
        setModal(true);
        setResizable(false);
        setBackground(GuiConstants.BEIGE);
        
        try {
	        initialize();
        } catch (MalformedURLException e) {
	        LOGGER.error("", e);
        }
    }

    /**
     * Set up GUI components.
     * @throws MalformedURLException 
     */
    private void initialize() throws MalformedURLException {
        setLayout(new BorderLayout());
        
        final JXImagePanel image = new JXImagePanel(getClass().getResource("/icons/Wremja-About.png")); //$NON-NLS-1$
        image.setBackground(GuiConstants.BEIGE);
        
        final JXPanel aboutInfo = new JXPanel();
        aboutInfo.setBackground(GuiConstants.BEIGE);
        final double border = 5;
        final double size[][] = { { border, TableLayout.PREFERRED, border, TableLayout.FILL, border }, // Columns
                { border, TableLayout.PREFERRED, border, TableLayout.PREFERRED, border*2} }; // Rows

        final TableLayout tableLayout = new TableLayout(size);
        aboutInfo.setLayout(tableLayout);
        
        aboutInfo.add(new JLabel("<html><b>" + textBundle.textFor("AboutDialog.HomepageLabel") + "</b></html>"), "1, 1"); //$NON-NLS-1$ //$NON-NLS-2$
        OpenBrowserAction action = new OpenBrowserAction("http://wremja.kenai.com"); //$NON-NLS-1$
        action.putValue(Action.NAME, "http://wremja.kenai.com"); //$NON-NLS-1$
        action.putValue(Action.SHORT_DESCRIPTION, textBundle.textFor("AboutDialog.HomepageUrl")); //$NON-NLS-1$
        final JXHyperlink hyperlinkHomepage = new JXHyperlink(action);
        aboutInfo.add(hyperlinkHomepage, "3, 1"); //$NON-NLS-1$

        action = new OpenBrowserAction("http://kenai.com/jira/browse/WREMJA"); //$NON-NLS-1$
        action.putValue(Action.NAME, "http://kenai.com/jira/browse/WREMJA"); //$NON-NLS-1$
        action.putValue(Action.SHORT_DESCRIPTION, textBundle.textFor("AboutDialog.BugUrl")); //$NON-NLS-1$
        aboutInfo.add(new JLabel("<html><b>" + textBundle.textFor("AboutDialog.BugLabel") + "</b></html>"), "1, 3"); //$NON-NLS-1$ //$NON-NLS-2$
        final JXHyperlink hyperlinkBug = new JXHyperlink(action);
        aboutInfo.add(hyperlinkBug, "3, 3"); //$NON-NLS-1$
        
        this.add(aboutInfo, BorderLayout.CENTER);
      
        // Get storage mode from ApplicationSettings
//        String storageMode = null;
//        if (ApplicationSettings.instance().isStoreDataInApplicationDirectory()) {
//            storageMode = textBundle.textFor("Settings.DataStorage.PortableLabel");
//        } else {
//            storageMode = textBundle.textFor("Settings.DataStorage.NormalLabel");
//        }
        
        
        String javaVersion = System.getProperty("java.version");
        final String versionInfo = "<html>" +
        		"<font color=blue size=\"big\"><h2><b>" +
        		textBundle.textFor("Global.Version") +
        		":</b> " + GuiConstants.WREMJA_VERSION +
        		"<br><b>Revision:</b> " + GuiConstants.WREMJA_REVISION +
        		"<br><b>Timestamp:</b> " + GuiConstants.WREMJA_TIMESTAMP +
        		//"<br><b>Application mode:</b> " + storageMode +
        		"<br><b>Java version:</b> " + javaVersion + "</h2></font>" +
        		"</html>";
        final JLabel versionLabel = new JXLabel(versionInfo, JLabel.CENTER);
        versionLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        
        this.add(versionLabel, BorderLayout.SOUTH);
        this.getContentPane().setBackground(GuiConstants.BEIGE);
        this.add(image, BorderLayout.NORTH);
        
        this.pack();
    }
    
}
