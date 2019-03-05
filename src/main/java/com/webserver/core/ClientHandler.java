package com.webserver.core;

import java.io.File;
import java.io.IOException;
import java.net.Socket;

import com.webserver.http.EmptyRequestException;
import com.webserver.http.HttpRequest;
import com.webserver.http.HttpResponse;
import com.webserver.servlets.HttpServlet;
import com.webserver.servlets.LoginServlet;
import com.webserver.servlets.RegServlet;
import com.webserver.servlets.ShowAllUserServlet;

/**
 * 处理客户端交互
 * 
 * @author ta
 *
 */
public class ClientHandler implements Runnable {
	private Socket socket;

	public ClientHandler(Socket socket) {
		this.socket = socket;
	}

	public void run() {
		try {
			// 1准备工作
			// 1.1解析请求
			HttpRequest request = new HttpRequest(socket);
			// 1.2创建响应
			HttpResponse response = new HttpResponse(socket);

			// 2处理请求
			/*
			 * 2.1:通过request获取请求的资源路径 
			 * 2.2:判断该请求是否为请求一个业务，不是则执行2.3 
			 * 2.3:从webapps目录中根据资源路径位置寻找
			 * 请求的资源是否存在
			 */
			// 2.1
			String uri = request.getRequestURI();
			// 2.2
			// 查看该请求是否为请求注册业务
			String servletName = ServerContext.getServletNameByUri(uri);
			
			if (servletName != null) {
				System.out.println("准备反射：" + servletName);
				
				Class cls = Class.forName(servletName);
				HttpServlet servlet = (HttpServlet) cls.newInstance();
				servlet.service(request, response);

			} else {
				// 2.3
				File file = new File("webapps" + uri);
				// 判断用户请求的资源是否存在?
				if (file.exists()) {
					System.out.println("该资源已找到!");
					// 将该资源响应给客户端
					response.setEntity(file);
				} else {
					System.out.println("该资源不存在!");
					response.setStatusCode(404);
					response.setEntity(new File("webapps/root/404.html"));
				}
			}
			// 3响应客户端
			response.flush();

		} catch (EmptyRequestException e) {
			System.out.println("空请求");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				// 响应完毕后与客户端断开连接!
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
