package org.pgist.wfengine;

import java.util.HashMap;
import java.util.Map;


/**
 * Declaration for PGame/PAutoGame/PGroup.
 * 
 * @author kenny
 * 
 * @hibernate.class table="litwf_declaration"
 */
public class Declaration {
    
    
    private Long id;
    
    /**
     * IN parameters map
     */
    private Map<String, String> ins = new HashMap<String, String>();
    
    /**
     * OUT parameters map
     */
    private Map<String, String> outs = new HashMap<String, String>();
    
    /**
     * properties map
     */
    private Map<String, String> properties = new HashMap<String, String>();
    
    
    /**
     * @return
     * 
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
     * @hibernate.map table="litwf_declare_in_map"
     * @hibernate.collection-key column="declaration_id"
     * @hibernate.collection-index column="name" type="string" length="255"
     * @hibernate.collection-element column="value" type="string"
     */
    public Map<String, String> getIns() {
        return ins;
    }


    public void setIns(Map<String, String> ins) {
        this.ins = ins;
    }


    /**
     * @return
     * 
     * @hibernate.map table="litwf_declare_out_map"
     * @hibernate.collection-key column="declaration_id"
     * @hibernate.collection-index column="name" type="string" length="255"
     * @hibernate.collection-element column="value" type="string"
     */
    public Map<String, String> getOuts() {
        return outs;
    }


    public void setOuts(Map<String, String> outs) {
        this.outs = outs;
    }


    /**
     * @return
     * 
     * @hibernate.map table="litwf_declare_prop_map"
     * @hibernate.collection-key column="declaration_id"
     * @hibernate.collection-index column="name" type="string" length="255"
     * @hibernate.collection-element column="property" type="text"
     */
    public Map<String, String> getProperties() {
        return properties;
    }


    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }
    
    
    /*
     * ------------------------------------------------------------------------
     */
    
    
    public void duplicate(Declaration model) {
        getIns().putAll(model.getIns());
        getOuts().putAll(model.getOuts());
        getProperties().putAll(model.getProperties());
    }//duplicate()
    
    
}//class Declaration
