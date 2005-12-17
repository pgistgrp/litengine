package org.pgist.wfengine;



/**
 * Pushdown Activity is an head which can push down, that is,
 * it has a next head.
 * 
 * @author kenny
 *
 */
public interface PushDownable {


    public Activity getNext();
    
    public void setNext(Activity next);


}//interface PushDownable
