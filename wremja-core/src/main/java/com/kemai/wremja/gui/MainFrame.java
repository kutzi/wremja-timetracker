package com.kemai.wremja.gui;

import static com.kemai.wremja.gui.GuiConstants.ACTIVE_ICON;
import static com.kemai.wremja.gui.GuiConstants.NORMAL_ICON;
import info.clearthought.layout.TableLayout;

import java.awt.SystemTray;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import org.jdesktop.swingx.JXFrame;

import com.jgoodies.looks.HeaderStyle;
import com.jgoodies.looks.Options;
import com.kemai.util.TextResourceBundle;
import com.kemai.wremja.FormatUtils;
import com.kemai.wremja.gui.actions.AboutAction;
import com.kemai.wremja.gui.actions.AbstractWremjaAction;
import com.kemai.wremja.gui.actions.AddActivityAction;
import com.kemai.wremja.gui.actions.ExitAction;
import com.kemai.wremja.gui.actions.ExportCsvAction;
import com.kemai.wremja.gui.actions.ExportDataAction;
import com.kemai.wremja.gui.actions.ExportExcelAction;
import com.kemai.wremja.gui.actions.ImportDataAction;
import com.kemai.wremja.gui.actions.ManageProjectsAction;
import com.kemai.wremja.gui.actions.SettingsAction;
import com.kemai.wremja.gui.events.WremjaEvent;
import com.kemai.wremja.gui.model.PresentationModel;
import com.kemai.wremja.gui.panels.ActivityPanel;
import com.kemai.wremja.gui.panels.ReportPanel;
import com.kemai.wremja.gui.settings.UserSettings;
import com.kemai.wremja.logging.Logger;

/**
 * The main frame of the application.
 * @author remast
 */
@SuppressWarnings("serial")//$NON-NLS-1$
public class MainFrame extends JXFrame implements Observer, WindowListener {

    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(MainFrame.class);

    private static final Logger log = Logger.getLogger(MainFrame.class);
    
    /** The model. */
    private final PresentationModel model;

    /** The tool bar. */
    private JToolBar toolBar = null;

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

    private JMenuItem aboutMenuItem = null;

    private JMenuItem addActivityMenuItem = null;

    private JMenuItem editProjectsMenuItem = null;

    private JMenuItem exportExcelItem = null;

    private JMenuItem exportCsvItem = null;

    private JMenuItem exportDataItem = null;

    private JMenuItem exitItem = null;

    private JMenuItem importItem = null;
    
    /** The Tray icon, if any. */
    private TrayIcon tray;

    /**
     * This is the default constructor.
     * @param model the model
     */
    public MainFrame(final PresentationModel model) {
        super();

        this.model = model;
        this.model.addObserver(this);

        initialize();
        initTrayIcon();
    }

    /**
     * Set up GUI components.
     */
    private void initialize() {
        this.setResizable(true);
        
        if (UserSettings.instance().isRememberWindowSizeLocation()) {
            this.setSize(UserSettings.instance().getWindowSize());
            this.setLocation(UserSettings.instance().getWindowLocation());
        } else {
            this.setSize(530, 720);
        }
        
        this.addComponentListener(new ComponentAdapter() {

            @Override
            public void componentMoved(ComponentEvent e) {
                UserSettings.instance().setWindowLocation(MainFrame.this.getLocation());
            }

            @Override
            public void componentResized(ComponentEvent e) {
                UserSettings.instance().setWindowSize(MainFrame.this.getSize());
            }
        });
        
        
        this.setJMenuBar(getMainMenuBar());

        this.addWindowListener(this);

        // 1. Init start-/stop-Buttons
        if (this.model.isActive()) {
            this.setIconImage(ACTIVE_ICON);
            this.setTitle(
                    textBundle.textFor("Global.Title") + " - " + this.model.getSelectedProject() + textBundle.textFor("MainFrame.9") + FormatUtils.formatTime(this.model.getStart()) //$NON-NLS-1$ //$NON-NLS-2$
            );
        } else {
            this.setIconImage(NORMAL_ICON);
            this.setTitle(textBundle.textFor("Global.Title")); //$NON-NLS-1$
        }

        // 3. Set layout
        final double[][] size = { 
                {TableLayout.FILL }, // Columns
                {TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.FILL} // Rows
        };

        TableLayout tableLayout = new TableLayout(size);
        this.setLayout(tableLayout);
        this.add(getToolBar(), "0, 0");
        this.add(getCurrentActivityPanel(), "0, 1");
        this.add(getReportPanel(), "0, 2");
    }
    
    /**
     * Initializes the lock file.
     * @param model the model to be displayed
     * @param mainInstance the main instance
     * @param mainFrame
     */
    private void initTrayIcon() {
        log.debug("Initializing tray icon ...");

        // Create try icon.
        try {
            if (SystemTray.isSupported()) {
                tray = new TrayIcon(model, this);
            } else {
                tray = null;
            }
        } catch (UnsupportedOperationException e) {
            // Tray icon not supported on the current platform.
            tray = null;
        }
    }
    
    private ReportPanel getReportPanel() {
        if (reportPanel == null) {
            reportPanel = new ReportPanel(this.model);
        }
        return reportPanel;
    }

    /**
     * This method initializes mainMenuBar.
     * @return javax.swing.JMenuBar
     */
    private JMenuBar getMainMenuBar() {
        if (mainMenuBar == null) {
            mainMenuBar = new JMenuBar();
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
    @Override
    public JToolBar getToolBar() {
        if (toolBar == null) {
            toolBar = new JToolBar();
            toolBar.setFloatable(false);
        }

        toolBar.add(new ManageProjectsAction(this, this.model));
        toolBar.add(new ExportExcelAction(this, this.model));
        toolBar.add(new AddActivityAction(this, this.model));
        toolBar.add(new JToolBar.Separator());
        toolBar.add(this.model.getEditStack().getUndoAction());
        toolBar.add(this.model.getEditStack().getRedoAction());

        toolBar.putClientProperty("JToolBar.isRollover", Boolean.TRUE);
        toolBar.putClientProperty(Options.HEADER_STYLE_KEY, HeaderStyle.BOTH);

        return toolBar;
    }

    /**
     * This method initializes currentPanel.
     * @return javax.swing.JPanel
     */
    private JPanel getCurrentActivityPanel() {
        if (currentActivityPanel == null) {
            currentActivityPanel = new ActivityPanel(model);
        }
        return currentActivityPanel;
    }

    /**
     * This method initializes aboutMenu.
     * @return javax.swing.JMenu
     */
    private JMenu getHelpMenu() {
        if (helpMenu == null) {
            helpMenu = new JMenu(textBundle.textFor("MainFrame.HelpMenu.Title"));
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
            fileMenu = new JMenu();
            fileMenu.setText(textBundle.textFor("MainFrame.FileMenu.Title")); //$NON-NLS-1$
            fileMenu.setMnemonic(textBundle.textFor("MainFrame.FileMenu.Title").charAt(0)); //$NON-NLS-1$

            fileMenu.add(getExportMenu());
            fileMenu.add(getImportMenu());
            fileMenu.addSeparator();

            fileMenu.add(getExitItem());
        }
        return fileMenu;
    }

    /**
     * This method initializes exitItem.
     * @return javax.swing.JMenuItem
     */
    private JMenuItem getImportItem() {
        if (importItem == null) {
            final AbstractWremjaAction exitAction = new ImportDataAction(this, this.model);
            importItem = new JMenuItem(exitAction);
            importItem.setMnemonic(exitAction.getMnemonic());
        }
        return importItem;
    }

    /**
     * This method initializes editMenu.
     * @return javax.swing.JMenu
     */
    private JMenu getEditMenu() {
        if (editMenu == null) {
            editMenu = new JMenu();
            editMenu.setText(textBundle.textFor("MainFrame.EditMenu.Title")); //$NON-NLS-1$
            editMenu.setMnemonic(textBundle.textFor("MainFrame.EditMenu.Title").charAt(0)); //$NON-NLS-1$

            editMenu.add(this.model.getEditStack().getRedoAction());
            editMenu.add(this.model.getEditStack().getUndoAction());

            editMenu.addSeparator();

            editMenu.add(getEditProjectsMenuItem());
            editMenu.add(getAddActivityMenuItem());

            editMenu.addSeparator();
            editMenu.add(new JMenuItem(new SettingsAction(this, model)));
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
            addActivityMenuItem.setMnemonic(addActivityAction.getMnemonic());
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
            editProjectsMenuItem.setMnemonic(manageProjectsAction.getMnemonic());
        }
        return editProjectsMenuItem;
    }

    /**
     * This method initializes aboutMenuItem.
     * @return javax.swing.JMenuItem
     */
    private JMenuItem getAboutMenuItem() {
        if (aboutMenuItem == null) {
            AbstractWremjaAction aboutAction = new AboutAction(this);
            aboutMenuItem = new JMenuItem(aboutAction);
            aboutMenuItem.setMnemonic(aboutAction.getMnemonic());
        }
        return aboutMenuItem;
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
            this.updateStart();
            break;

        case PROJECT_ACTIVITY_STOPPED:
            this.updateStop();
            break;

        case PROJECT_CHANGED:
            // If there is no active project leave everything as is
            if (model.isActive()) {
                this.updateTitle();
            }
            break;

        case PROJECT_ADDED:
            break;

        case PROJECT_REMOVED:
            break;

        case START_CHANGED:
            this.updateTitle();
            break;
        }
    }

    /**
     * Executed on events that change the title.
     */
    private void updateTitle() {
        if (this.model.isActive()) {
            this.setTitle(textBundle.textFor("Global.Title") + " - " + this.model.getSelectedProject() + textBundle.textFor("MainFrame.9") + FormatUtils.formatTime(this.model.getStart())); //$NON-NLS-1$ //$NON-NLS-2$
        } else {
            this.setTitle(textBundle.textFor("Global.Title") + " " + textBundle.textFor("MainFrame.12") + FormatUtils.formatTime(this.model.getStop())); //$NON-NLS-1$
        }
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
            final AbstractWremjaAction excelExportAction = new ExportExcelAction(this, this.model);
            exportExcelItem = new JMenuItem(excelExportAction);
            exportExcelItem.setMnemonic(excelExportAction.getMnemonic());
        }
        return exportExcelItem;
    }

    /**
     * This method initializes exportCsvItem.
     * @return javax.swing.JMenuItem
     */
    private JMenuItem getExportCsvItem() {
        if (exportCsvItem == null) {
            final AbstractWremjaAction csvExportAction = new ExportCsvAction(this, this.model);
            exportCsvItem = new JMenuItem(csvExportAction);
            exportCsvItem.setMnemonic(csvExportAction.getMnemonic());
        }
        return exportCsvItem;
    }

    /**
     * This method initializes exportDataItem.
     * @return javax.swing.JMenuItem
     */
    private JMenuItem getExportDataItem() {
        if (exportDataItem == null) {
            final AbstractWremjaAction exportDataAction = new ExportDataAction(this, this.model);
            exportDataItem = new JMenuItem(exportDataAction);
            exportDataItem.setMnemonic(exportDataAction.getMnemonic());
        }
        return exportDataItem;
    }

    /**
     * This method initializes exportMenu.
     * @return javax.swing.JMenu
     */
    private JMenu getExportMenu() {
        if (exportMenu == null) {
            exportMenu = new JMenu();
            exportMenu.setIcon(new ImageIcon(MainFrame.class.getResource("/icons/export-menu.png")));
            exportMenu.setText(textBundle.textFor("MainFrame.ExportMenu.Title")); //$NON-NLS-1$
            exportMenu.setMnemonic(textBundle.textFor("MainFrame.ExportMenu.Title").charAt(0)); //$NON-NLS-1$

            exportMenu.add(getExportExcelItem());
            exportMenu.add(getExportCsvItem());
            exportMenu.add(getExportDataItem());
        }
        return exportMenu;
    }

    /**
     * This method initializes importMenu.
     * @return javax.swing.JMenu
     */
    private JMenu getImportMenu() {
        if (importMenu == null) {
            importMenu = new JMenu();
            importMenu.setIcon(new ImageIcon(MainFrame.class.getResource("/icons/import-menu.png")));
            importMenu.setText(textBundle.textFor("MainFrame.ImportMenu.Title")); //$NON-NLS-1$
            importMenu.setMnemonic(textBundle.textFor("MainFrame.ImportMenu.Title").charAt(0)); //$NON-NLS-1$
            importMenu.add(getImportItem());
        }
        return importMenu;
    }

    @Override
    public void windowIconified(final java.awt.event.WindowEvent e) {
        if (getTray() != null) {
            setVisible(false);
            showTray(true);
        }
    }

    @Override
    public void windowOpened(final java.awt.event.WindowEvent e) {
        showTray(false);
    }

    @Override
    public void windowClosing(final java.awt.event.WindowEvent e) {
        if (getTray() != null) {
            setVisible(false);
            showTray(true);
        } else {
            boolean quit = true;

            if (model.isActive()) {
                final int dialogResult = JOptionPane.showConfirmDialog(
                        getOwner(), 
                        textBundle.textFor("ExitConfirmDialog.Message"), 
                        textBundle.textFor("ExitConfirmDialog.Title"), 
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.INFORMATION_MESSAGE
                );
                quit = JOptionPane.YES_OPTION == dialogResult;
            } 

            if (quit) {
                System.exit(0);
            }
        }
    }

    @Override
    public void windowClosed(final java.awt.event.WindowEvent e) {
    }

    @Override
    public void windowDeiconified(final java.awt.event.WindowEvent e) {
        showTray(false);
    }

    @Override
    public void windowActivated(final java.awt.event.WindowEvent e) {
    }

    @Override
    public void windowDeactivated(final java.awt.event.WindowEvent e) {
    }

    /**
     * This method initializes exitItem.
     * @return javax.swing.JMenuItem
     */
    private JMenuItem getExitItem() {
        if (exitItem == null) {
            final AbstractWremjaAction exitAction = new ExitAction(this, this.model);
            exitItem = new JMenuItem(exitAction);
            exitItem.setMnemonic(exitAction.getMnemonic());
        }
        return exitItem;
    }

    /**
     * Gets the tray icon. 
     * @return The tray icon or <code>null</code> if a tray icon
     * is not supported by the platform.
     */
    public TrayIcon getTray() {
        return this.tray;
    }

    public void showTray( boolean show ) {
        if( this.tray != null ) {
            if( show ) {
                this.tray.show();
            } else {
                this.tray.hide();
            }
        }
    }
}
