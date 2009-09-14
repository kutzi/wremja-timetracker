package com.kemai.wremja.model;

import java.util.List;

import org.joda.time.DateTime;

/**
 * Read-only view of the model data.
 */
public interface ReadableRepository {
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
     */
    public boolean isActive();

    /**
     * Looks for a project with given id.
     * @param id the id of the project to look for
     * @return the project or <code>null</code>
     */
    public Project findProjectById(long id);
}
