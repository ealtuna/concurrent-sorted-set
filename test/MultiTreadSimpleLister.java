package com.concurrentsortedset.test;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

import com.concurrentsortedset.config.CommonParam;
import com.concurrentsortedset.networking.ConnectionHandler;

public class MultiTreadSimpleLister
{
	public static List<ConnectionHandler> connectionLis = new LinkedList<ConnectionHandler>();
	
	public static boolean listening = true;
	
	public static void main(String[] args) {
		ServerSocket serverSocket = null;
	    try {
	        serverSocket = new ServerSocket(CommonParam.LISTEN_PORT);
	    } catch (IOException e) {
	        System.exit(1);
	    }
	    
		while (listening)
		{			
			Socket clientSocket = null;
	        try {
	            clientSocket = serverSocket.accept();
	        } catch (IOException e) {
	            System.exit(1);
	        }
	        ConnectionHandler connectionHandler = new ConnectionHandler(clientSocket);
	        
	        connectionHandler.start();
	        
	        synchronized (connectionLis) 
	        {
	        	connectionLis.add(connectionHandler);
			}	        
	        
	        connectionHandler = null;
	        
	        clientSocket  = null;
		}
		try {serverSocket.close();} catch (IOException e) {}
	}
}
