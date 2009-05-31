package com.kemai.swing.util;

import java.util.HashSet;
import java.util.Set;

import javax.swing.JMenu;
import javax.swing.JMenuBar;

public class WMenuBar extends JMenuBar implements MnemonicsContainer {

	private static final long serialVersionUID = 1L;
	
	private final Set<Integer> usedMnemonics = new HashSet<Integer>();

	@Override
	public JMenu add(JMenu c) {
		MnemonicHelper.updateMnemonicsFromMenuItem(c, this);
		return super.add(c);
	}
	
	@Override
	public boolean contains(int mnemonic) {
		return this.usedMnemonics.contains(Integer.valueOf(mnemonic));
	}

	@Override
	public boolean add(int mnemonic) {
		return this.usedMnemonics.add(Integer.valueOf(mnemonic));
	}
}
