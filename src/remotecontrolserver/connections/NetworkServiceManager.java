/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package remotecontrolserver.connections;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;

/**
 *
 * @author Username
 */
public class NetworkServiceManager {
	private final String TYPE = "_http._tcp.local.";
  private final String SERVICE_NAME = "Server";
	private int port = 0;
	
	private enum serviceInfoTags {
		TYPE_TAG, DEVICE_NAME_TAG
	}
	
	private enum deviceTypes {
		PC, PHONE
	}
	
	private JmDNS jmDNS;
	private ServiceInfo mServiceInfo;
	private ServiceListener mServiceListener;
	
	public boolean registerService() {
		try {
			InetAddress currentInetAddress = Server.getCurrentInetAddress();
			jmDNS = JmDNS.create(currentInetAddress);
			mServiceInfo = ServiceInfo.create(TYPE, SERVICE_NAME, port, SERVICE_NAME);

			Map<String, String> serviceInfoMap = new LinkedHashMap<String, String>(){{
				put(serviceInfoTags.TYPE_TAG.toString(), deviceTypes.PC.toString());
				put(serviceInfoTags.DEVICE_NAME_TAG.toString(), currentInetAddress.getHostName());
			}};

			mServiceInfo.setText(serviceInfoMap);
			jmDNS.registerService(mServiceInfo);
		} catch (IOException ex) {
			Logger.getLogger(NetworkServiceManager.class.getName()).log(Level.SEVERE, null, ex);
			return false;
		}
		return true;
	}
	
	public void unregisterServices() {
		if (jmDNS != null) {
			if (mServiceListener != null) {
				jmDNS.removeServiceListener(TYPE, mServiceListener);
				mServiceListener = null;
			}
			jmDNS.unregisterAllServices();
			try {
				jmDNS.close();
			} catch (IOException ex) {
				Logger.getLogger(NetworkServiceManager.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}

	public ServiceInfo getCurrentService() {
		return mServiceInfo;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
}
