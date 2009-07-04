package com.kemai.wremja.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.joda.time.DateTime;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

/**
 * Main data container of Wremja.
 * 
 * This consists mainly of project and activities in these projects.
 */
@XStreamAlias("proTrack") 
public class ActivityRepository implements ReadableRepository, Serializable {

	private static final long serialVersionUID = 1L;

	/** All active projects. */
	private final List<Project> activeProjects = new ArrayList<Project>();

	/** All projects that are not active any more but may be referenced in project activities. */
	private final List<Project> projectsToBeDeleted = new ArrayList<Project>();

	/** All project activities. */
	private final List<ProjectActivity> activities = new ArrayList<ProjectActivity>();

	/** Flag indicating a currently active activity. */
	@XStreamAsAttribute
	private boolean active = false;

	/** The start time of the current activity (if any). */
	@XStreamAsAttribute
	private DateTime startTime;

	/** The currently active project (if any). */
	private Project activeProject;
	
	/**
	 * The last time this data record was modified.
	 */
	private DateTime modifiedTimeStamp;
	
	@XStreamOmitField
	private boolean dirty = false;

	public ActivityRepository() {
	}

	/**
	 * Adds a new project.
	 * @param project the project to add
	 */
	public synchronized void add(final Project project) {
		if (project == null) {
			return;
		}
		
		this.activeProjects.add(project);
		this.dirty = true;
	}

	/**
	 * Removes a project.
	 * @param project the project to remove
	 */
	public synchronized void remove(final Project project) {
		if (project == null) {
			return;
		}
		
		this.activeProjects.remove(project);
		this.projectsToBeDeleted.add(project);
		this.dirty = true;
	}

	// TODO: this method is called from nowhere!?
	public synchronized void cleanup() {
		if (this.projectsToBeDeleted == null) {
			return;
		}

		final List<Project> referencedProjects = new ArrayList<Project>();        
		for(ProjectActivity projectActivity : this.activities) {
			Project project = projectActivity.getProject();
			referencedProjects.add(project);
		}

		final List<Project> removableProjects = new ArrayList<Project>();
		for (Project oldProject : this.projectsToBeDeleted) {
			if (!referencedProjects.contains(oldProject)) {
				removableProjects.add(oldProject);
			}
		}

		this.projectsToBeDeleted.removeAll(removableProjects);
	}

	/**
	 * Looks for a project with given id.
	 * @param id the id of the project to look for
	 * @return the project or <code>null</code>
	 */
	public Project findProjectById(final long id) {
		for (Project project : getProjects()) {
			if (id == project.getId()) {
				return project;
			}
		}

		return null;
	}

	/**
	 * @return the active
	 */
	public synchronized boolean isActive() {
		return active;
	}

	/**
	 * @param startTime the startTime to set
	 */
	public synchronized void start(final DateTime startTime) {
		this.active = true;
		this.startTime = startTime;
		this.dirty = true;
	}
	
	public synchronized void stop() {
        this.active = false;
        this.dirty = true;
    }

	public synchronized DateTime getStart() {
	    return startTime;
	}
	
    public synchronized void setStartTime(DateTime startTime) {
        this.startTime = startTime;
        this.dirty = true;
    }

	/**
	 * @return the activeProject
	 */
	public synchronized Project getActiveProject() {
		return activeProject;
	}

	/**
	 * @param activeProject the activeProject to set
	 */
	public synchronized void setActiveProject(final Project activeProject) {
		this.activeProject = activeProject;
		this.dirty = true;
	}

	/**
	 * @return read-only view of the activities
	 */
	public synchronized List<ProjectActivity> getActivities() {
		return Collections.unmodifiableList(activities);
	}
	
	/**
	 * Adds a new activity.
	 */
	public synchronized void addActivity(final ProjectActivity activity) {
	    this.activities.add(activity);
	    this.dirty = true;
	}
	
	/**
     * Removes an activity.
     */
    public synchronized boolean removeActivity(final ProjectActivity activity) {
        if( this.activities.remove(activity) ) {
            this.dirty = true;
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Removes an activity.
     */
    public synchronized boolean removeActivities(final Collection<ProjectActivity> activities) {
        if( this.activities.removeAll(activities) ) {
            this.dirty = true;
            return true;
        } else {
            return false;
        }
    }

	/**
	 * @return read-only view of the projects
	 */
	public synchronized List<Project> getProjects() {
		return Collections.unmodifiableList(activeProjects);
	}

	/**
	 * Replaces an old activity with a new, updated activity.
	 */
    public synchronized void replaceActivity(ProjectActivity oldActivity,
            ProjectActivity newActivity) {
        removeActivity(oldActivity);
        addActivity(newActivity);
        this.dirty = true;
    }

    /**
     * Replaces an old project with a new, updated project.
     */
    public synchronized void replaceProject(Project oldProject, Project newProject) {
        remove(oldProject);
        add(newProject);
        this.dirty = true;
    }

    /**
     * Returns the last time, when this repository modified.
     */
    public synchronized DateTime getModifiedTimeStamp() {
        return this.modifiedTimeStamp;
    }
    
    public synchronized boolean isDirty() {
        return this.dirty;
    }
    
    /**
     * Sets the dirty flag
     * 
     * Use with care!
     */
    public synchronized void setDirty(boolean b) {
        this.dirty = b;
    }
    
    public ProjectActivity getIntersection(ProjectActivity newActivity, ProjectActivity original) {
    	for(ProjectActivity activity : activities) {
    		if(!activity.equals(original)) {
    			if(activity.hasIntersection(newActivity)) {
    				return activity;
    			}
    		}
    	}
    	return null;
    }
}
