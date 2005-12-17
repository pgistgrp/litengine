package org.pgist.wfengine;

import java.util.Iterator;

import org.hibernate.Query;
import org.hibernate.Session;


/**
 * The implementation of WorkflowDAO interface.
 * 
 * @author kenny
 *
 */
public class WorkflowDAOImpl implements WorkflowDAO {
    
    
    private Session session;
    
    
    private static final String hql_getWorkflow = "from Workflow w where id=:id and finished=:finished and cancelled=:cancelled";
    
    
    public void setSession(Session session) {
        this.session = session;
    }
    
    
    public Workflow getWorkflow(Long id, boolean finished, boolean cancelled) {
        Workflow workflow = null;
        
        Query query = session.createQuery(hql_getWorkflow);
        query.setLong("id", id.longValue());
        query.setBoolean("finished", finished);
        query.setBoolean("cancelled", cancelled);
        
        Iterator iter = query.iterate();
        if (iter.hasNext()) {
            workflow = (Workflow) iter.next();
        }
        
        return workflow;
    }//getWorkflow()
    
    
    public Session getSession() {
        return null;
    }//getSession()
    
    
}//class WorkflowDAOImpl
