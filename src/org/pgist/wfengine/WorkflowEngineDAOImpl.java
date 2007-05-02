package org.pgist.wfengine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.pgist.wfengine.activity.MeetingActivity;
import org.pgist.wfengine.activity.PGameActivity;
import org.pgist.wfengine.activity.PMethodActivity;
import org.pgist.wfengine.activity.SituationActivity;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;


/**
 * The implementation of WorkflowEngineDAO interface.
 * 
 * @author kenny
 *
 */
public class WorkflowEngineDAOImpl extends HibernateDaoSupport implements WorkflowEngineDAO {
    
    
    public List<Workflow> getRunningWorkflows(int status) throws Exception {
        return getHibernateTemplate().find("from Workflow wf where wf.status="+status);
    }//getRunningWorkflows()
    
    
    public List<SituationActivity> getTemplateSituations() throws Exception {
        return getHibernateTemplate().find("from SituationActivity s where s.workflow is null");
    }//getTemplateSituations()
    
    
    public Map<String, PGameActivity> getTemplatePGames() throws Exception {
        List<PGameActivity> list = getHibernateTemplate().find("from PGameActivity p where p.prev is null and p.next is null");
        
        Map<String, PGameActivity> pgames = new HashMap<String, PGameActivity>();
        
        for (PGameActivity pgame : list) {
            pgames.put(pgame.getName(), pgame);
        }//for
        
        return pgames;
    }//getTemplatePGames()


    public Map<String, PMethodActivity> getTemplatePMethods() throws Exception {
        List<PMethodActivity> list = getHibernateTemplate().find("from PMethodActivity p where p.prev is null and p.next is null");
        
        Map<String, PMethodActivity> pmethods = new HashMap<String, PMethodActivity>();
        
        for (PMethodActivity pmethod : list) {
            pmethods.put(pmethod.getName(), pmethod);
        }//for
        
        return pmethods;
    }//getTemplatePMethods()


    public Map<String, MeetingActivity> getTemplateMeetings() throws Exception {
        List<MeetingActivity> list = getHibernateTemplate().find("from MeetingActivity p where p.prev is null and p.next is null");
        
        Map<String, MeetingActivity> meetings = new HashMap<String, MeetingActivity>();
        
        for (MeetingActivity meeting : list) {
            meetings.put(meeting.getName(), meeting);
        }//for
        
        return meetings;
    }//getTemplateMeetings()


    public Workflow createWorkflow(Long situationId) throws Exception {
        SituationActivity template = (SituationActivity) getHibernateTemplate().load(SituationActivity.class, situationId);
        
        SituationActivity situation = template.clone(null, null, null);
        
        Workflow workflow = new Workflow();
        workflow.setSituation(situation);
        situation.setWorkflow(workflow);
        
        getHibernateTemplate().save(workflow);
        
        return workflow;
    }//createWorkflow()


    public Workflow getWorkflowById(Long workflowId) throws Exception {
        return (Workflow) getHibernateTemplate().load(Workflow.class, workflowId);
    }//getWorkflowById()


    public RunningContext getContextById(Long contextId) throws Exception {
        return (RunningContext) getHibernateTemplate().load(RunningContext.class, contextId);
    }//getContextById()
    
    
    public Activity getActivityById(Long activityId) throws Exception {
        return (Activity) getHibernateTemplate().load(Activity.class, activityId);
    }//getActivityById()
    
    
    
    
    
    
    //--------------------------------
    
    
    private static final String hql_getWorkflow = "from Workflow w where id=:id and finished=:finished and cancelled=:cancelled";
    
    
    public WorkflowEngineDAOImpl() {
    }


    public Session getHibernateSession() throws Exception {
        // TODO Auto-generated method stub
        return null;
    }


    public Workflow getWorkflow(Long id, boolean finished, boolean cancelled) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }


    public void saveActivity(Activity activity) throws Exception {
        getHibernateTemplate().saveOrUpdate(activity);
    }


    public void saveWorkflow(Workflow workflow) throws Exception {
        // TODO Auto-generated method stub
        
    }


}//class WorkflowEngineDAOImpl
