package org.pgist.tests.wfengine;

import junit.framework.TestCase;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.pgist.wfengine.WorkflowEngine;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;


/**
 * 
 * @author kenny
 *
 */
public class WorkflowEngineTest extends TestCase {
    
    
    private ApplicationContext appContext = null;
    
    private SessionFactory sessionFactory = null; 
    
    private WorkflowEngine engine = null;
    
    
    protected void setUp() throws Exception {
        super.setUp();
        appContext = new FileSystemXmlApplicationContext(
            new String[] {
                "test/dataAccessContext-local.xml",
                "test/applicationContext.xml"
            }
        );
        
        sessionFactory = (SessionFactory) appContext.getBean("sessionFactory");
        Session session = SessionFactoryUtils.getSession(sessionFactory, true);
        TransactionSynchronizationManager.bindResource(sessionFactory, new SessionHolder(session));
        
        engine = (WorkflowEngine) appContext.getBean("litengine");
    }//setUp()
    
    
    protected void tearDown() throws Exception {
        super.tearDown();
        SessionHolder sessionHolder = (SessionHolder) TransactionSynchronizationManager.unbindResource(sessionFactory);
        sessionHolder.getSession().close();
        SessionFactoryUtils.releaseSession(sessionHolder.getSession(), sessionFactory);
    }//tearDown()
    
    
}//class WorkflowEngineTest
