package org.pgist.wfengine.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.pgist.wfengine.FlowEnvironment;
import org.pgist.wfengine.IAction;
import org.pgist.wfengine.activity.SequenceActivity;


public class MyActivity extends SequenceActivity {

    
    public MyActivity() {
        super(false);
        
        action = new IAction() {
            
            public boolean execute(FlowEnvironment env) {
                System.out.println("@ MyActivity.action.execute");
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                    System.out.print("Input your word:");
                    String s = reader.readLine();
                    if (s.length()>0) return true;
                } catch(Exception e) {
                    e.printStackTrace();
                }
                return false;
            }//execute()
            
        };//new IAction
    }
    
    
}//class MyActivity
