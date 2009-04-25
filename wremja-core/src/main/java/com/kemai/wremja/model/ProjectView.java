package com.kemai.wremja.model;

import java.util.List;

import org.joda.time.DateTime;

/**
 * Readable view of the model data.
 */
public interface ProjectView {
    /**
     * @return read-only view of the projects
     */
    public List<Project> getProjects();
    
    /**
     * @return read-only view of the activities
     */
    public List<ProjectActivity> getActivities();
    
    /**
     * @return the activeProject
     */
    public Project getActiveProject();
    
    /**
     * Returns the starting time of the currently active activity.
     */
    public DateTime getStart();
    
    /**
     * Returns <code>true</code> iff there's currently a started activity.
     * @return
     */
    public boolean isActive();
}
