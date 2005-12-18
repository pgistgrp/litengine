package org.pgist.wfengine.test;

import java.util.List;

import org.pgist.wfengine.WorkflowEngine;
import org.springframework.context.support.ClassPathXmlApplicationContext;


/**
 * Test XML Parser for workflow definition
 * 
 * @author kenny
 *
 */
public class ParserTest {
    
    
    /**
     * Execute the task
     */
    public static void main(String[] args) {
        ParserTest test = new ParserTest();
        test.run();

        /*
        try {
            Configuration cfg = new Configuration();
            cfg.configure(new File("/home/kenny/workdir/LITEngine/src/hibernate.cfg.xml"));
            SessionFactory sessionFactory = cfg.buildSessionFactory();
            
            //initialization begins here
            
            Session session = null;
            try {
                session = sessionFactory.openSession();
                Transaction transaction = session.beginTransaction();
                
                File file = new File("/home/kenny/workdir/LITEngine/test/flow.xml");
                WFDefinitionParser parser = new WFDefinitionParser(file);
                parser.parse();
                List processes = parser.getProcesses();
                
                for (int i=0, n=processes.size(); i<n; i++) {
                    WFProcess process = (WFProcess) processes.get(i);
                    session.save(process);
                }//for i
                
                transaction.commit();
            } catch(Exception ex) {
                ex.printStackTrace();
            } finally {
                session.close();
            }
            
            //initialization ends here
        } catch (Exception e) {
            e.printStackTrace();
        }
        */
    }//main()
    

    public void run() {
        try {
            ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext(
                new String[] {
                    "/test/dataAccessContext-local.xml",
                    "/test/applicationContext.xml"
                }
            );
            
            String[] names = appContext.getBeanDefinitionNames();
            for (int i=0; i<names.length; i++) {
                System.out.println("--> "+names[i]);
            }//for i
            
            //WorkflowDAO dao = (WorkflowDAO) appContext.getBean("workflowDAO");
            WorkflowEngine engine = (WorkflowEngine) appContext.getBean("litengine");
            List processes = engine.addProcess("/home/kenny/workdir/LITEngine/test/flow.xml");
        } catch(Exception e) {
            e.printStackTrace();
        }
    }//run()
    
    
}//class ParserTest
