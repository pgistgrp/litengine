package org.pgist.wfengine.parser;

import java.util.Map;

import org.pgist.wfengine.Activity;
import org.pgist.wfengine.activity.PGameActivity;


/**
 * 
 * @author kenny
 *
 */
public class PGameParseStrategy implements ParseStrategy {
    
    
    Map<String, PGameActivity> activities;
    
    
    public PGameParseStrategy(Map<String, PGameActivity> activities) {
        this.activities = activities;
    }
    
    
    public String getComponentName() {
        return "pgame";
    }
    
    
    public Activity getActivity(String name) {
        return activities.get(name);
    }
    
    
    public Activity clone(Activity activity) throws ParserException {
        if (activity instanceof PGameActivity) {
            PGameActivity old = (PGameActivity) activity;
            return old.clone();
        }
        throw new ParserException("");
    }
    
    
}//class PGameParseStrategy
