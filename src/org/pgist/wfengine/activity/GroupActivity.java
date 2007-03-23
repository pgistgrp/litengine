package org.pgist.wfengine.activity;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

import org.hibernate.Session;
import org.pgist.wfengine.Activity;
import org.pgist.wfengine.Declarable;
import org.pgist.wfengine.Declaration;
import org.pgist.wfengine.RunningContext;
import org.pgist.wfengine.SingleIn;
import org.pgist.wfengine.SingleOut;
import org.pgist.wfengine.util.Utils;


/**
 * <p>Class GroupActivity is a special activity which denotes a group of activities.
 * Those activities compose a piece of workflow instance, which has ONE head activity and ONE
 * tail activity. The head activity must be a backtracable activity and the tail activity must
 * be a pushdownable activity.
 * 
 * <p>It's possible that head activity is the same as the tail activity, that is, the group contains
 * only one activity.
 * 
 * <p>GroupActivity with a null definition is template. Before a GroupActivity running,
 * workflow engine will duplicate the definition.
 * 
 * @hibernate.joined-subclass name="GroupActivity" table="litwf_activity_group"
 *                            lazy="true" proxy="org.pgist.wfengine.activity.GroupActivity"
 * @hibernate.joined-subclass-key column="id"
 * 
 * @author kenny
 */
public abstract class GroupActivity extends Activity implements SingleIn, SingleOut, Declarable {
    
    
    protected String name;
    
    protected String description;
    
    protected Activity headActivity;
    
    protected Activity tailActivity;
    
    protected Declaration declaration = new Declaration();
    
    protected Activity prev;
    
    protected Activity next;
    
    protected RunningContext context = new RunningContext();
    
    
    public GroupActivity() {}
    
    
    /**
     * @return
     * @hibernate.property
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
     * @hibernate.many-to-one column="head_id" lazy="true" cascade="all"
     */
    public Activity getHeadActivity() {
        return headActivity;
    }
    
    
    public void setHeadActivity(Activity headActivity) {
        this.headActivity = headActivity;
    }
    
    
    /**
     * @return
     * 
     * @hibernate.many-to-one column="tail_id" lazy="true" cascade="all"
     */
    public Activity getTailActivity() {
        return tailActivity;
    }
    
    
    public void setTailActivity(Activity tailActivity) {
        this.tailActivity = tailActivity;
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
     * @hibernate.many-to-one column="prev_id" lazy="true" cascade="all"
     */
    public Activity getPrev() {
        return prev;
    }


    public void setPrev(Activity prev) {
        this.prev = prev;
    }


    /**
     * @return
     * @hibernate.many-to-one column="next_id" lazy="true" cascade="all"
     */
    public Activity getNext() {
        return next;
    }


    public void setNext(Activity next) {
        this.next = next;
    }
    
    
    /**
     * @return
     * 
     * @hibernate.many-to-one column="context_id" lazy="true" cascade="all"
     */
    public RunningContext getContext() {
        return context;
    }


    public void setContext(RunningContext context) {
        this.context = context;
    }


    /*
     * ------------------------------------------------------------------------------
     */
    
    
    public Activity getEnd() {
        return (getNext()==null) ? this : getNext().getEnd(); 
    }//getEnd()
    
    
    protected void doActivate(RunningContext context) {
        //create a return activity
        ReturnActivity returnActivity = new ReturnActivity();
        returnActivity.setCounts(0);
        returnActivity.setExpression(0);
        returnActivity.setGroup(this);
        returnActivity.setType(Activity.TYPE_RETURN);
        
        //duplicate from the definition
        //FlowPiece piece = duplicate((SingleIn) getDefinition().getHeadActivity(), (SingleOut) getDefinition().getTailActivity());
        //piece.getTail().setNext(returnActivity);
        
        //set head and tail
        //setHeadActivity((Activity) piece.getHead());
        setTailActivity(returnActivity);
        
        //put head in the context
        getContext().addActivity(getHeadActivity());
        setExpression(0);
    }//doActivate()
    
    
    synchronized protected boolean doExecute(RunningContext context, Stack stack) throws Exception {
        RunningContext myContext = getContext();
        if (myContext!=null) {
            myContext.execute();
        }
        if (getExpression()>0) {//task is finished
            next.activate(context);
            stack.push(next);
            return true;
        }
        return false;
    }//doExecute()
    
    
    protected void doDeActivate(RunningContext context) {
        setHeadActivity(null);
        setTailActivity(null);
        setContext(null);
    }//doDeActivate()
    
    
    public void saveState(Session session) {
        session.save(this);
        if (next!=null) next.saveState(session);
    }//saveState()
    
    
    public Set getRunningActivities(int type) {
        Set set = new HashSet();
        if (context==null) return set;
        Set activities = context.getRunningActivities();
        for (Iterator iter=activities.iterator(); iter.hasNext(); ) {
            Activity one = (Activity) Utils.narrow(iter.next());
            if (type == one.getType()) set.add(one);
        }//for iter
        return set;
    }//getRunningActivities()
    
    
}//class GroupActivity
