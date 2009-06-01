package com.kemai.swing.util;

import java.util.HashSet;
import java.util.Set;

import javax.swing.Action;
import javax.swing.JMenuItem;

import com.kemai.swing.action.AbstractWAction;
import com.kemai.util.StringUtils;

/**
 * A container for a collection of mnemonics. E.g. a JMenu or JMenuBar.
 * Useful to determine if a mnemonic is already taken in a menu.
 * 
 * Implements the semi-intelligent mnemonics auto-detect described here:
 * http://weblogs.java.net/blog/enicholas/archive/2006/06/mnemonic_magic.html
 * 
 * 
 * @author kutzi
 */
public class MnemonicsContainer {
	
	private final Set<Integer> usedMnemonics = new HashSet<Integer>();

	/**
	 * Adds the given mnemonic to the container.
	 * Returns true, if the container did NOT contain the mnemonic previously.
	 * Returns false otherwise.
	 */
	private boolean add(int mnemonic) {
		return this.usedMnemonics.add(Integer.valueOf(mnemonic));
	}
	
	public void addMnemonicsFor(JMenuItem item) {
		Action a = item.getAction();
		if(a instanceof AbstractWAction) {
			updateMnemonicsFromAction(item, (AbstractWAction)item.getAction(), this);
		} else {
			updateMnemonicsFromMenuItem(item, this);
		}
	}
	
	void updateMnemonicsFromAction(JMenuItem item, AbstractWAction action, MnemonicsContainer container) {
		if(action.hasMnemonicSet()) {
			item.setMnemonic(action.getMnemonic().intValue());
			item.setDisplayedMnemonicIndex(action.getDisplayedMnemonicIndex().intValue());
		} else {
			String name = getName(action);
			updateMnemonicsFromName(item, name, container);
		}
	}

	void updateMnemonicsFromMenuItem(JMenuItem item, MnemonicsContainer container) {
		updateMnemonicsFromName(item, item.getText(), container);
	}

//	   1. Try the first alphanumeric character in the string. Check to see if it is available (among other menu items in the menu or components in the window, as appropriate).
//	   2. If that isn't available, try capitalized letters in the order in which they appear.
//	   3. If no capitalized letters are available, try lowercase consonants.
//	   4. If no lowercase consonants are available, try lowercase vowels.
//	   5. If no lowercase vowels are available, give up.
	void updateMnemonicsFromName(JMenuItem item, String name, MnemonicsContainer container) {
		if(org.apache.commons.lang.StringUtils.isBlank(name)) {
			return;
		}
		
		name = name.trim();
		
		int m = name.codePointAt(0);
		if(Character.isLetterOrDigit(m)) {
    		if(container.add(m)) {
    			item.setMnemonic(m);
    			item.setDisplayedMnemonicIndex(0);
    			return;
    		}
		}
		
		// TODO: handle Unicode codepoints here!
		for(int i=1; i<name.length()-1; i++) {
			if(Character.isUpperCase(name.charAt(i))) {
				if(trySetMnemonic(name, i, container, item)) {
					return;
				}
			}
		}
		
		for(int i=1; i<name.length()-1; i++) {
			if(StringUtils.isConsonant(name.charAt(i))) {
				if(trySetMnemonic(name, i, container, item)) {
					return;
				}
			}
		}
		
		for(int i=1; i<name.length()-1; i++) {
			if(!StringUtils.isConsonant(name.charAt(i))) {
				if(trySetMnemonic(name, i, container, item)) {
					return;
				}
			}
		}

		// give up
		return;
	}

	private static boolean trySetMnemonic(CharSequence s, int index, MnemonicsContainer container, JMenuItem item) {
		int m = Character.codePointAt(s, index);
		if(container.add(m)) {
			item.setMnemonic(m);
			item.setDisplayedMnemonicIndex(index);
			return true;
		}
		return false;
	}
	
	private static String getName(Action action) {
		Object name = action.getValue(Action.NAME);
		return name != null ? name.toString() : "";
	}
}
