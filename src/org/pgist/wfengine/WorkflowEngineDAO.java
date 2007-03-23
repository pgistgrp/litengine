package org.pgist.wfengine;

import org.hibernate.Session;


/**
 * The interface WorkflowEngineDAO.
 * 
 * @author kenny
 *
 */
public interface WorkflowEngineDAO {
    
    
    Workflow getWorkflow(Long id, boolean finished, boolean cancelled) throws Exception;
    
    Session getHibernateSession() throws Exception;
    
    void saveWorkflow(Workflow workflow) throws Exception;
    
    void saveActivity(Activity activity) throws Exception;
    
    
}//interface WorkflowEngineDAO
