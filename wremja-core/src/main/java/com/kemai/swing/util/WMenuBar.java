package com.kemai.swing.util;

import javax.swing.JMenu;
import javax.swing.JMenuBar;

public class WMenuBar extends JMenuBar {

	private static final long serialVersionUID = 1L;
	
	private final MnemonicsContainer mnemonicsContainer = new MnemonicsContainer();
	
	@Override
	public JMenu add(JMenu menu) {
		mnemonicsContainer.addMnemonicsFor(menu);
		return super.add(menu);
	}
}
