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
    
    
    /**
     * Find int value with the given name in environment.
     * 
     * Proxy mechanism is used.
     * 
     * @param name
     * @return
     */
    public Integer findIntValue(String name) {
        Integer value = null;
        
        /*
         * search the name in current env.
         */
        value = getIntValues().get(name);
        
        if (value==null) {
            /*
             * Translate original name to declared name.
             */
            String realName = getContext().getDeclaration().getIns().get(name);
            if (realName==null || realName.length()==0) realName = name;
            
            RunningContext pctx = getContext().getParent();
            
            if (pctx!=null) {
                /*
                 * Proxy. Let the parent environment to find the value.
                 */
                
                value = pctx.getEnvironment().findIntValue(realName);
            }
        }
        
        return value;
    }//findIntValue()


    public Double findDblValue(String name) {
        String realName = getContext().getDeclaration().getIns().get(name);
        if (realName==null || realName.length()==0) realName = name;
        Double value = getDblValues().get(realName);
        if (value==null) {
            value = getContext().getParent().getEnvironment().findDblValue(realName);
        }
        
        return value;
    }//findDblValue()


    public String findStrValue(String name) {
        String realName = getContext().getDeclaration().getIns().get(name);
        if (realName==null || realName.length()==0) realName = name;
        String value = getStrValues().get(realName);
        if (value==null) {
            value = getContext().getParent().getEnvironment().findStrValue(realName);
        }
        
        return value;
    }//findStrValue()


    public Date findDateValue(String name) {
        String realName = getContext().getDeclaration().getIns().get(name);
        if (realName==null || realName.length()==0) realName = name;
        Date value = getDateValues().get(realName);
        if (value==null) {
            value = getContext().getParent().getEnvironment().findDateValue(realName);
        }
        
        return value;
    }//findDateValue()


    public Object findValue(String name) {
        String realName = getContext().getDeclaration().getIns().get(name);
        if (realName==null || realName.length()==0) realName = name;
        
        Object value = null;
        
        value = getIntValues().get(realName);
        if (value==null) value = getDblValues().get(realName);
        if (value==null) value = getStrValues().get(realName);
        if (value==null) value = getDateValues().get(realName);
        
        if (value==null && getContext().getParent()!=null) {
            value = getContext().getParent().getEnvironment().findValue(realName);
        }
        
        return value;
    }//findValue()


}//class Environment
