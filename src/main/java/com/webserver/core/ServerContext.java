package com.webserver.core;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/*
 * ����������Ϣ����
 */
public class ServerContext {
	private static Map<String, String> servletMapping = new HashMap<String, String>();

	static {
		initServletMapping();
	}
	/*
	 * Servletӳ��
	 *  key����Ӧ��uri·�� 
	 *  value������������Servlet������
	 */
	
	
	private static void initServletMapping() {
	SAXReader reader=new SAXReader();
	try {
		Document doc=reader.read(new FileInputStream("conf/servlets.xml"));
		Element root=doc.getRootElement();
		List<Element> list=root.elements("servlet");
		for(Element e:list) {
			servletMapping.put(e.attributeValue("uri"),e.attributeValue("class"));
		}
	
	} catch (FileNotFoundException | DocumentException e) {
		e.printStackTrace();
	}
	
	

	}
	
	public static String getServletNameByUri(String uri) {
		return servletMapping.get(uri);
	}
	
}
