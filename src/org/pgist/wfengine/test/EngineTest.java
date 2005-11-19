package org.pgist.wfengine.test;

import java.io.File;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.pgist.wfengine.WorkflowEnvironment;
import org.pgist.wfengine.Workflow;
import org.pgist.wfengine.activity.BranchActivity;
import org.pgist.wfengine.activity.SequenceActivity;
import org.pgist.wfengine.activity.StartActivity;
import org.pgist.wfengine.activity.SwitchActivity;
import org.pgist.wfengine.activity.TerminateActivity;


/**
 * Init the PGIST system
 * @author kenny
 *
 */
public class EngineTest {
    
    
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
                
                /*
                Workflow workflow = new Workflow();
                
                StartActivity start = new StartActivity();
                
                workflow.setDefinition(start);
                
                SequenceActivity sequence = new SequenceActivity();
                
                start.setNext(sequence);
                sequence.setPrev(start);
                
                SequenceActivity sequence1 = new SequenceActivity();
                sequence1.setAutomatic(false);
                sequence1.setPerformerClass(InquiryPerformer.class.getName());
                
                sequence.setNext(sequence1);
                sequence1.setPrev(sequence);
                
                SequenceActivity sequence2 = new SequenceActivity();
                sequence2.setAutomatic(true);
                
                sequence1.setNext(sequence2);
                sequence2.setPrev(sequence1);
                
                SequenceActivity sequence3 = new SequenceActivity();
                sequence3.setAutomatic(false);
                sequence3.setPerformerClass(InquiryPerformer.class.getName());
                
                sequence2.setNext(sequence3);
                sequence3.setPrev(sequence2);
                
                SequenceActivity sequence4 = new SequenceActivity();
                
                sequence3.setNext(sequence4);
                sequence4.setPrev(sequence3);
                
                TerminateActivity terminate = new TerminateActivity();
                
                terminate.setPrev(sequence4);
                sequence4.setNext(terminate);
                
                workflow.saveState(session);
                */
                
                Workflow workflow = (Workflow) session.load(Workflow.class, new Long(1));
                
                workflow.execute();
                
                workflow.saveState(session);
                
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
    }
    

}//class EngineTest
