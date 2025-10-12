package com.utils;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;

public class SystemHandler {
	
	public static String getIP() {
		try {
			return InetAddress.getLocalHost().getHostAddress();
		}
		catch (Exception e) {
			return "";
		}
	}
	
	/** Method copied from: <a>{@link https://stackoverflow.com/questions/434718/sockets-discover-port-availability-using-java}</a>
	 * @date:	Oct 16, 2022 at 14:02
	 * @author:	Noam Yizraeli
	*/
	public static boolean availablePort(int port) throws IllegalStateException {
	    try (Socket ignored = new Socket("localhost", port)) {
	        return false;
	    } catch (ConnectException e) {
	        return true;
	    } catch (IOException e) {
	        throw new IllegalStateException("Error while trying to check open port", e);
	    }
	}

}
