package com.kemai.wremja.gui.model;

import static org.junit.Assert.*;

import org.joda.time.DateTime;
import org.joda.time.ReadableInstant;
import org.junit.Test;

import com.kemai.util.DateUtils;
import com.kemai.wremja.model.ActivityRepository;
import com.kemai.wremja.model.Project;
import com.kemai.wremja.model.ProjectActivity;

/**
 * Tests for the presentation model.
 * @author remast
 * @author kutzi
 * @see PresentationModel
 */
public class PresentationModelTest {

    /** First test project. */
    private static Project project1 = new Project(1, "Project1", "Project 1");

    /** Second test project. */
    private static Project project2 = new Project(2, "Project2", "Project 2");

    /**
     * Test for an activity that goes on until after midnight.
     * @throws ProjectActivityStateException should never be thrown if test is ok
     * @see Issue <a href="http://wremja.origo.ethz.ch/node/87">#17</a>
     */
    @Test
    public void testAcitivityOverMidnight() throws ProjectActivityStateException {
        PresentationModel model = getTestModel();
        final DateTime yesterday = DateUtils.getNow().minusMinutes(5).minusDays(1);

        final ReadableInstant midnight = yesterday.plusDays(1).toDateMidnight();

        // Set active project
        model.changeProject(project1);

        // Start activity on yesterday
        model.start(yesterday);

        // End activity today
        final DateTime now = DateUtils.getNow();
        model.stop();

        // Verify outcome
        assertEquals(2, model.getActivitiesList().size());

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
     * Test for changing the active project.
     * @see PresentationModel#changeProject(Project)
     */
    @Test
    public void testChangeProject() {
        PresentationModel model = getTestModel();
        
        model.changeProject(project1);
        assertEquals(project1, model.getSelectedProject());
        assertTrue(model.isDirty());

        model.changeProject(project2);
        assertEquals(project2, model.getSelectedProject());
        assertEquals(true, model.isDirty());

        model.changeProject(null);
        assertEquals(null, model.getSelectedProject());
        assertEquals(true, model.isDirty());
    }
    
    @Test
    public void testChangeData() {
        PresentationModel model = getTestModel();
        
        ActivityRepository newData = new ActivityRepository();
        model.setData(newData);
        assertTrue(model.isDirty());
        
        assertEquals(0, model.getProjectList().size());
    }

    @Test
    public void testExceptionOnDoubleStart() throws ProjectActivityStateException {
        PresentationModel model = getTestModel();

        model.changeProject(project1);
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
    
    private PresentationModel getTestModel() {
        ActivityRepository rep = new ActivityRepository();
        rep.add(project1);
        rep.add(project2);
        PresentationModel model = new PresentationModel(rep);
        assertFalse(model.isDirty());
        assertEquals(2, model.getProjectList().size());
        assertEquals(0, model.getActivitiesList().size());
        return model;
    }
}
