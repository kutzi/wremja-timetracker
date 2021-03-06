/**
 * 
 */
package com.kemai.util;

import java.util.Iterator;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;

/**
 * Utility methods for working with strings.
 * @author remast
 */
public class StringUtils {

    /** Regular expression for xml tags. */
    private static final Pattern XML_TAG_PATTERN = Pattern.compile("<[^<>]+>");
    
    /**
     * Strip all xml tags from given String and unescape xml characters.
     * @param xml The XML string
     */
    public static String stripXmlTags(final String xml) {
        if (org.apache.commons.lang.StringUtils.isBlank(xml)) {
            return xml;
        }
        
        // 1. Remove xml tags
        String strippedXml = XML_TAG_PATTERN.matcher(xml).replaceAll("");
        
        // 2. Unescape xml entities
        strippedXml = StringEscapeUtils.unescapeXml(strippedXml);
        
        // 3. Trim whitespace
        strippedXml = strippedXml.trim();
        return strippedXml;
    }
    
    /**
     * Returns an {@link Iterable} which iterates over all Unicode 4 codepoints
     * in the given string.
     * 
     * {@link "http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=5003547"}
     */
    public static Iterable<Integer> codePoints(final String s) {
        return new Iterable<Integer>() {
            public Iterator<Integer> iterator() {
                return new Iterator<Integer>() {
                    int nextIndex = 0;
                    public boolean hasNext() {
                        return nextIndex < s.length();
                    }
                    public Integer next() {
                        int result = s.codePointAt(nextIndex);
                        nextIndex += Character.charCount(result);
                        return Integer.valueOf(result);
                    }
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }
    
    /**
     * Converts the given string into Unicode 4 codepoints.
     */
    public static int[] stringToCodepoints(String s) {
        int codePoints = s.codePointCount(0, s.length());
        int[] result = new int[codePoints];
        
        for(int i=0, nextIndex=0; i<result.length; i++) {
            result[i] = s.codePointAt(nextIndex);
            nextIndex += Character.charCount(result[i]);
        }
        return result;
    }
    
    /**
     * Returns a rough guess (!) if c is a consonant.
     * 
     * Currently only works reliably for English and German!
     */
    public static boolean isConsonant(char c) {
    	// FIXME: that's not i18n at all!
    	final String CONSONANTS = "bcdfghjklmnpqrstvwxzß" +
        	"BCDFGHJKLMNPQRSTVWXZ";
    	return CONSONANTS.indexOf(c) != -1;
    }
}
