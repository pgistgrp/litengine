package org.pgist.wfengine;

import java.util.Iterator;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.pgist.wfengine.activity.GroupActivity;
import org.pgist.wfengine.activity.PActActivity;
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


    public void saveTemplate(Template template) {
        //check if another template has the same refid
        //TODO
        getSession().save(template);
    }//saveProcess()


    public void saveWorkflow(Workflow workflow) {
        getSession().saveOrUpdate(workflow);
    }//saveWorkflow()


    private static final String hql_getTemplate = "from Template t where id=:id and deleted=:deleted";
    
    
    public Template getTemplate(Long id) {
        Template template = null;
        
        Query query = getSession().createQuery(hql_getTemplate);
        query.setLong("id", id.longValue());
        query.setBoolean("deleted", false);
        
        Iterator iter = query.iterate();
        if (iter.hasNext()) {
            template = (Template) iter.next();
        }
        
        return template;
    }//getTemplate()


    public void saveActivity(Activity activity) {
        //check if another activity has the same refid
        //TODO
        getSession().save(activity);
    }//saveActivity()


    private static final String hql_getPActActivityByRefId = "from PActActivity p where refid=?";
    
    
    public PActActivity getPActActivityByRefId(Long refId) {
        List list = getHibernateTemplate().find(hql_getPActActivityByRefId, refId);
        return (list.size()==0) ? null : (PActActivity) list.get(0);
    }//getPActActivity()


    private static final String hql_getGroupActivityByRefId = "from GroupActivity g where level=? and refid=?";
    
    
    public GroupActivity getGroupActivityByRefId(Long level, Long refId) {
        List list = getHibernateTemplate().find(hql_getGroupActivityByRefId, new Object[] {level, refId});
        return (list.size()==0) ? null : (GroupActivity) list.get(0);
    }//getGroupActivityByRefId()
    
    
}//class WorkflowDAOImpl
