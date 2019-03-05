package com.webserver.servlets;

import java.io.RandomAccessFile;
import java.util.Arrays;

import com.webserver.http.HttpRequest;
import com.webserver.http.HttpResponse;

/**
 * ����ע��ҵ��
 * @author ta
 *
 */
public class RegServlet extends HttpServlet {
	/**
	 * �������ҵ������ķ���
	 */
	public void service(HttpRequest request,HttpResponse response) {
		/*
		 * ע��ҵ������:
		 * 1:ͨ��HttpRequest��ȡ�û��ύ�ı���Ϣ
		 * 2:������Ϣд���ļ�����
		 * 3:��Ӧ�ͻ���ע������ҳ��
		 */
		try(
			RandomAccessFile raf 
				= new RandomAccessFile("user.dat","rw")	
		) {
			System.out.println("RegServlet:��ʼ����ע��");
			//1
			/*
			 * ͨ��request.getParameter��ȡ���ύ�����ݡ�
			 * ���ﴫ�ݵĲ���Ӧ����reg.htmlҳ����ж�Ӧ������
			 * ����һ��(name="xxxx",�����name���Ե�ֵ)
			 */
			String username = request.getParameter("username");
			String password = request.getParameter("password");
			String nickname = request.getParameter("nickname");
			int age = Integer.parseInt(
				request.getParameter("age")
			);
			
			//��֤�Ƿ�Ϊ�ظ��û�
			boolean check = false;
			for(int i=0;i<raf.length()/100;i++) {
				raf.seek(i*100);
				byte[] arr = new byte[32];
				raf.read(arr);
				String name = new String(arr,"UTF-8").trim();
				if(name.equals(username)) {
					check = true;//�д��û���
					break;
				}
			}
			
			if(check) {
				//ֱ����ת��ʾ���û��Ѵ��ڵ�ҳ��
				response.sendRedirect("reg_fail.html");
			}else {
				/*
				 * 2
				 * �����û���Ϣд���ļ�user.dat�С�
				 * ÿ����¼ռ��100�ֽ�
				 * ����:username,password,nicknameΪ�ַ�������
				 * ռ��32�ֽڣ�ageΪintֵռ��4�ֽڡ�
				 */
				//�Ƚ�ָ���ƶ����ļ�ĩβ
				raf.seek(raf.length());
				//д�û���
				byte[] data = username.getBytes("UTF-8");
				//���������ݵ�32�ֽ�
				data = Arrays.copyOf(data, 32);
				//д���32�ֽ�
				raf.write(data);			
				//д����
				data = password.getBytes("UTF-8");
				data = Arrays.copyOf(data, 32);
				raf.write(data);			
				//д�ǳ�
				data = nickname.getBytes("UTF-8");
				data = Arrays.copyOf(data, 32);
				raf.write(data);			
				//д����
				raf.writeInt(age);
						
				//3��Ӧ�ͻ���ע��ɹ���ҳ��
				response.sendRedirect("reg_success.html");
			}
			System.out.println("RegServlet:����ע�����");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}








