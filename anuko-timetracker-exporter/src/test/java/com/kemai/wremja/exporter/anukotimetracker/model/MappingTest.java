package com.kemai.wremja.exporter.anukotimetracker.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.kemai.wremja.model.ActivityRepository;
import com.kemai.wremja.model.Project;


public class MappingTest {

    @Test
    public void testSerialization() {
        
        ActivityRepository repo = new ActivityRepository();
        Project a = new Project(1, "a", "");
        Project b = new Project(2, "a", "");
        
        repo.add(a);
        repo.add(b);
        
        AnukoActivity aa1 = new AnukoActivity(1, "aa1");
        AnukoActivity aa2 = new AnukoActivity(2, "aa2");
        AnukoInfo info = new AnukoInfo();
        info.addActivity(aa1);
        info.addActivity(aa2);
        
        Mapping m = new Mapping();
        m.add(a, aa1);
        m.add(b, aa2);
        
        String s = m.toString();
        
        Mapping m2 = Mapping.fromString(s, repo, info);
        
        assertEquals(2, m2.size());
        assertEquals(aa1, m2.get(a));
        assertEquals(aa2, m2.get(b));
    }
}
