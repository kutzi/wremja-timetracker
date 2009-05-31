package com.kemai.swing.util;

import javax.swing.Action;
import javax.swing.JMenuItem;

import com.kemai.util.StringUtils;
import com.kemai.wremja.gui.actions.AbstractWremjaAction;

/**
 * Implements the semi-intelligent mnemonics auto-detect described here:
 * http://weblogs.java.net/blog/enicholas/archive/2006/06/mnemonic_magic.html
 * 
 * @author kutzi
 */
class MnemonicHelper {
	static void updateMnemonicsFromAction(JMenuItem item, AbstractWremjaAction action, MnemonicsContainer container) {
		if(action.hasMnemonicSet()) {
			item.setMnemonic(action.getMnemonic().intValue());
			item.setDisplayedMnemonicIndex(action.getDisplayedMnemonicIndex().intValue());
		} else {
			String name = getName(action);
			updateMnemonicsFromName(item, name, container);
		}
	}

	static void updateMnemonicsFromMenuItem(JMenuItem item, MnemonicsContainer container) {
		updateMnemonicsFromName(item, item.getText(), container);
	}

//	   1. Try the first character in the string. Check to see if it is available (among other menu items in the menu or components in the window, as appropriate).
//	   2. If that isn't available, try capitalized letters in the order in which they appear.
//	   3. If no capitalized letters are available, try lowercase consonants.
//	   4. If no lowercase consonants are available, try lowercase vowels.
//	   5. If no lowercase vowels are available, give up.
	static void updateMnemonicsFromName(JMenuItem item, String name, MnemonicsContainer container) {
		if(org.apache.commons.lang.StringUtils.isBlank(name)) {
			return;
		}
		
		Integer m = Integer.valueOf(name.codePointAt(0));
		if(container.add(m)) {
			item.setMnemonic(m.intValue());
			item.setDisplayedMnemonicIndex(0);
			return;
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
	
	private static String getName(AbstractWremjaAction action) {
		Object name = action.getValue(Action.NAME);
		return name != null ? name.toString() : "";
	}
}
