package com.kemai.wremja.exporter.anukotimetracker.model;

import java.util.HashMap;
import java.util.Map;

import com.kemai.wremja.model.Project;
import com.kemai.wremja.model.ReadableRepository;

/**
 * Mapping from Wremja {@link Project} to Anuko {@link AnukoActivity}.
 * 
 * @author kutzi
 */
public class Mapping {
    private final Map<Project, AnukoActivity> map; 
    
    public Mapping() {
        this.map = new HashMap<Project, AnukoActivity>();
    }
    
    public Mapping(Map<Project, AnukoActivity> map) {
        this.map = map;
    }

    public void add(Project wremjaProject, AnukoActivity anukoActivity) {
        this.map.put(wremjaProject, anukoActivity);
    }
    
    public void remove(Project wremjaProject) {
        this.map.remove(wremjaProject);
    }
    
    public Map<Project, AnukoActivity> getMap() {
        return this.map;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for(Map.Entry<Project, AnukoActivity> e : map.entrySet()) {
            builder.append("[")
                .append(e.getKey().getId())
                .append(",")
                .append(e.getValue().getId())
                .append("],");
        }
        return builder.toString();
    }
    
    public static Mapping fromString(String s, ReadableRepository repo, AnukoInfo anukoInfo) {
        String[] split = s.replace("[", " ").replace("]", " ").split(",[ ]*");
        
        Map<Project, AnukoActivity> map = new HashMap<Project, AnukoActivity>();
        for(int i=0; i < split.length; i++) {
            Project p = repo.findProjectById(Long.parseLong(split[i].trim()));
            AnukoActivity activity = anukoInfo.getActivityById(Long.parseLong(split[++i].trim()));
            map.put(p, activity);
        }
        
        return new Mapping(map);
    }
}
