package com.concurrentsortedset.networking;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

import com.concurrentsortedset.config.CommonParam;
import com.concurrentsortedset.sortedset.SortedSetManager;
import com.concurrentsortedset.sortedset.tree.Element;

public class ConnectionHandler extends Thread {
	
	public static int threadIdGen = 0;
	
	public static int threadId;

	private Socket clientSocket;
	
	public boolean terminated;
	
	public ConnectionHandler(Socket clientSocket)
	{
		this.clientSocket = clientSocket;
		terminated = false;
		threadId = threadIdGen++;
		this.setName("ConnectionHandler #" + threadId);
	}
	
	@Override
	public void run() 
	{
		try
		{
			DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
			DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());
			int command_code = dis.readInt();
			int set, key, score;
			SortedSetManager sortedSetManager = new SortedSetManager();
			switch (command_code)
			{
				case CommonParam.CODE_ADD:
					set = dis.readInt();
					key = dis.readInt();
					score = dis.readInt();
					System.out.println("[SERVER] ADD " + set+","+key+","+score);
					sortedSetManager.add(set, key, score);
					break;
				case CommonParam.CODE_REM:
					set = dis.readInt();
					key = dis.readInt();
					System.out.println("[SERVER] REM " + set+","+key);
					sortedSetManager.remove(set, key);
					break;
				case CommonParam.CODE_SIZE:
					set = dis.readInt();
					System.out.println("[SERVER] SIZE " + set);
					int set_size = sortedSetManager.size(set);
					dos.writeInt(set_size);
					dos.flush();
					System.out.println("[SERVER] SIZE=" + set_size);
					break;
				case CommonParam.CODE_GET:
					set = dis.readInt();
					key = dis.readInt();
					System.out.println("[SERVER] GET " + set+","+key);
					int score_retrieved = sortedSetManager.get(set, key);
					dos.writeInt(score_retrieved);
					dos.flush();
					System.out.println("[SERVER] GET=" + score_retrieved);
					break;
				case CommonParam.CODE_GETRANGE:
					List<Integer> sets = new LinkedList<Integer>();
					while ((set=dis.readInt()) != -1)
					{
						sets.add(set);
					}
					int lower = dis.readInt();
					int upper = dis.readInt();
					List<Element> in_range = sortedSetManager.getRange(sets, lower, upper);
					for (Element element : in_range)
					{
						dos.writeInt(element.key);
						dos.writeInt(element.score);
					}
					dos.writeInt(-1);
					dos.flush();
					break;
			}
		} 
		catch (Exception error)
		{
			error.printStackTrace();
		}
		finally
		{ 
			terminated = true;
			try {clientSocket.close();} catch (Exception e) {}
		}
	}
}
