package com.kemai.swing.text;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

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
				s = s.trim(); // conflate multiple whitespace
				
				if (sb.length() > 0) {
					sb.append(" "); // separate words with one space
				}
				sb.append(s);
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
}
