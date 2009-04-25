package com.kemai.util;

import com.kemai.util.StringUtils;

import junit.framework.TestCase;

/**
 * Tests the class {@link StringUtils}.
 * @author remast
 * @see StringUtils
 */
public class StringUtilsTest extends TestCase {
	
    /**
     * Test for {@link StringUtils#stripXmlTags(String)}.
     */
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
}
