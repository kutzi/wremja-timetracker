package com.kemai.swing.util;

import java.util.HashSet;
import java.util.Set;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import com.kemai.wremja.gui.actions.AbstractWremjaAction;

public class WMenu extends JMenu implements MnemonicsContainer {

	private static final long serialVersionUID = 1L;

	private final Set<Integer> usedMnemonics = new HashSet<Integer>();
	
	public WMenu(String text) {
		super(text);
	}
	
	@Override
	public JMenuItem add(JMenuItem menuItem) {
		Action a = menuItem.getAction();
		if(a instanceof AbstractWremjaAction) {
			MnemonicHelper.updateMnemonicsFromAction(menuItem, (AbstractWremjaAction)menuItem.getAction(), this);
		} else {
			MnemonicHelper.updateMnemonicsFromMenuItem(menuItem, this);
		}
		
		return super.add(menuItem);
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
