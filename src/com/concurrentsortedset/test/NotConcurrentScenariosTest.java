package com.concurrentsortedset.test;

import java.util.LinkedList;
import java.util.List;

import com.concurrentsortedset.client.command.AddCommand;
import com.concurrentsortedset.client.command.ClientCommand;
import com.concurrentsortedset.client.command.GetCommand;
import com.concurrentsortedset.client.command.SizeCommand;
import com.concurrentsortedset.sortedset.SortedSetManager;

public class NotConcurrentScenariosTest {

	static public void assertion(int received, int expected)
	{
		if (received==expected) System.out.println("As expected " + expected);
		else
			System.out.println("ERROR expected:" + expected + " received:" + received);
	}
	
	public static void main(String[] args) {
		SortedSetManager manager = new SortedSetManager();
		manager.remove(1, 1);
		assertion(manager.get(1, 1), -1);
		manager.add(1, 1, 2);
		assertion(manager.get(1, 1), 2);
		manager.add(1, 1, 3);
		assertion(manager.get(1, 1), 3);
		assertion(manager.size(2), 0);
		assertion(manager.size(1), 1);
		manager.add(1, 2, 4);
		assertion(manager.size(1), 2);
		manager.add(1, 2, 5);
		assertion(manager.size(1), 2);
		manager.add(1, 2, 5);
		assertion(manager.size(1), 2);
		assertion(manager.get(1, 2), 5);
		manager.remove(1, 2);
		assertion(manager.get(1, 2), -1);
		List<Integer> sets = new LinkedList<Integer>();
		sets.add(1);
		sets.add(2);
		manager.getRange(sets, 1, 10);
	}

}
