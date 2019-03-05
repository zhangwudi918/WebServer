package com.webserver.servlets;

import java.io.RandomAccessFile;

import com.webserver.http.HttpRequest;
import com.webserver.http.HttpResponse;
/**
 * ��ʾ����ע���û�
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
			builder.append("<title>�û��б�</title>");
			builder.append("</head>");
			
			builder.append("<body>");
			builder.append("<center>");
			builder.append("<h1>�û��б�</h1>");
			builder.append("<table border='1'>");
			
			builder.append("<tr><td>�û���</td><td>����</td><td>�ǳ�</td><td>����</td></tr>");
			
			for(int i=0;i<raf.length()/100;i++) {	
				//��ȡ�û���
				byte[] data = new byte[32];
				raf.read(data);
				String username = new String(data,"UTF-8").trim();
				//������
				raf.read(data);
				String password = new String(data,"UTF-8").trim();
				//���ǳ�
				raf.read(data);
				String nickname = new String(data,"UTF-8").trim();
				//������
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
			
			//��ƴ�ӳ���html����ת��Ϊһ���ֽ���Ϊ��Ӧ����
			byte[] data = builder.toString().getBytes("UTF-8");			
			
			//��Ӧ�ͻ���
			response.putHeader("Content-Type", "text/html");
			response.putHeader("Content-Length", data.length+"");
			
			response.setContentData(data);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}








