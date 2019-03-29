/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package remotecontrolserverfx;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import remotecontrolserverfx.exeptions.ServerInterruptedException;
import remotecontrolserverfx.exeptions.ServerRunningException;

/**
 *
 * @author Username
 */
public class Server {

	private int PORT = 49150;
	private final int COUNT_OF_CONNECTIONS = 5;
	
	private static ServerSocket server;
	
	public Server(){}

	public Server(int port) {
		this.PORT = port;
	}

	public boolean start() throws ServerRunningException {
	  Thread process = new Thread(() -> {
			System.out.println("Starting...");
			try {
				server = new ServerSocket(PORT);
				ExecutorService flowsPool = Executors.newFixedThreadPool(COUNT_OF_CONNECTIONS);
				Semaphore block = new Semaphore(COUNT_OF_CONNECTIONS);
				
				while (true) {
					block.acquire();
					Socket accept = server.accept();
					/*flowsPool.execute(*/Thread session = new Thread(()-> {
						try (
							InputStream inputStream = accept.getInputStream();
							OutputStream outputStream = accept.getOutputStream();
							BufferedReader readerStream = new BufferedReader(new InputStreamReader(inputStream, "cp1251"));
							PrintWriter writerStream = new PrintWriter(outputStream, true);
						){
							String command;
							while((command = readerStream.readLine()) != null){
								System.out.println(command);
								if(!Validation.check(command)){
									writerStream.print(false);
								}
							}
							
							accept.close();
							System.out.println("Session closed...");
						} catch (IOException ex) {
							ex.printStackTrace();
						} catch (NullPointerException ex){
							ex.printStackTrace();
						} finally {
							block.release();
						}
					});
					session.start();
					session.setName(String.valueOf(block.getQueueLength()));
				}
			} catch (InterruptedException ex) {
				throw new ServerInterruptedException(ex);
			} catch (IOException ex) {
				throw new ServerRunningException(ex);
			}
		});
		process.setName("ServerThread");
		process.start();
		return true;
	}

	public int getLocalPort(){
		return server.getLocalPort();
	}
    
	public static String getHostAddress() {
		String hostAddress = null;
		try {
			//try {
			hostAddress = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException ex) {
			Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
		}
//		try {
//			InetAddress inet = InetAddress.getLocalHost();
//			InetAddress[] ips = InetAddress.getAllByName(inet.getCanonicalHostName());
//			if (ips  != null ) {
//				for (int i = 0; i < ips.length; i++) {
//					System.out.println(ips[i]);
//				}
//			}
//		} catch (UnknownHostException e) {
//
//		}
			try {
				Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
				for (NetworkInterface netint : Collections.list(nets)) {
					//System.out.printf("Display name: %s\n", netint.getDisplayName());
					System.out.printf("Name: %s\n", netint.getName());
//					Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
//					for (InetAddress inetAddress : Collections.list(inetAddresses)) {
//						System.out.printf("InetAddress: %s\n", inetAddress);
//					}
//					System.out.printf("\n");
				}
			} catch (SocketException socketException) {
				socketException.getStackTrace();
			}
//		} catch (UnknownHostException ex) {
//			Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
//		}
		return hostAddress;
	}

	public static void stop() {
		try {
			server.close();
		} catch (IOException ex) {
			Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}
