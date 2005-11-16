package org.pgist.wfengine.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.hibernate.Session;
import org.pgist.wfengine.Activity;
import org.pgist.wfengine.WorkflowEnvironment;
import org.pgist.wfengine.IPerformer;


/**
 * 
 * @author kenny
 *
 * @hibernate.joined-subclass name="SwitchActivity" table="litwf_activity_switch"
 * @hibernate.joined-subclass-key column="id"
 */
public class SwitchActivity extends Activity {
    
    
    protected int expression = -1;
    
    protected List branches = new ArrayList();
    
    protected Activity others = null;
    
    
    public SwitchActivity() {
    }
    
    
    /**
     * @return
     * @hibernate.property unique="false" not-null="true"
     */
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
    
    
    /**
     * @return
     * @hibernate.many-to-one column="other_id" class="org.pgist.wfengine.Activity" casecad="all"
     */
    public Activity getOthers() {
        return others;
    }
    
    
    public void setOthers(Activity others) {
        this.others = others;
    }
    
    
    public boolean activate(WorkflowEnvironment env) {
        Stack stack = (Stack) env.getExecuteStack();
        
        expression = -1;
        
        try {
            if (performerClass!=null) {
                IPerformer performer = (IPerformer) Class.forName(performerClass).newInstance();
                expression = performer.perform(this, env);
            }
            
            if (expression<0 || expression>branches.size()) {
                if (others!=null) stack.push(others);
            } else {
                stack.push(branches.get(expression));
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        
        return true;
    }//activate()
    
    
    public void saveState(Session session) {
        session.saveOrUpdate(this);
        for (int i=0, n=branches.size(); i<n; i++) {
            Activity one = (Activity) branches.get(i);
            one.saveState(session);
        }//for i
        if (others!=null) others.saveState(session);
    }//saveState()
    
    
}//class SwitchActivity
