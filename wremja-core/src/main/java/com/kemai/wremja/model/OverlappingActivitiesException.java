package com.kemai.wremja.model;

/**
 * Indicates that an attempt was made to add an activity (or edit an existing one), so that it overlaps with another
 * activity.
 * 
 * @author kutzi
 */
public class OverlappingActivitiesException extends Exception {

	private static final long serialVersionUID = 1L;
	
	private final ProjectActivity newActivity;
	private final ProjectActivity existingActivity;
	
	public OverlappingActivitiesException(ProjectActivity newActivity, ProjectActivity existingActivity) {
		this.newActivity = newActivity;
		this.existingActivity = existingActivity;
	}

	public ProjectActivity getNewActivity() {
		return newActivity;
	}

	public ProjectActivity getExistingActivity() {
		return existingActivity;
	}
}
