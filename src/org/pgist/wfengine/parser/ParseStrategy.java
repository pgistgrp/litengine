package org.pgist.wfengine.parser;

import org.pgist.wfengine.Activity;


/**
 * 
 * @author kenny
 *
 */
public interface ParseStrategy {
    
    
    String getComponentName();
    
    Activity getActivity(String name);
    
    Activity clone(Activity activity) throws ParserException;
    
    
}//interface ParseStrategy
