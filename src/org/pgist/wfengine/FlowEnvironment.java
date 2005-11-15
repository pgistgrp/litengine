package org.pgist.wfengine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;


/**
 * Encapsulate the workflow internal environment
 * 
 * @author kenny
 *
 */
public class FlowEnvironment {
    
    
    private Stack executeStack = new Stack();
    
    private List waitingList = new ArrayList(16);
    
    private Map values = new HashMap();
    
    
    public Stack getExecuteStack() {
        return executeStack;
    }
    
    
    public List getWaitingList() {
        return waitingList;
    }
    
    
    public Map getValues() {
        return values;
    }
    
    
    public void setValues(Map values) {
        this.values = values;
    }
    
    
}//class FlowEnvironment
