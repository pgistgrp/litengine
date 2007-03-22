package org.pgist.wfengine;

import java.util.HashMap;
import java.util.Map;


/**
 * Encapsulate the workflow internal environment.<br>
 * 
 * An environment consists of sets of environment values.
 * 
 * @author kenny
 *
 * @hibernate.class table="litwf_environment"
 */
public class Environment {
    
    
    protected Long id = null;
    
    private Map<String, Integer> intValues = new HashMap<String, Integer>();
    
    private Map<String, String> strValues = new HashMap<String, String>();
    
    
    public Environment() {
    }
    
    
    /**
     * @return
     * @hibernate.id generator-class="foreign"
     * @hibernate.generator-param name="property" value="workflow"
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
     * @hibernate.map table="litwf_env_values_map"
     * @hibernate.collection-key column="env_id"
     * @hibernate.collection-index column="name" type="string" length="255"
     * @hibernate.collection-element column="int_value" type="integer"
     */
    public Map<String, Integer> getIntValues() {
        return intValues;
    }


    public void setIntValues(Map<String, Integer> intValues) {
        this.intValues = intValues;
    }


    /**
     * @return
     * 
     * @hibernate.map table="litwf_env_values_map"
     * @hibernate.collection-key column="env_id"
     * @hibernate.collection-index column="name" type="string" length="255"
     * @hibernate.collection-element column="str_value" type="string"
     */
    public Map<String, String> getStrValues() {
        return strValues;
    }


    public void setStrValues(Map<String, String> strValues) {
        this.strValues = strValues;
    }


}//class Environment
