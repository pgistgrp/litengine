package org.pgist.wfengine;

import java.util.List;

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
    
    
    Workflow getWorkflow(Long id, boolean finished, boolean cancelled) throws Exception;
    
    Session getHibernateSession() throws Exception;
    
    void saveTemplate(Template template) throws Exception;
    
    void saveWorkflow(Workflow workflow) throws Exception;
    
    Template getTemplate(Long id) throws Exception;
    
    void saveActivity(Activity activity) throws Exception;
    
    PActActivity getPActActivityByRefId(Long refId) throws Exception;
    
    GroupActivity getGroupActivityByRefId(Long level, Long refId) throws Exception;

    List getTemplates(int type) throws Exception;
    
    
}//interface WorkflowDAO
