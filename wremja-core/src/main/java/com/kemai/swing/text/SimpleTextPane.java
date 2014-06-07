package com.kemai.swing.text;

import javax.swing.JTextPane;
import javax.swing.text.StyledEditorKit;

public class SimpleTextPane extends JTextPane {
	
	private static final long serialVersionUID = 1L;
	
	public SimpleTextPane() {
		super();
		
		StyledEditorKit editorKit = new StyledEditorKit();
		setEditorKit(editorKit);
	}

	public SimpleTextPane(String text) {
		super();
		
		StyledEditorKit editorKit = new StyledEditorKit();
		setEditorKit(editorKit);
        
        setText(text);
	}

}
