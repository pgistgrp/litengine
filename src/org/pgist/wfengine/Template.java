package org.pgist.wfengine;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.pgist.wfengine.activity.BranchActivity;
import org.pgist.wfengine.activity.EndSwitchActivity;
import org.pgist.wfengine.activity.PActActivity;
import org.pgist.wfengine.activity.GroupActivity;
import org.pgist.wfengine.activity.JoinActivity;
import org.pgist.wfengine.activity.JumpActivity;
import org.pgist.wfengine.activity.LoopActivity;
import org.pgist.wfengine.activity.RepeatActivity;
import org.pgist.wfengine.activity.ReturnActivity;
import org.pgist.wfengine.activity.SwitchActivity;
import org.pgist.wfengine.activity.TerminateActivity;
import org.pgist.wfengine.activity.UntilActivity;
import org.pgist.wfengine.activity.WhileActivity;


/**
 * @author kenny
 * 
 * Class Template models the workflow template which can be based on to generate a piece of
 * workflow instance. One template is a series of activities which has ONE head activity and
 * ONE tail activity.
 * 
 * The head activity must be a SingleIn activity.
 * 
 * The tail activity must be a SingleOut activity.
 * 
 * So head is the only entry of the template, and tail is the only outlet of the template.
 * 
 * The generated piece of workflow instance will has the exact structure as the template, and
 * can be run by the workflow engine.
 * 
 * There're four types of template:
 *   TYPE_SITUATION - Dicision Situation flow
 *   TYPE_MEETING   - Meeting flow
 *   TYPE_PMETHOD   - Participatory Method flow
 *   TYPE_PGAME     - Participatory Game flow
 *   
 * TYPE_PGAME is the smallest unit of template.
 * 
 * Use the method spawn() to generate a new flow instance of the template. The spawning algorithm
 * is a two pass process:
 * 
 *    (1) Traverse the template graph, and replicate each activities, the spawned activities are 
 *        saved in a HashMap with the original activities as the keys.
 *        
 *    (2) Weave the spawned activities to a flow graph.
 *
 * @hibernate.class table="litwf_templates"
 */
public class Template {
    
    
    public static final int TYPE_SITUATION = 1;
    
    public static final int TYPE_MEETING   = 2;
    
    public static final int TYPE_PMETHOD   = 3;
    
    public static final int TYPE_PGAME     = 4;
    
    
    protected Long id;
    
    protected int type = 3;
    
    protected String name;
    
    protected String description;
    
    protected Activity head;
    
    protected Activity tail;
    
    protected boolean deleted;
    
    
    /**
     * @return
     * @hibernate.id generator-class="native"
     */
    public Long getId() {
        return id;
    }
    
    
    public void setId(Long id) {
        this.id = id;
    }


    /**
     * @return
     * @hibernate.property not-null="true"
     */
    public int getType() {
        return type;
    }


    public void setType(int type) {
        this.type = type;
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
     * @hibernate.property not-null="true"
     */
    public String getDescription() {
        return description;
    }


    public void setDescription(String description) {
        this.description = description;
    }


    /**
     * @return
     * @hibernate.many-to-one column="head_id" class="org.pgist.wfengine.Activity" cascade="all"
     */
    public Activity getHead() {
        return (Activity) head;
    }


    public void setHead(Activity activity) {
        this.head = activity;
    }
    
    
    /**
     * @return
     * @hibernate.many-to-one column="tail_id" class="org.pgist.wfengine.Activity" cascade="all"
     */
    public Activity getTail() {
        return (Activity) tail;
    }


    public void setTail(Activity tail) {
        this.tail = tail;
    }
    
    
    /**
     * @return
     * @hibernate.property not-null="true"
     */
    public boolean isDeleted() {
        return deleted;
    }


    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }


    /*
     * -------------------------------------------------------------------
     */
    
    
    private Map replicate() {
        Map map = new HashMap();
        Stack stack = new Stack();
        stack.push(head);
        
        Activity one = null;
        Activity two = null;
        
        //traverse the flow
        while (!stack.empty()) {
            one = (Activity) stack.pop();
            if (one==null) continue;
            
            //push children on stack
            switch(one.getType()) {
                case Activity.TYPE_PACT:
                    two = new PActActivity();
                    stack.push( ((PActActivity) one).getNext() );
                    break;
                case Activity.TYPE_PGAME:
                case Activity.TYPE_PMETHOD:
                case Activity.TYPE_MEETING:
                    GroupActivity realOne = (GroupActivity) one;
                    GroupActivity realTwo = new GroupActivity(realOne.getLevel());
                    two = realTwo;
                    realTwo.setTemplate(realOne.getTemplate());
                    stack.push( realOne.getNext() );
                    break;
                case Activity.TYPE_RETURN:
                    two = new ReturnActivity();
                    break;
                case Activity.TYPE_BRANCH:
                    two = new BranchActivity();
                    stack.addAll(((BranchActivity) one).getBranches());
                    break;
                case Activity.TYPE_JOIN:
                    two = new JoinActivity();
                    stack.push( ((JoinActivity) one).getNext() );
                    break;
                case Activity.TYPE_SWITCH:
                    two = new SwitchActivity();
                    stack.addAll( ((SwitchActivity) one).getSwitches() );
                    break;
                case Activity.TYPE_ENDSWITCH:
                    two = new EndSwitchActivity();
                    stack.push( ((EndSwitchActivity) one).getNext() );
                    break;
                case Activity.TYPE_WHILE:
                    two = new WhileActivity();
                    stack.push( ((WhileActivity) one).getNext() );
                    break;
                case Activity.TYPE_LOOP:
                    two = new LoopActivity();
                    stack.push( ((LoopActivity) one).getNext() );
                    break;
                case Activity.TYPE_REPEAT:
                    two = new RepeatActivity();
                    stack.push( ((RepeatActivity) one).getNext() );
                    break;
                case Activity.TYPE_UNTIL:
                    two = new UntilActivity();
                    stack.push( ((UntilActivity) one).getNext() );
                    break;
                case Activity.TYPE_JUMP:
                    two = new JumpActivity();
                    stack.push( ((JumpActivity) one).getNext() );
                    break;
                case Activity.TYPE_TERMINATE:
                    two = new TerminateActivity();
                    break;
            }//switch
            
            two.setCount(0);
            two.setExpression(0);
            two.setType(one.getType());
            
            map.put(one, two);
        }//while
        
        return map;
    }//replicate()
    
    
    /**
     * @return
     */
    public FlowPiece spawn() {
        //1st pass, replicate all activities
        Map map = replicate();
        
        List list = null;
        
        //2nd pass, weave the spawned activities
        for (Iterator iter=map.entrySet().iterator(); iter.hasNext(); ) {
            Map.Entry entry = (Map.Entry) iter.next();
            Activity one = (Activity) entry.getKey();
            Activity two = (Activity) entry.getValue();
            
            switch(one.getType()) {
                case Activity.TYPE_PACT:
                    PActActivity pactOne = (PActActivity) one;
                    PActActivity pactTwo = (PActActivity) two;
                    pactTwo.setPrev( (Activity)map.get(pactOne.getPrev()) );
                    break;
                case Activity.TYPE_PGAME:
                case Activity.TYPE_PMETHOD:
                case Activity.TYPE_MEETING:
                    GroupActivity groupOne = (GroupActivity) one;
                    GroupActivity groupTwo = (GroupActivity) two;
                    groupTwo.setPrev( (Activity)map.get(groupOne.getPrev()) );
                    groupTwo.setNext( (Activity)map.get(groupOne.getNext()) );
                    break;
                case Activity.TYPE_RETURN:
                    ReturnActivity returnOne = (ReturnActivity) one;
                    ReturnActivity returnTwo = (ReturnActivity) two;
                    returnTwo.setPrev( (Activity)map.get(returnOne.getPrev()) );
                    returnTwo.setGroup( (GroupActivity)map.get(returnOne.getGroup()) );
                    break;
                case Activity.TYPE_BRANCH:
                    BranchActivity branchOne = (BranchActivity) one;
                    BranchActivity branchTwo = (BranchActivity) two;
                    branchTwo.setPrev( (Activity)map.get(branchOne.getPrev()) );
                    branchTwo.setJoinActivity( (JoinActivity)map.get(branchOne.getJoinActivity()) );
                    list = branchOne.getBranches();
                    for (int i=0,n=list.size(); i<n; i++) {
                        branchTwo.getBranches().add( map.get(list.get(i)) );
                    }//for i
                    break;
                case Activity.TYPE_JOIN:
                    JoinActivity joinOne = (JoinActivity) one;
                    JoinActivity joinTwo = (JoinActivity) two;
                    joinTwo.setNext( (Activity)map.get(joinOne.getNext()) );
                    joinTwo.setBranchActivity( (BranchActivity)map.get(joinOne.getBranchActivity()) );
                    for (Iterator iter1=joinOne.getJoins().iterator(); iter1.hasNext(); ) {
                        joinTwo.getJoins().add( map.get(iter1.next()) );
                    }//for iter
                    break;
                case Activity.TYPE_SWITCH:
                    SwitchActivity switchOne = (SwitchActivity) one;
                    SwitchActivity switchTwo = (SwitchActivity) two;
                    switchTwo.setPrev( (Activity)map.get(switchOne.getPrev()) );
                    switchTwo.setEndSwitchActivity( (EndSwitchActivity)map.get(switchOne.getEndSwitchActivity()) );
                    list = switchOne.getSwitches();
                    for (int i=0,n=list.size(); i<n; i++) {
                        switchTwo.getSwitches().add( map.get(list.get(i)) );
                    }//for i
                    break;
                case Activity.TYPE_ENDSWITCH:
                    EndSwitchActivity endSwitchOne = (EndSwitchActivity) one;
                    EndSwitchActivity endSwitchTwo = (EndSwitchActivity) two;
                    endSwitchTwo.setNext( (Activity)map.get(endSwitchOne.getNext()) );
                    endSwitchTwo.setSwitchActivity( (SwitchActivity)map.get(endSwitchOne.getSwitchActivity()) );
                    for (Iterator iter1=endSwitchOne.getChoices().iterator(); iter1.hasNext(); ) {
                        endSwitchTwo.getChoices().add( map.get(iter1.next()) );
                    }//for iter
                    break;
                case Activity.TYPE_WHILE:
                    WhileActivity whileOne = (WhileActivity) one;
                    WhileActivity whileTwo = (WhileActivity) two;
                    whileTwo.setPrev( (Activity)map.get(whileOne.getPrev()) );
                    whileTwo.setNext( (Activity)map.get(whileOne.getNext()) );
                    whileTwo.setLoop( (LoopActivity)map.get(whileOne.getLoop()) );
                    break;
                case Activity.TYPE_LOOP:
                    LoopActivity loopOne = (LoopActivity) one;
                    LoopActivity loopTwo = (LoopActivity) two;
                    loopTwo.setPrev( (Activity)map.get(loopOne.getPrev()) );
                    loopTwo.setNext( (Activity)map.get(loopOne.getNext()) );
                    loopTwo.setWhilst( (WhileActivity)map.get(loopOne.getWhilst()) );
                    break;
                case Activity.TYPE_REPEAT:
                    RepeatActivity repeatOne = (RepeatActivity) one;
                    RepeatActivity repeatTwo = (RepeatActivity) two;
                    repeatTwo.setPrev( (Activity)map.get(repeatOne.getPrev()) );
                    repeatTwo.setNext( (Activity)map.get(repeatOne.getNext()) );
                    repeatTwo.setUntil( (UntilActivity)map.get(repeatOne.getUntil()) );
                    break;
                case Activity.TYPE_UNTIL:
                    UntilActivity untilOne = (UntilActivity) one;
                    UntilActivity untilTwo = (UntilActivity) two;
                    untilTwo.setPrev( (Activity)map.get(untilOne.getPrev()) );
                    untilTwo.setNext( (Activity)map.get(untilOne.getNext()) );
                    untilTwo.setRepeat( (RepeatActivity)map.get(untilOne.getRepeat()) );
                    break;
                case Activity.TYPE_JUMP:
                    JumpActivity jumpOne = (JumpActivity) one;
                    JumpActivity jumpTwo = (JumpActivity) two;
                    jumpTwo.setPrev( (Activity)map.get(jumpOne.getPrev()) );
                    jumpTwo.setNext( (Activity)map.get(jumpOne.getNext()) );
                    list = jumpOne.getJumps();
                    for (int i=0,n=list.size(); i<n; i++) {
                        jumpTwo.getJumps().add( map.get(list.get(i)) );
                    }//for i
                    break;
                case Activity.TYPE_TERMINATE:
                    TerminateActivity termOne = (TerminateActivity) one;
                    TerminateActivity termTwo = (TerminateActivity) two;
                    termTwo.setPrev( (Activity)map.get(termOne.getPrev()) );
                    break;
            }//switch
        }//for
        
        return new FlowPiece((SingleIn)map.get(head), (SingleOut)map.get(tail));
    }//spawn()


}//class Template
