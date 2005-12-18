package org.pgist.wfengine;

import java.util.Iterator;

import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;


/**
 * The implementation of WorkflowDAO interface.
 * 
 * @author kenny
 *
 */
public class WorkflowDAOImpl extends HibernateDaoSupport implements WorkflowDAO {
    
    
    private static final String hql_getWorkflow = "from Workflow w where id=:id and finished=:finished and cancelled=:cancelled";
    
    
    public WorkflowDAOImpl() {
    }
    
    
    public Workflow getWorkflow(Long id, boolean finished, boolean cancelled) {
        Workflow workflow = null;
        
        Query query = getSession().createQuery(hql_getWorkflow);
        query.setLong("id", id.longValue());
        query.setBoolean("finished", finished);
        query.setBoolean("cancelled", cancelled);
        
        Iterator iter = query.iterate();
        if (iter.hasNext()) {
            workflow = (Workflow) iter.next();
        }
        
        return workflow;
    }//getWorkflow()
    
    
    public Session getHibernateSession() {
        return getSession();
    }//getSession()


    public void saveProcess(WFProcess process) {
        //check if another process has the same name
        //TODO
        getSession().save(process);
    }//saveProcess()
    
    
}//class WorkflowDAOImpl
