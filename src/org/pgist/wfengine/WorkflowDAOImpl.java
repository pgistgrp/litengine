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
    
    
    public Workflow getWorkflow(Long id, boolean finished, boolean cancelled) throws Exception {
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
    
    
    public Session getHibernateSession() throws Exception {
        return getSession();
    }//getHibernateSession()


    public void saveTemplate(Template template) throws Exception {
        //check if another template has the same refid
        //TODO
        getSession().saveOrUpdate(template);
    }//saveTemplate()


    public void saveWorkflow(Workflow workflow) throws Exception {
        getSession().saveOrUpdate(workflow);
    }//saveWorkflow()


    private static final String hql_getTemplates = "from Template t where type=? and deleted=?";
    
    
    public List getTemplates(int type) throws Exception {
        return getHibernateTemplate().find(hql_getTemplates, new Object[] {new Integer(type), new Boolean(false)});
    }//getTemplates()
    
    
    private static final String hql_getTemplate = "from Template t where id=:id and deleted=:deleted";
    
    
    public Template getTemplate(Long id) throws Exception {
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


    public void saveActivity(Activity activity) throws Exception {
        //check if another activity has the same refid
        //TODO
        getSession().saveOrUpdate(activity);
    }//saveActivity()


    private static final String hql_getPActActivityByRefId = "from PActActivity p where refid=?";
    
    
    public PActActivity getPActActivityByRefId(Long refId) throws Exception {
        List list = getHibernateTemplate().find(hql_getPActActivityByRefId, refId);
        return (list.size()==0) ? null : (PActActivity) list.get(0);
    }//getPActActivity()


    private static final String hql_getGroupActivityByRefId = "from GroupActivity g where level=? and refid=?";
    
    
    public GroupActivity getGroupActivityByRefId(Long level, Long refId) throws Exception {
        List list = getHibernateTemplate().find(hql_getGroupActivityByRefId, new Object[] {level, refId});
        return (list.size()==0) ? null : (GroupActivity) list.get(0);
    }//getGroupActivityByRefId()


}//class WorkflowDAOImpl
