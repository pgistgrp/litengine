package org.pgist.wfengine;

import java.util.Date;
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
    
    private Map<String, Double> dblValues = new HashMap<String, Double>();
    
    private Map<String, String> strValues = new HashMap<String, String>();
    
    private Map<String, Date> dateValues = new HashMap<String, Date>();
    
    
    public Environment() {
    }
    
    
    public Environment(RunningContext context) {
        this.context = context;
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
     * 
     * @hibernate.many-to-one column="context_id" cascade="none"
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
     * @hibernate.map table="litwf_env_values_int_map"
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
     * @hibernate.map table="litwf_env_values_double_map"
     * @hibernate.collection-key column="env_id"
     * @hibernate.collection-index column="name" type="string" length="255"
     * @hibernate.collection-element column="double_value" type="double"
     */
    public Map<String, Double> getDblValues() {
        return dblValues;
    }


    public void setDblValues(Map<String, Double> dblValues) {
        this.dblValues = dblValues;
    }


    /**
     * @return
     * 
     * @hibernate.map table="litwf_env_values_string_map"
     * @hibernate.collection-key column="env_id"
     * @hibernate.collection-index column="name" type="string" length="255"
     * @hibernate.collection-element column="str_value" type="text"
     */
    public Map<String, String> getStrValues() {
        return strValues;
    }


    public void setStrValues(Map<String, String> strValues) {
        this.strValues = strValues;
    }


    /**
     * @return
     * 
     * @hibernate.map table="litwf_env_values_date_map"
     * @hibernate.collection-key column="env_id"
     * @hibernate.collection-index column="name" type="string" length="255"
     * @hibernate.collection-element column="date_value" type="timestamp"
     */
    public Map<String, Date> getDateValues() {
        return dateValues;
    }


    public void setDateValues(Map<String, Date> dateValues) {
        this.dateValues = dateValues;
    }


    public void clear() {
        getIntValues().clear();
        getDblValues().clear();
        getStrValues().clear();
        getDateValues().clear();
    }//clear()


    public void merge(Environment downEnv, Declaration downDecl) {
        Map<String, String> outs = downDecl.getOuts();
        
        for (Map.Entry<String, Integer> entry : downEnv.getIntValues().entrySet()) {
            String name = entry.getKey();
            Integer value = entry.getValue();
            String realName = outs.get(name);
            System.out.println(outs);
            System.out.printf("    (name: %s, realName: %s, value: %d\n", name, realName, value);
            if (realName==null) realName = name;
            getIntValues().put(realName, value);
        }//for
        
        for (Map.Entry<String, Double> entry : downEnv.getDblValues().entrySet()) {
            String name = entry.getKey();
            Double value = entry.getValue();
            String realName = outs.get(name);
            if (realName==null) realName = name;
            getDblValues().put(realName, value);
        }//for
        
        for (Map.Entry<String, String> entry : downEnv.getStrValues().entrySet()) {
            String name = entry.getKey();
            String value = entry.getValue();
            String realName = outs.get(name);
            if (realName==null) realName = name;
            getStrValues().put(realName, value);
        }//for
        
        for (Map.Entry<String, Date> entry : downEnv.getDateValues().entrySet()) {
            String name = entry.getKey();
            Date value = entry.getValue();
            String realName = outs.get(name);
            if (realName==null) realName = name;
            getDateValues().put(realName, value);
        }//for
    }//merge()


    public void duplicate(Environment model) {
        getIntValues().putAll(model.getIntValues());
        getDblValues().putAll(model.getDblValues());
        getStrValues().putAll(model.getStrValues());
        getDateValues().putAll(model.getDateValues());
    }//duplicate()
    
    
    public Integer findIntValue(String name) {
        String realName = getContext().getDeclaration().getIns().get(name);
        if (realName==null || realName.length()==0) realName = name;
        Integer value = getContext().getEnvironment().getIntValues().get(realName);
        if (value==null) {
            value = getContext().getParent().getEnvironment().findIntValue(realName);
        }
        
        return value;
    }//findIntValue()


}//class Environment
