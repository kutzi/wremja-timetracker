/*
 * Copyright 2008 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.jdesktop.swinghelper.tray;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.TrayIcon;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JDialog;
import javax.swing.JPopupMenu;
import javax.swing.RootPaneContainer;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

public class JXTrayIcon extends TrayIcon {
    private JPopupMenu menu;
    private static final Window dialog;
    static {
		dialog = new JDialog((Frame) null, "TrayDialog");
		((JDialog)dialog).setUndecorated(true);

// I've read on some forum that this should fix some problems on Linux, but I've
// not seen these problems. And worse: JWindow seems to behave worse than JDialog on Linux (e.g.window going away, when mouse button is released)		
//		if( OS.isLinux() ) {
//    		// avoid some problems on Linux (flickering)
//    		dialog = new JWindow((Frame)null);
//    	}
    	
    	dialog.setAlwaysOnTop(true);
    }
    
    private static final PopupMenuListener popupListener = new PopupMenuListener() {
        
        @Override
        public void popupMenuWillBecomeVisible(final PopupMenuEvent e) {
        }

        @Override
        public void popupMenuWillBecomeInvisible(final PopupMenuEvent e) {
            dialog.setVisible(false);
        }

        @Override
        public void popupMenuCanceled(final PopupMenuEvent e) {
            dialog.setVisible(false);
        }
    };


    public JXTrayIcon(final Image image) {
        super(image);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(final MouseEvent e) {
                showJPopupMenu(e);
            }

            @Override
            public void mouseReleased(final MouseEvent e) {
                showJPopupMenu(e);
            }
        });
    }

    private void showJPopupMenu(final MouseEvent e) {
        if (e.isPopupTrigger() && menu != null) {
            Dimension size = menu.getPreferredSize();
            
            // set location depending on system tray location (e.g. top or bottom)
            Point centerPoint = GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint();
            int x;
            int y;
            if(e.getY() > centerPoint.getY()) {
            	y = e.getY() - size.height;
            } else {
            	y = e.getY();
            }
            
            if(e.getX() > centerPoint.getX()) {
            	x = e.getX() - size.width;
            } else {
            	x = e.getX();
            }
            
            dialog.setLocation(x, y);
            
            dialog.setVisible(true);
            menu.show(((RootPaneContainer) dialog).getContentPane(), 0, 0);
            // popup works only for focused windows
            dialog.toFront();
        }
    }

    public JPopupMenu getJPopupMenu() {
        return menu;
    }

    public void setJPopupMenu(final JPopupMenu menu) {
        if (this.menu != null) {
            this.menu.removePopupMenuListener(popupListener);
        }
        this.menu = menu;
        menu.addPopupMenuListener(popupListener);
    }
} 
