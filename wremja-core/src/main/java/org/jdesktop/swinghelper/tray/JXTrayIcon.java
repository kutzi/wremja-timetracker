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
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeListener;

import javax.swing.Action;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.RootPaneContainer;
import javax.swing.UIManager;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import com.kemai.swing.util.WPopupMenu;

public class JXTrayIcon extends TrayIcon {
    private JPopupMenu menu;
    private static final Window dialog;
    static {
		dialog = new JDialog((Frame) null, "TrayDialog");
		((JDialog)dialog).setUndecorated(true);
    	dialog.setAlwaysOnTop(true);
    }
    
    private long popupMenuLastShown = 0L;
    
    private final PopupMenuListener popupListener = new PopupMenuListener() {
        
        @Override
        public void popupMenuWillBecomeVisible(final PopupMenuEvent e) {
        }

        @Override
        public void popupMenuWillBecomeInvisible(final PopupMenuEvent e) {
        	if (System.currentTimeMillis() > popupMenuLastShown + 500) {
        		dialog.setVisible(false);
        	}
        }

        @Override
        public void popupMenuCanceled(final PopupMenuEvent e) {
        	if (System.currentTimeMillis() > popupMenuLastShown + 500) {
        		dialog.setVisible(false);
        	}
        }
    };

    public JXTrayIcon(final Image image) {
        super(image);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(final MouseEvent e) {
            	if (System.currentTimeMillis() < popupMenuLastShown + 500) {
            		return;
            	}
                showJPopupMenu(e);
            }

            @Override
            public void mouseReleased(final MouseEvent e) {
            	if (System.currentTimeMillis() < popupMenuLastShown + 500) {
            		return;
            	}
                showJPopupMenu(e);
            }
        });
    }

    private void showJPopupMenu(final MouseEvent e) {
        if (e.isPopupTrigger() && menu != null) {
        	if (menu.isVisible()) {
        		menu.setVisible(false);
        		return;
        	}
        	
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
            popupMenuLastShown = System.currentTimeMillis();
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
        this.menu.addPopupMenuListener(popupListener);
    }
    
    public class PopupMenu extends WPopupMenu {
        private static final long serialVersionUID = 1L;

        private final MouseListener ML = new MouseAdapter() {
			@Override
            public void mouseEntered(MouseEvent evt) {
				JMenuItem jMenuItem = (JMenuItem) evt.getSource();
				jMenuItem.setBackground(UIManager
				        .getColor("MenuItem.selectionBackground"));
				jMenuItem.setForeground(UIManager
				        .getColor("MenuItem.selectionForeground"));
			}

			@Override
            public void mouseExited(MouseEvent evt) {
				JMenuItem jMenuItem = (JMenuItem) evt.getSource();
				jMenuItem.setBackground(UIManager
				        .getColor("MenuItem.background"));
				jMenuItem.setForeground(UIManager
				        .getColor("MenuItem.foreground"));
			}  
        };
        
		@Override
    	public void setVisible(boolean b) {
			// workaround for 'flickering' menu on Linux
    		if (System.currentTimeMillis() > popupMenuLastShown + 500) {
    			super.setVisible(b);
    			popupMenuLastShown = System.currentTimeMillis();
    		}
    	}

		@Override
        public JMenuItem add(final Action a) {
			Action b =  new Action() {
				@Override
				public void actionPerformed(ActionEvent e) {
					// workaround for menu not disappearing on action
					menu.setVisible(false);
					a.actionPerformed(e);
				}
				
				@Override
				public void setEnabled(boolean b) {
					a.setEnabled(b);
				}
				
				@Override
				public void removePropertyChangeListener(PropertyChangeListener listener) {
					a.removePropertyChangeListener(listener);
				}
				
				@Override
				public void putValue(String key, Object value) {
					a.putValue(key, value);
				}
				
				@Override
				public boolean isEnabled() {
					return a.isEnabled();
				}
				
				@Override
				public Object getValue(String key) {
					return a.getValue(key);
				}
				
				@Override
				public void addPropertyChangeListener(PropertyChangeListener listener) {
					a.addPropertyChangeListener(listener);
				}
			};
	        JMenuItem item = super.add(b);
	        // workaround for the problem that the menu items are not
	        // displayed as selected (anymore) if mouse is over them:
	        item.addMouseListener(ML);
	        return item;
        }
    }
} 
