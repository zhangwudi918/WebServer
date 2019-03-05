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
 * 响应对象
 * @author ta
 *
 */
public class HttpResponse {
	/*
	 * 状态行相关信息
	 */
	//状态代码
	private int statusCode = 200;
	//状态描述
	private String statusReason = "OK";
	
	
	/*
	 * 响应头相关信息
	 */
	//key:响应头名字         value:响应头的值
	private Map<String,String> headers = new HashMap<String,String>();	
	
	
	/*
	 * 响应正文相关信息
	 */
	//响应的实体文件
	private File entity;
	
	//响应正文数据
	private byte[] contentData;
	
	
	
	//和连接相关的信息
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
	 * 将当前响应对象内容按照HTTP响应格式发送给客户端
	 */
	public void flush() {
		/*
		 * 1:发送状态行
		 * 2:发送响应头
		 * 3:发送响应正文
		 */
		sendStatusLine();
		sendHeaders();
		sendContent();
	}
	/**
	 * 发送状态行
	 */
	private void sendStatusLine() {
		String line = "HTTP/1.1"+" "+statusCode+" "+statusReason;
		println(line);
	}
	/**
	 * 发送响应头
	 */
	private void sendHeaders() {		
		/*
		 * 遍历headers，将每个响应头发送给客户端
		 */
		Set<Entry<String,String>> entrySet = headers.entrySet();
		for(Entry<String,String> header : entrySet) {
			String line = header.getKey()+": "+header.getValue();
			println(line);
		}
		
		//单独发送CRLF表示响应头发送完毕
		println("");
	}
	/**
	 * 发送响应正文
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
	 * 向客户端发送一行字符串(发送后会自动发送CRLF结尾)
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
	 * 要求客户端重定向到指定路径
	 * @param url
	 */
	public void sendRedirect(String url) {
		//1设置对应的状态代码
		setStatusCode(302);
		
		//2设置对应的响应头
		headers.put("Location", url);
	}
	
	
	
	
	/*
	 * 属性对应的get，set方法
	 */
	public File getEntity() {
		return entity;
	}
	/**
	 * 设置响应正文的实体文件
	 * 在设置该文件的同时，自动设置对应该正文内容的
	 * 两个响应头:Content-Type,Content-Length
	 * @param entity
	 */
	public void setEntity(File entity) {
		this.entity = entity;
		putHeaderByEntity();
	}
	/**
	 * 根据响应正文的实体文件设置对应的响应头:
	 * Content-Type和Content-Length
	 */
	private void putHeaderByEntity() {
		//Content-Type是告知客户端响应正文的数据类型	
		//Content-Length是告知客户端正文的长度，单位是字节
		String fileName= entity.getName();
		//根据文件名的后缀，获取对应的mime类型
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
	 * 设置状态代码，设置的同时会自动设置该状态代码默认
	 * 对应的状态描述
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










