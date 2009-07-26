package com.kemai.wremja.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;


public class ProjectTest {

    /**
     * Projects must only be equal if the id matches.
     */
    @Test
    public void testEqualsAndHashCode() {
        Project a = new Project(1, "a", "");
        Project a1 = new Project(2, "a", "");
        Project b = new Project(3, "b", "");
        
        // this should be prohibited in the repository to have 2 projects with the same id
        Project a2 = new Project(1, "b", "");
        
        assertFalse(a.equals(a1));
        assertFalse(a.equals(b));
        assertTrue(a.equals(a2));
        assertEquals(a.hashCode(), a2.hashCode());
    }
    
    /**
     * Compare to should sort by title but must also return != 0 for projects with same title, but
     * different id.
     */
    @Test
    public void testCompareTo() {
        Project a = new Project(1, "a", "");
        Project a1 = new Project(2, "a", "");
        Project b = new Project(3, "b", "");
        Project a2 = new Project(1, "b", "");
        
        assertTrue(a.compareTo(a1) != 0);
        assertTrue(a.compareTo(b) < 0);
        assertTrue(a.compareTo(a2) < 0);
    }
    
}
