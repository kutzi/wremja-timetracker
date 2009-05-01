package com.kemai.wremja.gui.model;

import junit.framework.TestCase;

import org.joda.time.DateTime;
import org.joda.time.ReadableInstant;

import com.kemai.util.DateUtils;
import com.kemai.wremja.model.Project;
import com.kemai.wremja.model.ProjectActivity;

/**
 * Tests for the presentation model.
 * @author remast
 * @see PresentationModel
 */
public class PresentationModelTest extends TestCase {

    /** The model under test. */
    private PresentationModel model = new PresentationModel();

    /** First test project. */
    private Project project1 = new Project(1, "Project1", "Project 1");

    /** Second test project. */
    private Project project2 = new Project(2, "Project2", "Project 2");

    @Override
    protected void setUp() throws Exception {
        model.addProject(project1, this);
        model.addProject(project2, this);
    }

    /**
     * Test for an activity that goes on until after midnight.
     * @throws ProjectActivityStateException should never be thrown if test is ok
     * @see Issue <a href="http://wremja.origo.ethz.ch/node/87">#17</a>
     */
    public void testAcitivityOverMidnight() throws ProjectActivityStateException {
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
    public void testChangeProject() {
        model.setDirty(false);
        
        model.changeProject(project1);
        assertEquals(project1, model.getSelectedProject());
        assertEquals(true, model.isDirty());

        model.changeProject(project2);
        assertEquals(project2, model.getSelectedProject());

        model.changeProject(null);
        assertEquals(null, model.getSelectedProject());
    }

    public void testExceptionOnDoubleStart() throws ProjectActivityStateException {
        model.setDirty(false);
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
}
