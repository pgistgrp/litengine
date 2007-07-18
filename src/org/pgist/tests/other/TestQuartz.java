package org.pgist.tests.other;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.pgist.wfengine.WorkflowEngine;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;


/**
 * Test for Quartz
 * 
 * @author kenny
 */
public class TestQuartz {
    
    
    static private ApplicationContext appContext = null;
    
    static private SessionFactory sessionFactory = null; 
    
    static private WorkflowEngine engine = null;
    
    
    @BeforeClass
    public static void setUp() throws Exception {
        appContext = new FileSystemXmlApplicationContext(
            new String[] {
                "test/dataAccessContext-local.xml",
                "test/applicationContext-tasks.xml",
                "test/applicationContext.xml",
            }
        );
        
        sessionFactory = (SessionFactory) appContext.getBean("sessionFactory");
        Session session = SessionFactoryUtils.getSession(sessionFactory, true);
        TransactionSynchronizationManager.bindResource(sessionFactory, new SessionHolder(session));
        
        engine = (WorkflowEngine) appContext.getBean("litengine");
    }//setUp()
    
    
    @AfterClass
    public static void tearDown() throws Exception {
//        SessionHolder sessionHolder = (SessionHolder) TransactionSynchronizationManager.unbindResource(sessionFactory);
//        sessionHolder.getSession().flush();
//        sessionHolder.getSession().close();
//        SessionFactoryUtils.releaseSession(sessionHolder.getSession(), sessionFactory);
    }//tearDown()
    
    
    @Test
    public void test1() throws Exception {
        MockService service = (MockService) appContext.getBean("service");
        
        try {
            service.setJob();
        } catch (Exception e) {
        }
        
        Thread.sleep(10000L);
    }//test1()
    
    
}//class TestQuartz
