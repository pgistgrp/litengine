package org.pgist.wfengine.test;

import org.pgist.wfengine.FlowEnvironment;
import org.pgist.wfengine.IAction;
import org.pgist.wfengine.activity.SequenceActivity;


public class YourActivity extends SequenceActivity {

    
    public YourActivity() {
        super(false);
        
        action = new IAction() {
            
            public boolean execute(FlowEnvironment env) {
                System.out.println("@ YourActivity.action.execute");
                
                return true;
            }//execute()
            
        };//new IAction
    }
    
    
}//class YourActivity
