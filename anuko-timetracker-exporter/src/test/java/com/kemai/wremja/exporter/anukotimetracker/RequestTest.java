package com.kemai.wremja.exporter.anukotimetracker;

import javax.swing.JDialog;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;

import org.jdesktop.swingx.plaf.LookAndFeelAddons;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.jgoodies.looks.plastic.Plastic3DLookAndFeel;
import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.kemai.util.DateUtils;
import com.kemai.wremja.exporter.anukotimetracker.gui.ExportDialog;
import com.kemai.wremja.gui.settings.IUserSettings;
import com.kemai.wremja.gui.settings.UserSettingsAdapter;
import com.kemai.wremja.model.ActivityRepository;
import com.kemai.wremja.model.Project;
import com.kemai.wremja.model.ProjectActivity;
import com.kemai.wremja.model.filter.Filter;

/**
 * Test for the Anuko exporter
 */
public class RequestTest {

    @Test @Ignore
    public void testApp() throws InterruptedException {
        ActivityRepository data = new ActivityRepository();
        Project project1 = new Project(1, "Project 1", "Description of project 1");
        Project project2 = new Project(2, "Project 2", "Description of project 2");
        data.add(project1);
        data.add(project2);
        
        DateTime now = new DateTime();
        ProjectActivity activity1 = new ProjectActivity( now.minusHours(1), now, project1);
        ProjectActivity activity2 = new ProjectActivity( now.minusHours(2), now, project2);
        data.addActivity(activity1);
        data.addActivity(activity2);
        
        DateTime yesterday = now.minusDays(1);
        ProjectActivity activity3 = new ProjectActivity( yesterday.minusHours(3), now, project2);
        data.addActivity(activity3);
        
        Filter filter = new Filter();
        filter.setWeekOfYear(DateUtils.getNow().getWeekOfWeekyear());
        filter.setYear(DateUtils.getNow().getYear());
        
        displayExportDialog( data, filter );
    }
    
    private void displayExportDialog(ActivityRepository data, Filter filter) throws InterruptedException {
        initLookAndFeel();

        IUserSettings settings = createUserSettings();
        
        
        JDialog exportDialog = new ExportDialog(null,
        		settings,
                "http://timetracker.wrconsulting.com/",
                "kutzi_user", "moin", data, filter);
        exportDialog.setLocationByPlatform(true);
        exportDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        exportDialog.pack();
        exportDialog.setVisible(true);
        
//        final Object lock = new Object();
        
//        exportDialog.addWindowListener( new WindowAdapter() {
//            @Override
//            public void windowClosed(WindowEvent e) {
//                synchronized (lock) {
//                    lock.notify();
//                }
//            }
//        });
//        
//        // wait for dialog to close
//        synchronized (lock) {
//            lock.wait();
//        }
    }

    private IUserSettings createUserSettings() {
	    return new UserSettingsAdapter() {

			@Override
            public String getAnukoMappings() {
				return "[1,42430],[2,2]";
            }

			@Override
            public void setAnukoMappings(String s) {
	            System.out.println("saved mappings: " + s);
            }
	    	
	    };
    }

	private void initLookAndFeel() {
    	// enable antialiasing
        System.setProperty("swing.aatext", "true");

        // use Xrender pipeline on Linux, if available
        System.setProperty("sun.java2d.xrender", "true");

        try {
            // a) Try windows
            UIManager.setLookAndFeel(new Plastic3DLookAndFeel());
            Plastic3DLookAndFeel.setTabStyle(PlasticLookAndFeel.TAB_STYLE_METAL_VALUE);
            Plastic3DLookAndFeel.setHighContrastFocusColorsEnabled(true);
        } catch (UnsupportedLookAndFeelException e) {
            // b) Try system look & feel
            try {
                UIManager.setLookAndFeel(
                        UIManager.getSystemLookAndFeelClassName()
                );
            } catch (Exception ex) {
                Assert.fail(ex.toString());
            }
        }
        String s = LookAndFeelAddons.getBestMatchAddonClassName();
        try {
            LookAndFeelAddons.setAddon(s);
        } catch (Exception e) {
        	Assert.fail(e.toString());
        }
    }
}
