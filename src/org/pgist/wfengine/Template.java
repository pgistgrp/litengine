package org.pgist.wfengine;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.hibernate.Session;
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
    
    
    private Map replicate(Session session) {
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
                    one = (PActActivity) session.load(PActActivity.class, one.getId());
                    PActActivity pactOne = (PActActivity) one;
                    PActActivity pactTwo = new PActActivity();
                    two = pactTwo;
                    pactTwo.setName( pactOne.getName() );
                    pactTwo.setDescription( pactOne.getDescription() );
                    stack.push( ((PActActivity) one).getNext() );
                    break;
                case Activity.TYPE_PGAME:
                case Activity.TYPE_PMETHOD:
                case Activity.TYPE_MEETING:
                    GroupActivity realOne = (GroupActivity) session.load(GroupActivity.class, one.getId());
                    one = realOne;
                    GroupActivity realTwo = new GroupActivity(realOne.getLevel());
                    two = realTwo;
                    realTwo.setTemplate(realOne.getTemplate());
                    stack.push( realOne.getNext() );
                    break;
                case Activity.TYPE_RETURN:
                    one = (ReturnActivity) session.load(ReturnActivity.class, one.getId());
                    two = new ReturnActivity();
                    break;
                case Activity.TYPE_BRANCH:
                    one = (BranchActivity) session.load(BranchActivity.class, one.getId());
                    two = new BranchActivity();
                    stack.addAll(((BranchActivity) one).getBranches());
                    break;
                case Activity.TYPE_JOIN:
                    one = (JoinActivity) session.load(JoinActivity.class, one.getId());
                    two = new JoinActivity();
                    stack.push( ((JoinActivity) one).getNext() );
                    break;
                case Activity.TYPE_SWITCH:
                    one = (SwitchActivity) session.load(SwitchActivity.class, one.getId());
                    two = new SwitchActivity();
                    stack.addAll( ((SwitchActivity) one).getSwitches() );
                    break;
                case Activity.TYPE_ENDSWITCH:
                    one = (EndSwitchActivity) session.load(EndSwitchActivity.class, one.getId());
                    two = new EndSwitchActivity();
                    stack.push( ((EndSwitchActivity) one).getNext() );
                    break;
                case Activity.TYPE_WHILE:
                    one = (WhileActivity) session.load(WhileActivity.class, one.getId());
                    two = new WhileActivity();
                    stack.push( ((WhileActivity) one).getNext() );
                    break;
                case Activity.TYPE_LOOP:
                    one = (LoopActivity) session.load(LoopActivity.class, one.getId());
                    two = new LoopActivity();
                    stack.push( ((LoopActivity) one).getNext() );
                    break;
                case Activity.TYPE_REPEAT:
                    one = (RepeatActivity) session.load(RepeatActivity.class, one.getId());
                    two = new RepeatActivity();
                    stack.push( ((RepeatActivity) one).getNext() );
                    break;
                case Activity.TYPE_UNTIL:
                    one = (UntilActivity) session.load(UntilActivity.class, one.getId());
                    two = new UntilActivity();
                    stack.push( ((UntilActivity) one).getNext() );
                    break;
                case Activity.TYPE_JUMP:
                    one = (JumpActivity) session.load(JumpActivity.class, one.getId());
                    two = new JumpActivity();
                    stack.push( ((JumpActivity) one).getNext() );
                    break;
                case Activity.TYPE_TERMINATE:
                    one = (TerminateActivity) session.load(TerminateActivity.class, one.getId());
                    two = new TerminateActivity();
                    break;
            }//switch
            
            two.setCount(0);
            two.setExpression(0);
            two.setType(one.getType());
            
            map.put(one.getId(), new Activity[] {one, two});
        }//while
        
        return map;
    }//replicate()
    
    
    /**
     * @return
     */
    public FlowPiece spawn(Session session, GroupActivity group) {
        //1st pass, replicate all activities
        Map map = replicate(session);
        
        List list = null;
        
        Activity prev = null;
        Activity next = null;
        Activity[] pair = null;
        
        //2nd pass, weave the spawned activities
        for (Iterator iter=map.values().iterator(); iter.hasNext(); ) {
            Activity[] activities = (Activity[]) iter.next();
            Activity one = activities[0];//old activity
            Activity two = activities[1];//new activity
            
            switch(one.getType()) {
                case Activity.TYPE_PACT:
                    PActActivity pactOne = (PActActivity) one;
                    PActActivity pactTwo = (PActActivity) two;
                    prev = pactOne.getPrev();
                    if (prev!=null) {
                        pair = (Activity[]) map.get(prev.getId());
                        pactTwo.setPrev( pair[1] );
                    }
                    next = pactOne.getNext();
                    if (next!=null) {
                        pair = (Activity[]) map.get(next.getId());
                        pactTwo.setNext( pair[1] );
                    }
                    break;
                case Activity.TYPE_PGAME:
                case Activity.TYPE_PMETHOD:
                case Activity.TYPE_MEETING:
                    GroupActivity groupOne = (GroupActivity) one;
                    GroupActivity groupTwo = (GroupActivity) two;
                    prev = groupOne.getPrev();
                    if (prev!=null) {
                        pair = (Activity[]) map.get(prev.getId());
                        groupTwo.setPrev( pair[1] );
                    }
                    next = groupOne.getNext();
                    if (next!=null) {
                        pair = (Activity[]) map.get(next.getId());
                        groupTwo.setNext( pair[1] );
                    }
                    break;
                case Activity.TYPE_RETURN:
                    ReturnActivity returnOne = (ReturnActivity) one;
                    ReturnActivity returnTwo = (ReturnActivity) two;
                    prev = returnOne.getPrev();
                    if (prev!=null) {
                        pair = (Activity[]) map.get(prev.getId());
                        returnTwo.setPrev( pair[1] );
                    }
                    returnTwo.setGroup( group );
                    break;
                case Activity.TYPE_BRANCH:
                    BranchActivity branchOne = (BranchActivity) one;
                    BranchActivity branchTwo = (BranchActivity) two;
                    prev = branchOne.getPrev();
                    if (prev!=null) {
                        pair = (Activity[]) map.get(prev.getId());
                        branchTwo.setPrev( pair[1] );
                    }
                    JoinActivity join = branchOne.getJoinActivity();
                    pair = (Activity[]) map.get(join.getId());
                    branchTwo.setJoinActivity( (JoinActivity)pair[1] );
                    list = branchOne.getBranches();
                    for (int i=0,n=list.size(); i<n; i++) {
                        pair = (Activity[]) map.get( ((Activity)list.get(i)).getId() );
                        branchTwo.getBranches().add( pair[1] );
                    }//for i
                    break;
                case Activity.TYPE_JOIN:
                    JoinActivity joinOne = (JoinActivity) one;
                    JoinActivity joinTwo = (JoinActivity) two;
                    next = joinOne.getNext();
                    if (next!=null) {
                        pair = (Activity[]) map.get(next.getId());
                        joinTwo.setNext( pair[1] );
                    }
                    BranchActivity branch = joinOne.getBranchActivity();
                    pair = (Activity[]) map.get(branch.getId());
                    joinTwo.setBranchActivity( (BranchActivity)pair[1] );
                    for (Iterator iter1=joinOne.getJoins().iterator(); iter1.hasNext(); ) {
                        pair = (Activity[]) map.get( ((Activity)iter1.next()).getId() );
                        joinTwo.getJoins().add( pair[1] );
                    }//for iter
                    break;
                case Activity.TYPE_SWITCH:
                    SwitchActivity switchOne = (SwitchActivity) one;
                    SwitchActivity switchTwo = (SwitchActivity) two;
                    prev = switchOne.getPrev();
                    if (prev!=null) {
                        pair = (Activity[]) map.get(prev.getId());
                        switchTwo.setPrev( pair[1] );
                    }
                    EndSwitchActivity endSwitch = switchOne.getEndSwitchActivity();
                    pair = (Activity[]) map.get(endSwitch.getId());
                    switchTwo.setEndSwitchActivity( (EndSwitchActivity)pair[1] );
                    list = switchOne.getSwitches();
                    for (int i=0,n=list.size(); i<n; i++) {
                        pair = (Activity[]) map.get( ((Activity)list.get(i)).getId() );
                        switchTwo.getSwitches().add( pair[1] );
                    }//for i
                    break;
                case Activity.TYPE_ENDSWITCH:
                    EndSwitchActivity endSwitchOne = (EndSwitchActivity) one;
                    EndSwitchActivity endSwitchTwo = (EndSwitchActivity) two;
                    next = endSwitchOne.getNext();
                    if (next!=null) {
                        pair = (Activity[]) map.get(next.getId());
                        endSwitchTwo.setNext( pair[1] );
                    }
                    SwitchActivity switche = endSwitchOne.getSwitchActivity();
                    pair = (Activity[]) map.get(switche.getId());
                    endSwitchTwo.setSwitchActivity( (SwitchActivity) pair[1] );
                    for (Iterator iter1=endSwitchOne.getChoices().iterator(); iter1.hasNext(); ) {
                        pair = (Activity[]) map.get( ((Activity)iter1.next()).getId() );
                        endSwitchTwo.getChoices().add( pair[1] );
                    }//for iter
                    break;
                case Activity.TYPE_WHILE:
                    WhileActivity whileOne = (WhileActivity) one;
                    WhileActivity whileTwo = (WhileActivity) two;
                    prev = whileOne.getPrev();
                    if (prev!=null) {
                        pair = (Activity[]) map.get(prev.getId());
                        whileTwo.setPrev( pair[1] );
                    }
                    next = whileOne.getNext();
                    if (next!=null) {
                        pair = (Activity[]) map.get(next.getId());
                        whileTwo.setNext( pair[1] );
                    }
                    LoopActivity loop = whileOne.getLoop();
                    pair = (Activity[]) map.get(loop.getId());
                    whileTwo.setLoop( (LoopActivity) pair[1] );
                    break;
                case Activity.TYPE_LOOP:
                    LoopActivity loopOne = (LoopActivity) one;
                    LoopActivity loopTwo = (LoopActivity) two;
                    prev = loopOne.getPrev();
                    if (prev!=null) {
                        pair = (Activity[]) map.get(prev.getId());
                        loopTwo.setPrev( pair[1] );
                    }
                    next = loopOne.getNext();
                    if (next!=null) {
                        pair = (Activity[]) map.get(next.getId());
                        loopTwo.setNext( pair[1] );
                    }
                    WhileActivity whilst = loopOne.getWhilst();
                    pair = (Activity[]) map.get(whilst.getId());
                    loopTwo.setWhilst( (WhileActivity) pair[1] );
                    break;
                case Activity.TYPE_REPEAT:
                    RepeatActivity repeatOne = (RepeatActivity) one;
                    RepeatActivity repeatTwo = (RepeatActivity) two;
                    prev = repeatOne.getPrev();
                    if (prev!=null) {
                        pair = (Activity[]) map.get(prev.getId());
                        repeatTwo.setPrev( pair[1] );
                    }
                    next = repeatOne.getNext();
                    if (next!=null) {
                        pair = (Activity[]) map.get(next.getId());
                        repeatTwo.setNext( pair[1] );
                    }
                    UntilActivity until = repeatOne.getUntil();
                    pair = (Activity[]) map.get(until.getId());
                    repeatTwo.setUntil( (UntilActivity) pair[1] );
                    break;
                case Activity.TYPE_UNTIL:
                    UntilActivity untilOne = (UntilActivity) one;
                    UntilActivity untilTwo = (UntilActivity) two;
                    prev = untilOne.getPrev();
                    if (prev!=null) {
                        pair = (Activity[]) map.get(prev.getId());
                        untilTwo.setPrev( pair[1] );
                    }
                    next = untilOne.getNext();
                    if (next!=null) {
                        pair = (Activity[]) map.get(next.getId());
                        untilTwo.setNext( pair[1] );
                    }
                    RepeatActivity repeat = untilOne.getRepeat();
                    pair = (Activity[]) map.get(repeat.getId());
                    untilTwo.setRepeat( (RepeatActivity) pair[1] );
                    break;
                case Activity.TYPE_JUMP:
                    JumpActivity jumpOne = (JumpActivity) one;
                    JumpActivity jumpTwo = (JumpActivity) two;
                    prev = jumpOne.getPrev();
                    if (prev!=null) {
                        pair = (Activity[]) map.get(prev.getId());
                        jumpTwo.setPrev( pair[1] );
                    }
                    next = jumpOne.getNext();
                    if (next!=null) {
                        pair = (Activity[]) map.get(next.getId());
                        jumpTwo.setNext( pair[1] );
                    }
                    list = jumpOne.getJumps();
                    for (int i=0,n=list.size(); i<n; i++) {
                        pair = (Activity[]) map.get(((Activity)list.get(i)).getId());
                        jumpTwo.getJumps().add( pair[1] );
                    }//for i
                    break;
                case Activity.TYPE_TERMINATE:
                    TerminateActivity termOne = (TerminateActivity) one;
                    TerminateActivity termTwo = (TerminateActivity) two;
                    prev = termOne.getPrev();
                    if (prev!=null) {
                        pair = (Activity[]) map.get(prev.getId());
                        termTwo.setPrev( pair[1] );
                    }
                    break;
            }//switch
        }//for
        
        FlowPiece piece = new FlowPiece();
        pair = (Activity[]) map.get(head.getId());
        piece.setHead((SingleIn)pair[1]);
        pair = (Activity[]) map.get(tail.getId());
        piece.setTail((SingleOut)pair[1]);
        return piece;
    }//spawn()


}//class Template
