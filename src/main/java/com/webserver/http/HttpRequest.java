package com.webserver.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 请求对象
 * @author ta
 *
 */
public class HttpRequest {
	/*
	 * 请求行相关信息定义
	 */
	//请求的方式
	private String method;
	//请求资源的抽象路径
	private String url;
	//请求使用的协议版本
	private String protocol;
	
	//请求路径url中"?"左侧内容，没有"?"那么值与url一样
	private String requestURI;
	//请求路径url中"?"右侧内容，没有"?"那么值为null
	private String queryString;
	//用于保存具体的每一个参数,key:参数名 value:参数值
	private Map<String,String> parameter = new HashMap<String,String>();
	
	/*
	 * 消息头相关信息定义
	 */
	//key:消息头的名字     value:消息头对应的值
	private Map<String,String> headers = new HashMap<String,String>();
	
	/*
	 * 消息正文相关信息定义
	 */
	private byte[] content;
	
	
	//和连接相关的属性
	private Socket socket;
	
	private InputStream in;
	/**
	 * 构造方法，创建HttpRequest实例的同时要求传入
	 * Socket实例，该构造方法会通过Socket获取输入流
	 * 读取客户端发送过来的请求内容并解析来完成初始化
	 * @param socket
	 * @throws EmptyRequestException 
	 */
	public HttpRequest(Socket socket) throws EmptyRequestException {
		try {
			this.socket = socket;
			this.in = socket.getInputStream();
			/*
			 * 解析一个请求的过程
			 * 1:解析请求行
			 * 2:解析消息头
			 * 3:解析消息正文
			 */
			parseRequestLine();
			parseHeaders();
			parseContent();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 解析请求行
	 * @throws EmptyRequestException 
	 */
	private void parseRequestLine() throws EmptyRequestException {
		System.out.println("解析请求行...");
		/*
		 * 1:先通过输入流读取第一行发送过来的字符串
		 * 2:按照空格拆分这行字符串，这样应当会拆分出
		 *   三部分
		 * 3:分别将这三部分内容设置到对应属性上
		 *   method,url,protocol  
		 */
		try {
			String line = readLine();
			System.out.println("请求行:"+line);
			//若请求行内容是一个空串，则是空请求
			if("".equals(line)) {
				throw new EmptyRequestException();
			}
			/*
			 * 这里后期可能出现数组下标越界，这是因为
			 * HTTP协议允许客户端发送空请求(实际上连接
			 * 后什么也没有发送，这时拆分后得不到三项)
			 * 后期解决。
			 */
			String[] data = line.split("\\s");
			this.method = data[0];
			this.url = data[1];
			this.protocol = data[2];
			
			//进一步解析url
			parseUrl();
			
			System.out.println("method:"+method);
			System.out.println("url:"+url);
			System.out.println("protocol:"+protocol);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("请求行解析完毕!");
	}
	/**
	 * 进一步解析url
	 */
	private void parseUrl() {
		System.out.println("进一步解析url...");
		/*
		 * url可能存在两种情况:
		 * 带参数或不带参数。是否带参数可以通过查看当前
		 * url中是否含有"?"来得知。
		 * 
		 * 如果该url不含有参数，那么直接将url赋值给属性
		 * requestURI即可。
		 * 若含有参数，那么就按照"?"将url拆分为两部分
		 * 第一部分("?"左侧内容)赋值给requestURI属性，
		 * 第二部分("?"右侧内容)赋值给queryString属性
		 * 然后还要对参数部分进行进一步解析:
		 * 因为参数部分的格式是固定的:
		 * name=value&name=value&.....
		 * 所以我们可以将参数部分首先按照"&"拆分为若干段，
		 * 每一段的内容应当为一个"name=value",然后我们将
		 * 每一个参数再按照"="拆分为参数名与参数值，分别
		 * 将他们当做key与value存入到属性parameter中完成
		 * 解析参数的工作
		 */
		//1判断请求路径中是否含有"?"
		if(url.indexOf("?")!=-1) {
			//2按照"?"将url拆分为两部分
			String[] data = url.split("\\?");
			requestURI = data[0];
			//此判断是看url的"?"后面是否有内容
			if(data.length>1) {
				queryString = data[1];
				//将参数部分解码,将"%XX"的内容转换为对应字符
				try {
					/*
					 * username=%E8%8C%83%E4%BC%A0%E5%A5%87&password=123456
					 * username=范传奇&password=123456
					 */
					System.out.println("解码前queryString:"+queryString);
					queryString = URLDecoder.decode(queryString, "UTF-8");
					System.out.println("解码后queryString:"+queryString);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				//3拆分每个参数
				parseParameters(queryString);
			}	
		}else {
			requestURI = url;
		}		
		System.out.println("requestURI:"+requestURI);
		System.out.println("queryString:"+queryString);
		System.out.println("parameter:"+parameter);
		
		
		System.out.println("解析url完毕!");
	}
	/**
	 * 解析参数
	 * 参数是一个字符串，格式应当为:name=value&name=value&...
	 * @param line
	 */
	private void parseParameters(String line) {
		String[] data = line.split("&");
		for(String paraLine : data) {
			//按照"="将参数拆分为两部分
			String[] paraArr = paraLine.split("=");
			//判断该参数是否有值
			if(paraArr.length>1) {
				parameter.put(paraArr[0], paraArr[1]);
			}else {
				parameter.put(paraArr[0], null);
			}
		}
	}
	
	
	/**
	 * 解析消息头
	 */
	private void parseHeaders() {
		System.out.println("解析消息头...");
		/*
		 * 由于解析请求行操作中通过readLine方法读取了
		 * 请求的第一行内容(请求行内容)，那么在这里继续
		 * 调用readLine方法通过输入流读取的每一行内容
		 * 应该都是一个消息头。所以在这里循环读取每一行
		 * 内容，直到返回值为空字符串时(返回空字符串
		 * 说明单独读取了一个CRLF)停止
		 * 然后将每个消息头按照": "进行拆分，并将消息头
		 * 的名字作为key，将消息头的值作为value保存到
		 * 属性headers这个Map中。
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
		
		System.out.println("消息头:"+headers);
		System.out.println("消息头解析完毕...");
	}
	/**
	 * 解析消息正文
	 * @throws IOException 
	 */
	private void parseContent() throws IOException {
		System.out.println("解析消息正文...");
		/*
		 * 首先判断该请求是否含有消息正文，判断依据是看
		 * 当前请求的消息头中是否含有Content-Length
		 */
		if(headers.containsKey("Content-Length")) {
			/*
			 * 获取消息正文的长度，并实际读取对应的字节量
			 */
			int len = Integer.parseInt(
				headers.get("Content-Length")
			);
			byte[] data = new byte[len];
			//将正文内容读取出来
			in.read(data);
			//设置到消息正文对应属性上
			content = data;
			
			//通过消息头获取该消息正文的类型
			String type = headers.get("Content-Type");
			
			/*
			 * 根据类型判定当前消息正文内容
			 */
			//判断是否为表单提交的数据
			if("application/x-www-form-urlencoded".equals(type)) {
				/*
				 * 那么该正文是一行字符串，内容就是原GET请求提交表单
				 * 时url中"?"右侧内容
				 */
				String line = new String(content,"ISO8859-1");
				System.out.println("正文内容:"+line);
				//解码
				try {
					line = URLDecoder.decode(line, "UTF-8");
				} catch (Exception e) {
					e.printStackTrace();
				}
				//进一步解析参数
				parseParameters(line);
				
			}
			
			
			
			
		}
		
		
		
		
		
		System.out.println("消息正文解析完毕!");
	}
	
	/**
	 * 通过输入流读取客户端发送过来的一行字符串
	 * 以CRLF结尾为一行的结束，返回的字符串不含有
	 * 最后的CRLF
	 * @return
	 */
	public String readLine() throws IOException {
		//每次读取到的字节
		int d = -1;
		//c1表示上次读取的字节，c2表示本次读取的字节
		char c1='a',c2='a';
		//用来拼接读取到的一行字符串
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
	 * 以下为属性的get方法
	 * 请求中的属性不需要对外提供set方法，因为请求对象
	 * 表示的是客户端发送过来的请求内容，所以无需外界
	 * 对这些属性赋其他值。
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
	 * 根据给定的消息的名字获取对应消息头的值
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
	 * 根据给定的参数名获取对应的参数值
	 * @param name
	 * @return
	 */
	public String getParameter(String name) {
		return parameter.get(name);
	}
	
	
}










