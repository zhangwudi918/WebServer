package com.webserver.core;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * WebServer主类
 * @author ta
 *
 */
public class WebServer {
	private ServerSocket server;
	/**
	 * 初始化服务端
	 */
	private ExecutorService threadPool;
	public WebServer() {
		try {
			server = new ServerSocket(8088);
			threadPool=Executors.newFixedThreadPool(30);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 服务端开始工作的方法
	 */
	public void start() {
		try {
			
			while(true) {
				System.out.println("等待客户端连接...");
				Socket socket = server.accept();
				System.out.println("一个客户端连接了!");
				
				//启动一个线程处理与该客户端的交互
				ClientHandler handler = new ClientHandler(socket);
				threadPool.execute(handler);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		WebServer server = new WebServer();
		server.start();
	}
}







