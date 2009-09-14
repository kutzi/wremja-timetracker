package com.kemai.wremja.model;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("project") 
public class Project implements Serializable, Comparable<Project>{
    
    public static final String PROPERTY_TITLE = "com.kemai.wremja.model.title";
    public static final String PROPERTY_BILLABLE = "com.kemai.wremja.model.billable";
    public static final String PROPERTY_VISIBLE = "com.kemai.wremja.model.visible";
    
    private static final long serialVersionUID = 1L;

    /** The unique identifier of the project. */
    private long id;
    
    /** The title of the project. */
    private String title;
    
    /** A description of the project. */
    private String description;
    
    @Getter @Setter private Boolean billable;
    @Getter @Setter private Boolean enabled;
    
    /**
     * Creates a new project.
     * @param id the unique id
     * @param title the project title
     * @param description the project description
     */
    public Project(final long id, final String title, final String description) {
    	if( StringUtils.isEmpty(title) ) {
    		throw new IllegalArgumentException("project title must not be empty!");
    	}
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
     * @deprecated only to be used in XStream deserialization
     */
    public void setId(long id) {
        this.id = id;
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
     * @param title the new title
     */
    public void setTitle(final String title) {
        this.title = title;
    }
    
    public boolean isVisible() {
        return Boolean.TRUE.equals(getEnabled());
    }
    
    public boolean isBillable() {
        return Boolean.TRUE.equals(getBillable());
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
    public int compareTo(final Project that) {
        if (that == null || this.getTitle() == null) {
            return 0;
        }
        
        
        int result = getTitle().compareTo(that.getTitle());
        
        if(result != 0) {
            return result;
        }

        if(this.getId() < that.getId()) {
            return -1;
        } else if (this.getId() > that.getId()){
            return 1;
        }
        
        return 0; 
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
        hashCodeBuilder.append(this.getId());
        return hashCodeBuilder.toHashCode();
    }
    
    public static boolean validateProjectName(String name) {
    	return StringUtils.isNotBlank(name);
    }
    
    private Object readResolve() {
        if (this.enabled == null) {
            this.enabled = Boolean.TRUE;
        }
        if (this.billable == null) {
            this.billable = Boolean.TRUE;
        }
        return this;
    }
}
