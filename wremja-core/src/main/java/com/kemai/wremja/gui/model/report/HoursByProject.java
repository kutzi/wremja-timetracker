package com.kemai.wremja.gui.model.report;

import org.apache.commons.lang.ObjectUtils;

import com.kemai.wremja.model.Project;

/**
 * Item of the hours by project report.
 * @author remast
 */
public class HoursByProject extends HoursByPeriod implements Comparable<HoursByProject> {
    
    /** The project. */
    private final Project project;
    
    public HoursByProject(final Project project, final double hours) {
        super(hours);
        this.project = project;
    }

    /**
     * @return the project
     */
    public Project getProject() {
        return project;
    }

    @Override
    public boolean equals(final Object that) {
        if (this == that) {
            return true;
        }
        if (!(that instanceof HoursByProject)) {
            return false;
        }

        final HoursByProject accAct = (HoursByProject) that;
        return ObjectUtils.equals(this.getProject(), accAct.getProject());
    }

    @Override
    public int compareTo(HoursByProject hoursByProject) {
        if (hoursByProject == null) {
            return 0;
        }
        
        if (this.equals(hoursByProject)) {
            return 0;
        }

        return this.getProject().compareTo(hoursByProject.getProject());
    }

    @Override
    public int hashCode() {
        return this.getProject().hashCode();
    }
}
