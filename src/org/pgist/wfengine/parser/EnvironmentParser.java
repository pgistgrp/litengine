package org.pgist.wfengine.parser;

import java.util.List;
import java.util.Map;

import org.dom4j.Element;
import org.pgist.wfengine.Environment;


/**
 * Parser for "environment" element.
 * 
 * @author kenny
 */
public class EnvironmentParser {
    
    
    public Environment parse(Element rootElement) throws ParserException {
        Environment environment = new Environment();
        
        Map<String, String> strValues = environment.getStrValues();
        Map<String, Integer> intValues = environment.getIntValues();
        
        List<Element> vars = rootElement.elements("var");
        
        for (Element var : vars) {
            //type, default is "string"
            String type = var.attributeValue("type");
            if (type==null) type = "string";
            type = type.trim();
            
            //name
            String name = var.attributeValue("name");
            if (name==null) throw new ParserException("attribute 'name' is required for element 'var'");
            
            name = name.trim();
            if (name.length()==0) throw new ParserException("attribute 'name' is required for element 'var'");
            
            //value
            String value = var.attributeValue("value");
            if (value==null) throw new ParserException("attribute 'value' is required for element 'var'");
            
            value = value.trim();
            if (value.length()==0) throw new ParserException("attribute 'value' can't be empty");
            
            //now both name and value are available
            
            if ("string".equalsIgnoreCase(type)) {
                strValues.put(name, value);
            } else if ("integer".equalsIgnoreCase(type)) {
                intValues.put(name, new Integer(value));
            } else {
                throw new ParserException("unknown type '"+type+"'");
            }
        }//for
        
        return environment;
    }//parse()
    
    
}//class EnvironmentParser
