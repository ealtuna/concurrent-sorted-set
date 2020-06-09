package com.concurrentsortedset.init;

import com.concurrentsortedset.networking.ConnectionListener;

public class Launcher {
	public static void main(String[] args) throws InterruptedException {
		ConnectionListener listener = new ConnectionListener();
		listener.start();
		listener.join();
	}
}
