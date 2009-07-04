package com.kemai.wremja.model.report;

import static org.junit.Assert.assertEquals;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import com.kemai.wremja.AbstractWremjaTestCase;
import com.kemai.wremja.model.ActivityRepository;
import com.kemai.wremja.model.Project;
import com.kemai.wremja.model.ProjectActivity;


public class AccumulatedActivitiesReportTest extends AbstractWremjaTestCase {

	private ActivityRepository repository;
	
	@Before
	public void setup() {
		this.repository = new ActivityRepository();
	}
	
	@Test
	public void testAccumulation() {
		
		Project projectA  = new Project(1, "A", "A");
		this.repository.add(projectA);
		Project projectB  = new Project(1, "B", "B");
		this.repository.add(projectB);
		
		DateTime start = new DateTime(2009, 1, 1, 0, 0, 0, 0);
		ProjectActivity a = new ProjectActivity(start, start.plusHours(1), projectA);
		this.repository.addActivity(a);
		
		{
			AccumulatedActivitiesReport report = new AccumulatedActivitiesReport(this.repository, null);
			
			assertEquals(1, report.getAccumulatedActivitiesByDay().size());
			AccumulatedProjectActivity aa = report.getAccumulatedActivitiesByDay().get(0);
			assertEquals(1, aa.getTime(), 0.001);
		}
		
		{
			a = new ProjectActivity(start.plusHours(1), start.plusHours(3), projectA);
			this.repository.addActivity(a);
			
			AccumulatedActivitiesReport report = new AccumulatedActivitiesReport(this.repository, null);
			
			assertEquals(1, report.getAccumulatedActivitiesByDay().size());
			AccumulatedProjectActivity aa = report.getAccumulatedActivitiesByDay().get(0);
			assertEquals(3, aa.getTime(), 0.001);
		}
		
		{
			a = new ProjectActivity(start.plusHours(1), start.plusHours(3), projectB);
			this.repository.addActivity(a);
			
			AccumulatedActivitiesReport report = new AccumulatedActivitiesReport(this.repository, null);
			
			assertEquals(2, report.getAccumulatedActivitiesByDay().size());
			
			// order is defined here as project 'A' comes before 'B'
			AccumulatedProjectActivity aa = report.getAccumulatedActivitiesByDay().get(0);
			assertEquals(3, aa.getTime(), 0.001);
			
			aa = report.getAccumulatedActivitiesByDay().get(1);
			assertEquals(2, aa.getTime(), 0.001);
		}
		
		{
			a = new ProjectActivity(start.plusDays(1), start.plusDays(1).plusHours(5), projectB);
			this.repository.addActivity(a);
			
			AccumulatedActivitiesReport report = new AccumulatedActivitiesReport(this.repository, null);
			
			assertEquals(3, report.getAccumulatedActivitiesByDay().size());
			
			// newest acc. activity must be first
			AccumulatedProjectActivity aa = report.getAccumulatedActivitiesByDay().get(0);
			assertEquals(5, aa.getTime(), 0.001);
		}
	}
	
    @Test
    public void testAccumulationWithOtherYears() {

        Project projectA = new Project(1, "A", "A");
        this.repository.add(projectA);

        DateTime start = new DateTime(2009, 1, 1, 0, 0, 0, 0);
        ProjectActivity a = new ProjectActivity(start, start.plusHours(1),
                projectA);
        this.repository.addActivity(a);

        DateTime start2 = new DateTime(2008, 1, 1, 0, 0, 0, 0);
        ProjectActivity a2 = new ProjectActivity(start2, start2.plusHours(1),
                projectA);
        this.repository.addActivity(a2);

        {
            AccumulatedActivitiesReport report = new AccumulatedActivitiesReport(
                    this.repository, null);

            assertEquals(2, report.getAccumulatedActivitiesByDay().size());
            AccumulatedProjectActivity aa = report
                    .getAccumulatedActivitiesByDay().get(0);
            assertEquals(1, aa.getTime(), 0.001);
            
            aa = report.getAccumulatedActivitiesByDay().get(1);
            assertEquals(1, aa.getTime(), 0.001);
        }
        
        DateTime start3 = new DateTime(2010, 1, 1, 0, 0, 0, 0);
        ProjectActivity a3 = new ProjectActivity(start3, start3.plusHours(1),
                projectA);
        this.repository.addActivity(a3);
        {
            AccumulatedActivitiesReport report = new AccumulatedActivitiesReport(
                    this.repository, null);

            assertEquals(3, report.getAccumulatedActivitiesByDay().size());
            AccumulatedProjectActivity aa = report
                    .getAccumulatedActivitiesByDay().get(0);
            assertEquals(1, aa.getTime(), 0.001);
            
            aa = report.getAccumulatedActivitiesByDay().get(1);
            assertEquals(1, aa.getTime(), 0.001);
            
            aa = report.getAccumulatedActivitiesByDay().get(2);
            assertEquals(1, aa.getTime(), 0.001);
        }
        
    }
}
