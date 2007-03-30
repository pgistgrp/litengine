package org.pgist.wfengine.activity;

import org.hibernate.Session;
import org.pgist.wfengine.Activity;
import org.pgist.wfengine.Declarable;
import org.pgist.wfengine.Declaration;
import org.pgist.wfengine.SingleIn;
import org.pgist.wfengine.SingleOut;


/**
 * 
 * @author kenny
 *
 * @hibernate.joined-subclass name="PGameActivity" table="litwf_activity_pgame"
 *                            lazy="true" proxy="org.pgist.wfengine.activity.PGameActivity"
 * @hibernate.joined-subclass-key column="id"
 */
public abstract class PGameActivity extends Activity implements SingleIn, SingleOut, Declarable {
    
    
    protected String name = null;
    
    protected String description = null;
    
    protected Declaration declaration = new Declaration();
    
    protected Activity prev;
    
    protected Activity next;
    
    
    public PGameActivity() {
    }
    
    
    /**
     * @return
     * @hibernate.property not-null="true"
     */
    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    /**
     * @return
     * @hibernate.property
     */
    public String getDescription() {
        return description;
    }


    public void setDescription(String description) {
        this.description = description;
    }


    /**
     * @return
     * 
     * @hibernate.many-to-one column="declaration_id" cascade="all" lazy="false"
     */
    public Declaration getDeclaration() {
        return declaration;
    }


    public void setDeclaration(Declaration declaration) {
        this.declaration = declaration;
    }


    /**
     * @return
     * @hibernate.many-to-one column="prev_id" class="org.pgist.wfengine.Activity" cascade="all" lazy="true"
     */
    public Activity getPrev() {
        return prev;
    }
    
    
    public void setPrev(Activity prev) {
        this.prev = prev;
    }


    /**
     * @return
     *
     * @hibernate.many-to-one column="next_id" class="org.pgist.wfengine.Activity" cascade="all" lazy="true"
     */
    public Activity getNext() {
        return next;
    }
    
    
    public void setNext(Activity next) {
        this.next = next;
    }


    /*
     * ------------------------------------------------------------------------
     */
    
    
    public void saveState(Session session) {
        session.save(this);
        if (next!=null) next.saveState(session);
    }//saveState()


}//class PGameActivity
