package org.pgist.wfengine.activity;

import org.hibernate.Session;
import org.pgist.wfengine.Activity;
import org.pgist.wfengine.Workflow;


/**
 * 
 * @author kenny
 *
 * @hibernate.joined-subclass name="MethodActivity" table="litwf_activity_pmethod"
 * @hibernate.joined-subclass-key column="id"
 */
public class MethodActivity extends GroupActivity {
    
    
    protected Activity[] doExecute(Workflow workflow) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }
    
    
    protected void proceed() throws Exception {
        // TODO Auto-generated method stub

    }
    
    
    protected void proceed(int decision) throws Exception {
        // TODO Auto-generated method stub

    }
    
    
    public void saveState(Session session) {
        // TODO Auto-generated method stub

    }
    
    
    public Activity clone(Activity prev) {
        // TODO Auto-generated method stub
        return null;
    }
    
    
    public Activity probe() {
        // TODO Auto-generated method stub
        return null;
    }
    
    
}//class MethodActivity
