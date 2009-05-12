package com.kemai.wremja.exporter.anukotimetracker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.joda.time.DateTime;
import org.junit.Ignore;
import org.junit.Test;

import com.kemai.wremja.exporter.anukotimetracker.model.AnukoActivity;
import com.kemai.wremja.exporter.anukotimetracker.model.AnukoProject;
import com.kemai.wremja.exporter.anukotimetracker.util.AnukoAccess;
import com.kemai.wremja.model.Project;
import com.kemai.wremja.model.ProjectActivity;

@Ignore // Is Internet access dependent
public class SubmitActivityTest {

    private String url = "http://timetracker.wrconsulting.com/wginfo.php";
    private String login = "kutzi_user";
    private String password = "moin";
    
    @Test
    public void testSubmit() throws ClientProtocolException, IOException {
        DateTime now = new DateTime();
        Project proj = new Project(4711,"est", "");
        List<ProjectActivity> activities = new ArrayList<ProjectActivity>();
        activities.add( new ProjectActivity( now.minusMinutes(5), now, proj) );
        activities.add( new ProjectActivity( now.minusMinutes(10), now, proj) );
        
        Map<Project, AnukoActivity> mappings = new HashMap<Project, AnukoActivity>();
        AnukoActivity activity = new AnukoActivity(42430, "Activity A2");
        AnukoProject anukoProject = new AnukoProject(35718, "Project A");
        activity.addProject(anukoProject);
        mappings.put(proj, activity);
        
        AnukoAccess access = new AnukoAccess(url, login, password);
        access.submitActivities(activities, mappings);
        
    }
}
