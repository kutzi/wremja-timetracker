package com.kemai.swing.util;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

public class WMenu extends JMenu {

	private static final long serialVersionUID = 1L;

	private final MnemonicsContainer mnemonicsContainer = new MnemonicsContainer();
	
	public WMenu(String text) {
		super(text);
	}
	
	@Override
	public JMenuItem add(JMenuItem menuItem) {
		mnemonicsContainer.addMnemonicsFor(menuItem);
		
		return super.add(menuItem);
	}
}
