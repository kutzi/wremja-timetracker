package com.kemai.wremja.gui.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.atomic.AtomicInteger;

import org.joda.time.DateTime;
import org.joda.time.ReadableInstant;
import org.junit.BeforeClass;
import org.junit.Test;

import com.kemai.util.DateUtils;
import com.kemai.wremja.AbstractWremjaTestCase;
import com.kemai.wremja.gui.events.WremjaEvent;
import com.kemai.wremja.gui.events.WremjaEvent.Type;
import com.kemai.wremja.gui.settings.IUserSettings;
import com.kemai.wremja.gui.settings.UserSettingsAdapter;
import com.kemai.wremja.model.ActivityRepository;
import com.kemai.wremja.model.OverlappingActivitiesException;
import com.kemai.wremja.model.Project;
import com.kemai.wremja.model.ProjectActivity;

/**
 * Tests for the presentation model.
 * @author remast
 * @author kutzi
 * @see PresentationModel
 */
public class PresentationModelTest extends AbstractWremjaTestCase {
	
	@BeforeClass
	public static void beforeClass() {
		System.setProperty("java.awt.headless", "true");
	}

    /** First test project. */
    private static Project project1 = new Project(1, "Project1", "Project 1");

    /** Second test project. */
    private static Project project2 = new Project(2, "Project2", "Project 2");
    
    /**
     * Test for an activity that goes on until after midnight.
     * @throws ProjectActivityStateException should never be thrown if test is ok
     * @see <a href="http://baralga.origo.ethz.ch/node/87">Baralga Issue #17</a>
     */
    @Test
    public void testActivityOverMidnight() throws ProjectActivityStateException {
        PresentationModel model = getNewTestModel();
        final DateTime yesterday = DateUtils.getNow().withTimeAtStartOfDay().minusMinutes(5);

        final ReadableInstant midnight = yesterday.plusDays(1).withTimeAtStartOfDay();

        // Set active project
        model.startNewProject(project1);

        // Start activity on yesterday
        model.start(yesterday);

        final AtomicInteger activityAddedEventCount = new AtomicInteger(0);
        model.addObserver(new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                if(arg instanceof WremjaEvent) {
                    WremjaEvent event = (WremjaEvent) arg;
                    if(event.getType() == Type.PROJECT_ACTIVITY_ADDED) {
                        activityAddedEventCount.incrementAndGet();
                    }
                }
            }
        });
        // End activity today
        final DateTime now = DateUtils.getNow();
        model.stop();

        // Verify outcome
        assertEquals(2, model.getActivitiesList().size());
        
        assertEquals(2, activityAddedEventCount.get());

        for (ProjectActivity activity : model.getActivitiesList()) {
            assertEquals(project1, activity.getProject());
        }

        final ProjectActivity todaysActivity = model.getActivitiesList().get(0);
        final ProjectActivity yesterdaysActivity = model.getActivitiesList().get(1);

        // 1. Check yesterdays activity
        assertEquals(yesterday, yesterdaysActivity.getStart());
        assertEquals(midnight, yesterdaysActivity.getEnd());

        // 2. Check today activity
        assertEquals(midnight, todaysActivity.getStart());
        assertEquals(now, todaysActivity.getEnd());
    }

    /**
     * Test for an activity that goes on until after midnight.
     */
    @Test
    public void testAcitivityOverMidnight2() throws ProjectActivityStateException {
        PresentationModel model = getNewTestModel();
        final DateTime threeDaysAgo = DateUtils.getNow().withTimeAtStartOfDay().minusDays(2).minusMinutes(5);

        final DateTime midnight = threeDaysAgo.plusDays(1).withTimeAtStartOfDay();

        // Set active project
        model.startNewProject(project1);

        // Start activity on threeDaysAgo
        model.start(threeDaysAgo);

        final AtomicInteger activityAddedEventCount = new AtomicInteger(0);
        model.addObserver(new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                if(arg instanceof WremjaEvent) {
                    WremjaEvent event = (WremjaEvent) arg;
                    if(event.getType() == Type.PROJECT_ACTIVITY_ADDED) {
                        activityAddedEventCount.incrementAndGet();
                    }
                }
            }
        });
        // End activity today
        final DateTime now = DateUtils.getNow();
        model.stop();

        // Verify outcome
        assertEquals(4, model.getActivitiesList().size());
        assertEquals(4, activityAddedEventCount.get());

        for (ProjectActivity activity : model.getActivitiesList()) {
            assertEquals(project1, activity.getProject());
        }

        final ProjectActivity todaysActivity = model.getActivitiesList().get(0);
        final ProjectActivity thirdActivity = model.getActivitiesList().get(1);
        final ProjectActivity secondActivity = model.getActivitiesList().get(2);
        final ProjectActivity firstActivity = model.getActivitiesList().get(3);

        // 1. Check 1st activity
        assertEquals(threeDaysAgo, firstActivity.getStart());
        assertEquals(midnight, firstActivity.getEnd());
        
        // 2. Check 2nd activity
        assertEquals(midnight, secondActivity.getStart());
        assertEquals(midnight.plusDays(1), secondActivity.getEnd());
        
        // 3. Check 3rd activity
        assertEquals(midnight.plusDays(1), thirdActivity.getStart());
        assertEquals(midnight.plusDays(2), thirdActivity.getEnd());

        // 4. Check today activity
        assertEquals(midnight.plusDays(2), todaysActivity.getStart());
        assertEquals(now, todaysActivity.getEnd());
    }

    /**
     * Test for changing the active project.
     * @see PresentationModel#startNewProject(Project)
     */
    @Test
    public void testChangeProject() {
        PresentationModel model = getNewTestModel();
        
        model.startNewProject(project1);
        assertEquals(project1, model.getSelectedProject());
        assertTrue(model.isDirty());

        model.startNewProject(project2);
        assertEquals(project2, model.getSelectedProject());
        assertTrue(model.isDirty());

        model.startNewProject(null);
        assertEquals(null, model.getSelectedProject());
        assertTrue(model.isDirty());
    }
    
    @Test
    public void testChangeData() {
        PresentationModel model = getNewTestModel();
        
        ActivityRepository newData = new ActivityRepository();
        model.setData(newData);
        assertTrue(model.isDirty());
        
        assertEquals(0, model.getProjectList().size());
    }

    @Test
    public void testExceptionOnDoubleStart() throws ProjectActivityStateException {
        PresentationModel model = getNewTestModel();

        model.startNewProject(project1);
        model.start();
        
        assertTrue(model.isActive());
        assertTrue(model.isDirty());
        
        try {
            model.start();
            fail( "ProjectActivityStateException expected" );
        } catch( ProjectActivityStateException e ) {
            // ok, expected
        }
    }
    
    @Test
    public void testProjectRename() throws OverlappingActivitiesException {
    	PresentationModel model = getNewTestModel();
    	
    	Project project = new Project(4711, "TestProject", "TestProject");
    	model.addProject(project, this);
    	
    	DateTime start = new DateTime(0);
    	
    	ProjectActivity a1 = new ProjectActivity(start, start.plusHours(1), project);
    	ProjectActivity a2 = new ProjectActivity(start.plusHours(1), start.plusHours(2), project);
    	model.addActivity(a1, this);
    	model.addActivity(a2, this);

    	assertEquals(2, model.getActivitiesList().size());

    	project.setTitle("Project");
    	PropertyChangeEvent propertyChangeEvent = new PropertyChangeEvent(project, Project.PROPERTY_TITLE, "TestProject", "Project");
    	model.fireProjectChangedEvent(project, propertyChangeEvent);
    	
    	// check that activities are still attached to the same project:
    	assertEquals(project, a1.getProject());
    	assertEquals(project, a2.getProject());
    }
    
    @Test
    public void testDisallowOverlappingActivities() {
        {
            IUserSettings settings = new UserSettingsAdapter() {
                @Override
                public boolean isAllowOverlappingActivities() {
                    return false;
                }
            };
            PresentationModel model = new PresentationModel(settings, getNewLastTouchFile());
            DateTime start = new DateTime(0);
            
            Project project = new Project(4711, "TestProject", "TestProject");
            model.addProject(project, this);
            
            ProjectActivity a1 = new ProjectActivity(start, start.plusHours(1), project);
            ProjectActivity a2 = new ProjectActivity(start, start.plusHours(2), project);
            try {
                model.addActivity(a1, this);
                model.addActivity(a2, this);
                fail(OverlappingActivitiesException.class.getName() + " expected");
            } catch (OverlappingActivitiesException e) {
                // okay
            }
        }
        
        {
            IUserSettings settings = new UserSettingsAdapter() {
                @Override
                public boolean isAllowOverlappingActivities() {
                    return true;
                }
            };
            PresentationModel model = new PresentationModel(settings, getNewLastTouchFile());
            DateTime start = new DateTime(0);
            
            Project project = new Project(4711, "TestProject", "TestProject");
            model.addProject(project, this);
            
            ProjectActivity a1 = new ProjectActivity(start, start.plusHours(1), project);
            ProjectActivity a2 = new ProjectActivity(start, start.plusHours(2), project);
            try {
                model.addActivity(a1, this);
                model.addActivity(a2, this);
            } catch (OverlappingActivitiesException e) {
                fail(e.toString());
            }
        }
    }
    
    @Test
    public void testDisallowEmptyActivities() throws OverlappingActivitiesException, ProjectActivityStateException {
        {
            IUserSettings settings = new UserSettingsAdapter() {
                @Override
                public boolean isDiscardEmptyActivities() {
                    return true;
                }
            };
            PresentationModel model = new PresentationModel(settings, getNewLastTouchFile());
            DateTime start = new DateTime(0);
            
            Project project = new Project(1, "TestProject", "");
            model.addProject(project, this);
            model.startNewProject(project);
            DateTime now = DateUtils.getNow();
            model.start(now);
            model.stop(now);
            assertTrue(model.getActivitiesList().isEmpty());
            
            
            // test project change:
            Project projectB = new Project(2, "Project B", "");
            model.addProject(projectB, this);
            model.start();
            model.startNewProject(projectB);
            model.stop();
            assertTrue(model.getActivitiesList().isEmpty());
            
            // explicitly added empty activities are still okay 
            ProjectActivity a1 = new ProjectActivity(start, start, project);
            model.addActivity(a1, this);

            assertEquals(1, model.getActivitiesList().size());
        }
        
        {
            IUserSettings settings = new UserSettingsAdapter() {
                @Override
                public boolean isDiscardEmptyActivities() {
                    return false;
                }
            };
            PresentationModel model = new PresentationModel(settings, getNewLastTouchFile());
            DateTime start = new DateTime(0);
            
            Project project = new Project(1, "TestProject", "");
            model.addProject(project, this);
            model.startNewProject(project);
            DateTime now = DateUtils.getNow();
            model.start(now);
            model.stop(now);
            assertEquals(1, model.getActivitiesList().size());
            
            
            // test project change:
            Project projectB = new Project(2, "Project B", "");
            model.addProject(projectB, this);
            model.start();
            model.startNewProject(projectB);
            model.stop();
            assertEquals(3, model.getActivitiesList().size());
            
            // explicitly added empty activities are still okay 
            ProjectActivity a1 = new ProjectActivity(start, start, project);
            model.addActivity(a1, this);
            
            assertEquals(4, model.getActivitiesList().size());
        }
    }
    
    private PresentationModel getNewTestModel() {
        ActivityRepository rep = new ActivityRepository();
        rep.add(project1);
        rep.add(project2);
        assertTrue(rep.isDirty());
        PresentationModel model = new PresentationModel(new UserSettingsAdapter(), getNewLastTouchFile());
        model.setData(rep, false);
        rep.setDirty(false);
        assertEquals(2, model.getProjectList().size());
        assertEquals(0, model.getActivitiesList().size());
        return model;
    }
    
    private File getNewLastTouchFile() {
        File tmp = null;
        try {
            tmp = File.createTempFile("wremja-test", null);
            tmp.deleteOnExit();
        } catch (IOException e) {
            fail(e.toString());
        }
        return tmp;
    }
}
