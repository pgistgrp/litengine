package org.pgist.wfengine;

import org.hibernate.Session;


/**
 * The implementation of WorkflowDAO interface.
 * 
 * @author kenny
 *
 */
public class WorkflowDAOImpl implements WorkflowDAO {
    
    
    private ThreadLocal session;
    
    
    public Session getSession() {
        return (Session) session.get();
    }
    
    
    public void setSession(Session session) {
        this.session.set(session);
    }
    
    
}//class WorkflowDAOImpl
