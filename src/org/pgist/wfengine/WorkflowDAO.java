package org.pgist.wfengine;

import org.hibernate.Session;



/**
 * The interface WorkflowDAO.
 * 
 * @author kenny
 *
 */
public interface WorkflowDAO {
    
    
    Workflow getWorkflow(Long id, boolean finished, boolean cancelled);
    
    Session getSession();
    
    
}//interface WorkflowDAO
