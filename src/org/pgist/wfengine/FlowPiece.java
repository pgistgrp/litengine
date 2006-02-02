package org.pgist.wfengine;


/**
 * Class FlowPiece is a holder to contain a piece of workflow. It's a transient object to hold
 * the head and the tail of the flow.
 * 
 * Some methods of some classes will use FlowPiece to return the head and tail together.
 * 
 * @author kenny
 *
 */
public class FlowPiece {
    
    
    private SingleIn head;
    
    private SingleOut tail;
    
    
    public FlowPiece() {
    }
    
    
    public FlowPiece(SingleIn first, SingleOut last) {
        this.head = first;
        this.tail = last;
    }
    
    
    public SingleIn getHead() {
        return head;
    }
    
    
    public void setHead(SingleIn head) {
        this.head = head;
    }


    public SingleOut getTail() {
        return tail;
    }


    public void setTail(SingleOut tail) {
        this.tail = tail;
    }
    
    
}//class FlowPiece
