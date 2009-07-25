package com.kemai.wremja.exporter.anukotimetracker.gui;

import info.clearthought.layout.TableLayout;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import com.kemai.swing.dialog.EscapeDialog;
import com.kemai.util.TextResourceBundle;
import com.kemai.wremja.exporter.anukotimetracker.model.AnukoInfo;
import com.kemai.wremja.exporter.anukotimetracker.model.Mapping;
import com.kemai.wremja.exporter.anukotimetracker.util.AnukoAccess;
import com.kemai.wremja.gui.settings.IUserSettings;
import com.kemai.wremja.logging.Logger;
import com.kemai.wremja.model.ProjectActivity;
import com.kemai.wremja.model.ReadableRepository;
import com.kemai.wremja.model.filter.Filter;

/**
 * Dialog for exporting to Anuko time tracker
 */
public class ExportDialog extends EscapeDialog {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = Logger.getLogger(ExportDialog.class);
    
    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(ExportDialog.class);

    private final AnukoAccess anukoAccess;
    
    private AnukoInfo anukoInfo;
    
    private final ReadableRepository data;

    private final Filter filter;

    private final ProjectMappingPanel panel;
    
    private JLabel warningLabel;
    
    private JButton exportButton;

    private DateTime startDate;

    private DateTime endDate;

	private final IUserSettings settings;
	
	private boolean export = false;

    /**
     * Create a new dialog.
     * @param owner
     * @param anukoMappings 
     * @param model
     */
    public ExportDialog(Frame owner, 
    		IUserSettings settings, String url, String login, String password,
            ReadableRepository data, Filter filter ) {
        super(owner);
		this.settings = settings;
        if( filter == null ) {
            throw new NullPointerException("filter must not be null!");
        }
        this.anukoAccess = new AnukoAccess( url, login, password );
        this.data = data;
        this.filter = filter;

        initRelevantDates();
        updateInfo();

        this.panel = new ProjectMappingPanel( this.anukoInfo, this.data, this.filter,
        		this.settings.getAnukoMappings(),
        		getExportButton() );
        
        initialize();
    }
    
    private void initRelevantDates() {
        List<ProjectActivity> activities = this.filter.applyFilters(this.data.getActivities());

        if( ! activities.isEmpty() ) {
            Collections.sort(activities);
            // activities are now sorted in reverse order!
            // I.e. the latest activity is the first element!

            this.endDate = activities.get(0).getDay();
            this.startDate = activities.get(activities.size() - 1).getDay();
        }
    }

    /**
     * Sets up GUI components.
     */
    private void initialize() {
        getWarningLabel();
        
        setLocationRelativeTo(getOwner());
        //this.setIconImage(new ImageIcon(getClass().getResource("/icons/gtk-add.png")).getImage()); //$NON-NLS-1$
        this.setMinimumSize(new Dimension(250, 100));
        setTitle(textBundle.textFor("ExportDialog.Title")); //$NON-NLS-1$
        setModal(true);

        initializeLayout();

        // Set default Button to AddActtivityButton.
        this.getRootPane().setDefaultButton(exportButton);
    }

    private void updateInfo() {
        if( this.startDate != null ) {
            try {
                this.anukoInfo = this.anukoAccess.getMergedAnukoInfo(this.startDate, this.endDate);
                
                if( anukoInfo.getDailyTime().isLongerThan(Duration.ZERO)) {
                    
                    DecimalFormat df = new DecimalFormat("0.00");
                    double hours = anukoInfo.getDailyTime().getStandardSeconds() / (60D * 60);
                    this.warningLabel.setText("<html><b>" +
                    		"You have already " + df.format(hours) + "h recorded for that period!<br>" +
                    		"Exported activities will be added to the existing!</b></html>");
                }
            } catch (Exception e) {
                LOG.error(e, e);
            }
        } else {
            this.anukoInfo = new AnukoInfo();
        }
    }
    
    private void initializeLayout() {
        final double border = 5;
        final double size[][] = {
                { border, TableLayout.PREFERRED, border, TableLayout.FILL, border, TableLayout.PREFERRED, border }, // Columns
                { border, TableLayout.PREFERRED, border, TableLayout.FILL, border,
                    TableLayout.PREFERRED, border, TableLayout.PREFERRED, border } }; // Rows

        final TableLayout tableLayout = new TableLayout(size);
        this.setLayout(tableLayout);
        
        this.add( new JLabel("URL :"), "1, 1");
        this.add( new JLabel(this.anukoAccess.getUrl()), "3, 1");
        
        this.add(this.panel, "0, 3, 4, 3");
        
        this.add(getWarningLabel(), "1, 5, 3, 5");

        this.add(getExportButton(), "1, 7, 3, 7");
    }

    private JLabel getWarningLabel() {
        if( warningLabel == null ) {
            warningLabel = new JLabel();
            warningLabel.setForeground(Color.RED);
            warningLabel.setText("<html><br></html>");
        }
        return warningLabel;
    }

    private JButton getExportButton() {
        if (exportButton == null) {
            exportButton = new JButton();
            exportButton.setText(textBundle.textFor("ExportDialog.ExportLabel")); //$NON-NLS-1$
            //addActivityButton.setIcon(new ImageIcon(getClass().getResource("/icons/gtk-add.png"))); //$NON-NLS-1$

            // Confirm with 'Enter' key
            exportButton.setMnemonic(KeyEvent.VK_ENTER);

            exportButton.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent event) {
                    settings.setAnukoMappings(panel.getMappings().toString());
                    export = true;
                    ExportDialog.this.dispose();
                }
            });
            exportButton.setEnabled(false);

            exportButton.setDefaultCapable(true);
        }
        return exportButton;
    }

    public Mapping getMappings() {
    	return this.panel.getMappings();
    }
    
    public boolean isExport() {
    	return this.export;
    }
}
