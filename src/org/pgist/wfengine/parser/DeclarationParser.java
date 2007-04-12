package org.pgist.wfengine.parser;

import java.util.List;
import java.util.Map;

import org.dom4j.Element;
import org.pgist.wfengine.Declaration;


/**
 * Parser for Declaration.
 * 
 * @author kenny
 */
public class DeclarationParser {
    
    
    /**
     * Parse the "var" element under "declaration" element.
     * 
     * @param map the variable declaration will be put in map
     * @param vars a list of "var" xml elements
     * @throws ParserException
     */
    private void parseVars(Map<String, String> map, List<Element> vars) throws ParserException {
        for (Element var : vars) {
            //name
            String name = var.attributeValue("name");
            if (name==null) throw new ParserException("attribute 'name' is required for element 'var'");
            
            name = name.trim();
            if (name.length()==0) throw new ParserException("attribute 'name' is required for element 'var'");
            
            //ref
            String ref = var.attributeValue("ref");
            if (ref==null) ref = name;
            else {
                ref = ref.trim();
                if (ref.length()==0) throw new ParserException("attribute 'ref' can't be empty, but can be omitted which means 'ref' is the same as 'name'");
            }
            
            //now both name and ref are available
            map.put(name, ref);
        }
    }//parseVars()
    
    
    /**
     * Parse a "declaration" xml element.
     * 
     * @param rootElement the "declaration" element
     * @return a newly created Declaration object
     * @throws ParserException
     */
    public Declaration parse(Element rootElement) throws ParserException {
        Declaration declaration = new Declaration();
        
        //ins
        Element ins = rootElement.element("ins");
        if (ins!=null) {
            parseVars(declaration.getIns(), ins.elements("var"));
        }
        
        //outs
        Element outs = rootElement.element("outs");
        if (outs!=null) {
            parseVars(declaration.getOuts(), outs.elements("var"));
        }
        
        //properties
        Element props = rootElement.element("props");
        if (props!=null) {
            for (Element prop : (List<Element>) props.elements("property")) {
                //name
                String name = prop.attributeValue("name");
                if (name==null) throw new ParserException("attribute 'name' is required for element 'property'");
                
                name = name.trim();
                if (name.length()==0) throw new ParserException("attribute 'name' is required for element 'property'");
                
                //value
                String value = prop.attributeValue("value");
                if (value==null) value = "";
                else {
                    value = value.trim();
                }
                
                //now both name and value are available
                declaration.getProperties().put(name, value);
            }//for
        }
        
        return declaration;
    }//parse()
    
    
}//class DeclarationParser
