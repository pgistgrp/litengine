package org.pgist.wfengine;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * A wrapper class for Environment variables, provides a read/write interface to the environment.
 * 
 * @author kenny
 */
public class EnvironmentInOuts {
    
    
    RunningContext context;
    
    Declaration declaration;
    
    private Map<String, Integer> intValues = new HashMap<String, Integer>();
    
    private Map<String, Double> dblValues = new HashMap<String, Double>();
    
    private Map<String, String> strValues = new HashMap<String, String>();
    
    private Map<String, Date> dateValues = new HashMap<String, Date>();
    

    public EnvironmentInOuts(RunningContext context, Declaration declaration) {
        this.context = context;
        this.declaration = declaration;
    }
    
    
    /*
     * ------------------------------------------------------------------------
     */
    
    
    public Integer getIntValue(String name) {
        String realName = declaration.getIns().get(name);
        if (realName==null || realName.length()==0) realName = name;
        return context.getEnvironment().findIntValue(realName);
    }//getIntValue()
    
    
    public Double getDblValue(String name) {
        return context.getEnvironment().getDblValues().get(declaration.getIns().get(name));
    }
    
    
    public String getStrValue(String name) {
        return context.getEnvironment().getStrValues().get(declaration.getIns().get(name));
    }
    
    
    public Date getDateValue(String name) {
        return context.getEnvironment().getDateValues().get(declaration.getIns().get(name));
    }
    
    
    public void setIntValue(String name, Integer value) {
        intValues.put(name, value);
    }
    
    
    public void setDblValue(String name, Double value) {
        dblValues.put(name, value);
    }
    
    
    public void setStrValue(String name, String value) {
        strValues.put(name, value);
    }
    
    
    public void setDateValue(String name, Date date) {
        dateValues.put(name, date);
    }
    
    
    protected void merge() {
        for (Map.Entry<String, Integer> entry : intValues.entrySet()) {
            String name = entry.getKey();
            Integer value = entry.getValue();
            String realName = declaration.getOuts().get(name);
            if (realName==null || realName.length()==0) realName = name;
            context.getEnvironment().getIntValues().put(realName, value);
        }//for
        
        for (Map.Entry<String, Double> entry : dblValues.entrySet()) {
            String name = entry.getKey();
            Double value = entry.getValue();
            String realName = declaration.getOuts().get(name);
            if (realName==null || realName.length()==0) realName = name;
            context.getEnvironment().getDblValues().put(realName, value);
        }//for
        
        for (Map.Entry<String, String> entry : strValues.entrySet()) {
            String name = entry.getKey();
            String value = entry.getValue();
            String realName = declaration.getOuts().get(name);
            if (realName==null || realName.length()==0) realName = name;
            context.getEnvironment().getStrValues().put(realName, value);
        }//for
        
        for (Map.Entry<String, Date> entry : dateValues.entrySet()) {
            String name = entry.getKey();
            Date value = entry.getValue();
            String realName = declaration.getOuts().get(name);
            if (realName==null || realName.length()==0) realName = name;
            context.getEnvironment().getDateValues().put(realName, value);
        }//for
    }//merge()
    
    
}//class EnvironmentInOuts