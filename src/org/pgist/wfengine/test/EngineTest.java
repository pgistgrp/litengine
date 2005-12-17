package org.pgist.wfengine.test;

import java.io.File;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.pgist.wfengine.WorkflowEnvironment;
import org.pgist.wfengine.Workflow;
import org.pgist.wfengine.activity.BranchActivity;
import org.pgist.wfengine.activity.JoinActivity;
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
                
                //*
                Workflow workflow = new Workflow();
                
                StartActivity start = new StartActivity();
                
                workflow.setDefinition(start);
                
                SequenceActivity sequence = new SequenceActivity();
                sequence.setTaskName("aa");
                
                start.setNext(sequence);
                sequence.setPrev(start);
                
                SequenceActivity sequence1 = new SequenceActivity();
                sequence1.setTaskName("aa");

                sequence1.setAutomatic(false);
                sequence1.setPerformerClass(InquiryPerformer.class.getName());
                
                sequence.setNext(sequence1);
                sequence1.setPrev(sequence);
                
                BranchActivity branch = new BranchActivity();
                JoinActivity join = new JoinActivity();
                branch.setAutomatic(true);
                branch.setJoinActivity(join);
                join.setAutomatic(true);
                join.setBranchActivity(branch);
                
                sequence1.setNext(branch);
                branch.setPrev(sequence1);
                
                SequenceActivity sequence2 = new SequenceActivity();
                sequence2.setTaskName("aa");
                sequence2.setAutomatic(true);
                
                branch.getBranches().add(sequence2);
                
                SequenceActivity sequence3 = new SequenceActivity();
                sequence3.setTaskName("aa");
                sequence3.setAutomatic(false);
                sequence3.setPerformerClass(InquiryPerformer.class.getName());
                
                branch.getBranches().add(sequence3);
                
                SequenceActivity sequence4 = new SequenceActivity();
                sequence4.setTaskName("aa");
                
                sequence2.setNext(sequence4);
                sequence4.setPrev(sequence2);
                sequence4.setNext(join);
                join.getJoins().add(sequence4);
                
                SequenceActivity sequence5 = new SequenceActivity();
                sequence5.setTaskName("aa");
                
                sequence3.setNext(sequence5);
                sequence5.setPrev(sequence3);
                sequence5.setNext(join);
                join.getJoins().add(sequence5);
                
                TerminateActivity terminate = new TerminateActivity();
                
                terminate.setPrev(join);
                join.setNext(terminate);
                
                workflow.saveState(session);
                //*/
                
                //Workflow workflow = (Workflow) session.load(Workflow.class, new Long(1));
                
                //workflow.execute();
                
                //workflow.saveState(session);
                
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
