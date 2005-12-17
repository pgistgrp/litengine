package org.pgist.wfengine;



/**
 * Backtrace Activity is an head which can push down, that is,
 * it has a next head.
 * 
 * @author kenny
 *
 */
public interface BackTracable {


    public Activity getPrev();
    
    public void setPrev(Activity prev);


}//interface BackTracable
