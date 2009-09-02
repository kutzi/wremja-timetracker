package com.kemai.wremja.model.export;

import java.io.ByteArrayOutputStream;
import java.util.Locale;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.kemai.wremja.model.ActivityRepository;
import com.kemai.wremja.model.Project;
import com.kemai.wremja.model.ProjectActivity;
import com.kemai.wremja.model.io.IOConstants;

/**
 * Test case for the {@link CsvExporter}.
 *
 * @author kutzi
 */
public class CsvExporterTest {

    private Locale oldLocale;
    
    @Before
    public void setup() {
        oldLocale = Locale.getDefault();
        Locale.setDefault(Locale.US);
    }
    
    @After
    public void tearDown() {
        Locale.setDefault(oldLocale);
    }
    
    @Test
    public void testCsvExport() throws Exception {
        
        String expectedOutput = "\"Project\";\"Date\";\"Start Time\";\"End Time\";\"Hours\";\"Description\"\n" + 
        		"\"With Quote(\"\") in it\";\"13.03.2009\";\"03:00\";\"03:00\";\"0.00\";\n" + 
        		"\"The Answer\";\"13.03.2009\";\"03:00\";\"05:00\";\"2.00\";\n";
        
        ActivityRepository data = new ActivityRepository();
        Project a = new Project(42, "With Quote(\") in it", "foo!");
        Project b = new Project(4711, "The Answer", "To the question");
        data.add(a);
        data.add(b);
        
        data.setActiveProject(b);
        data.start(new DateTime(2009, 3, 14, 18, 0, 0, 0));
        
        ProjectActivity activity = new ProjectActivity(new DateTime(2009, 3, 13, 15, 0, 0, 0),
                new DateTime(2009, 3, 13, 15, 0, 0, 0), a);
        data.addActivity(activity);
        
        activity = new ProjectActivity(new DateTime(2009, 3, 13, 15, 0, 0, 0),
                new DateTime(2009, 3, 13, 17, 0, 0, 0), b);
        data.addActivity(activity);
        
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CsvExporter exporter = new CsvExporter();
        exporter.export(data, null, baos);
        
        String result = baos.toString(IOConstants.FILE_ENCODING);
        Assert.assertEquals(expectedOutput, result);
    }
    
    @Test
    public void testCsvExportWithNewline() throws Exception {
        
        String expectedOutput = "\"Project\";\"Date\";\"Start Time\";\"End Time\";\"Hours\";\"Description\"\n" + 
                "\"With Newline \n\n in it\";\"13.03.2009\";\"03:00\";\"03:00\";\"0.00\";\n" + 
                "\"With carriage \r return\";\"13.03.2009\";\"03:00\";\"05:00\";\"2.00\";\n";
        
        ActivityRepository data = new ActivityRepository();
        Project a = new Project(42, "With Newline \n\n in it", "");
        Project b = new Project(4711, "With carriage \r return", "");
        data.add(a);
        data.add(b);
        
        data.setActiveProject(b);
        data.start(new DateTime(2009, 3, 14, 18, 0, 0, 0));
        
        ProjectActivity activity = new ProjectActivity(new DateTime(2009, 3, 13, 15, 0, 0, 0),
                new DateTime(2009, 3, 13, 15, 0, 0, 0), a);
        data.addActivity(activity);
        
        activity = new ProjectActivity(new DateTime(2009, 3, 13, 15, 0, 0, 0),
                new DateTime(2009, 3, 13, 17, 0, 0, 0), b);
        data.addActivity(activity);
        
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CsvExporter exporter = new CsvExporter();
        exporter.export(data, null, baos);
        
        String result = baos.toString(IOConstants.FILE_ENCODING);
        Assert.assertEquals(expectedOutput, result);
    }
    
}
