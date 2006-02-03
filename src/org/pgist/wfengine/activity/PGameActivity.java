package org.pgist.wfengine.activity;

import org.hibernate.Session;
import org.pgist.wfengine.Activity;
import org.pgist.wfengine.Workflow;


/**
 * The SequenceActivity implement the sequence flow activity.
 * 
 * @author kenny
 *
 * @hibernate.joined-subclass name="PGameActivity" table="litwf_activity_pgame"
 * @hibernate.joined-subclass-key column="id"
 */
public class PGameActivity extends GroupActivity {
    
    
    private static final long serialVersionUID = 1783552526486991064L;
    
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
    
    
}//class PGameActivity
