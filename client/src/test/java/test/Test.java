package test;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class Test {
	public static List<Map<String,Object>> xmltoList(String xml) {  
        try {  
            List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();  
            Document document = DocumentHelper.parseText(xml);  
            Element nodesElement = document.getRootElement();  
            @SuppressWarnings("unchecked")
			List<Map<String, Object>> nodes = nodesElement.elements();  
            for (Iterator<Map<String, Object>> its = nodes.iterator(); its.hasNext();) {  
                Element nodeElement = (Element) its.next();  
                Map<String, Object> map = xmltoMap(nodeElement.asXML());  
                list.add(map);  
                map = null;  
            }  
            nodes = null;  
            nodesElement = null;  
            document = null;  
            return list;  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return null;  
    }
	
	public static Map<String,Object> xmltoMap(String xml) {  
        try {  
            Map<String, Object> map = new HashMap<String, Object>();  
            Document document = DocumentHelper.parseText(xml);  
            Element nodeElement = document.getRootElement();  
            @SuppressWarnings("unchecked")
			List<Map<String,Object>> node = nodeElement.elements();  
            for (Iterator<Map<String, Object>> it = node.iterator(); it.hasNext();) {  
                Element elm = (Element) it.next();  
                //map.put(elm.attributeValue("label"), elm.getText()); 
                map.put(elm.getName(), elm.getText());  
                elm = null;  
            }  
            node = null;  
            nodeElement = null;  
            document = null;  
            return map;  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return null;  
    }  
	public enum Status {
		ERR01("未审核"), ERR02("审核");
		private String name;

		private Status(String s) {
			name = s;
		}

		public String getName() {
			return name;
		}
	}
	
	public static void main(String[] args) {
		double x = -10;
		double y = 10;
		Rectangle rectangle = new Rectangle();
		boolean flag = rectangle.contains(x, y);
		System.out.println(Test.Status.ERR01);
	}
}
