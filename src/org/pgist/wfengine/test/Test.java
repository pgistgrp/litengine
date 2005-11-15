package org.pgist.wfengine.test;

import org.pgist.wfengine.Workflow;
import org.pgist.wfengine.activity.SequenceActivity;
import org.pgist.wfengine.activity.StartActivity;

public class Test {
    
    
    public static void main(String[] args) {
        System.out.println("---> ");
        
        StartActivity start = new StartActivity();
        
        SequenceActivity first = new MyActivity();
        start.setNext(first);
        first.setPrevious(start);
        
        SequenceActivity second = new YourActivity();
        first.setNext(second);
        second.setPrevious(first);
        
        Workflow flow = new Workflow();
        flow.setdefinition(start);
        flow.execute();
        
        if (flow.isFinished()) System.out.println("--> Workflow is finished!");
    }//main()
    
    
}//class Test
