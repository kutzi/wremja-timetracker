package com.kemai.wremja.model.io;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;

import com.kemai.wremja.model.ActivityRepository;
import com.kemai.wremja.model.Project;
import com.kemai.wremja.model.ProjectActivity;

public class ProTrackWriterTest {
    
    /**
     * Tests that a predefined ActivityRepository data always results in the same output format.
     * 
     * I.e. a regression test against involuntarily changing the data format.
     * 
     * Adapt this test, when you make deliberate changes to the data model!
     */
	@Test
    public void testProTrackWriting() throws IOException {
    	
        ActivityRepository data = new ActivityRepository();
        Project a = new Project(42, "foobar", "foo!");
        Project b = new Project(4711, "The Answer", "To the question");
        data.add(a);
        data.add(b);
        
        data.setActiveProject(b);
        data.start(time(2009, 3, 14, 18, 0, 0, 0));
        
        ProjectActivity activity = new ProjectActivity(time(2009, 3, 13, 15, 0, 0, 0),
                time(2009, 3, 13, 17, 0, 0, 0), a);
        data.addActivity(activity);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ProTrackWriter writer = ProTrackWriter.instance();
        writer.write(data, baos);
        
        String written = baos.toString(IOConstants.FILE_ENCODING);
        String expected = "<proTrack id=\"1\" active=\"true\" startTime=\"2009-03-14 18:00:00.0 UTC\">\n" + 
        		"  <activeProjects id=\"2\">\n" + 
        		"    <project id=\"3\">\n" + 
        		"      <id>42</id>\n" + 
        		"      <title>foobar</title>\n" + 
        		"      <description>foo!</description>\n" +
        		"      <billable>true</billable>\n" +
        		"      <enabled>true</enabled>\n" +
        		"    </project>\n" + 
        		"    <project id=\"4\">\n" + 
        		"      <id>4711</id>\n" + 
        		"      <title>The Answer</title>\n" + 
        		"      <description>To the question</description>\n" +
        		"      <billable>true</billable>\n" +
        		"      <enabled>true</enabled>\n" +
        		"    </project>\n" + 
        		"  </activeProjects>\n" + 
        		"  <projectsToBeDeleted id=\"5\"/>\n" + 
        		"  <activities id=\"6\">\n" + 
        		"    <projectActivity id=\"7\">\n" + 
        		"      <start id=\"8\">2009-03-13 15:00:00.0 UTC</start>\n" + 
        		"      <end id=\"9\">2009-03-13 17:00:00.0 UTC</end>\n" + 
        		"      <project reference=\"3\"/>\n" + 
        		"    </projectActivity>\n" + 
        		"  </activities>\n" + 
        		"  <activeProject reference=\"4\"/>\n" +
        		"  <projectIdSequence>4711</projectIdSequence>\n" +
        		"</proTrack>";
        
        assertEquals(normalizeWhitespace(expected), normalizeWhitespace(written));
    }
    
    
    private String normalizeWhitespace(String string) {
        StringBuilder normalized = new StringBuilder();
        
        String[] split = string.split("\\r\\n|\\r|\\n");
        for (String s : split) {
            normalized.append(s.trim());
        }
        return normalized.toString();
    }
    
	private DateTime time(int year, int month, int day, int hour, int minutes, int seconds, int ms) {
		return new DateTime(year, month, day, hour, minutes, seconds, ms, DateTimeZone.UTC);
	}
}
