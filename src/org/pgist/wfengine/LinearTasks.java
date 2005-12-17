package org.pgist.wfengine;


/**
 * @author kenny
 *
 */
public class LinearTasks {
    
    private BackTracable first;
    private PushDownable last;
    
    public LinearTasks(BackTracable first, PushDownable last) {
        this.first = first;
        this.last = last;
    }
    
    public BackTracable getFirst() {
        return first;
    }
    
    public PushDownable getLast() {
        return last;
    }
    
}//class LinearTasks
