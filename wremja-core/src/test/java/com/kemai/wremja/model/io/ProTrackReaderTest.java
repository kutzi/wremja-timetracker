package com.kemai.wremja.model.io;

import java.io.IOException;
import java.io.InputStream;

import junit.framework.TestCase;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.kemai.wremja.model.ActivityRepository;
import com.kemai.wremja.model.Project;
import com.kemai.wremja.model.ProjectActivity;

public class ProTrackReaderTest extends TestCase {
	
	private static final DateTimeZone TZ_BERLIN = DateTimeZone.forID("Europe/Berlin");
    
    /**
     * Tests that a predefined data file can be read in correctly.
     * 
     * I.e. a regression test against involuntarily changing the data format.
     */
    public void testProTrackReading() throws IOException {
        InputStream in = ProTrackReaderTest.class.getResourceAsStream("/Development.ptd.xml");
        assertNotNull(in);
        
        ProTrackReader reader = new ProTrackReader();
        ActivityRepository data = reader.read(in);
        assertNotNull(data);
        
        assertEquals(3, data.getProjects().size());
        assertEquals("Testing", data.getActiveProject().getTitle());
        assertFalse(data.isActive());
        DateTime startTime = time(2009, 1, 28, 19, 24, 0, 0);
        assertEquals(startTime, data.getStart());
        
        assertEquals(6, data.getActivities().size());
        ProjectActivity activity = data.getActivities().get(3);
        assertEquals("Bugfixing", activity.getProject().getTitle());
        DateTime activityStartTime = time(2008, 11, 29, 15, 0, 0, 0);
        DateTime activityEndTime = time(2008, 11, 29, 15, 15, 0, 0);
        assertEquals(activityStartTime, activity.getStart());
        assertEquals(activityEndTime, activity.getEnd());
        
        String description = "<html>\r\n" + 
        		"  <head>\r\n" + 
        		"\r\n" + 
        		"  </head>\r\n" + 
        		"  <body>\r\n" + 
        		"    <p style=\"margin-top: 0\">\r\n" + 
        		"      Added title to about dialog.\r\n" + 
        		"    </p>\r\n" + 
        		"  </body>\r\n" + 
        		"</html>";
        assertEquals(description, activity.getDescription().trim());
    }


    /**
     * Tests that a 'legacy' data file can be read in correctly. I.e. a file with:
     * 
     * - an activity with end < start
     * - an activity which ends on a later day than start
     * 
     * These 'features' are not supported anymore in Baralga.
     * Nevertheless, existing data files should be read in correctly. 
     */
    public void testProTrackReadLegacyData() throws IOException {
        InputStream in = ProTrackReaderTest.class.getResourceAsStream("/ProTrack_legacy.ptd.xml");
        assertNotNull(in);
        
        ProTrackReader reader = new ProTrackReader();
        ActivityRepository data = reader.read(in);
        assertNotNull(data);
        
        assertEquals(3, data.getProjects().size());
        assertEquals(6, data.getDeletedProjects().size());
        assertEquals("Testing", data.getActiveProject().getTitle());
        assertFalse(data.isActive());
        DateTime expectedStartTime = time(2009, 1, 28, 19, 24, 0, 0);
        assertEquals(expectedStartTime, data.getStart());
        
        // when reading legacy (without sequence) data, the calculated
        // next sequence value must be active + deleted projects count
        long expectedProjectSequence = data.getProjects().size() + data.getDeletedProjects().size();
        assertEquals(expectedProjectSequence, data.getProjectIdSequence());
        
        for (Project project : data.getProjects()) {
            //When reading legacy projects, billable and enabled must be set to true
            assertEquals(Boolean.TRUE, project.getBillable());
            assertEquals(Boolean.TRUE, project.getEnabled());
        }
        
        assertEquals(5, data.getActivities().size());
        
        {
            ProjectActivity activity = data.getActivities().get(0);
            assertEquals("Testing", activity.getProject().getTitle());
            DateTime activityStartTime = time(2009, 1, 28, 20, 0, 0, 0);
            DateTime activityEndTime = time(2009, 1, 28, 19, 0, 0, 0);
            assertEquals(activityStartTime, activity.getStart());
            assertEquals(activityEndTime, activity.getEnd());
            assertEquals(-1.0, activity.getDuration(), 0.001);
            
            String description = "Activity which ends before it starts";
            assertEquals(description, activity.getDescription().trim());
        }
        
        {
            ProjectActivity activity = data.getActivities().get(1);
            assertEquals("Development", activity.getProject().getTitle());
            DateTime activityStartTime = time(2009, 1, 28, 15, 0, 0, 0);
            DateTime activityEndTime = time(2009, 1, 29, 17, 30, 0, 0);
            assertEquals(activityStartTime, activity.getStart());
            assertEquals(activityEndTime, activity.getEnd());
            assertEquals(24 + 2.5, activity.getDuration(), 0.001);
            
            String description = "Activity which ends on next day";
            assertEquals(description, activity.getDescription().trim());
        }
        
        {
            ProjectActivity activity = data.getActivities().get(2);
            assertEquals("Bugfixing", activity.getProject().getTitle());
            DateTime activityStartTime = time(2008, 11, 25, 0, 0, 0, 0);
            DateTime activityEndTime = time(2008, 11, 29, 15, 15, 0, 0);
            assertEquals(activityStartTime, activity.getStart());
            assertEquals(activityEndTime, activity.getEnd());
            assertEquals(4*24 + 15.25, activity.getDuration(), 0.001);
            
            String description = "Activity which ends several days later";
            assertEquals(description, activity.getDescription().trim());
        }
    }
    
	private DateTime time(int year, int month, int day, int hour, int minutes, int seconds, int ms) {
		// dates in file are expressed as CET and then we need it to convert into the local
		// timezone:
		return new DateTime(year, month, day, hour, minutes, seconds, ms, TZ_BERLIN)
			.withZone(DateTimeZone.getDefault());
	}
}
