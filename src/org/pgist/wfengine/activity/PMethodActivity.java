package org.pgist.wfengine.activity;

import org.hibernate.Session;
import org.pgist.wfengine.Activity;
import org.pgist.wfengine.Workflow;


/**
 * 
 * @author kenny
 *
 * hibernate.joined-subclass name="PMethodActivity" table="litwf_activity_pmethod"
 * hibernate.joined-subclass-key column="id"
 */
public class PMethodActivity extends GroupActivity {
    
    
    private static final long serialVersionUID = 6584355992932852926L;


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
    
    
}//class PMethodActivity
