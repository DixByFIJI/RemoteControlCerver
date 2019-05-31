/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package remotecontrolserver.connections;

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
import remotecontrolserver.Validator;
import remotecontrolserver.connections.NetworkServiceManager;
import remotecontrolserver.entities.NetworkNode;
import remotecontrolserver.exceptions.ServerRunningException;
import sun.nio.cs.ext.MacUkraine;

/**
 *
 * @author Username
 */
public class Server {

	private int PORT = 0;
	private final int COUNT_OF_CONNECTIONS = 5;
	private static boolean isStarted = false;
	
	private static ServerSocket server;
	private static NetworkServiceManager serviceManager;
	
	public Server(){}

	public Server(int port) {
		this.PORT = port;
	}

	/**
	 * Launches server for listening connections
	 * @param listener implemented instance of listener
	 */
	
	public synchronized void start(ServerStartingListener listener) {
		FutureTask<Boolean> serverTask = new FutureTask<Boolean>(new Callable<Boolean>(){
			@Override
			public Boolean call() {
					try {
						server = new ServerSocket(PORT);
						serviceManager = new NetworkServiceManager();
						serviceManager.setPort(server.getLocalPort());
						listener.onBeginning();
						serviceManager.registerService();
						listener.onEstablished();
						isStarted = true;
						Semaphore block = new Semaphore(COUNT_OF_CONNECTIONS);

						while (true) {
							block.acquire();
							Socket accept = server.accept();
							Thread session = new Thread(()-> {
								try (
									InputStream inputStream = accept.getInputStream();
									OutputStream outputStream = accept.getOutputStream();
									BufferedReader readerStream = new BufferedReader(new InputStreamReader(inputStream, "cp1251"));
									PrintWriter writerStream = new PrintWriter(outputStream, true);
								){
									Gson gson = new Gson();
									String jsonInstanceString;
									while((jsonInstanceString = readerStream.readLine()) != null){
										NetworkNode node = gson.fromJson(jsonInstanceString, NetworkNode.class);
										writerStream.println(Validator.check(node));
									}

									accept.close();
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
				return true;
			}
		});
		
	  Thread process = new Thread(serverTask);
		process.setName("ServerThread");
		process.start();
	}

	/**
	 * Gets local port from server
	 * @return number of local port
	 */
	
	public int getLocalPort(){
		return server.getLocalPort();
	}
    
	/**
	 * Gets local IP-V4 address from current connection
	 * @return instance host address
	 */
	
	public static InetAddress getCurrentInetAddress() {
		InetAddress hostAddress = null;
		try {
			hostAddress = Collections.list(NetworkInterface.getByName("wlan0").getInetAddresses()).get(0);
		} catch (SocketException ex) {
			Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
		}
		return hostAddress;
	}

	/**
	 * Interrupts server
	 * @param listener implemented instance of listener
	 */
	
	public synchronized void stop(ServerCompletingListener listener) {
		Thread stopThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					listener.onBeginning();
					serviceManager.unregisterServices();
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
	
	/**
	 * Gets state of server
	 * @return {@code true} if server is started
	 */
	
	public boolean isServerStarted(){
		return isStarted;
	}
}
