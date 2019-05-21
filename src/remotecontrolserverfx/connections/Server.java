/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package remotecontrolserverfx.connections;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import remotecontrolserverfx.Validation;
import remotecontrolserverfx.connections.NetworkServiceManager;
import remotecontrolserverfx.entities.NetworkNode;
import remotecontrolserverfx.exeptions.ServerInterruptedException;
import remotecontrolserverfx.exeptions.ServerRunningException;
import sun.nio.cs.ext.MacUkraine;

/**
 *
 * @author Username
 */
public class Server {

	private int PORT = 49150;
	private final int COUNT_OF_CONNECTIONS = 5;
	private static boolean isStarted = false;
	
	private static ServerSocket server;
	private static NetworkServiceManager serviceManager;
	
	public Server(){}

	public Server(int port) {
		this.PORT = port;
	}

	public synchronized boolean start(ServerStartingListener listener) {
		FutureTask<Boolean> serverTask = new FutureTask<Boolean>(new Callable<Boolean>(){
			@Override
			public Boolean call() {
				serviceManager = new NetworkServiceManager();
				serviceManager.setPort(PORT);
				listener.onBeginning();
				if(serviceManager.registerService()) {
					System.out.println("Starting...");
					try {
						server = new ServerSocket(PORT);
						isStarted = true;
						listener.onEstablished();
						ExecutorService flowsPool = Executors.newFixedThreadPool(COUNT_OF_CONNECTIONS);
						Semaphore block = new Semaphore(COUNT_OF_CONNECTIONS);

						while (true) {
							block.acquire();
							Socket accept = server.accept();
							Thread session = new Thread(()-> {
								try (
									InputStream inputStream = accept.getInputStream();
									OutputStream outputStream = accept.getOutputStream();
									//ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
									BufferedReader readerStream = new BufferedReader(new InputStreamReader(inputStream, "cp1251"));
									PrintWriter writerStream = new PrintWriter(outputStream, true);
								){
									System.out.println("New session...");
									Gson gson = new Gson();
									String dataNode;
									while((dataNode = readerStream.readLine()) != null){
//										NetworkNode node = gson.fromJson(dataNode, NetworkNode.class);
//										String command = node.getData();
//										System.out.println(command);
//										writerStream.println(Validation.check(command));
										System.out.println(dataNode);
										writerStream.println(Validation.check(dataNode));
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
							session.setName("Session " + String.valueOf(block.getQueueLength()));
						}
					} catch (IOException ex) {
						Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
						return false;
					} catch (InterruptedException ex) {
						Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
					} 
				} else {
					return false;
				}
				return true;
			}
		});
		
	  Thread process = new Thread(serverTask);
		process.setName("ServerThread");
		process.start();
		
//		try {
//			isStarted = serverTask.get();
//		} catch (InterruptedException ex) {
//			Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
//		} catch (ExecutionException ex) {
//			Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
//		}
		
		return isStarted;
	}

	public int getLocalPort(){
		return server.getLocalPort();
	}
    
	public static InetAddress getCurrentInetAddress() {
		InetAddress hostAddress = null;
		try {
			hostAddress = Collections.list(NetworkInterface.getByName("wlan0").getInetAddresses()).get(0);
		} catch (SocketException ex) {
			Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
		}
		return hostAddress;
	}

	public synchronized void stop(ServerStopingListener listener) {
		Thread stopThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					listener.onBeginning();
					serviceManager.unregisterServices();
					System.out.println("Services unregistered");
					server.close();
					isStarted  = false;
					listener.onStopped();
				} catch (IOException ex) {
					Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		});
		
		stopThread.start();
	}
	
	public boolean isServerStarted(){
		return isStarted;
	}
}
