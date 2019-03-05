package com.webserver.http;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * HTTPЭ�������Ϣ����
 * @author ta
 *
 */
public class HttpContext {
	/**
	 * ��������ӳ��
	 * key:��Դ��׺��
	 * value:��Ӧ��mime���Ͷ���(Content-Type��Ӧ��ֵ)
	 */
	private static Map<String,String> mimeTypeMapping = new HashMap<String,String>();
	/**
	 * ״̬������������ӳ��
	 * key:״̬����
	 * value:״̬����
	 */
	private static Map<Integer,String> statusCodeReasonMapping = new HashMap<Integer,String>();
	
	
	static {
		//��ʼ����̬��Դ
		
		//1��ʼ��mime����
		initMimeTypeMapping();
		
		//2��ʼ��״̬������������ӳ��
		initStatusCodeReasonMapping();
	}
	/**
	 * ��ʼ��״̬������������ӳ��
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
	 * ��ʼ��Mime����ӳ��
	 */
	private static void initMimeTypeMapping() {
		/*
		 * ��ʼ������
		 * 1:����conf/web.xml
		 * 2:��ȡ���ļ��и���ǩ��������Ϊ<mime-mapping>��
		 *   �ӱ�ǩ
		 *   ע�⣬����ǩ�²�ֹ��<mime-mapping>���ֵ��ӱ�ǩ
		 *   ���������ģ����Ի�ȡ�����ӱ�ǩʱ����ָ������
		 * 3:����ÿһ��<mime-mapping>��ǩ:
		 *   �����ӱ�ǩ<extension>�м���ı���Ϊkey.
		 *   �����ӱ�ǩ<mime-type>�м���ı���Ϊvalue.
		 *   ���뵽mimeTypeMapping���Map����ɳ�ʼ��  
		 *   
		 * �������mimeTypeMapping��Ӧ����1000���Ԫ��
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
	 * ������Դ��׺��ȡ��Ӧ��mime����
	 * @param ext
	 * @return
	 */
	public static String getMimeType(String ext) {
		return mimeTypeMapping.get(ext);
	}
	/**
	 * ���ݸ�����״̬�����ȡ���Ӧ��״̬����
	 * @param statusCode
	 * @return
	 */
	public static String getStatusReason(int statusCode) {
		return statusCodeReasonMapping.get(statusCode);
	}
	
	
	public static void main(String[] args) {
//		String fileName = "header.css";
//		//�����ļ����ĺ�׺����ȡ��Ӧ��mime����
//		String ext = fileName.substring(
//			fileName.lastIndexOf(".")+1
//		);		
//		String type = getMimeType(ext);
//		System.out.println(type);// text/css
		
		
		String reason = getStatusReason(404);
		System.out.println(reason);
	}
}









