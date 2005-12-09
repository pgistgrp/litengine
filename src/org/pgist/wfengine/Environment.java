package org.pgist.wfengine;


/**
 * 
 * @author kenny
 *
 */
public class Environment {

    
    private Long id;
    

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
    
    
    public Object get(String name) {
        return null;
    }
    
    
    public void put(String name, Object value) {
    }
    
    
}//class Environment
