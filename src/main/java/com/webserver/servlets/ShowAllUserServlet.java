package com.webserver.servlets;

import java.io.RandomAccessFile;

import com.webserver.http.HttpRequest;
import com.webserver.http.HttpResponse;
/**
 * 显示所有注册用户
 * @author ta
 *
 */
public class ShowAllUserServlet extends HttpServlet{
	public void service(HttpRequest request, HttpResponse response) {
		try (
			RandomAccessFile raf = new RandomAccessFile("user.dat","r");	
		){
			StringBuilder builder = new StringBuilder();
			builder.append("<html>");
			builder.append("<head>");
			builder.append("<meta charset='UTF-8'>");
			builder.append("<title>用户列表</title>");
			builder.append("</head>");
			
			builder.append("<body>");
			builder.append("<center>");
			builder.append("<h1>用户列表</h1>");
			builder.append("<table border='1'>");
			
			builder.append("<tr><td>用户名</td><td>密码</td><td>昵称</td><td>年龄</td></tr>");
			
			for(int i=0;i<raf.length()/100;i++) {	
				//读取用户名
				byte[] data = new byte[32];
				raf.read(data);
				String username = new String(data,"UTF-8").trim();
				//读密码
				raf.read(data);
				String password = new String(data,"UTF-8").trim();
				//读昵称
				raf.read(data);
				String nickname = new String(data,"UTF-8").trim();
				//读年龄
				int age = raf.readInt();
				
				builder.append("<tr>");
				builder.append("<td>"+username+"</td>");
				builder.append("<td>"+password+"</td>");
				builder.append("<td>"+nickname+"</td>");
				builder.append("<td>"+age+"</td>");
				builder.append("</tr>");
			}
			
			
			builder.append("</table>");
			builder.append("</center>");
			builder.append("</body>");
			builder.append("</html>");
			
			//将拼接出的html代码转换为一组字节作为响应正文
			byte[] data = builder.toString().getBytes("UTF-8");			
			
			//响应客户端
			response.putHeader("Content-Type", "text/html");
			response.putHeader("Content-Length", data.length+"");
			
			response.setContentData(data);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}








