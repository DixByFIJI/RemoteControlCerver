package remotecontrolserverfx.entities;

import java.io.Serializable;

public class NetworkNode implements Serializable {
	private NetworkDevice device;
	private String data;

	public NetworkNode(NetworkDevice device, String data) {
			this.device = device;
			this.data = data;
	}

	public NetworkDevice getDevice() {
			return device;
	}

	public void setDevice(NetworkDevice device) {
			this.device = device;
	}

	public String getData() {
			return data;
	}

	public void setData(String data) {
			this.data = data;
	}
}
