package org.pgist.wfengine.activity;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

import org.hibernate.Session;
import org.hibernate.proxy.HibernateProxy;
import org.pgist.wfengine.Activity;
import org.pgist.wfengine.FlowPiece;
import org.pgist.wfengine.RunningContext;
import org.pgist.wfengine.SingleIn;
import org.pgist.wfengine.SingleOut;
import org.pgist.wfengine.Template;
import org.pgist.wfengine.util.Utils;


/**
 * @author kenny
 * 
 * Class GroupActivity is a special activity which denotes an independent group of activities.
 * Those activities compose a piece of workflow instance, which has ONE head activity and ONE
 * tail activity. The head activity must be a backtracable activity and the tail activity must
 * be a pushdownable activity.
 * 
 * It's possible that head activity is the same as the tail activity, that is, the group contains
 * only one activity.
 * 
 * @hibernate.joined-subclass name="GroupActivity" table="litwf_activity_group"
 *                            lazy="true" proxy="org.pgist.wfengine.activity.GroupActivity"
 * @hibernate.joined-subclass-key column="id"
 */
public class GroupActivity extends Activity implements SingleIn, SingleOut {
    
    
    private static final long serialVersionUID = -6374034565784447958L;
    
    public static final int LEVEL_UNKNOWN   = 0;
    
    public static final int LEVEL_SITUATION = 1;
    
    public static final int LEVEL_MEETING   = 2;
    
    public static final int LEVEL_PMETHOD   = 3;
    
    public static final int LEVEL_PGAME     = 4;
    
    protected Long refId = null;
    
    protected int level = LEVEL_UNKNOWN;
    
    protected String name;
    
    protected String description;
    
    protected Template template;
    
    protected Activity headActivity;
    
    protected Activity tailActivity;
    
    protected Activity prev;
    
    protected Activity next;
    
    protected RunningContext context = null;
    
    
    public GroupActivity() {}
    
    
    public GroupActivity(int level) {
        this.level = level;
    }
    
    
    /**
     * @return
     * @hibernate.property
     */
    public Long getRefId() {
        return refId;
    }


    public void setRefId(Long refId) {
        this.refId = refId;
    }


    /**
     * @return
     * @hibernate.property not-null="true"
     */
    public int getLevel() {
        return level;
    }


    public void setLevel(int level) {
        this.level = level;
    }


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
     * @hibernate.many-to-one column="template_id" class="org.pgist.wfengine.Template" cascade="all"
     */
    public Template getTemplate() {
        return template;
    }


    public void setTemplate(Template template) {
        this.template = template;
    }


    /**
     * @return
     * @hibernate.many-to-one column="head_id" class="org.pgist.wfengine.Activity" lazy="true" cascade="all"
     */
    public Activity getHeadActivity() {
        return headActivity;
    }
    
    
    public void setHeadActivity(Activity headActivity) {
        this.headActivity = headActivity;
    }
    
    
    /**
     * @return
     * @hibernate.many-to-one column="tail_id" class="org.pgist.wfengine.Activity" lazy="true" cascade="all"
     */
    public Activity getTailActivity() {
        return tailActivity;
    }
    
    
    public void setTailActivity(Activity tailActivity) {
        this.tailActivity = tailActivity;
    }


    /**
     * @return
     * @hibernate.many-to-one column="prev_id" class="org.pgist.wfengine.Activity" lazy="true" cascade="all"
     */
    public Activity getPrev() {
        return prev;
    }


    public void setPrev(Activity prev) {
        this.prev = prev;
    }


    /**
     * @return
     * @hibernate.many-to-one column="next_id" class="org.pgist.wfengine.Activity" lazy="true" cascade="all"
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
     * @hibernate.many-to-one column="context_id" class="org.pgist.wfengine.RunningContext" lazy="true" cascade="all"
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
    
    
    protected void doActivate(RunningContext context) {
        FlowPiece piece = template.spawn();
        
        setHeadActivity((Activity) piece.getHead());
        
        ReturnActivity returnActivity = new ReturnActivity();
        returnActivity.setCount(0);
        returnActivity.setExpression(0);
        returnActivity.setGroup(this);
        returnActivity.setPrev((Activity)piece.getTail());
        returnActivity.setType(Activity.TYPE_RETURN);
        ((SingleOut) returnActivity.getPrev()).setNext(returnActivity);
        setTailActivity(returnActivity);
        
        setContext(new RunningContext(context));
        getContext().addActivity(getHeadActivity());
        setExpression(0);
    }//doActivate()
    
    
    synchronized protected boolean doExecute(RunningContext context, Stack stack) throws Exception {
        getContext().execute();
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
        Set activities = context.getRunningActivities();
        for (Iterator iter=activities.iterator(); iter.hasNext(); ) {
            Activity one = (Activity) Utils.narrow(iter.next());
            if (type == one.getType()) set.add(one);
        }//for iter
        return set;
    }//getRunningActivities()
    
    
}//class GroupActivity
