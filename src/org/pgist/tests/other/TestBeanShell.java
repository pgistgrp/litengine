package org.pgist.tests.other;

import java.util.Date;

import org.junit.Test;

import bsh.Interpreter;


/**
 * 
 * @author kenny
 *
 */
public class TestBeanShell {
    
    
    @Test
    public void test1() throws Exception {
        Interpreter interp = new Interpreter();
        interp.set("foo", 5);
        interp.set("date", new Date() ); 
        
        Date date = (Date) interp.get("date");
        
        Object result = interp.eval("50 == foo*10");
        
        System.out.println( date );
        System.out.println( result );
        System.out.println( interp.get("bar") );
        
    }//test1()
    
    
}//class TestBeanShell
