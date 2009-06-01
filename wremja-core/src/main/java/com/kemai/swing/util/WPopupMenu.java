package com.kemai.swing.util;

import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

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
}
