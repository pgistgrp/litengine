package org.pgist.wfengine;

import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.pgist.wfengine.activity.MeetingActivity;
import org.pgist.wfengine.activity.PGameActivity;
import org.pgist.wfengine.activity.PMethodActivity;
import org.pgist.wfengine.activity.SituationActivity;


/**
 * The interface WorkflowEngineDAO.
 * 
 * @author kenny
 *
 */
public interface WorkflowEngineDAO {
    
    
    Map<String, PGameActivity> getTemplatePGames() throws Exception;
    
    Map<String, PMethodActivity> getTemplatePMethods() throws Exception;
    
    Map<String, MeetingActivity> getTemplateMeetings() throws Exception;
    
    List<SituationActivity> getTemplateSituations() throws Exception;
    
    
    List<Workflow> getRunningWorkflows(int status) throws Exception;
    
    Workflow createWorkflow(Long situationId) throws Exception;
    
    Workflow getWorkflowById(Long workflowId) throws Exception;
    
    
    
    //-------------
    
    Workflow getWorkflow(Long id, boolean finished, boolean cancelled) throws Exception;
    
    Session getHibernateSession() throws Exception;
    
    void saveWorkflow(Workflow workflow) throws Exception;
    
    void saveActivity(Activity activity) throws Exception;





    
}//interface WorkflowEngineDAO
