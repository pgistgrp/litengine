package org.pgist.wfengine;


/**
 * 
 * @author kenny
 *
 * @hibernate.class table="litwf_env_vars"
 */
public class EnvironmentVariable {
    
    
    private Long id;
    
    private Object object;
    
    
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
     * @return object
     * 
     * @hibernate.any id-type="long" cascade="all" meta-type="string"
     * @hibernate.any-column name="class_name"
     * @hibernate.any-column name="class_id"
     */
    public Object getObject() {
        return object;
    }
    
    
    public void setObject(Object object) {
        this.object = object;
    }
    
    
}//class EnvironmentVariable
