package com.kemai.wremja.gui;

import static com.kemai.wremja.gui.GuiConstants.ACTIVE_ICON;
import static com.kemai.wremja.gui.GuiConstants.NORMAL_ICON;

import java.awt.AWTException;
import java.awt.SystemTray;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

import org.jdesktop.swinghelper.tray.JXTrayIcon;

import com.kemai.util.TextResourceBundle;
import com.kemai.wremja.FormatUtils;
import com.kemai.wremja.gui.actions.ChangeProjectAction;
import com.kemai.wremja.gui.actions.ExitAction;
import com.kemai.wremja.gui.actions.StartAction;
import com.kemai.wremja.gui.actions.StopAction;
import com.kemai.wremja.gui.events.WremjaEvent;
import com.kemai.wremja.gui.model.PresentationModel;
import com.kemai.wremja.logging.Logger;
import com.kemai.wremja.model.Project;

/**
 * Tray icon for quick start, stop and switching of project activities.
 * @author remast
 */
public class TrayIcon implements Observer {

    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(TrayIcon.class);

    /** The logger. */
    private static final Logger log = Logger.getLogger(TrayIcon.class);

    /** The model. */
    private final PresentationModel model;

    /** The tray icon. */
    private JXTrayIcon trayIcon;

    /** The menu of the tray icon. */
    private JPopupMenu menu = new JPopupMenu();

    public TrayIcon(final PresentationModel model, final MainFrame mainFrame) {
        this.model = model;
        this.model.addObserver(this);

        buildMenu();

        if (model.isActive()) {
            trayIcon = new JXTrayIcon(ACTIVE_ICON);
            trayIcon.setToolTip(textBundle.textFor("Global.Title") + " - " + model.getSelectedProject() + textBundle.textFor("MainFrame.9") + FormatUtils.formatTime(model.getStart()));
        } else {
            trayIcon = new JXTrayIcon(NORMAL_ICON); //$NON-NLS-1$
            trayIcon.setToolTip(textBundle.textFor("Global.Title"));
        }
        trayIcon.setJPopupMenu(menu);
        trayIcon.setImageAutoSize(true);

        trayIcon.addActionListener(new ActionListener() {

            public void actionPerformed(final ActionEvent event) {
                mainFrame.setVisible(true);
                mainFrame.setState(JFrame.NORMAL);
                mainFrame.requestFocus();
                
                mainFrame.showTray(false);
            }

        });
    }

    /**
     * Build the context menu of the tray icon.
     */
    private void buildMenu() {
        menu.removeAll();
        final ExitAction exitAction = new ExitAction(null, model);
        exitAction.putValue(AbstractAction.SMALL_ICON, null);
        menu.add(exitAction);

        // Add separator
        menu.add(new JSeparator());

        for (Project project : model.getProjectList()) {
        	final ChangeProjectAction changeAction = new ChangeProjectAction(model, project);
            menu.add(changeAction);
        }

        // Add separator
        menu.add(new JSeparator());

        if (model.isActive()) {
        	final StopAction stopAction = new StopAction(model);
        	stopAction.putValue(AbstractAction.SMALL_ICON, null);
            menu.add(stopAction);
        } else {
        	final StartAction startAction = new StartAction(null, model);
        	startAction.putValue(AbstractAction.SMALL_ICON, null);
            menu.add(startAction);
        }
    }

    /**
     * Show the tray icon.
     */
    public void show() {
    	if (SystemTray.isSupported()) {
    		SystemTray tray = SystemTray.getSystemTray(); 
    		try {
    			tray.add(trayIcon);
    		} catch (AWTException e) {
    			log.error(e, e);
    		}
    	}
    }

    /**
     * Hide the tray icon.
     */
    public void hide() {
    	if (SystemTray.isSupported()) {
    		SystemTray.getSystemTray().remove(trayIcon);        
    	}
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
            trayIcon.setToolTip(textBundle.textFor("Global.Title") + " - " + model.getSelectedProject() + textBundle.textFor("MainFrame.9") + FormatUtils.formatTime(model.getStart()));
        } else {
            trayIcon.setToolTip(textBundle.textFor("Global.Title") + " - "+ textBundle.textFor("MainFrame.12") + FormatUtils.formatTime(model.getStop()));
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

}
