package org.pgist.wfengine.activity;

import org.pgist.wfengine.Activity;
import org.pgist.wfengine.SingleIn;
import org.pgist.wfengine.SingleOut;
import org.pgist.wfengine.Template;


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
 */
public abstract class GroupActivity extends Activity implements SingleIn, SingleOut {
    
    
    protected Template template;
    
    protected Activity headActivity;
    
    protected Activity tailActivity;
    
    protected Activity prev;
    
    protected Activity next;
    
    
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
     * @hibernate.many-to-one column="prev_id" class="org.pgist.wfengine.Activity" lazy="true" cascade="all"
     */
    public Activity getPrev() {
        return prev;
    }


    public void setPrev(Activity prev) {
        this.prev = prev;
    }
    
    
}//class GroupActivity
