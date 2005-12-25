package org.pgist.wfengine;


/**
 * Task is a reusable step in workflow definition.
 * 
 * @author kenny
 *
 * @hibernate.class table="litwf_task"
 */
public abstract class Task {
    
    
    private Long id;
    
    private String name;
    
    private String description;
    
    private String className;
    
    
    public Task() {
    }
    
    
    /**
     * @return
     * @hibernate.id generator-class="native"
     */
    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    /**
     * @return
     * @hibernate.property not-null="true"
     */
    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    /**
     * @return
     * @hibernate.property not-null="true"
     */
    public String getDescription() {
        return description;
    }


    public void setDescription(String description) {
        this.description = description;
    }


    /**
     * @return
     * @hibernate.property not-null="true"
     */
    public String getClassName() {
        return className;
    }


    public void setClassName(String className) {
        this.className = className;
    }


    abstract public Object clone();
    
    
}//class Task
