package org.pgist.wfengine;

import java.util.HashMap;
import java.util.Map;


/**
 * 
 * @author kenny
 *
 * @hibernate.class table="litwf_env"
 */
public class Environment {
    
    
    private Long id;
    
    private Map map = new HashMap();
    

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
     * 
     * @hibernate.map table="litwf_env_var_link"
     * @hibernate.collection-key column="env_id"
     * @hibernate.collection-index column="env_var_name" type="string"
     * @hibernate.collection-many-to-many class="org.pgist.wfengine.EnvironmentVariable" column="env_var_id"
     */
    public Map getMap() {
        return map;
    }


    public void setMap(Map map) {
        this.map = map;
    }
    
    
    /*
     * ------------------------------------------------------------------------
     */
    
    
    public Object get(String name) {
        return map.get(name);
    }
    
    
    public void put(String name, Object value) {
        EnvironmentVariable var = new EnvironmentVariable();
        var.setObject(value);
        map.put(name, var);
    }


}//class Environment
