package com.concurrentsortedset.networking;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

import com.concurrentsortedset.config.CommonParam;

public class ConnectionListener extends Thread {
	
	public static boolean listening = true;
	
	public List<ConnectionHandler> connectionLis = new  LinkedList<ConnectionHandler>();
	
	public void cleanThreads()
	{
		synchronized (connectionLis)
		{
			for (int i=0;i<connectionLis.size();i++)
			{
				if (connectionLis.get(i).terminated)
				{
					connectionLis.remove(i);
					i--;
				}
			}
		}
	}
	
	@Override
	public void run() {
		ServerSocket serverSocket = null;
	    try {
	        serverSocket = new ServerSocket(CommonParam.LISTEN_PORT);
	    } catch (IOException e) {
	        System.exit(1);
	    }
	    
		while (listening)
		{			
			cleanThreads();
			Socket clientSocket = null;
	        try {
	            clientSocket = serverSocket.accept();
	        } catch (IOException e) {
	            System.exit(1);
	        }
	        ConnectionHandler connectionHandler = new ConnectionHandler(clientSocket);
	        
	        synchronized (connectionLis) 
	        {
	        	connectionLis.add(connectionHandler);
			}	        
	        
	        connectionHandler.start();
	        
	        connectionHandler = null;
	        
	        clientSocket  = null;
		}
		try {serverSocket.close();} catch (IOException e) {}
	}
	
	public static void stopListenning()
	{
		listening = false;
	}
}
