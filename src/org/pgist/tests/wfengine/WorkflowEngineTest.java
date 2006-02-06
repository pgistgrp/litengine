package org.pgist.tests.wfengine;

import java.io.FileInputStream;
import java.util.List;

import junit.framework.TestCase;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.pgist.wfengine.Activity;
import org.pgist.wfengine.FlowPiece;
import org.pgist.wfengine.Template;
import org.pgist.wfengine.WorkflowDAO;
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
    
    protected SessionFactory sessionFactory = null; 
    
    private WorkflowEngine engine = null;
    
    private WorkflowDAO workflowDAO = null;
    
    
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
        workflowDAO = (WorkflowDAO) appContext.getBean("workflowDAO");
    }//setUp()
    
    
    protected void tearDown() throws Exception {
        super.tearDown();
        SessionHolder sessionHolder = (SessionHolder) TransactionSynchronizationManager.unbindResource(sessionFactory);
        SessionFactoryUtils.releaseSession(sessionHolder.getSession(), sessionFactory);
    }//tearDown()
    
    
    /*
     * Test method for 'org.pgist.wfengine.WorkflowEngine.addPActs(InputStream)'
     */
    public void testAddPActs() {
        String filePath = "data/pacts.xml";
        try {
            List list = engine.addPActs(new FileInputStream(filePath));
            assertEquals(2, list.size());
        } catch(Exception e) {
            e.printStackTrace();
        }
    }//testAddPActs()
    
    
    /*
     * Test method for 'org.pgist.wfengine.WorkflowEngine.addTemplates(InputStream)'
     */
    public void testAddTemplates() {
        String filePath = "data/templates.xml";
        try {
            List list = engine.addTemplates(new FileInputStream(filePath));
            assertEquals(8, list.size());
        } catch(Exception e) {
            e.printStackTrace();
        }
    }//testAddTemplates()
    
    
    /*
     * Test method for 'org.pgist.wfengine.WorkflowEngine.getTemplate(Long)'
     */
    public void testGetTemplate() {
        try {
            Template template = engine.getTemplate(new Long(3));
            assertNotNull(template);
            FlowPiece piece = template.spawn(workflowDAO.getHibernateSession(), null);
            workflowDAO.saveActivity((Activity) piece.getHead());
            System.out.println("Head ---> "+ ((Activity)piece.getHead()).getId());
            System.out.println("Tail ---> "+ ((Activity)piece.getTail()).getId());
        } catch(Exception e) {
            e.printStackTrace();
        }
    }//testGetTemplate()
    
    
}
