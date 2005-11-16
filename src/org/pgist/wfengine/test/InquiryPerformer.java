package org.pgist.wfengine.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.pgist.wfengine.Activity;
import org.pgist.wfengine.WorkflowEnvironment;
import org.pgist.wfengine.IPerformer;


public class InquiryPerformer implements IPerformer {
    
    
    public InquiryPerformer() {
    }
    
    
    public int perform(Activity activity, WorkflowEnvironment env) {
        System.out.print(activity.getId()+", Please enter your word: ");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            String s = reader.readLine();
            if (s.length()>3) {
                return 1;
            } else {
                return 0;
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    
}//class DefaultPerformer
