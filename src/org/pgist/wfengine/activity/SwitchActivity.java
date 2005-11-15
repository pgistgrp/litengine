package org.pgist.wfengine.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.pgist.wfengine.Activity;
import org.pgist.wfengine.FlowEnvironment;


public class SwitchActivity extends Activity {
    
    
    private int expression = -1;
    
    private List branches = new ArrayList();
    
    private Activity others = null;
    
    
    public SwitchActivity(boolean automatic) {
        this.automatic = automatic;
    }
    
    
    public int getExpression() {
        return expression;
    }
    
    
    public void setExpression(int expression) {
        this.expression = expression;
    }
    
    
    public List getBranches() {
        return branches;
    }
    
    
    public void setBranches(List branches) {
        this.branches = branches;
    }
    
    
    public void setOthers(Activity others) {
        this.others = others;
    }
    
    
    public boolean activate(FlowEnvironment env) {
        Stack stack = (Stack) env.getExecuteStack();
        
        if (action!=null) {
            action.execute(env);
            String s = (String) env.getValues().get(action);
            try {
                expression = Integer.parseInt(s);
            } catch(Exception e) {
                expression = -1;
            }
        }
        
        if (expression<0 || expression>branches.size()) {
            if (others!=null) stack.push(others);
        } else {
            stack.push(branches.get(expression));
        }
        
        return true;
    }//activate()
    
    
    public void proceed() {
        
    }//proceed()
    
    
}//class SwitchActivity
