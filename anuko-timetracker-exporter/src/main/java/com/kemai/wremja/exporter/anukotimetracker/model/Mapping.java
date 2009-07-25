package com.kemai.wremja.exporter.anukotimetracker.model;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import com.kemai.wremja.logging.Logger;
import com.kemai.wremja.model.Project;
import com.kemai.wremja.model.ReadableRepository;

/**
 * Mapping from Wremja {@link Project} to Anuko {@link AnukoActivity}.
 * 
 * @author kutzi
 */
public class Mapping {
	private static final Logger LOG  = Logger.getLogger(Mapping.class);
	
    private final SortedMap<Project, AnukoActivity> map; 
    
    public Mapping() {
        this.map = new TreeMap<Project, AnukoActivity>();
    }
    
    public Mapping(SortedMap<Project, AnukoActivity> map) {
        this.map = map;
    }

    public void add(Project wremjaProject, AnukoActivity anukoActivity) {
        this.map.put(wremjaProject, anukoActivity);
    }
    
    public void remove(Project wremjaProject) {
        this.map.remove(wremjaProject);
    }
    
    public AnukoActivity get(Project wremjaProject) {
    	return this.map.get(wremjaProject);
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
        if(builder.length() > 0) {
        	builder.deleteCharAt(builder.length() - 1); // delete last comma
        }
        return builder.toString();
    }
    
    public static Mapping fromString(String s, ReadableRepository repo, AnukoInfo anukoInfo) {
        String[] split = s.replace("[", " ").replace("]", " ").split(",[ ]*");
        
        SortedMap<Project, AnukoActivity> map = new TreeMap<Project, AnukoActivity>();
        for(int i=0; i < split.length; i++) {
        	try {
	            long wremjaId = Long.parseLong(split[i].trim());
	            long anukoId = Long.parseLong(split[++i].trim());
	            Project p = repo.findProjectById(wremjaId);
	            AnukoActivity activity = anukoInfo.getActivityById(anukoId);
	            
	            if(p != null && activity != null) {
	            	map.put(p, activity);
	            } else {
	            	LOG.info("Unresolved mapping from wremja project " + wremjaId +
	            			" to anuko activity " + anukoId);
	            }
            } catch (NumberFormatException e) {
            	LOG.error(e, e);
            }
        }
        
        return new Mapping(map);
    }

	public int size() {
	    return this.map.size();
    }
}
