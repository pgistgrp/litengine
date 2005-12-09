package org.pgist.wfengine.activity;

import org.pgist.wfengine.Activity;


/**
 * Backtrace Activity is an activity which can push down, that is,
 * it has a next activity.
 * 
 * @author kenny
 *
 */
public interface BackTracable {


    public Activity getPrev();
    
    public void setPrev(Activity prev);


}//interface BackTracable
