package com.webserver.servlets;

import java.io.File;
import java.io.RandomAccessFile;

import com.webserver.http.HttpRequest;
import com.webserver.http.HttpResponse;

/**
 * 登录业务
 * @author ta
 *
 */
public class LoginServlet extends HttpServlet{
	public void service(HttpRequest request,HttpResponse response) {
		System.out.println("开始处理登录");
		
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		
		try (
			RandomAccessFile raf
				= new RandomAccessFile("user.dat","r");
		){
			//默认是登录失败
			boolean check = false;
			for(int i=0;i<raf.length()/100;i++) {
				raf.seek(i*100);
				byte[] data = new byte[32];
				raf.read(data);
				String name = new String(data,"UTF-8").trim();
				if(name.equals(username)) {
					//找到此用户,比对密码
					raf.read(data);
					String pwd = new String(data,"UTF-8").trim();
					if(pwd.equals(password)) {
						//验证成功
						check = true;
					}
					break;
				}
			}
			
			if(check) {
				/*
				 * 需要注意，重定向中指定的相对路径是针对浏览器
				 * 的。而浏览器之前的请求路径是请求当前Servlet
				 * 时的路径
				 * http://localhost:8088/myweb/login
				 * 所以当前目录是
				 * http://localhost:8088/myweb/
				 * 对此，我们指定重定向路径如:login_success.html
				 * 浏览器在接收到后，就会自动请求:
				 * http://localhost:8088/myweb/login_success.html
				 */
				//重定向登录成功页面
				response.sendRedirect("login_success.html");
			}else {
				//跳转登录失败页面
				response.sendRedirect("login_fail.html");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		System.out.println("处理登录完毕");
	}
}








