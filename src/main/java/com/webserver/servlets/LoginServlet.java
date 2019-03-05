package com.webserver.servlets;

import java.io.File;
import java.io.RandomAccessFile;

import com.webserver.http.HttpRequest;
import com.webserver.http.HttpResponse;

/**
 * ��¼ҵ��
 * @author ta
 *
 */
public class LoginServlet extends HttpServlet{
	public void service(HttpRequest request,HttpResponse response) {
		System.out.println("��ʼ�����¼");
		
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		
		try (
			RandomAccessFile raf
				= new RandomAccessFile("user.dat","r");
		){
			//Ĭ���ǵ�¼ʧ��
			boolean check = false;
			for(int i=0;i<raf.length()/100;i++) {
				raf.seek(i*100);
				byte[] data = new byte[32];
				raf.read(data);
				String name = new String(data,"UTF-8").trim();
				if(name.equals(username)) {
					//�ҵ����û�,�ȶ�����
					raf.read(data);
					String pwd = new String(data,"UTF-8").trim();
					if(pwd.equals(password)) {
						//��֤�ɹ�
						check = true;
					}
					break;
				}
			}
			
			if(check) {
				/*
				 * ��Ҫע�⣬�ض�����ָ�������·������������
				 * �ġ��������֮ǰ������·��������ǰServlet
				 * ʱ��·��
				 * http://localhost:8088/myweb/login
				 * ���Ե�ǰĿ¼��
				 * http://localhost:8088/myweb/
				 * �Դˣ�����ָ���ض���·����:login_success.html
				 * ������ڽ��յ��󣬾ͻ��Զ�����:
				 * http://localhost:8088/myweb/login_success.html
				 */
				//�ض����¼�ɹ�ҳ��
				response.sendRedirect("login_success.html");
			}else {
				//��ת��¼ʧ��ҳ��
				response.sendRedirect("login_fail.html");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		System.out.println("�����¼���");
	}
}








