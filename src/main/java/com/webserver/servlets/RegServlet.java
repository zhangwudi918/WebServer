package com.webserver.servlets;

import java.io.RandomAccessFile;
import java.util.Arrays;

import com.webserver.http.HttpRequest;
import com.webserver.http.HttpResponse;

/**
 * 处理注册业务
 * @author ta
 *
 */
public class RegServlet extends HttpServlet {
	/**
	 * 处理具体业务操作的方法
	 */
	public void service(HttpRequest request,HttpResponse response) {
		/*
		 * 注册业务流程:
		 * 1:通过HttpRequest获取用户提交的表单信息
		 * 2:将该信息写入文件保存
		 * 3:响应客户端注册结果的页面
		 */
		try(
			RandomAccessFile raf 
				= new RandomAccessFile("user.dat","rw")	
		) {
			System.out.println("RegServlet:开始处理注册");
			//1
			/*
			 * 通过request.getParameter获取表单提交的数据。
			 * 这里传递的参数应当与reg.html页面表单中对应输入框的
			 * 名字一致(name="xxxx",输入框name属性的值)
			 */
			String username = request.getParameter("username");
			String password = request.getParameter("password");
			String nickname = request.getParameter("nickname");
			int age = Integer.parseInt(
				request.getParameter("age")
			);
			
			//验证是否为重复用户
			boolean check = false;
			for(int i=0;i<raf.length()/100;i++) {
				raf.seek(i*100);
				byte[] arr = new byte[32];
				raf.read(arr);
				String name = new String(arr,"UTF-8").trim();
				if(name.equals(username)) {
					check = true;//有此用户了
					break;
				}
			}
			
			if(check) {
				//直接跳转提示此用户已存在的页面
				response.sendRedirect("reg_fail.html");
			}else {
				/*
				 * 2
				 * 将该用户信息写入文件user.dat中。
				 * 每条记录占用100字节
				 * 其中:username,password,nickname为字符串，各
				 * 占用32字节，age为int值占用4字节。
				 */
				//先将指针移动到文件末尾
				raf.seek(raf.length());
				//写用户名
				byte[] data = username.getBytes("UTF-8");
				//将数组扩容到32字节
				data = Arrays.copyOf(data, 32);
				//写入该32字节
				raf.write(data);			
				//写密码
				data = password.getBytes("UTF-8");
				data = Arrays.copyOf(data, 32);
				raf.write(data);			
				//写昵称
				data = nickname.getBytes("UTF-8");
				data = Arrays.copyOf(data, 32);
				raf.write(data);			
				//写年龄
				raf.writeInt(age);
						
				//3响应客户端注册成功的页面
				response.sendRedirect("reg_success.html");
			}
			System.out.println("RegServlet:处理注册完毕");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}








