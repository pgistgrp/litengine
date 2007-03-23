package org.pgist.wfengine;

import java.util.Iterator;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.pgist.wfengine.activity.GroupActivity;
import org.pgist.wfengine.activity.PGameActivity;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;


/**
 * The implementation of WorkflowEngineDAO interface.
 * 
 * @author kenny
 *
 */
public class WorkflowEngineDAOImpl extends HibernateDaoSupport implements WorkflowEngineDAO {
    
    
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
