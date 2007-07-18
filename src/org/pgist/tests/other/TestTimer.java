package org.pgist.tests.other;

import java.util.Timer;
import java.util.TimerTask;

import org.junit.Test;


class Task extends TimerTask {
    
    
    @Override
    public void run() {
        System.out.println("@ Task.run()");
    }//run()
    
    
}//class Task


/**
 * 
 * @author kenny
 *
 */
public class TestTimer {
    
    
    @Test
    public void test1() throws Exception {
        Timer timer = new Timer();
        Task task = new Task();
        
        System.out.println("........");
        
        timer.schedule(task, 1000L);//System.currentTimeMillis() + 0000L);
        
        Thread.sleep(10000);
    }//test1()
    
    
}//class TestTimer
