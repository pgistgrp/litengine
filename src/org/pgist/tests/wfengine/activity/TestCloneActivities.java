package org.pgist.tests.wfengine.activity;

import static org.junit.Assert.*;

import java.util.Stack;

import org.junit.Test;
import org.pgist.wfengine.Activity;
import org.pgist.wfengine.activity.BranchActivity;
import org.pgist.wfengine.activity.PAutoGameActivity;
import org.pgist.wfengine.activity.PGameActivity;
import org.pgist.wfengine.activity.PManualGameActivity;
import org.pgist.wfengine.activity.RepeatActivity;
import org.pgist.wfengine.activity.SwitchActivity;
import org.pgist.wfengine.activity.WhileActivity;


/**
 * Test for cloning activities
 * 
 * @author kenny
 */
public class TestCloneActivities {
    
    
    /**
     * Test Case: simple branch clone
     */
    @Test
    public void testCloneBranch1() {
        //organize the model
        BranchActivity branch = new BranchActivity();
        
        PAutoGameActivity pgame = new PAutoGameActivity();
        pgame.setName("pgame 1");
        pgame.setPrev(branch);
        pgame.setNext(branch.getJoinActivity());
        branch.getBranches().add(pgame);
        branch.getJoinActivity().getJoins().add(pgame);
        
        pgame = new PAutoGameActivity();
        pgame.setName("pgame 2");
        pgame.setPrev(branch);
        pgame.setNext(branch.getJoinActivity());
        branch.getBranches().add(pgame);
        branch.getJoinActivity().getJoins().add(pgame);
        
        pgame = new PAutoGameActivity();
        pgame.setName("pgame 3");
        pgame.setPrev(branch);
        pgame.setNext(branch.getJoinActivity());
        branch.getBranches().add(pgame);
        branch.getJoinActivity().getJoins().add(pgame);
        
        //clone the model
        BranchActivity newBranch = branch.clone(null, new Stack<Activity>(), new Stack<Activity>());
        
        //check cloned flow
        
        assertNotNull("new branch is null", newBranch);
        
        PAutoGameActivity one = (PAutoGameActivity) newBranch.getBranches().get(0);
        assertNotNull("new pgame 1 is null", one);
        assertEquals("the first branch is not 'pgame 1'", "pgame 1", one.getName());
        
        PAutoGameActivity two = (PAutoGameActivity) newBranch.getBranches().get(1);
        assertNotNull("new pgame 2 is null", two);
        assertEquals("the second branch is not 'pgame 2'", "pgame 2", two.getName());
        
        PAutoGameActivity three = (PAutoGameActivity) newBranch.getBranches().get(2);
        assertNotNull("new pgame 3 is null", three);
        assertEquals("the third branch is not 'pgame 3'", "pgame 3", three.getName());
        
        assertEquals("new pgame 1 is not connected to branch", newBranch, one.getPrev());
        assertEquals("new pgame 2 is not connected to branch", newBranch, two.getPrev());
        assertEquals("new pgame 3 is not connected to branch", newBranch, three.getPrev());
        
        assertEquals("new pgame 1 is not connected to join", newBranch.getJoinActivity(), one.getNext());
        assertEquals("new pgame 2 is not connected to join", newBranch.getJoinActivity(), two.getNext());
        assertEquals("new pgame 3 is not connected to join", newBranch.getJoinActivity(), three.getNext());
        
        assertEquals("join is not connected to new pgame 1", one, newBranch.getJoinActivity().getJoins().get(0));
        assertEquals("join is not connected to new pgame 2", two, newBranch.getJoinActivity().getJoins().get(1));
        assertEquals("join is not connected to new pgame 3", three, newBranch.getJoinActivity().getJoins().get(2));
        
        assertEquals("failed to get end", newBranch.getJoinActivity(), newBranch.getEnd());
    }//testCloneBranch1()
    
    
    /**
     * Test Case: embedded branch clone
     */
    @Test
    public void testCloneBranch2() {
        //organize the model
        BranchActivity branch1 = new BranchActivity();
        
        PAutoGameActivity pgame1 = new PAutoGameActivity();
        pgame1.setName("pgame 1");
        pgame1.setPrev(branch1);
        pgame1.setNext(branch1.getJoinActivity());
        branch1.getBranches().add(pgame1);
        branch1.getJoinActivity().getJoins().add(pgame1);
        
        PAutoGameActivity pgame2 = new PAutoGameActivity();
        pgame2.setName("pgame 2");
        pgame2.setPrev(branch1);
        pgame2.setNext(branch1.getJoinActivity());
        branch1.getBranches().add(pgame2);
        branch1.getJoinActivity().getJoins().add(pgame2);
        
        BranchActivity branch2 = new BranchActivity();
        branch2.setPrev(branch1);
        branch2.getJoinActivity().setNext(branch1.getJoinActivity());
        branch1.getBranches().add(branch2);
        branch1.getJoinActivity().getJoins().add(branch2.getJoinActivity());
        
        PAutoGameActivity pgame11 = new PAutoGameActivity();
        pgame11.setName("pgame 11");
        pgame11.setPrev(branch2);
        pgame11.setNext(branch2.getJoinActivity());
        branch2.getBranches().add(pgame11);
        branch2.getJoinActivity().getJoins().add(pgame11);
        
        PAutoGameActivity pgame12 = new PAutoGameActivity();
        pgame12.setName("pgame 12");
        pgame12.setPrev(branch2);
        pgame12.setNext(branch2.getJoinActivity());
        branch2.getBranches().add(pgame12);
        branch2.getJoinActivity().getJoins().add(pgame12);
        
        BranchActivity branch3 = new BranchActivity();
        branch3.setPrev(branch2);
        branch3.getJoinActivity().setNext(branch2.getJoinActivity());
        branch2.getBranches().add(branch3);
        branch2.getJoinActivity().getJoins().add(branch3.getJoinActivity());
        
        PAutoGameActivity pgame21 = new PAutoGameActivity();
        pgame21.setName("pgame 21");
        pgame21.setPrev(branch3);
        pgame21.setNext(branch3.getJoinActivity());
        branch3.getBranches().add(pgame21);
        branch3.getJoinActivity().getJoins().add(pgame21);
        
        PAutoGameActivity pgame22 = new PAutoGameActivity();
        pgame22.setName("pgame 22");
        pgame22.setPrev(branch3);
        pgame22.setNext(branch3.getJoinActivity());
        branch3.getBranches().add(pgame22);
        branch3.getJoinActivity().getJoins().add(pgame22);
        
        //clone the model
        branch1 = branch1.clone(null, new Stack<Activity>(), new Stack<Activity>());
        
        //check cloned flow
        
        assertNotNull("new branch is null", branch1);
        
        pgame1 = (PAutoGameActivity) branch1.getBranches().get(0);
        assertNotNull("new pgame 1 is null", pgame1);
        assertEquals("the first branch is not 'pgame 1'", "pgame 1", pgame1.getName());
        assertEquals("new pgame 1 is not connected to branch1", branch1, pgame1.getPrev());
        assertEquals("new pgame 1 is not connected to join", branch1.getJoinActivity(), pgame1.getNext());
        assertEquals("new pgame 1 is not connected to join", pgame1, branch1.getJoinActivity().getJoins().get(0));
        
        pgame2 = (PAutoGameActivity) branch1.getBranches().get(1);
        assertNotNull("new pgame 2 is null", pgame2);
        assertEquals("the second branch is not 'pgame 2'", "pgame 2", pgame2.getName());
        assertEquals("new pgame 2 is not connected to branch1", branch1, pgame2.getPrev());
        assertEquals("new pgame 2 is not connected to join", branch1.getJoinActivity(), pgame2.getNext());
        assertEquals("new pgame 2 is not connected to join", pgame2, branch1.getJoinActivity().getJoins().get(1));
        
        branch2 = (BranchActivity) branch1.getBranches().get(2);
        assertNotNull("branch2 is null", branch2);
        assertEquals("branch2 is not connected to branch1", branch1, branch2.getPrev());
        assertEquals("branch2 is not connected to join", branch1.getJoinActivity(), branch2.getJoinActivity().getNext());
        assertEquals("branch2 is not connected to join", branch2.getJoinActivity(), branch1.getJoinActivity().getJoins().get(2));
        
        pgame11 = (PAutoGameActivity) branch2.getBranches().get(0);
        assertNotNull("new pgame 11 is null", pgame11);
        assertEquals("the first branch is not 'pgame 11'", "pgame 11", pgame11.getName());
        assertEquals("new pgame 11 is not connected to branch2", branch2, pgame11.getPrev());
        assertEquals("new pgame 11 is not connected to join", branch2.getJoinActivity(), pgame11.getNext());
        assertEquals("new pgame 11 is not connected to join", pgame11, branch2.getJoinActivity().getJoins().get(0));
        
        pgame12 = (PAutoGameActivity) branch2.getBranches().get(1);
        assertNotNull("new pgame 12 is null", pgame12);
        assertEquals("the second branch is not 'pgame 12'", "pgame 12", pgame12.getName());
        assertEquals("new pgame 12 is not connected to branch2", branch2, pgame12.getPrev());
        assertEquals("new pgame 12 is not connected to join", branch2.getJoinActivity(), pgame12.getNext());
        assertEquals("new pgame 12 is not connected to join", pgame12, branch2.getJoinActivity().getJoins().get(1));
        
        branch3 = (BranchActivity) branch2.getBranches().get(2);
        assertNotNull("branch3 is null", branch3);
        assertEquals("branch3 is not connected to branch2", branch2, branch3.getPrev());
        assertEquals("branch3 is not connected to join", branch2.getJoinActivity(), branch3.getJoinActivity().getNext());
        assertEquals("branch3 is not connected to join", branch3.getJoinActivity(), branch2.getJoinActivity().getJoins().get(2));
        
        pgame21 = (PAutoGameActivity) branch3.getBranches().get(0);
        assertNotNull("new pgame 21 is null", pgame21);
        assertEquals("the first branch is not 'pgame 21'", "pgame 21", pgame21.getName());
        assertEquals("new pgame 21 is not connected to branch3", branch3, pgame21.getPrev());
        assertEquals("new pgame 21 is not connected to join", branch3.getJoinActivity(), pgame21.getNext());
        assertEquals("new pgame 21 is not connected to join", pgame21, branch3.getJoinActivity().getJoins().get(0));
        
        pgame22 = (PAutoGameActivity) branch3.getBranches().get(1);
        assertNotNull("new pgame 22 is null", pgame22);
        assertEquals("the second branch is not 'pgame 22'", "pgame 22", pgame22.getName());
        assertEquals("new pgame 22 is not connected to branch3", branch3, pgame22.getPrev());
        assertEquals("new pgame 22 is not connected to join", branch3.getJoinActivity(), pgame22.getNext());
        assertEquals("new pgame 22 is not connected to join", pgame22, branch3.getJoinActivity().getJoins().get(1));
        
        assertEquals("failed to get end", branch1.getJoinActivity(), branch1.getEnd());
    }//testCloneBranch2()
    
    
    /**
     * Test Case: simple switch clone
     */
    @Test
    public void testCloneSwitch1() {
        //organize the model
        SwitchActivity swt = new SwitchActivity();
        
        PAutoGameActivity pgame = new PAutoGameActivity();
        pgame.setName("pgame 1");
        pgame.setPrev(swt);
        pgame.setNext(swt.getEndSwitchActivity());
        swt.getSwitches().add(pgame);
        swt.getEndSwitchActivity().getChoices().add(pgame);
        
        pgame = new PAutoGameActivity();
        pgame.setName("pgame 2");
        pgame.setPrev(swt);
        pgame.setNext(swt.getEndSwitchActivity());
        swt.getSwitches().add(pgame);
        swt.getEndSwitchActivity().getChoices().add(pgame);
        
        pgame = new PAutoGameActivity();
        pgame.setName("pgame 3");
        pgame.setPrev(swt);
        pgame.setNext(swt.getEndSwitchActivity());
        swt.getSwitches().add(pgame);
        swt.getEndSwitchActivity().getChoices().add(pgame);
        
        //clone the model
        SwitchActivity newSwt = swt.clone(null, new Stack<Activity>(), new Stack<Activity>());
        
        //check cloned flow
        
        assertNotNull("new switch is null", newSwt);
        
        PAutoGameActivity one = (PAutoGameActivity) newSwt.getSwitches().get(0);
        assertNotNull("new pgame 1 is null", one);
        assertEquals("the first swt is not 'pgame 1'", "pgame 1", one.getName());
        
        PAutoGameActivity two = (PAutoGameActivity) newSwt.getSwitches().get(1);
        assertNotNull("new pgame 2 is null", two);
        assertEquals("the second swt is not 'pgame 2'", "pgame 2", two.getName());
        
        PAutoGameActivity three = (PAutoGameActivity) newSwt.getSwitches().get(2);
        assertNotNull("new pgame 3 is null", three);
        assertEquals("the third swt is not 'pgame 3'", "pgame 3", three.getName());
        
        assertEquals("new pgame 1 is not connected to swt", newSwt, one.getPrev());
        assertEquals("new pgame 2 is not connected to swt", newSwt, two.getPrev());
        assertEquals("new pgame 3 is not connected to swt", newSwt, three.getPrev());
        
        assertEquals("new pgame 1 is not connected to endswt", newSwt.getEndSwitchActivity(), one.getNext());
        assertEquals("new pgame 2 is not connected to endswt", newSwt.getEndSwitchActivity(), two.getNext());
        assertEquals("new pgame 3 is not connected to endswt", newSwt.getEndSwitchActivity(), three.getNext());
        
        assertEquals("endswt is not connected to new pgame 1", one, newSwt.getEndSwitchActivity().getChoices().get(0));
        assertEquals("endswt is not connected to new pgame 2", two, newSwt.getEndSwitchActivity().getChoices().get(1));
        assertEquals("endswt is not connected to new pgame 3", three, newSwt.getEndSwitchActivity().getChoices().get(2));
        
        assertEquals("failed to get end", newSwt.getEndSwitchActivity(), newSwt.getEnd());
    }//testCloneSwitch1()
    
    
    /**
     * Test Case: embedded switch clone
     */
    @Test
    public void testCloneSwitch2() {
        //organize the model
        SwitchActivity swt1 = new SwitchActivity();
        
        PAutoGameActivity pgame1 = new PAutoGameActivity();
        pgame1.setName("pgame 1");
        pgame1.setPrev(swt1);
        pgame1.setNext(swt1.getEndSwitchActivity());
        swt1.getSwitches().add(pgame1);
        swt1.getEndSwitchActivity().getChoices().add(pgame1);
        
        PAutoGameActivity pgame2 = new PAutoGameActivity();
        pgame2.setName("pgame 2");
        pgame2.setPrev(swt1);
        pgame2.setNext(swt1.getEndSwitchActivity());
        swt1.getSwitches().add(pgame2);
        swt1.getEndSwitchActivity().getChoices().add(pgame2);
        
        SwitchActivity swt2 = new SwitchActivity();
        swt2.setPrev(swt1);
        swt2.getEndSwitchActivity().setNext(swt1.getEndSwitchActivity());
        swt1.getSwitches().add(swt2);
        swt1.getEndSwitchActivity().getChoices().add(swt2.getEndSwitchActivity());
        
        PAutoGameActivity pgame11 = new PAutoGameActivity();
        pgame11.setName("pgame 11");
        pgame11.setPrev(swt2);
        pgame11.setNext(swt2.getEndSwitchActivity());
        swt2.getSwitches().add(pgame11);
        swt2.getEndSwitchActivity().getChoices().add(pgame11);
        
        PAutoGameActivity pgame12 = new PAutoGameActivity();
        pgame12.setName("pgame 12");
        pgame12.setPrev(swt2);
        pgame12.setNext(swt2.getEndSwitchActivity());
        swt2.getSwitches().add(pgame12);
        swt2.getEndSwitchActivity().getChoices().add(pgame12);
        
        SwitchActivity swt3 = new SwitchActivity();
        swt3.setPrev(swt2);
        swt3.getEndSwitchActivity().setNext(swt2.getEndSwitchActivity());
        swt2.getSwitches().add(swt3);
        swt2.getEndSwitchActivity().getChoices().add(swt3.getEndSwitchActivity());
        
        PAutoGameActivity pgame21 = new PAutoGameActivity();
        pgame21.setName("pgame 21");
        pgame21.setPrev(swt3);
        pgame21.setNext(swt3.getEndSwitchActivity());
        swt3.getSwitches().add(pgame21);
        swt3.getEndSwitchActivity().getChoices().add(pgame21);
        
        PAutoGameActivity pgame22 = new PAutoGameActivity();
        pgame22.setName("pgame 22");
        pgame22.setPrev(swt3);
        pgame22.setNext(swt3.getEndSwitchActivity());
        swt3.getSwitches().add(pgame22);
        swt3.getEndSwitchActivity().getChoices().add(pgame22);
        
        //clone the model
        swt1 = swt1.clone(null, new Stack<Activity>(), new Stack<Activity>());
        
        //check cloned flow
        
        assertNotNull("new swt is null", swt1);
        
        pgame1 = (PAutoGameActivity) swt1.getSwitches().get(0);
        assertNotNull("new pgame 1 is null", pgame1);
        assertEquals("the first swt is not 'pgame 1'", "pgame 1", pgame1.getName());
        assertEquals("new pgame 1 is not connected to swt1", swt1, pgame1.getPrev());
        assertEquals("new pgame 1 is not connected to join", swt1.getEndSwitchActivity(), pgame1.getNext());
        assertEquals("new pgame 1 is not connected to join", pgame1, swt1.getEndSwitchActivity().getChoices().get(0));
        
        pgame2 = (PAutoGameActivity) swt1.getSwitches().get(1);
        assertNotNull("new pgame 2 is null", pgame2);
        assertEquals("the second swt is not 'pgame 2'", "pgame 2", pgame2.getName());
        assertEquals("new pgame 2 is not connected to swt1", swt1, pgame2.getPrev());
        assertEquals("new pgame 2 is not connected to endswt1", swt1.getEndSwitchActivity(), pgame2.getNext());
        assertEquals("new pgame 2 is not connected to endswt1", pgame2, swt1.getEndSwitchActivity().getChoices().get(1));
        
        swt2 = (SwitchActivity) swt1.getSwitches().get(2);
        assertNotNull("swt2 is null", swt2);
        assertEquals("swt2 is not connected to swt1", swt1, swt2.getPrev());
        assertEquals("swt2 is not connected to endswt1", swt1.getEndSwitchActivity(), swt2.getEndSwitchActivity().getNext());
        assertEquals("swt2 is not connected to endswt1", swt2.getEndSwitchActivity(), swt1.getEndSwitchActivity().getChoices().get(2));
        
        pgame11 = (PAutoGameActivity) swt2.getSwitches().get(0);
        assertNotNull("new pgame 11 is null", pgame11);
        assertEquals("the first swt is not 'pgame 11'", "pgame 11", pgame11.getName());
        assertEquals("new pgame 11 is not connected to swt2", swt2, pgame11.getPrev());
        assertEquals("new pgame 11 is not connected to endswt2", swt2.getEndSwitchActivity(), pgame11.getNext());
        assertEquals("new pgame 11 is not connected to endswt2", pgame11, swt2.getEndSwitchActivity().getChoices().get(0));
        
        pgame12 = (PAutoGameActivity) swt2.getSwitches().get(1);
        assertNotNull("new pgame 12 is null", pgame12);
        assertEquals("the second swt is not 'pgame 12'", "pgame 12", pgame12.getName());
        assertEquals("new pgame 12 is not connected to swt2", swt2, pgame12.getPrev());
        assertEquals("new pgame 12 is not connected to endswt2", swt2.getEndSwitchActivity(), pgame12.getNext());
        assertEquals("new pgame 12 is not connected to endswt2", pgame12, swt2.getEndSwitchActivity().getChoices().get(1));
        
        swt3 = (SwitchActivity) swt2.getSwitches().get(2);
        assertNotNull("swt3 is null", swt3);
        assertEquals("swt3 is not connected to swt2", swt2, swt3.getPrev());
        assertEquals("swt3 is not connected to endswt2", swt2.getEndSwitchActivity(), swt3.getEndSwitchActivity().getNext());
        assertEquals("swt3 is not connected to endswt2", swt3.getEndSwitchActivity(), swt2.getEndSwitchActivity().getChoices().get(2));
        
        pgame21 = (PAutoGameActivity) swt3.getSwitches().get(0);
        assertNotNull("new pgame 21 is null", pgame21);
        assertEquals("the first swt is not 'pgame 21'", "pgame 21", pgame21.getName());
        assertEquals("new pgame 21 is not connected to swt3", swt3, pgame21.getPrev());
        assertEquals("new pgame 21 is not connected to endswt3", swt3.getEndSwitchActivity(), pgame21.getNext());
        assertEquals("new pgame 21 is not connected to endswt3", pgame21, swt3.getEndSwitchActivity().getChoices().get(0));
        
        pgame22 = (PAutoGameActivity) swt3.getSwitches().get(1);
        assertNotNull("new pgame 22 is null", pgame22);
        assertEquals("the second swt is not 'pgame 22'", "pgame 22", pgame22.getName());
        assertEquals("new pgame 22 is not connected to swt3", swt3, pgame22.getPrev());
        assertEquals("new pgame 22 is not connected to endswt3", swt3.getEndSwitchActivity(), pgame22.getNext());
        assertEquals("new pgame 22 is not connected to endswt3", pgame22, swt3.getEndSwitchActivity().getChoices().get(1));
        
        assertEquals("failed to get end", swt1.getEndSwitchActivity(), swt1.getEnd());
    }//testCloneSwitch2()
    
    
    /**
     * Test Case: simple while clone
     */
    @Test
    public void testCloneWhile1() {
        //organize the model
        WhileActivity whilst = new WhileActivity();
        
        PAutoGameActivity pgame1 = new PAutoGameActivity();
        whilst.setNext(pgame1);
        pgame1.setName("pgame 1");
        pgame1.setDescription("pgame 1");
        pgame1.setPrev(whilst);
        
        PManualGameActivity pgame2 = new PManualGameActivity();
        pgame1.setNext(pgame2);
        pgame2.setName("pgame 2");
        pgame2.setDescription("pgame 2");
        pgame2.setPrev(pgame1);
        pgame2.setNext(whilst.getLoop());
        whilst.getLoop().setPrev(pgame2);
        
        PManualGameActivity pgame3 = new PManualGameActivity();
        whilst.getLoop().setNext(pgame3);
        pgame3.setName("pgame 3");
        pgame3.setDescription("pgame 3");
        pgame3.setPrev(whilst.getLoop());
        
        //clone the model
        whilst = whilst.clone(null, new Stack<Activity>(), new Stack<Activity>());
        
        //check cloned flow
        
        assertNotNull("new while is null", whilst);
        
        pgame1 = (PAutoGameActivity) whilst.getNext();
        assertNotNull("new pgame 1 is null", pgame1);
        assertEquals("pgame is not 'pgame 1'", "pgame 1", pgame1.getName());
        assertEquals("pgame 1 is not connected to while", whilst, pgame1.getPrev());
        
        pgame2 = (PManualGameActivity) pgame1.getNext();
        assertNotNull("new pgame 2 is null", pgame2);
        assertEquals("pgame 2 is not connected to pgame 1", pgame2, pgame1.getNext());
        assertEquals("pgame 2 is not connected to pgame 1", pgame1, pgame2.getPrev());
        assertEquals("pgame 2 is not connected to loop", pgame2, whilst.getLoop().getPrev());
        assertEquals("pgame 2 is not connected to loop", whilst.getLoop(), pgame2.getNext());
        
        pgame3 = (PManualGameActivity) whilst.getLoop().getNext();
        assertNotNull("new pgame 3 is null", pgame3);
        assertEquals("pgame 3 is not connected to loop", whilst.getLoop(), pgame3.getPrev());
        assertEquals("pgame 3 is not connected to loop", pgame3, whilst.getLoop().getNext());
        
        assertEquals("failed to get end", pgame3, whilst.getEnd());
    }//testCloneWhile1()
    
    
    /**
     * Test Case: embedded while clone
     */
    @Test
    public void testCloneWhile2() {
        //organize the model
        WhileActivity whilst1 = new WhileActivity();
        
        PAutoGameActivity pgame1 = new PAutoGameActivity();
        whilst1.setNext(pgame1);
        pgame1.setPrev(whilst1);
        pgame1.setName("pgame 1");
        pgame1.setDescription("pgame 1");
        
        WhileActivity whilst2 = new WhileActivity();
        pgame1.setNext(whilst2);
        whilst2.setPrev(pgame1);
        whilst2.getLoop().setNext(whilst1.getLoop());
        whilst1.getLoop().setPrev(whilst2);
        
        PManualGameActivity pgame2 = new PManualGameActivity();
        whilst2.setNext(pgame2);
        pgame2.setPrev(whilst2);
        pgame2.setName("pgame 2");
        pgame2.setDescription("pgame 2");
        
        WhileActivity whilst3 = new WhileActivity();
        pgame2.setNext(whilst3);
        whilst3.setPrev(pgame2);
        whilst2.getLoop().setPrev(whilst3);
        whilst3.getLoop().setNext(whilst2.getLoop());
        
        PAutoGameActivity pgame3 = new PAutoGameActivity();
        whilst3.setNext(pgame3);
        pgame3.setPrev(whilst3);
        pgame3.setName("pgame 3");
        pgame3.setDescription("pgame 3");
        pgame3.setNext(whilst3.getLoop());
        whilst3.getLoop().setPrev(pgame3);
        
        PManualGameActivity pgame4 = new PManualGameActivity();
        whilst1.getLoop().setNext(pgame4);
        pgame4.setPrev(whilst3.getLoop());
        pgame4.setName("pgame 4");
        pgame4.setDescription("pgame 4");
        
        //clone the model
        whilst1 = whilst1.clone(null, new Stack<Activity>(), new Stack<Activity>());
        
        //check cloned flow
        
        assertNotNull("whilst1 is null", whilst1);
        
        pgame1 = (PAutoGameActivity) whilst1.getNext();
        assertNotNull("pgame1 is null", pgame1);
        assertEquals("pgame is not 'pgame 1'", "pgame 1", pgame1.getName());
        assertEquals("pgame 1 is not connected to while1", whilst1, pgame1.getPrev());
        
        whilst2 = (WhileActivity) pgame1.getNext();
        assertNotNull("whilst2 is null", whilst2);
        assertEquals("pgame 1 is not connected to while2", whilst2, pgame1.getNext());
        assertEquals("pgame 1 is not connected to while2", pgame1, whilst2.getPrev());
        
        pgame2 = (PManualGameActivity) whilst2.getNext();
        assertNotNull("pgame2 is null", pgame2);
        assertEquals("pgame is not 'pgame 2'", "pgame 2", pgame2.getName());
        assertEquals("pgame 1 is not connected to while2", whilst2, pgame2.getPrev());
        assertEquals("pgame 1 is not connected to while2", pgame2, whilst2.getNext());
        
        whilst3 = (WhileActivity) pgame2.getNext();
        assertNotNull("whilst3 is null", whilst3);
        assertEquals("pgame 2 is not connected to while3", whilst3, pgame2.getNext());
        assertEquals("pgame 2 is not connected to while3", pgame2, whilst3.getPrev());
        
        pgame3 = (PAutoGameActivity) whilst3.getNext();
        assertNotNull("pgame3 is null", pgame3);
        assertEquals("pgame is not 'pgame 3'", "pgame 3", pgame3.getName());
        assertEquals("pgame 3 is not connected to while3", whilst3, pgame3.getPrev());
        assertEquals("pgame 3 is not connected to while3", pgame3, whilst3.getNext());
        
        pgame4 = (PManualGameActivity) whilst1.getLoop().getNext();
        assertNotNull("pgame4 is null", pgame4);
        assertEquals("pgame is not 'pgame 4'", "pgame 4", pgame4.getName());
        assertEquals("pgame 4 is not connected to loop1", whilst1.getLoop(), pgame4.getPrev());
        
        assertEquals("failed to get end", pgame4, whilst1.getEnd());
    }//testCloneWhile2()
    
    
    /**
     * Test Case: simple repeat clone
     */
    @Test
    public void testCloneRepeat1() {
        //organize the model
        RepeatActivity repeat = new RepeatActivity();
        
        PAutoGameActivity pgame1 = new PAutoGameActivity();
        repeat.setNext(pgame1);
        pgame1.setName("pgame 1");
        pgame1.setDescription("pgame 1");
        pgame1.setPrev(repeat);
        
        PManualGameActivity pgame2 = new PManualGameActivity();
        pgame1.setNext(pgame2);
        pgame2.setName("pgame 2");
        pgame2.setDescription("pgame 2");
        pgame2.setPrev(pgame1);
        pgame2.setNext(repeat.getUntil());
        repeat.getUntil().setPrev(pgame2);
        
        PManualGameActivity pgame3 = new PManualGameActivity();
        repeat.getUntil().setNext(pgame3);
        pgame3.setName("pgame 3");
        pgame3.setDescription("pgame 3");
        pgame3.setPrev(repeat.getUntil());
        
        //clone the model
        repeat = repeat.clone(null, new Stack<Activity>(), new Stack<Activity>());
        
        //check cloned flow
        
        assertNotNull("new while is null", repeat);
        
        pgame1 = (PAutoGameActivity) repeat.getNext();
        assertNotNull("new pgame 1 is null", pgame1);
        assertEquals("pgame is not 'pgame 1'", "pgame 1", pgame1.getName());
        assertEquals("pgame 1 is not connected to while", repeat, pgame1.getPrev());
        
        pgame2 = (PManualGameActivity) pgame1.getNext();
        assertNotNull("new pgame 2 is null", pgame2);
        assertEquals("pgame 2 is not connected to pgame 1", pgame2, pgame1.getNext());
        assertEquals("pgame 2 is not connected to pgame 1", pgame1, pgame2.getPrev());
        assertEquals("pgame 2 is not connected to loop", pgame2, repeat.getUntil().getPrev());
        assertEquals("pgame 2 is not connected to loop", repeat.getUntil(), pgame2.getNext());
        
        pgame3 = (PManualGameActivity) repeat.getUntil().getNext();
        assertNotNull("new pgame 3 is null", pgame3);
        assertEquals("pgame 3 is not connected to loop", repeat.getUntil(), pgame3.getPrev());
        assertEquals("pgame 3 is not connected to loop", pgame3, repeat.getUntil().getNext());
        
        assertEquals("failed to get end", pgame3, repeat.getEnd());
    }//testCloneRepeat1()
    
    
    /**
     * Test Case: embedded while clone
     */
    @Test
    public void testCloneRepeat2() {
        //organize the model
        RepeatActivity repeat1 = new RepeatActivity();
        
        PAutoGameActivity pgame1 = new PAutoGameActivity();
        repeat1.setNext(pgame1);
        pgame1.setPrev(repeat1);
        pgame1.setName("pgame 1");
        pgame1.setDescription("pgame 1");
        
        RepeatActivity repeat2 = new RepeatActivity();
        pgame1.setNext(repeat2);
        repeat2.setPrev(pgame1);
        repeat2.getUntil().setNext(repeat1.getUntil());
        repeat1.getUntil().setPrev(repeat2);
        
        PManualGameActivity pgame2 = new PManualGameActivity();
        repeat2.setNext(pgame2);
        pgame2.setPrev(repeat2);
        pgame2.setName("pgame 2");
        pgame2.setDescription("pgame 2");
        
        RepeatActivity repeat3 = new RepeatActivity();
        pgame2.setNext(repeat3);
        repeat3.setPrev(pgame2);
        repeat2.getUntil().setPrev(repeat3);
        repeat3.getUntil().setNext(repeat2.getUntil());
        
        PAutoGameActivity pgame3 = new PAutoGameActivity();
        repeat3.setNext(pgame3);
        pgame3.setPrev(repeat3);
        pgame3.setName("pgame 3");
        pgame3.setDescription("pgame 3");
        pgame3.setNext(repeat3.getUntil());
        repeat3.getUntil().setPrev(pgame3);
        
        PManualGameActivity pgame4 = new PManualGameActivity();
        repeat1.getUntil().setNext(pgame4);
        pgame4.setPrev(repeat3.getUntil());
        pgame4.setName("pgame 4");
        pgame4.setDescription("pgame 4");
        
        //clone the model
        repeat1 = repeat1.clone(null, new Stack<Activity>(), new Stack<Activity>());
        
        //check cloned flow
        
        assertNotNull("whilst1 is null", repeat1);
        
        pgame1 = (PAutoGameActivity) repeat1.getNext();
        assertNotNull("pgame1 is null", pgame1);
        assertEquals("pgame is not 'pgame 1'", "pgame 1", pgame1.getName());
        assertEquals("pgame 1 is not connected to while1", repeat1, pgame1.getPrev());
        
        repeat2 = (RepeatActivity) pgame1.getNext();
        assertNotNull("repeat2 is null", repeat2);
        assertEquals("pgame 1 is not connected to while2", repeat2, pgame1.getNext());
        assertEquals("pgame 1 is not connected to while2", pgame1, repeat2.getPrev());
        
        pgame2 = (PManualGameActivity) repeat2.getNext();
        assertNotNull("pgame2 is null", pgame2);
        assertEquals("pgame is not 'pgame 2'", "pgame 2", pgame2.getName());
        assertEquals("pgame 1 is not connected to while2", repeat2, pgame2.getPrev());
        assertEquals("pgame 1 is not connected to while2", pgame2, repeat2.getNext());
        
        repeat3 = (RepeatActivity) pgame2.getNext();
        assertNotNull("repeat3 is null", repeat3);
        assertEquals("pgame 2 is not connected to while3", repeat3, pgame2.getNext());
        assertEquals("pgame 2 is not connected to while3", pgame2, repeat3.getPrev());
        
        pgame3 = (PAutoGameActivity) repeat3.getNext();
        assertNotNull("pgame3 is null", pgame3);
        assertEquals("pgame is not 'pgame 3'", "pgame 3", pgame3.getName());
        assertEquals("pgame 3 is not connected to while3", repeat3, pgame3.getPrev());
        assertEquals("pgame 3 is not connected to while3", pgame3, repeat3.getNext());
        
        pgame4 = (PManualGameActivity) repeat1.getUntil().getNext();
        assertNotNull("pgame4 is null", pgame4);
        assertEquals("pgame is not 'pgame 4'", "pgame 4", pgame4.getName());
        assertEquals("pgame 4 is not connected to loop1", repeat1.getUntil(), pgame4.getPrev());
        
        assertEquals("failed to get end", pgame4, repeat1.getEnd());
    }//testCloneRepeat2()
    
    
}//TestCloneActivities
