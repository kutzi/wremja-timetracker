package com.kemai.swing.text;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

public class StyledTextPane {
	
	private final JTextPane pane = new JTextPane();

	private StyledTextPane() {
		super();
	}
	
	public static StyledTextPane newHtmlTextPane(String htmlText) {
		StyledTextPane p = new StyledTextPane();

		StyleSheet styleSheet = new StyleSheet();
        styleSheet.addRule("body {font-family: Tahoma; font-size: 11pt; font-style: normal; font-weight: normal;}");

        HTMLEditorKit editorKit = new HTMLEditorKit();
        editorKit.setStyleSheet(styleSheet);
        p.pane.setEditorKit(editorKit);
        
        p.pane.setText(htmlText);
        
        return p;
	}

	
	public static StyledTextPane newStyledTextPane() {
		StyledTextPane p = new StyledTextPane();
		
		StyledEditorKit editorKit = new StyledEditorKit();
		p.pane.setEditorKit(editorKit);
		
		return p;
	}
	
	public void appendText(String text) {
		Document doc = pane.getDocument();
		try {
			doc.insertString(doc.getLength(), text, null);
		} catch (BadLocationException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void appendText(String text, Object style, boolean on) {
		SimpleAttributeSet sas = null;
		sas = new SimpleAttributeSet();
		sas.addAttribute(style, on);
		Document doc = pane.getDocument();
		try {
			doc.insertString(doc.getLength(), text, sas);
		} catch (BadLocationException e) {
			throw new RuntimeException(e);
		}
	}
	
	public JTextPane asComponent() {
		return pane;
	}
}
