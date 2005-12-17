package org.pgist.wfengine.test;

import java.io.File;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.pgist.wfengine.WFDefinitionParser;
import org.pgist.wfengine.WFProcess;


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
    }//main()
    

}//class ParserTest
