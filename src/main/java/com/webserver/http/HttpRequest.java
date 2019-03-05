package com.webserver.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * �������
 * @author ta
 *
 */
public class HttpRequest {
	/*
	 * �����������Ϣ����
	 */
	//����ķ�ʽ
	private String method;
	//������Դ�ĳ���·��
	private String url;
	//����ʹ�õ�Э��汾
	private String protocol;
	
	//����·��url��"?"������ݣ�û��"?"��ôֵ��urlһ��
	private String requestURI;
	//����·��url��"?"�Ҳ����ݣ�û��"?"��ôֵΪnull
	private String queryString;
	//���ڱ�������ÿһ������,key:������ value:����ֵ
	private Map<String,String> parameter = new HashMap<String,String>();
	
	/*
	 * ��Ϣͷ�����Ϣ����
	 */
	//key:��Ϣͷ������     value:��Ϣͷ��Ӧ��ֵ
	private Map<String,String> headers = new HashMap<String,String>();
	
	/*
	 * ��Ϣ���������Ϣ����
	 */
	private byte[] content;
	
	
	//��������ص�����
	private Socket socket;
	
	private InputStream in;
	/**
	 * ���췽��������HttpRequestʵ����ͬʱҪ����
	 * Socketʵ�����ù��췽����ͨ��Socket��ȡ������
	 * ��ȡ�ͻ��˷��͹������������ݲ���������ɳ�ʼ��
	 * @param socket
	 * @throws EmptyRequestException 
	 */
	public HttpRequest(Socket socket) throws EmptyRequestException {
		try {
			this.socket = socket;
			this.in = socket.getInputStream();
			/*
			 * ����һ������Ĺ���
			 * 1:����������
			 * 2:������Ϣͷ
			 * 3:������Ϣ����
			 */
			parseRequestLine();
			parseHeaders();
			parseContent();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * ����������
	 * @throws EmptyRequestException 
	 */
	private void parseRequestLine() throws EmptyRequestException {
		System.out.println("����������...");
		/*
		 * 1:��ͨ����������ȡ��һ�з��͹������ַ���
		 * 2:���տո��������ַ���������Ӧ�����ֳ�
		 *   ������
		 * 3:�ֱ����������������õ���Ӧ������
		 *   method,url,protocol  
		 */
		try {
			String line = readLine();
			System.out.println("������:"+line);
			//��������������һ���մ������ǿ�����
			if("".equals(line)) {
				throw new EmptyRequestException();
			}
			/*
			 * ������ڿ��ܳ��������±�Խ�磬������Ϊ
			 * HTTPЭ������ͻ��˷��Ϳ�����(ʵ��������
			 * ��ʲôҲû�з��ͣ���ʱ��ֺ�ò�������)
			 * ���ڽ����
			 */
			String[] data = line.split("\\s");
			this.method = data[0];
			this.url = data[1];
			this.protocol = data[2];
			
			//��һ������url
			parseUrl();
			
			System.out.println("method:"+method);
			System.out.println("url:"+url);
			System.out.println("protocol:"+protocol);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("�����н������!");
	}
	/**
	 * ��һ������url
	 */
	private void parseUrl() {
		System.out.println("��һ������url...");
		/*
		 * url���ܴ����������:
		 * �������򲻴��������Ƿ����������ͨ���鿴��ǰ
		 * url���Ƿ���"?"����֪��
		 * 
		 * �����url�����в�������ôֱ�ӽ�url��ֵ������
		 * requestURI���ɡ�
		 * �����в�������ô�Ͱ���"?"��url���Ϊ������
		 * ��һ����("?"�������)��ֵ��requestURI���ԣ�
		 * �ڶ�����("?"�Ҳ�����)��ֵ��queryString����
		 * Ȼ��Ҫ�Բ������ֽ��н�һ������:
		 * ��Ϊ�������ֵĸ�ʽ�ǹ̶���:
		 * name=value&name=value&.....
		 * �������ǿ��Խ������������Ȱ���"&"���Ϊ���ɶΣ�
		 * ÿһ�ε�����Ӧ��Ϊһ��"name=value",Ȼ�����ǽ�
		 * ÿһ�������ٰ���"="���Ϊ�����������ֵ���ֱ�
		 * �����ǵ���key��value���뵽����parameter�����
		 * ���������Ĺ���
		 */
		//1�ж�����·�����Ƿ���"?"
		if(url.indexOf("?")!=-1) {
			//2����"?"��url���Ϊ������
			String[] data = url.split("\\?");
			requestURI = data[0];
			//���ж��ǿ�url��"?"�����Ƿ�������
			if(data.length>1) {
				queryString = data[1];
				//���������ֽ���,��"%XX"������ת��Ϊ��Ӧ�ַ�
				try {
					/*
					 * username=%E8%8C%83%E4%BC%A0%E5%A5%87&password=123456
					 * username=������&password=123456
					 */
					System.out.println("����ǰqueryString:"+queryString);
					queryString = URLDecoder.decode(queryString, "UTF-8");
					System.out.println("�����queryString:"+queryString);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				//3���ÿ������
				parseParameters(queryString);
			}	
		}else {
			requestURI = url;
		}		
		System.out.println("requestURI:"+requestURI);
		System.out.println("queryString:"+queryString);
		System.out.println("parameter:"+parameter);
		
		
		System.out.println("����url���!");
	}
	/**
	 * ��������
	 * ������һ���ַ�������ʽӦ��Ϊ:name=value&name=value&...
	 * @param line
	 */
	private void parseParameters(String line) {
		String[] data = line.split("&");
		for(String paraLine : data) {
			//����"="���������Ϊ������
			String[] paraArr = paraLine.split("=");
			//�жϸò����Ƿ���ֵ
			if(paraArr.length>1) {
				parameter.put(paraArr[0], paraArr[1]);
			}else {
				parameter.put(paraArr[0], null);
			}
		}
	}
	
	
	/**
	 * ������Ϣͷ
	 */
	private void parseHeaders() {
		System.out.println("������Ϣͷ...");
		/*
		 * ���ڽ��������в�����ͨ��readLine������ȡ��
		 * ����ĵ�һ������(����������)����ô���������
		 * ����readLine����ͨ����������ȡ��ÿһ������
		 * Ӧ�ö���һ����Ϣͷ������������ѭ����ȡÿһ��
		 * ���ݣ�ֱ������ֵΪ���ַ���ʱ(���ؿ��ַ���
		 * ˵��������ȡ��һ��CRLF)ֹͣ
		 * Ȼ��ÿ����Ϣͷ����": "���в�֣�������Ϣͷ
		 * ��������Ϊkey������Ϣͷ��ֵ��Ϊvalue���浽
		 * ����headers���Map�С�
		 */
		try {
			String line = null;
			while(true) {
				line = readLine();
				if("".equals(line)) {
					break;
				}
				String[] data = line.split(": ");
				headers.put(data[0], data[1]);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}		
		
		System.out.println("��Ϣͷ:"+headers);
		System.out.println("��Ϣͷ�������...");
	}
	/**
	 * ������Ϣ����
	 * @throws IOException 
	 */
	private void parseContent() throws IOException {
		System.out.println("������Ϣ����...");
		/*
		 * �����жϸ������Ƿ�����Ϣ���ģ��ж������ǿ�
		 * ��ǰ�������Ϣͷ���Ƿ���Content-Length
		 */
		if(headers.containsKey("Content-Length")) {
			/*
			 * ��ȡ��Ϣ���ĵĳ��ȣ���ʵ�ʶ�ȡ��Ӧ���ֽ���
			 */
			int len = Integer.parseInt(
				headers.get("Content-Length")
			);
			byte[] data = new byte[len];
			//���������ݶ�ȡ����
			in.read(data);
			//���õ���Ϣ���Ķ�Ӧ������
			content = data;
			
			//ͨ����Ϣͷ��ȡ����Ϣ���ĵ�����
			String type = headers.get("Content-Type");
			
			/*
			 * ���������ж���ǰ��Ϣ��������
			 */
			//�ж��Ƿ�Ϊ���ύ������
			if("application/x-www-form-urlencoded".equals(type)) {
				/*
				 * ��ô��������һ���ַ��������ݾ���ԭGET�����ύ��
				 * ʱurl��"?"�Ҳ�����
				 */
				String line = new String(content,"ISO8859-1");
				System.out.println("��������:"+line);
				//����
				try {
					line = URLDecoder.decode(line, "UTF-8");
				} catch (Exception e) {
					e.printStackTrace();
				}
				//��һ����������
				parseParameters(line);
				
			}
			
			
			
			
		}
		
		
		
		
		
		System.out.println("��Ϣ���Ľ������!");
	}
	
	/**
	 * ͨ����������ȡ�ͻ��˷��͹�����һ���ַ���
	 * ��CRLF��βΪһ�еĽ��������ص��ַ���������
	 * ����CRLF
	 * @return
	 */
	public String readLine() throws IOException {
		//ÿ�ζ�ȡ�����ֽ�
		int d = -1;
		//c1��ʾ�ϴζ�ȡ���ֽڣ�c2��ʾ���ζ�ȡ���ֽ�
		char c1='a',c2='a';
		//����ƴ�Ӷ�ȡ����һ���ַ���
		StringBuilder builder = new StringBuilder();		
		while((d = in.read())!=-1) {
			c2 = (char)d;
			if(c1==13&&c2==10) {
				break;
			}
			builder.append(c2);
			c1 = c2;
		}
		return builder.toString().trim();
	}
	
	
	
	


	/*
	 * ����Ϊ���Ե�get����
	 * �����е����Բ���Ҫ�����ṩset��������Ϊ�������
	 * ��ʾ���ǿͻ��˷��͹������������ݣ������������
	 * ����Щ���Ը�����ֵ��
	 */
	public String getMethod() {
		return method;
	}


	public String getUrl() {
		return url;
	}


	public String getProtocol() {
		return protocol;
	}
	/**
	 * ���ݸ�������Ϣ�����ֻ�ȡ��Ӧ��Ϣͷ��ֵ
	 * @param name
	 * @return
	 */
	public String getHeader(String name) {
		return headers.get(name);
	}
	public String getRequestURI() {
		return requestURI;
	}
	public String getQueryString() {
		return queryString;
	}
	/**
	 * ���ݸ����Ĳ�������ȡ��Ӧ�Ĳ���ֵ
	 * @param name
	 * @return
	 */
	public String getParameter(String name) {
		return parameter.get(name);
	}
	
	
}










