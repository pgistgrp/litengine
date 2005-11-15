package org.pgist.wfengine.activity;

import org.pgist.wfengine.FlowEnvironment;
import org.pgist.wfengine.IAction;

public class StartActivity extends SequenceActivity {
    
    
    public StartActivity() {
        super(true);
        
        setPrevious(null);
        setNext(null);
        
        action = new IAction() {
            
            public boolean execute(FlowEnvironment env) {
                System.out.println("@ StartActivity.action.execute");
                
                return true;
            }//execute()
            
        };//new IAction
    }
    
    
}//class StartActivity
