package com.kemai.swing.util;

import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 * A {@link JPopupMenu} which automatically generates mnemonics for added
 * {@link Action}s in the order in which they are added/inserted.
 * 
 * @author kutzi
 */
public class WPopupMenu extends JPopupMenu {
	private static final long serialVersionUID = 1L;
	
	private final MnemonicsContainer mnemonicsContainer = new MnemonicsContainer();

	@Override
	public JMenuItem add(Action a) {
		JMenuItem item = createActionComponent(a);
		item.setAction(a);
		mnemonicsContainer.addMnemonicsFor(item);
		return super.add(item);
	}

	@Override
    public void insert(Action a, int index) {
		JMenuItem item = createActionComponent(a);
		item.setAction(a);
		mnemonicsContainer.addMnemonicsFor(item);
	    super.insert(item, index);
    }
	
	
}
