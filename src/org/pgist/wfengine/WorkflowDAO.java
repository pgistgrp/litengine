package org.pgist.wfengine;

import org.hibernate.Session;
import org.pgist.wfengine.activity.GroupActivity;
import org.pgist.wfengine.activity.PActActivity;



/**
 * The interface WorkflowDAO.
 * 
 * @author kenny
 *
 */
public interface WorkflowDAO {
    
    
    Workflow getWorkflow(Long id, boolean finished, boolean cancelled);
    
    Session getHibernateSession();
    
    void saveTemplate(Template template);
    
    void saveWorkflow(Workflow workflow);
    
    Template getTemplate(Long id);
    
    void saveActivity(Activity activity);
    
    PActActivity getPActActivityByRefId(Long refId);
    
    GroupActivity getGroupActivityByRefId(Long level, Long refId);
    
    
}//interface WorkflowDAO
