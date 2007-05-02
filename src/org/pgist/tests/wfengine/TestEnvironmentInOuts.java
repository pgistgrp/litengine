package org.pgist.tests.wfengine;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.pgist.wfengine.Declaration;
import org.pgist.wfengine.EnvironmentInOuts;
import org.pgist.wfengine.RunningContext;


/**
 * Test cases for class EnvironmentInOuts.
 * 
 * @author kenny
 */
public class TestEnvironmentInOuts {
    
    
    @Test
    public void test1() throws Exception {
        Declaration decl = new Declaration();
        decl.getProperties().put("name", "test_name");
        decl.getProperties().put("value", "test_value");
        
        RunningContext context = new RunningContext();
        
        EnvironmentInOuts inOuts = new EnvironmentInOuts(context, decl);
        
        System.out.println("---> "+inOuts.getProperty("name"));
        assertEquals("name is not extracted correctly", "test_name", inOuts.getProperty("name"));
        assertEquals("value is not extracted correctly", "test_value", inOuts.getProperty("value"));
    }//test1()
    
    
}//class TestEnvironmentInOuts
