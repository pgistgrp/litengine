package org.pgist.wfengine.activity;

import org.pgist.wfengine.Activity;


/**
 * Pushdown Activity is an activity which can push down, that is,
 * it has a next activity.
 * 
 * @author kenny
 *
 */
public interface PushDownable {


    public Activity getNext();
    
    public void setNext(Activity next);


}//interface PushDownable
