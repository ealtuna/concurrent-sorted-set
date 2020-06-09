package com.concurrentsortedset.client.command;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import com.concurrentsortedset.client.ClientConfig;
import com.concurrentsortedset.config.CommonParam;

public abstract class ClientCommand {
	
	DataInputStream dis;
	DataOutputStream dos;

	public abstract void fullCommand() throws IOException;
	
	public abstract int commandCode();
	
	public void doWork() throws Exception 
	{
		Socket client = new Socket();
		InetSocketAddress socket_address = new InetSocketAddress(ClientConfig.SERVER_ADDRESS, CommonParam.LISTEN_PORT);
		client.connect(socket_address, CommonParam.CONNECTION_TIMEOUT);
		dis = new DataInputStream(client.getInputStream());
		dos = new DataOutputStream(client.getOutputStream());
		dos.writeInt(commandCode());
		fullCommand();		
		dos.flush();
		client.close();
	}
}
