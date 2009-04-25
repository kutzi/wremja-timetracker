package com.kemai.wremja.model;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("project") //$NON-NLS-1$
public class Project implements Serializable, Comparable<Project>{
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /** The unique identifier of the project. */
    private final long id;
    
    /** The title of the project. */
    private String title;
    
    /** A description of the project. */
    private String description;
    
    public static final String PROPERTY_TITLE = "com.kemai.wremja.model.title";

    /**
     * Creates a new project.
     * @param id the unique id
     * @param title the project title
     * @param description the project description
     */
    public Project(final long id, final String title, final String description) {
        this.id = id;
        this.title = title;
        this.description = description;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * Getter for the title.
     * @return the title
     */
    public String getTitle() {
        return title;
    }
    
    /**
     * Setter for the title.
     * @param title the title to set
     */
    public void setTitle(final String title) {
        this.title = title;
        
        // Use title also as description for the moment.
        this.description = title;
    }

    @Override
    public String toString() {
        return getTitle();
    }
    
    @Override
    public boolean equals(final Object that) {
        if (this == that) {
            return true;
        }
        
        if (that == null || !(that instanceof Project)) {
            return false;
        }
        
        final Project project = (Project) that;
        
        final EqualsBuilder eqBuilder = new EqualsBuilder();
        eqBuilder.append(this.getId(), project.getId());
        
        return eqBuilder.isEquals();
    }

    @Override
    public int compareTo(final Project project) {
        if (project == null || this.getTitle() == null) {
            return 0;
        }
        
        // Compare the title only.
        return this.getTitle().compareTo(project.getTitle());
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
        hashCodeBuilder.append(this.getId());
        return hashCodeBuilder.toHashCode();
    }
}
