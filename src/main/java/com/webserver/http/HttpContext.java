package com.webserver.http;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * HTTP协议相关信息定义
 * @author ta
 *
 */
public class HttpContext {
	/**
	 * 介质类型映射
	 * key:资源后缀名
	 * value:对应的mime类型定义(Content-Type对应的值)
	 */
	private static Map<String,String> mimeTypeMapping = new HashMap<String,String>();
	/**
	 * 状态代码与描述的映射
	 * key:状态代码
	 * value:状态描述
	 */
	private static Map<Integer,String> statusCodeReasonMapping = new HashMap<Integer,String>();
	
	
	static {
		//初始化静态资源
		
		//1初始化mime类型
		initMimeTypeMapping();
		
		//2初始化状态代码与描述的映射
		initStatusCodeReasonMapping();
	}
	/**
	 * 初始化状态代码与描述的映射
	 */
	private static void initStatusCodeReasonMapping() {
		statusCodeReasonMapping.put(200, "OK");
		statusCodeReasonMapping.put(201, "Created");
		statusCodeReasonMapping.put(202, "Accepted");
		statusCodeReasonMapping.put(204, "No Content");
		statusCodeReasonMapping.put(301, "Moved Permanently");
		statusCodeReasonMapping.put(302, "Moved Temporarily");
		statusCodeReasonMapping.put(304, "Not Modified");
		statusCodeReasonMapping.put(400, "Bad Request");
		statusCodeReasonMapping.put(401, "Unauthorized");
		statusCodeReasonMapping.put(403, "Forbidden");
		statusCodeReasonMapping.put(404, "Not Found");
		statusCodeReasonMapping.put(500, "Internal Server Error");
		statusCodeReasonMapping.put(501, "Not Implemented");
		statusCodeReasonMapping.put(502, "Bad Gateway");
		statusCodeReasonMapping.put(503, "Service Unavailable");
	}
	
	/**
	 * 初始化Mime类型映射
	 */
	private static void initMimeTypeMapping() {
		/*
		 * 初始化过程
		 * 1:解析conf/web.xml
		 * 2:获取该文件中根标签下所有名为<mime-mapping>的
		 *   子标签
		 *   注意，根标签下不止有<mime-mapping>名字的子标签
		 *   还有其他的，所以获取所有子标签时必须指定名字
		 * 3:遍历每一个<mime-mapping>标签:
		 *   将其子标签<extension>中间的文本作为key.
		 *   将其子标签<mime-type>中间的文本作为value.
		 *   存入到mimeTypeMapping这个Map中完成初始化  
		 *   
		 * 解析后该mimeTypeMapping中应当有1000多个元素
		 */
		try {
			SAXReader reader = new SAXReader();
			Document doc = reader.read(new File("conf/web.xml"));
			
			Element root = doc.getRootElement();
			List<Element> mimeList = root.elements("mime-mapping");
			
			for(Element mimeEle : mimeList) {
				String key = mimeEle.elementText("extension");
				String value = mimeEle.elementText("mime-type");
				mimeTypeMapping.put(key, value);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
		
	
	/**
	 * 根据资源后缀获取对应的mime类型
	 * @param ext
	 * @return
	 */
	public static String getMimeType(String ext) {
		return mimeTypeMapping.get(ext);
	}
	/**
	 * 根据给定的状态代码获取其对应的状态描述
	 * @param statusCode
	 * @return
	 */
	public static String getStatusReason(int statusCode) {
		return statusCodeReasonMapping.get(statusCode);
	}
	
	
	public static void main(String[] args) {
//		String fileName = "header.css";
//		//根据文件名的后缀，获取对应的mime类型
//		String ext = fileName.substring(
//			fileName.lastIndexOf(".")+1
//		);		
//		String type = getMimeType(ext);
//		System.out.println(type);// text/css
		
		
		String reason = getStatusReason(404);
		System.out.println(reason);
	}
}









