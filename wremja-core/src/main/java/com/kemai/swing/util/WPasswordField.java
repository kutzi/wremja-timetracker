package com.kemai.swing.util;

import javax.swing.JPasswordField;
import javax.swing.text.Document;

import de.kutzi.javautils.system.JavaUtil;

/**
 * A custom {@link JPasswordField} with a workaround for the bug:
 * http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6801620
 * 
 * @author kutzi
 */
public class WPasswordField extends JPasswordField {

	private static final long serialVersionUID = 1L;

	//private static volatile boolean warningDisplayed = false;
	
	public WPasswordField() {
		super();
		checkJPasswordFieldBug();
	}

	public WPasswordField(Document doc, String txt, int columns) {
		super(doc, txt, columns);
		checkJPasswordFieldBug();
	}

	public WPasswordField(int columns) {
		super(columns);
		checkJPasswordFieldBug();
	}

	public WPasswordField(String text, int columns) {
		super(text, columns);
		checkJPasswordFieldBug();
	}

	public WPasswordField(String text) {
		super(text);
		checkJPasswordFieldBug();
	}

	private void checkJPasswordFieldBug() {
		if(JavaUtil.hasJPasswordFieldBug()) {
			enableInputMethods(true);
		}
	}
}
