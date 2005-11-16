package org.pgist.wfengine;

import org.hibernate.Session;


/**
 * The interface WorkflowDAO.
 * 
 * @author kenny
 *
 */
public interface WorkflowDAO {
    
    
    Session getSession();
    
    void setSession(Session session);
    
    
}//interface WorkflowDAO
