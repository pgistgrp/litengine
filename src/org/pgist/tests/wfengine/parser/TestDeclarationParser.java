package org.pgist.tests.wfengine.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.dom4j.Document;
import org.junit.Test;
import org.pgist.tests.wfengine.TestHelper;
import org.pgist.wfengine.Declaration;
import org.pgist.wfengine.parser.DeclarationParser;


/**
 * Test case for DeclarationParser.
 * 
 * @author kenny
 */
public class TestDeclarationParser {
    
    
    private DeclarationParser parser = new DeclarationParser();
    
    
    /*
     * ------------------------------------------------------------------------
     */
    
    
    /**
     * Test Case: Declaration is empty.
     * 
     * @throws Exception
     */
    @Test
    public void test1() throws Exception {
        Document doc = TestHelper.getDocument("<declaration/>");
        
        Declaration declaration = parser.parse(doc.getRootElement());
        
        assertNotNull("'declaration' is null", declaration);
        assertNotNull("'ins' is null", declaration.getIns());
        assertNotNull("'outs' is null", declaration.getOuts());
        
        assertTrue("'ins' is not empty", 0==declaration.getIns().size());
        assertTrue("'outs' is not empty", 0==declaration.getOuts().size());
    }//test1()
    
    
    /**
     * Test Case: Declaration contains only "ins".
     * 
     * @throws Exception
     */
    @Test
    public void test2() throws Exception {
        String xml =
            "<declaration>"
            + "    <ins>"
            + "        <var name=\" in_1 \"/>"
            + "        <var name=\" in_2 \" ref=\" parent.in_2 \"/>"
            + "        <var name=\" in_3 \" ref=\" parent.in_3 \"/>"
            + "    </ins>"
            + "</declaration>";
        
        Document doc = TestHelper.getDocument(xml);
        
        Declaration declaration = parser.parse(doc.getRootElement());
        
        assertNotNull("'declaration' is null", declaration);
        assertNotNull("'ins' is null", declaration.getIns());
        assertNotNull("'outs' is null", declaration.getOuts());
        
        //ins
        assertTrue("'ins' is empty", declaration.getIns().size()>0);
        assertTrue("'ins' size incorrect", declaration.getIns().size()==3);
        
        assertTrue("'ins' doesn't contain 'in_1'", declaration.getIns().containsKey("in_1"));
        assertEquals("ref for 'in_1' incorrect", "in_1", declaration.getIns().get("in_1"));
        
        assertTrue("'ins' doesn't contain 'in_2'", declaration.getIns().containsKey("in_2"));
        assertEquals("ref for 'in_2' incorrect", "parent.in_2", declaration.getIns().get("in_2"));
        
        assertTrue("'ins' doesn't contain 'in_3'", declaration.getIns().containsKey("in_3"));
        assertEquals("ref for 'in_3' incorrect", "parent.in_3", declaration.getIns().get("in_3"));
        
        assertTrue("'outs' is not empty", 0==declaration.getOuts().size());
    }//test2()
    
    
    /**
     * Test Case: Declaration contains only "outs".
     * 
     * @throws Exception
     */
    @Test
    public void test3() throws Exception {
        String xml =
            "<declaration>"
            + "    <outs>"
            + "        <var name=\" out_1 \"/>"
            + "        <var name=\" out_2 \" ref=\" parent.out_2 \"/>"
            + "        <var name=\" out_3 \" ref=\" parent.out_3 \"/>"
            + "    </outs>"
            + "</declaration>";
        
        Document doc = TestHelper.getDocument(xml);
        
        Declaration declaration = parser.parse(doc.getRootElement());
        
        assertNotNull("'declaration' is null", declaration);
        assertNotNull("'ins' is null", declaration.getIns());
        assertNotNull("'outs' is null", declaration.getOuts());
        
        assertTrue("'ins' is not empty", 0==declaration.getIns().size());
        
        //outs
        assertTrue("'outs' is empty", declaration.getOuts().size()>0);
        assertTrue("'outs' size incorrect", declaration.getOuts().size()==3);
        
        assertTrue("'outs' doesn't contain 'out_1'", declaration.getOuts().containsKey("out_1"));
        assertEquals("ref for 'out_1' incorrect", "out_1", declaration.getOuts().get("out_1"));
        
        assertTrue("'outs' doesn't contain 'out_2'", declaration.getOuts().containsKey("out_2"));
        assertEquals("ref for 'out_2' incorrect", "parent.out_2", declaration.getOuts().get("out_2"));
        
        assertTrue("'outs' doesn't contain 'out_3'", declaration.getOuts().containsKey("out_3"));
        assertEquals("ref for 'out_3' incorrect", "parent.out_3", declaration.getOuts().get("out_3"));
    }//test3()
    
    
    /**
     * Test Case: Declaration contains both "ins" and "outs".
     * 
     * @throws Exception
     */
    @Test
    public void test4() throws Exception {
        String xml =
            "<declaration>"
            + "    <ins>"
            + "        <var name=\" in_1 \"/>"
            + "        <var name=\" in_2 \" ref=\" parent.in_2 \"/>"
            + "        <var name=\" in_3 \" ref=\" parent.in_3 \"/>"
            + "    </ins>"
            + "    <outs>"
            + "        <var name=\" out_1 \"/>"
            + "        <var name=\" out_2 \" ref=\" parent.out_2 \"/>"
            + "        <var name=\" out_3 \" ref=\" parent.out_3 \"/>"
            + "    </outs>"
            + "</declaration>";
        
        Document doc = TestHelper.getDocument(xml);
        
        Declaration declaration = parser.parse(doc.getRootElement());
        
        assertNotNull("'declaration' is null", declaration);
        assertNotNull("'ins' is null", declaration.getIns());
        assertNotNull("'outs' is null", declaration.getOuts());
        
        //ins
        assertTrue("'ins' size incorrect", declaration.getIns().size()==3);
        
        assertTrue("'ins' doesn't contain 'in_1'", declaration.getIns().containsKey("in_1"));
        assertEquals("ref for 'in_1' incorrect", "in_1", declaration.getIns().get("in_1"));
        
        assertTrue("'ins' doesn't contain 'in_2'", declaration.getIns().containsKey("in_2"));
        assertEquals("ref for 'in_2' incorrect", "parent.in_2", declaration.getIns().get("in_2"));
        
        assertTrue("'ins' doesn't contain 'in_3'", declaration.getIns().containsKey("in_3"));
        assertEquals("ref for 'in_3' incorrect", "parent.in_3", declaration.getIns().get("in_3"));
        
        //outs
        assertTrue("'outs' size incorrect", declaration.getOuts().size()==3);
        
        assertTrue("'outs' doesn't contain 'out_1'", declaration.getOuts().containsKey("out_1"));
        assertEquals("ref for 'out_1' incorrect", "out_1", declaration.getOuts().get("out_1"));
        
        assertTrue("'outs' doesn't contain 'out_2'", declaration.getOuts().containsKey("out_2"));
        assertEquals("ref for 'out_2' incorrect", "parent.out_2", declaration.getOuts().get("out_2"));
        
        assertTrue("'outs' doesn't contain 'out_3'", declaration.getOuts().containsKey("out_3"));
        assertEquals("ref for 'out_3' incorrect", "parent.out_3", declaration.getOuts().get("out_3"));
    }//test4()
    
    
    /**
     * Test Case: Declaration contains fuzzy elements besides of "ins" and "outs".
     *            Fuzzy elements should be ignored.
     * 
     * @throws Exception
     */
    @Test
    public void test5() throws Exception {
        String xml =
            "<declaration>"
            + "    <ins>"
            + "        <var name=\" in_1 \"/>"
            + "        <var name=\" in_2 \" ref=\" parent.in_2 \"/>"
            + "        <var name=\" in_3 \" ref=\" parent.in_3 \"/>"
            + "    </ins>"
            + "    <outs>"
            + "        <var name=\" out_1 \"/>"
            + "        <var name=\" out_2 \" ref=\" parent.out_2 \"/>"
            + "        <var name=\" out_3 \" ref=\" parent.out_3 \"/>"
            + "    </outs>"
            + "    <fuzzy>"
            + "        <fuzzy1>"
            + "            <fuzzy2 name=\"fuzzy\">fuzzy fuzzy</fuzzy2>"
            + "        </fuzzy1>"
            + "    </fuzzy>"
            + "</declaration>";
        
        Document doc = TestHelper.getDocument(xml);
        
        Declaration declaration = parser.parse(doc.getRootElement());
        
        assertNotNull("'declaration' is null", declaration);
        assertNotNull("'ins' is null", declaration.getIns());
        assertNotNull("'outs' is null", declaration.getOuts());
        
        //ins
        assertTrue("'ins' size incorrect", declaration.getIns().size()==3);
        
        assertTrue("'ins' doesn't contain 'in_1'", declaration.getIns().containsKey("in_1"));
        assertEquals("ref for 'in_1' incorrect", "in_1", declaration.getIns().get("in_1"));
        
        assertTrue("'ins' doesn't contain 'in_2'", declaration.getIns().containsKey("in_2"));
        assertEquals("ref for 'in_2' incorrect", "parent.in_2", declaration.getIns().get("in_2"));
        
        assertTrue("'ins' doesn't contain 'in_3'", declaration.getIns().containsKey("in_3"));
        assertEquals("ref for 'in_3' incorrect", "parent.in_3", declaration.getIns().get("in_3"));
        
        //outs
        assertTrue("'outs' size incorrect", declaration.getOuts().size()==3);
        
        assertTrue("'outs' doesn't contain 'out_1'", declaration.getOuts().containsKey("out_1"));
        assertEquals("ref for 'out_1' incorrect", "out_1", declaration.getOuts().get("out_1"));
        
        assertTrue("'outs' doesn't contain 'out_2'", declaration.getOuts().containsKey("out_2"));
        assertEquals("ref for 'out_2' incorrect", "parent.out_2", declaration.getOuts().get("out_2"));
        
        assertTrue("'outs' doesn't contain 'out_3'", declaration.getOuts().containsKey("out_3"));
        assertEquals("ref for 'out_3' incorrect", "parent.out_3", declaration.getOuts().get("out_3"));
    }//test5()
    
    
}//class TestDeclarationParser
