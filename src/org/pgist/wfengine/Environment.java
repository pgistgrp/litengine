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
    
    
    private Long id;
    
    private RunningContext context;
    
    private Map<String, Integer> intValues = new HashMap<String, Integer>();
    
    private Map<String, String> strValues = new HashMap<String, String>();
    
    
    public Environment() {
    }
    
    
    /**
     * @return
     * @hibernate.id generator-class="foreign"
     * @hibernate.generator-param name="property" value="context"
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
     * @hibernate.one-to-one constrained="true" cascade="none"
     */
    public RunningContext getContext() {
        return context;
    }


    public void setContext(RunningContext context) {
        this.context = context;
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
