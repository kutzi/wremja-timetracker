package com.kemai.wremja.gui;

import static com.kemai.wremja.gui.GuiConstants.ACTIVE_ICON;
import static com.kemai.wremja.gui.GuiConstants.NORMAL_ICON;

import java.awt.AWTException;
import java.awt.SystemTray;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import org.jdesktop.swinghelper.tray.JXTrayIcon;

import com.kemai.util.TextResourceBundle;
import com.kemai.wremja.FormatUtils;
import com.kemai.wremja.gui.actions.AbstractWremjaAction;
import com.kemai.wremja.gui.actions.ChangeProjectAction;
import com.kemai.wremja.gui.actions.ExitAction;
import com.kemai.wremja.gui.actions.StartAction;
import com.kemai.wremja.gui.actions.StopAction;
import com.kemai.wremja.gui.events.WremjaEvent;
import com.kemai.wremja.gui.model.PresentationModel;
import com.kemai.wremja.gui.settings.UserSettings;
import com.kemai.wremja.logging.Logger;
import com.kemai.wremja.model.Project;

/**
 * Tray icon for quick start, stop and switching of project activities.
 * @author remast
 * @author kutzi
 */
public class TraySupport implements Observer, WindowListener {

    /** The bundle for internationalized texts. */
    private static final TextResourceBundle TEXT_BUNDLE = TextResourceBundle.getBundle(TraySupport.class);
    
    private static final Logger LOGGER = Logger.getLogger(TraySupport.class);
    
    private static final boolean DEBUG = false;

    /** The model. */
    private final PresentationModel model;

    /** The tray icon itself. */
    private JXTrayIcon trayIcon;

    /** The menu of the tray icon. */
    private JPopupMenu menu;
    private final MainFrame mainFrame;

	private boolean shown;
	
	private final WindowListener DEFAULT_WINDOW_LISTENER = new WindowAdapter() {

		@Override
        public void windowClosing(WindowEvent e) {
			new ExitAction(mainFrame, model).actionPerformed(null);
        }
	};

    public TraySupport(final PresentationModel model, final MainFrame mainFrame) {
        this.model = model;
        this.model.addObserver(this);
        this.mainFrame = mainFrame;
        this.mainFrame.addWindowListener(DEFAULT_WINDOW_LISTENER);
    }
    
    void enable() {
    	if (SystemTray.isSupported()) {
	
	        if (model.isActive()) {
	            trayIcon = new JXTrayIcon(ACTIVE_ICON);
	            trayIcon.setToolTip(TEXT_BUNDLE.textFor("Global.Title") + " - " + model.getSelectedProject() + TEXT_BUNDLE.textFor("MainFrame.9") + FormatUtils.formatTime(model.getStart()));
	        } else {
	            trayIcon = new JXTrayIcon(NORMAL_ICON); 
	            trayIcon.setToolTip(TEXT_BUNDLE.textFor("Global.Title"));
	        }
	        trayIcon.setImageAutoSize(true);
	        // tray popup menu on Linux (Gnome only?) is flaky
	        // we do it ourself by a MouseListener
	        menu = trayIcon.new PopupMenu();
	        buildMenu();
	        trayIcon.setJPopupMenu(menu);
	        trayIcon.setImageAutoSize(true);
	
	        trayIcon.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if(SwingUtilities.isLeftMouseButton(e)) {
						miniIt();
					}
//					} else if (SwingUtilities.isRightMouseButton(e)) {
//						if (menu.isVisible()) {
//							menu.setVisible(false);
//						} else {
//							//calcLocation(menu, e.getLocationOnScreen());
//							AWTUtils.keepInScreenBounds(e.getLocationOnScreen(), menu);
//							menu.setVisible(true);
//						}
//					}
				}
	        });
	        this.mainFrame.removeWindowListener(DEFAULT_WINDOW_LISTENER);
	        this.mainFrame.addWindowListener(this);
    	} else {
    		LOGGER.info("Tray icon is not supported on this platform");
    	}
    }
    

    /**
     * Build the context menu of the tray icon.
     */
    private void buildMenu() {
        menu.removeAll();
        
        AbstractWremjaAction restoreAction = new AbstractWremjaAction(this.mainFrame) {
			private static final long serialVersionUID = 1L;

			{
        		setName(TEXT_BUNDLE.textFor("Global.RestoreMainFrame.Title"));
                setTooltip(TEXT_BUNDLE.textFor("Global.RestoreMainFrame.ToolTipText"));
        	}
        	
			@Override
			public void actionPerformed(ActionEvent e) {
				toggleState();
			}
        };
        menu.add(restoreAction);
        final ExitAction exitAction = new ExitAction(this.mainFrame, model);
        exitAction.setIcon(null);
        menu.add(exitAction);

        menu.addSeparator();

        for (Project project : model.getProjectList()) {
        	final ChangeProjectAction changeAction = new ChangeProjectAction(model, project);
            menu.add(changeAction);
        }

        menu.addSeparator();

        if (model.isActive()) {
        	final StopAction stopAction = new StopAction(model);
        	stopAction.setIcon(null);
            menu.add(stopAction);
        } else {
        	final StartAction startAction = new StartAction(null, model);
        	startAction.setIcon(null);
            menu.add(startAction);
        }
    }

    private void setIconVisible( boolean show ) {
        if( show ) {
            showIcon();
            
        } else {
            hideIcon();
        }
        
        UserSettings.instance().setWindowMinimized(show);
    }

    /**
     * Show the tray icon.
     */
    private void showIcon() {
    	if (!shown) {
    		SystemTray tray = SystemTray.getSystemTray(); 
    		try {
    			tray.add(trayIcon);
    			shown = true;
    		} catch (AWTException e) {
    			LOGGER.error(e, e);
    		}
    	}
    }

    /**
     * Hides the tray icon.
     */
    private void hideIcon() {
		SystemTray.getSystemTray().remove(trayIcon);
		shown = false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(final Observable source, final Object eventObject) {
        if (eventObject != null && eventObject instanceof WremjaEvent) {
            WremjaEvent event = (WremjaEvent) eventObject;

            switch (event.getType()) {

                case PROJECT_ACTIVITY_STARTED:
                    this.updateStart();
                    break;

                case PROJECT_ACTIVITY_STOPPED:
                    this.updateStop();
                    break;

                case PROJECT_CHANGED:
                    this.buildMenu();

                    if (model.isActive()) {
                        this.updateToolTip();
                    }
                    
                    break;

                case PROJECT_ADDED:
                    this.buildMenu();
                    break;

                case PROJECT_REMOVED:
                    this.buildMenu();
                    break;
                    
                case START_CHANGED:
                    this.updateToolTip();
                    break;
                    
            }
        }
    }

    /**
     * Executed on events that change the title.
     */    
    private void updateToolTip() {
        if (model.isActive()) {
            trayIcon.setToolTip(TEXT_BUNDLE.textFor("Global.Title") + " - " + model.getSelectedProject() + TEXT_BUNDLE.textFor("MainFrame.9") + FormatUtils.formatTime(model.getStart()));
        } else {
            trayIcon.setToolTip(TEXT_BUNDLE.textFor("Global.Title") + " - "+ TEXT_BUNDLE.textFor("MainFrame.12") + FormatUtils.formatTime(model.getStop()));
        }
    }

    /**
     * Executed on start event.
     */    
    private void updateStop() {
        this.trayIcon.setImage(NORMAL_ICON);
        updateToolTip();
        this.buildMenu();
    }

    /**
     * Executed on start event.
     */    
    private void updateStart() {
        this.trayIcon.setImage(ACTIVE_ICON);
        updateToolTip();
        this.buildMenu();
    }

    private long lastIconified = 0L;
    
    // taken from JDownloader
    private void miniIt() {
        if (System.currentTimeMillis() > this.lastIconified + 750) {
            this.lastIconified = System.currentTimeMillis();
            if (mainFrame.isVisible()) {
                mainFrame.setVisible(false);
                setIconVisible(true);
            } else {
                new Thread() {

                	@Override
                    public void run() {

                        // It's important,to set the frame visible first then
                        // set state. otherwise the framestatus may be broken
                        // (like synthetica: does disable minimize buttons
                        mainFrame.setVisible(true);
                        mainFrame.setExtendedState(JFrame.NORMAL);

                        mainFrame.toFront();
                        setIconVisible(false);
                    }

                }.start();

            }
        }
    }
    
    // taken from JDownloader
    private void toggleState() {
    	if (System.currentTimeMillis() > this.lastIconified + 750) {
            this.lastIconified = System.currentTimeMillis();
            if (mainFrame.isVisible()) {
            	mainFrame.setVisible(false);
            	setIconVisible(true);
            } else {
            	mainFrame.setVisible(true);
            	mainFrame.setExtendedState(JFrame.NORMAL);

            	mainFrame.toFront();
            	setIconVisible(false);
            }
        } else {
            mainFrame.setVisible(true);
            mainFrame.setExtendedState(JFrame.NORMAL);

            miniIt();
        }
    }
    
    @Override
    public void windowIconified(final java.awt.event.WindowEvent e) {
    	if(DEBUG) {
    		System.out.println("iconified");
    	}
    	toggleState();
    }

    @Override
    public void windowOpened(final java.awt.event.WindowEvent e) {
    	if(DEBUG) {
    		System.out.println("opened");
    	}
    }

    @Override
    public void windowClosing(final java.awt.event.WindowEvent e) {
    	if(DEBUG) {
    		System.out.println("closing");
    	}
    	toggleState();
    }

    @Override
    public void windowClosed(final java.awt.event.WindowEvent e) {
    	if(DEBUG) {
    		System.out.println("closed");
    	}
    }

    @Override
    public void windowDeiconified(final java.awt.event.WindowEvent e) {
    	if(DEBUG) {
    		System.out.println("Deiconified");
    	}
    	this.lastIconified = System.currentTimeMillis();
    }

    @Override
    public void windowActivated(final java.awt.event.WindowEvent e) {
    	if(DEBUG) {
    		System.out.println("activated");
    	}
    }

    @Override
    public void windowDeactivated(final java.awt.event.WindowEvent e) {
    	if(DEBUG) {
    		System.out.println("deactivated");
    	}
    }
}
