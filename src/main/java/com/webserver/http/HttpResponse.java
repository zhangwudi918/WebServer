package com.webserver.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * ��Ӧ����
 * @author ta
 *
 */
public class HttpResponse {
	/*
	 * ״̬�������Ϣ
	 */
	//״̬����
	private int statusCode = 200;
	//״̬����
	private String statusReason = "OK";
	
	
	/*
	 * ��Ӧͷ�����Ϣ
	 */
	//key:��Ӧͷ����         value:��Ӧͷ��ֵ
	private Map<String,String> headers = new HashMap<String,String>();	
	
	
	/*
	 * ��Ӧ���������Ϣ
	 */
	//��Ӧ��ʵ���ļ�
	private File entity;
	
	//��Ӧ��������
	private byte[] contentData;
	
	
	
	//��������ص���Ϣ
	private Socket socket;
	private OutputStream out;
	
	public HttpResponse(Socket socket) {
		try {
			this.socket = socket;
			this.out = socket.getOutputStream();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * ����ǰ��Ӧ�������ݰ���HTTP��Ӧ��ʽ���͸��ͻ���
	 */
	public void flush() {
		/*
		 * 1:����״̬��
		 * 2:������Ӧͷ
		 * 3:������Ӧ����
		 */
		sendStatusLine();
		sendHeaders();
		sendContent();
	}
	/**
	 * ����״̬��
	 */
	private void sendStatusLine() {
		String line = "HTTP/1.1"+" "+statusCode+" "+statusReason;
		println(line);
	}
	/**
	 * ������Ӧͷ
	 */
	private void sendHeaders() {		
		/*
		 * ����headers����ÿ����Ӧͷ���͸��ͻ���
		 */
		Set<Entry<String,String>> entrySet = headers.entrySet();
		for(Entry<String,String> header : entrySet) {
			String line = header.getKey()+": "+header.getValue();
			println(line);
		}
		
		//��������CRLF��ʾ��Ӧͷ�������
		println("");
	}
	/**
	 * ������Ӧ����
	 */
	private void sendContent() {
		if(contentData != null) {
			try {
				out.write(contentData);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else if(entity!=null) {
			try(
				FileInputStream fis = new FileInputStream(entity);	
			){			
				int len = -1;
				byte[] data = new byte[1024*10];
				while((len = fis.read(data))!=-1) {
					out.write(data, 0, len);
				}				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * ��ͻ��˷���һ���ַ���(���ͺ���Զ�����CRLF��β)
	 * @param line
	 */
	private void println(String line) {
		try {
			out.write(line.getBytes("ISO8859-1"));
			out.write(13);//written CR
			out.write(10);//written LF
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Ҫ��ͻ����ض���ָ��·��
	 * @param url
	 */
	public void sendRedirect(String url) {
		//1���ö�Ӧ��״̬����
		setStatusCode(302);
		
		//2���ö�Ӧ����Ӧͷ
		headers.put("Location", url);
	}
	
	
	
	
	/*
	 * ���Զ�Ӧ��get��set����
	 */
	public File getEntity() {
		return entity;
	}
	/**
	 * ������Ӧ���ĵ�ʵ���ļ�
	 * �����ø��ļ���ͬʱ���Զ����ö�Ӧ���������ݵ�
	 * ������Ӧͷ:Content-Type,Content-Length
	 * @param entity
	 */
	public void setEntity(File entity) {
		this.entity = entity;
		putHeaderByEntity();
	}
	/**
	 * ������Ӧ���ĵ�ʵ���ļ����ö�Ӧ����Ӧͷ:
	 * Content-Type��Content-Length
	 */
	private void putHeaderByEntity() {
		//Content-Type�Ǹ�֪�ͻ�����Ӧ���ĵ���������	
		//Content-Length�Ǹ�֪�ͻ������ĵĳ��ȣ���λ���ֽ�
		String fileName= entity.getName();
		//�����ļ����ĺ�׺����ȡ��Ӧ��mime����
		String ext = fileName.substring(
				fileName.lastIndexOf(".")+1
		);		
		String type = HttpContext.getMimeType(ext);
		headers.put("Content-Type", type);
		headers.put("Content-Length", entity.length()+"");
	}

	public int getStatusCode() {
		return statusCode;
	}
	/**
	 * ����״̬���룬���õ�ͬʱ���Զ����ø�״̬����Ĭ��
	 * ��Ӧ��״̬����
	 * @param statusCode
	 */
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
		this.statusReason = HttpContext.getStatusReason(statusCode);
	}

	public String getStatusReason() {
		return statusReason;
	}

	public void setStatusReason(String statusReason) {
		this.statusReason = statusReason;
	}

	public byte[] getContentData() {
		return contentData;
	}

	public void setContentData(byte[] contentData) {
		this.contentData = contentData;
	}
	
	public void putHeader(String name,String value) {
		this.headers.put(name, value);
	}
	
	
}










