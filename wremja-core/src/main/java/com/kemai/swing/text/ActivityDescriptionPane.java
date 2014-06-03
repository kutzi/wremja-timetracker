package com.kemai.swing.text;

import javax.swing.JTextPane;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

public class ActivityDescriptionPane extends JTextPane {
	
	private static final long serialVersionUID = 1L;

	public ActivityDescriptionPane(String text) {
		super();
		StyleSheet styleSheet = new StyleSheet();
        styleSheet.addRule("body {font-family: Tahoma; font-size: 11pt; font-style: normal; font-weight: normal;}");

        HTMLEditorKit editorKit = new HTMLEditorKit();
        editorKit.setStyleSheet(styleSheet);
        setEditorKit(editorKit);
        
        setText(text);
	}

}
