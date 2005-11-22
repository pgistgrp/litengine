package org.pgist.wfengine;

import org.hibernate.Session;



/**
 * The interface WorkflowDAO.
 * 
 * @author kenny
 *
 */
public interface WorkflowDAO {
    
    
    Workflow getWorkflow(Long id);
    
    Session getSession();
    
    
}//interface WorkflowDAO
