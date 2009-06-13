package com.kemai.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JLabel;

import org.junit.Ignore;
import org.junit.Test;

/**
 * Tests the class {@link StringUtils}.
 * @author remast
 * @author kutzi
 * @see StringUtils
 */
public class StringUtilsTest {
	
    /**
     * Test for {@link StringUtils#stripXmlTags(String)}.
     */
    @Test
	public void testStripXmlTags() {
        assertEquals(null, com.kemai.util.StringUtils.stripXmlTags(null));
        assertNotSame("", com.kemai.util.StringUtils.stripXmlTags(null));
        assertEquals("", com.kemai.util.StringUtils.stripXmlTags(""));
        assertEquals("<", com.kemai.util.StringUtils.stripXmlTags("<"));
        assertEquals("<", com.kemai.util.StringUtils.stripXmlTags("&lt;"));

        String htmlText = "<p>content</p>";
        String text = "content";
        assertEquals(text, com.kemai.util.StringUtils.stripXmlTags(htmlText));

        htmlText = 
	        "<html> " 
	        +  "<body>" 
	        +    "<p>content <i>Italic</i> and < b> bold</B>" 
	        +  "</body>" 
	        + 
	        "</html>";
	    text = "content Italic and  bold";
	    assertEquals(text, com.kemai.util.StringUtils.stripXmlTags(htmlText));
	}
    
    @Test
    @Ignore
    public void testStringToCodepoints() throws IOException {
        {
            String s = "äöü\uD840\uDC00";
            assertEquals(5, s.length());
            display(s);
            int[] codePoints = StringUtils.stringToCodepoints(s);
            assertEquals(4, codePoints.length);
        }
        
        {
            String s = "\u0041\u00DF\u6771\uD801\uDC00";
            assertEquals(5, s.length());
            display(s);
            int[] codePoints = StringUtils.stringToCodepoints(s);
            assertEquals(4, codePoints.length);
        }
        
        String s = "\u03D1";
        display(s);
        
        s ="\u2AA7";
        //display(s);
        
        System.in.read();
    }
    
    private void display(String s) {
        JFrame frame = new JFrame ();
        frame.add(new JLabel(s));
        frame.pack();
        frame.setVisible(true);
    }
}
