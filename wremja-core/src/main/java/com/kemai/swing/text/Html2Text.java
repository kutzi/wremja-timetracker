package com.kemai.swing.text;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML.Tag;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;

public class Html2Text{

	private Html2Text() {
	}

	public static String parse(Reader in) throws IOException {

		final StringBuilder sb = new StringBuilder();
		
		ParserDelegator delegator = new ParserDelegator();
		// the third parameter is TRUE to ignore charset directive
		delegator.parse(in, new HTMLEditorKit.ParserCallback() {
			@Override
			public void handleText(char[] text, int pos) {
				String s = new String(text);
				//s = s.trim(); // conflate multiple whitespace
				
//				if (sb.length() > 0) {
//					sb.append("\n");
//				}
				sb.append(s);
			}

			@Override
			public void handleStartTag(Tag t, MutableAttributeSet a, int pos) {
				if (t.toString().equals("li")) {
					sb.append("- ");
				}
			}

			@Override
			public void handleEndTag(Tag t, int pos) {
				if (isParagraph(t)) {
					sb.append("\n");
				}
				
				if (t.toString().equals("li")) {
					sb.append("\n");
				}
			}
			
		}, Boolean.TRUE);
		
		return sb.toString();
	}

	public static String parse(String s) {
		try {
			return parse(new StringReader(s));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
    private static boolean isParagraph(Tag t) {
        return (
            t == Tag.P
               || t == Tag.IMPLIED
               || t == Tag.DT
               || t == Tag.H1
               || t == Tag.H2
               || t == Tag.H3
               || t == Tag.H4
               || t == Tag.H5
               || t == Tag.H6
        );
    }
}
