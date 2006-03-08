package org.pgist.tests.wfengine;

import java.io.FileInputStream;
import java.util.List;

import junit.framework.TestCase;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.pgist.wfengine.Template;
import org.pgist.wfengine.Workflow;
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
    
    
    /*
     * Test method for 'org.pgist.wfengine.WorkflowEngine.addPGames(InputStream)'
     */
    public void testAddPGames() {
        String filePath = "data/pgames.xml";
        try {
            List list = engine.addPGames(new FileInputStream(filePath));
            assertEquals(2, list.size());
        } catch(Exception e) {
            e.printStackTrace();
        }
    }//testAddPGames()
    
    
    /*
     * Test method for 'org.pgist.wfengine.WorkflowEngine.addTemplates(InputStream)'
     */
    public void testAddTemplates() {
        String filePath = "data/templates.xml";
        try {
            List list = engine.addTemplates(new FileInputStream(filePath));
            assertEquals(4, list.size());
        } catch(Exception e) {
            e.printStackTrace();
        }
    }//testAddTemplates()
    
    
    /*
     * Test method for 'org.pgist.wfengine.WorkflowEngine.getTemplates(int)'
     */
    public void testGetTemplates() {
        try {
            List list = engine.getSituationTemplates();
            assertEquals(1, list.size());
        } catch(Exception e) {
            e.printStackTrace();
        }
    }//testGetTemplate()
    
    
    /*
     * Test method for 'org.pgist.wfengine.WorkflowEngine.getTemplate(Long)'
     */
    public void testGetTemplate() {
        try {
            Template template = engine.getTemplate(new Long(3));
            assertNotNull(template);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }//testGetTemplate()
    
    
    /*
     * Test method for 'org.pgist.wfengine.WorkflowEngine.spawn(Long)'
     */
    public void testSpawn() {
        try {
            Workflow workflow = engine.spawn(new Long(7));
            assertNotNull(workflow);
            System.out.println("New workflow: ---> "+workflow.getId());
        } catch(Exception e) {
            e.printStackTrace();
        }
    }//testGetTemplate()
    
    
    public void testExecuteWorkflow() {
        try {
            Workflow workflow = engine.getWorkflow(new Long(85));
            engine.executeWorkflow(workflow);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }//testExecuteWorkflow()
    
    
}//class WorkflowEngineTest
