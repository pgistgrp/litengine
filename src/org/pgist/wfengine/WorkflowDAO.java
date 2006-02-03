package org.pgist.wfengine;

import org.hibernate.Session;
import org.pgist.wfengine.activity.MeetingActivity;
import org.pgist.wfengine.activity.PActActivity;
import org.pgist.wfengine.activity.PGameActivity;
import org.pgist.wfengine.activity.PMethodActivity;



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
    
    PGameActivity getPGameActivityByRefId(Long refId);

    PMethodActivity getPMethodActivityByRefId(Long refId);

    MeetingActivity getMeetingActivityByRefId(Long refId);
    
    
}//interface WorkflowDAO
