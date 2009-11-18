package com.kemai.wremja.gui;

import static com.kemai.wremja.gui.GuiConstants.ACTIVE_ICON;
import static com.kemai.wremja.gui.GuiConstants.NORMAL_ICON;
import info.clearthought.layout.TableLayout;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import org.jdesktop.swingx.JXFrame;
import org.joda.time.DateTime;

import com.jgoodies.looks.HeaderStyle;
import com.jgoodies.looks.Options;
import com.kemai.swing.util.WMenu;
import com.kemai.swing.util.WMenuBar;
import com.kemai.util.DateUtils;
import com.kemai.util.TextResourceBundle;
import com.kemai.wremja.FormatUtils;
import com.kemai.wremja.gui.actions.AboutAction;
import com.kemai.wremja.gui.actions.AbstractExportAction;
import com.kemai.wremja.gui.actions.AbstractWremjaAction;
import com.kemai.wremja.gui.actions.AddActivityAction;
import com.kemai.wremja.gui.actions.ExitAction;
import com.kemai.wremja.gui.actions.ExportCsvAction;
import com.kemai.wremja.gui.actions.ExportDataAction;
import com.kemai.wremja.gui.actions.ExportExcelAction;
import com.kemai.wremja.gui.actions.ImportDataAction;
import com.kemai.wremja.gui.actions.ManageProjectsAction;
import com.kemai.wremja.gui.actions.SettingsAction;
import com.kemai.wremja.gui.dialogs.AddOrEditActivityDialog;
import com.kemai.wremja.gui.events.WremjaEvent;
import com.kemai.wremja.gui.model.PresentationModel;
import com.kemai.wremja.gui.model.ProjectActivityStateException;
import com.kemai.wremja.gui.panels.ActivityPanel;
import com.kemai.wremja.gui.panels.ReportPanel;
import com.kemai.wremja.gui.settings.IUserSettings;
import com.kemai.wremja.logging.Logger;
import com.kemai.wremja.model.ProjectActivity;

/**
 * The main frame of the application.
 * @author remast
 * @author kutzi
 */
@SuppressWarnings("serial")
public class MainFrame extends JXFrame implements Observer {
	
    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(MainFrame.class);

    private static final Logger LOG = Logger.getLogger(MainFrame.class);
    
    /** The model. */
    private final PresentationModel model;
    
    private final IUserSettings settings;

    /** The tool bar. */
    private final JToolBar toolBar = new JToolBar();

    /**
     * The panel with details about the current activity. Like the current project and description.
     */
    private JPanel currentActivityPanel = null;

    // ------------------------------------------------
    // Other stuff
    // ------------------------------------------------

    /** The filtered report. */
    private ReportPanel reportPanel;


    // ------------------------------------------------
    // The menus
    // ------------------------------------------------

    /** The menu bar containing all menus. */
    private JMenuBar mainMenuBar = null;

    /** The file menu. */
    private JMenu fileMenu = null;

    /** The help menu. */
    private JMenu helpMenu = null;

    /** The export menu. */
    private JMenu exportMenu = null;

    /** The import menu. */
    private JMenu importMenu = null;

    /** The edit menu. */
    private JMenu editMenu = null;


    // ------------------------------------------------
    // The menu items
    // ------------------------------------------------

    private JMenuItem addActivityMenuItem = null;

    private JMenuItem editProjectsMenuItem = null;

    private JMenuItem exportExcelItem = null;

    private JMenuItem exportCsvItem = null;

    private JMenuItem exportDataItem = null;

    private JMenuItem exitItem = null;

    private JMenuItem importItem = null;
    
    /** The Tray icon, if any. */
    private final TraySupport tray;

    /**
     * This is the default constructor.
     * @param model the model
     */
    public MainFrame(final PresentationModel model, IUserSettings settings) {
        super();

        this.model = model;
        this.model.addObserver(this);
        this.settings = settings;

        initialize();
        this.tray = new TraySupport(this.model, this);
        if (this.settings.isUseTrayIcon()) {
        	this.tray.enable();
        }
    }

    /**
     * Set up GUI components.
     */
    private void initialize() {
        this.setResizable(true);
        
        if (this.settings.isRememberWindowSizeLocation()) {
            this.setSize(this.settings.getWindowSize());
            this.setLocation(this.settings.getWindowLocation());
        } else {
            this.setSize(530, 720);
        }
        
        this.addComponentListener(new ComponentAdapter() {

            @Override
            public void componentMoved(ComponentEvent e) {
                settings.setWindowLocation(MainFrame.this.getLocation());
            }

            @Override
            public void componentResized(ComponentEvent e) {
                settings.setWindowSize(MainFrame.this.getSize());
            }
        });
        
        
        this.setJMenuBar(getMainMenuBar());

        // 1. Init start-/stop-Buttons
        if (this.model.isActive()) {
            this.setIconImage(ACTIVE_ICON);
        } else {
            this.setIconImage(NORMAL_ICON);
        }
        updateTitle();

        // 3. Set layout
        final double[][] size = { 
                {TableLayout.FILL }, // Columns
                {TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.FILL} // Rows
        };

        TableLayout tableLayout = new TableLayout(size);
        this.setLayout(tableLayout);
        this.add(initToolBar(), "0, 0");
        JSplitPane splitPane = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                true,
                getCurrentActivityPanel(),
                getReportPanel());
        splitPane.setDividerSize(2);
        this.add(splitPane, "0, 1, 0, 2");
    }
    
    private ReportPanel getReportPanel() {
        if (reportPanel == null) {
            reportPanel = new ReportPanel(this.model, this.settings);
        }
        return reportPanel;
    }

    /**
     * This method initializes mainMenuBar.
     * @return javax.swing.JMenuBar
     */
    private JMenuBar getMainMenuBar() {
        if (mainMenuBar == null) {
            mainMenuBar = new WMenuBar();
            mainMenuBar.add(getFileMenu());
            mainMenuBar.add(getEditMenu());
            mainMenuBar.add(getHelpMenu());
            mainMenuBar.putClientProperty(Options.HEADER_STYLE_KEY, HeaderStyle.BOTH);
        }
        return mainMenuBar;
    }

    /**
     * This method initializes toolBar.
     * @return javax.swing.JToolBar
     */
    public JToolBar initToolBar() {
        toolBar.setFloatable(false);

        toolBar.add(new ManageProjectsAction(this, this.model));
        
        toolBar.add(new AddActivityAction(this, this.model));
        toolBar.add(new JToolBar.Separator());
        toolBar.add(new ExportExcelAction(this, this.model, this.settings));
        toolBar.add(new ExportCsvAction(this, this.model, this.settings));
        toolBar.add(new JToolBar.Separator());
        toolBar.add(this.model.getEditStack().getUndoAction());
        toolBar.add(this.model.getEditStack().getRedoAction());

        // enable 'rollover' buttons in toolbar
        toolBar.putClientProperty("JToolBar.isRollover", Boolean.TRUE);
        // 
        toolBar.putClientProperty(Options.HEADER_STYLE_KEY, HeaderStyle.BOTH);

        return toolBar;
    }

    /**
     * This method initializes currentPanel.
     * @return javax.swing.JPanel
     */
    private JPanel getCurrentActivityPanel() {
        if (currentActivityPanel == null) {
            currentActivityPanel = new ActivityPanel(model, this.settings);
        }
        return currentActivityPanel;
    }

    /**
     * This method initializes aboutMenu.
     * @return javax.swing.JMenu
     */
    private JMenu getHelpMenu() {
        if (helpMenu == null) {
            helpMenu = new WMenu(textBundle.textFor("MainFrame.HelpMenu.Title"));
            helpMenu.setMnemonic(textBundle.textFor("MainFrame.HelpMenu.Title").charAt(0));
            helpMenu.add(getAboutMenuItem());
        }
        return helpMenu;
    }

    /**
     * This method initializes fileMenu.
     * @return javax.swing.JMenu
     */
    private JMenu getFileMenu() {
        if (fileMenu == null) {
            fileMenu = new WMenu(textBundle.textFor("MainFrame.FileMenu.Title")); //$NON-NLS-1$
            fileMenu.setMnemonic(textBundle.textFor("MainFrame.FileMenu.Title").charAt(0)); //$NON-NLS-1$

            fileMenu.add(getExportMenu());
            fileMenu.add(getImportMenu());
            fileMenu.addSeparator();

            fileMenu.add(getExitItem());
        }
        return fileMenu;
    }

    /**
     * This method initializes the import item.
     * @return javax.swing.JMenuItem
     */
    private JMenuItem getImportItem() {
        if (importItem == null) {
            final AbstractWremjaAction importAction = new ImportDataAction(this, this.model);
            importItem = new JMenuItem(importAction);
        }
        return importItem;
    }

    /**
     * This method initializes editMenu.
     * @return javax.swing.JMenu
     */
    private JMenu getEditMenu() {
        if (editMenu == null) {
            editMenu = new WMenu(textBundle.textFor("MainFrame.EditMenu.Title")); //$NON-NLS-1$
            editMenu.setMnemonic(textBundle.textFor("MainFrame.EditMenu.Title").charAt(0)); //$NON-NLS-1$

            editMenu.add(this.model.getEditStack().getRedoAction());
            editMenu.add(this.model.getEditStack().getUndoAction());

            editMenu.addSeparator();

            editMenu.add(getEditProjectsMenuItem());
            editMenu.add(getAddActivityMenuItem());

            editMenu.addSeparator();
            editMenu.add(new JMenuItem(new SettingsAction(this, model, this.settings)));
        }
        return editMenu;
    }

    /**
     * This method initializes addActivityMenuItem.
     * @return javax.swing.JMenuItem
     */
    private JMenuItem getAddActivityMenuItem() {
        if (addActivityMenuItem == null) {
            AbstractWremjaAction addActivityAction = new AddActivityAction(this, this.model);
            addActivityMenuItem = new JMenuItem(addActivityAction);
        }
        return addActivityMenuItem;
    }

    /**
     * This method initializes editProjectsMenuItem.
     * @return javax.swing.JMenuItem
     */
    private JMenuItem getEditProjectsMenuItem() {
        if (editProjectsMenuItem == null) {
            AbstractWremjaAction manageProjectsAction = new ManageProjectsAction(this, this.model);
            editProjectsMenuItem = new JMenuItem(manageProjectsAction);
        }
        return editProjectsMenuItem;
    }

    /**
     * This method initializes aboutMenuItem.
     * @return javax.swing.JMenuItem
     */
    private JMenuItem getAboutMenuItem() {
        AbstractWremjaAction aboutAction = new AboutAction(this);
        return new JMenuItem(aboutAction);
    }

    /**
     * {@inheritDoc}
     */
    public void update(final Observable source, final Object eventObject) {
        if (eventObject == null || !(eventObject instanceof WremjaEvent)) {
            return;
        }

        final WremjaEvent event = (WremjaEvent) eventObject;

        switch (event.getType()) {

        case PROJECT_ACTIVITY_STARTED:
            updateStart();
            break;

        case PROJECT_ACTIVITY_STOPPED:
            updateStop();
            break;

        case PROJECT_CHANGED:
            // If there is no active project leave everything as is
            if (model.isActive()) {
                updateTitle();
            }
            break;

        case PROJECT_ADDED:
            break;

        case PROJECT_REMOVED:
            break;

        case START_CHANGED:
            updateTitle();
            break;
        }
    }

    /**
     * Executed on events that change the title.
     */
    private void updateTitle() {
    	String title;
    	final DateTime date;
        if (this.model.isActive()) {
        	date = this.model.getStart();
            title = textBundle.textFor("Global.Title") + " - " +
            		this.model.getSelectedProject() + textBundle.textFor("MainFrame.9") +
            		FormatUtils.formatTime(date); 
        } else {
        	date = this.model.getStop();
            title = textBundle.textFor("Global.Title") + " " + textBundle.textFor("MainFrame.12") +
            		FormatUtils.formatTime(date); 
        }
        
        if(date != null && !DateUtils.isSameDay(date, DateUtils.getNow())) {
        	title += " (" + FormatUtils.formatDate(date) + ")";
        }
        setTitle(title);
    }

    /**
     * Executed on start event.
     */
    private void updateStart() {
        setIconImage(ACTIVE_ICON);
        updateTitle();
    }

    /**
     * Executed on stop event.
     */
    private void updateStop() {
        setIconImage(NORMAL_ICON);
        updateTitle();
    }

    /**
     * This method initializes exportExcelItem.
     * @return javax.swing.JMenuItem
     */
    private JMenuItem getExportExcelItem() {
        if (exportExcelItem == null) {
            final AbstractWremjaAction excelExportAction = new ExportExcelAction(this, this.model, this.settings);
            exportExcelItem = new JMenuItem(excelExportAction);
        }
        return exportExcelItem;
    }

    /**
     * This method initializes exportCsvItem.
     * @return javax.swing.JMenuItem
     */
    private JMenuItem getExportCsvItem() {
        if (exportCsvItem == null) {
            final AbstractWremjaAction csvExportAction = new ExportCsvAction(this, this.model, this.settings);
            exportCsvItem = new JMenuItem(csvExportAction);
        }
        return exportCsvItem;
    }

    /**
     * This method initializes exportDataItem.
     * @return javax.swing.JMenuItem
     */
    private JMenuItem getExportDataItem() {
        if (exportDataItem == null) {
            final AbstractWremjaAction exportDataAction = new ExportDataAction(this, this.model, this.settings);
            exportDataItem = new JMenuItem(exportDataAction);
        }
        return exportDataItem;
    }

    /**
     * This method initializes exportMenu.
     * @return javax.swing.JMenu
     */
    private JMenu getExportMenu() {
        if (exportMenu == null) {
            exportMenu = new WMenu(textBundle.textFor("MainFrame.ExportMenu.Title")); //$NON-NLS-1$
            exportMenu.setIcon(new ImageIcon(MainFrame.class.getResource("/icons/document-save-as.png")));
            exportMenu.setMnemonic(textBundle.textFor("MainFrame.ExportMenu.Title").charAt(0)); //$NON-NLS-1$

            exportMenu.add(getExportExcelItem());
            exportMenu.add(getExportCsvItem());
            exportMenu.add(getExportDataItem());
            
            for(AbstractExportAction action : getExporterPlugins()) {
            	exportMenu.add(new JMenuItem(action));
            }
        }
        return exportMenu;
    }
    
    // not implemented, yet
    private List<AbstractExportAction> getExporterPlugins() {
//    	try {
//    		Class<?> clazz = Class.forName("com.kemai.wremja.exporter.anukotimetracker.AnukoExporterAction");
//    		@SuppressWarnings("unchecked")
//    		Constructor<AbstractExportAction> cons = (Constructor<AbstractExportAction>) clazz.getConstructor(Frame.class, PresentationModel.class, IUserSettings.class);
//    		AbstractExportAction action = cons.newInstance(this, model, settings);
//    		return Collections.singletonList(action);
//    	} catch(Exception e) {
//    		LOG.warn(e, e);
//    		return Collections.emptyList();
//    	}
    	return Collections.emptyList();
    }

    /**
     * This method initializes importMenu.
     * @return javax.swing.JMenu
     */
    private JMenu getImportMenu() {
        if (importMenu == null) {
            importMenu = new WMenu(textBundle.textFor("MainFrame.ImportMenu.Title")); //$NON-NLS-1$
            importMenu.setIcon(new ImageIcon(MainFrame.class.getResource("/icons/document-open.png")));
            importMenu.setMnemonic(textBundle.textFor("MainFrame.ImportMenu.Title").charAt(0)); //$NON-NLS-1$
            importMenu.add(getImportItem());
        }
        return importMenu;
    }

    /**
     * This method initializes exitItem.
     * @return javax.swing.JMenuItem
     */
    private JMenuItem getExitItem() {
        if (exitItem == null) {
            final AbstractWremjaAction exitAction = new ExitAction(this, this.model);
            exitItem = new JMenuItem(exitAction);
        }
        return exitItem;
    }

    /**
     * Gets the tray icon. 
     * @return The tray icon or <code>null</code> if a tray icon
     * is not supported by the platform.
     */
    public TraySupport getTray() {
        return this.tray;
    }
    
    public void handleUnfinishedActivityOnStartup(long lastModified) throws InterruptedException, InvocationTargetException {
        setVisible(true);
        
        final DateTime lastTouch = new DateTime(lastModified);
        
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                Object[] options = new Object[3];
                options[0] = "Stop";
                options[1] = "Stop & Edit";
                options[2] = "Continue activity";
                
                String text = ""
                    + "There is an unfinished activity from a previous run!" +
                      " That can result from various reasons. For example:\n\n" +
                      "- Wremja was still running when the OS was shut down\n" +
                      "- Wremja crashed\n" +
                      "\nWould you like the activity on project '" + model.getData().getActiveProject().getTitle() 
                      + "' starting at "+ FormatUtils.formatDate( model.getStart()) + " " + FormatUtils.formatTime( model.getStart()) 
                      + "h to stop at " + FormatUtils.formatTime(lastTouch) + "h?";
                int chosen = JOptionPane.showOptionDialog(MainFrame.this, text,
                        "Unfinished activity", 0, JOptionPane.INFORMATION_MESSAGE, null,
                        options, options[0]);
                
                // prevent stop < start:
                long stopTs = Math.max(lastTouch.getMillis(), model.getStart().getMillis());
                DateTime stop = new DateTime(stopTs);
                if( chosen == 0 ) {
                    try {
                        model.stop(stop, true);
                    } catch (ProjectActivityStateException e1) {
                        LOG.error(e1, e1);
                    }
                } else if( chosen == 1) {
                    // TODO: for activities over multiple days, this only edits the last of these
                    try {
                        ProjectActivity addedActivity = model.stop(stop, true);
                        if(addedActivity != null) {
                            AddOrEditActivityDialog editActivityDialog = new AddOrEditActivityDialog(
                                    MainFrame.this, 
                                    model, 
                                    addedActivity
                            );
                            editActivityDialog.pack();
                            editActivityDialog.setLocationRelativeTo(MainFrame.this);
                            editActivityDialog.setVisible(true);
                        }
                    } catch (ProjectActivityStateException e) {
                        LOG.error(e, e);
                    }
                } else if( chosen == 2 || chosen == JOptionPane.CLOSED_OPTION) {
                    // do nothing, let activity continue
                } else {
                    throw new IllegalStateException("" + chosen);
                }
            }
        });
    }

}
