package com.concurrentsortedset.client;

import java.util.ArrayList;
import java.util.List;

import com.concurrentsortedset.client.command.AddCommand;
import com.concurrentsortedset.client.command.ClientCommand;
import com.concurrentsortedset.client.command.GetCommand;
import com.concurrentsortedset.client.command.GetRangeCommand;
import com.concurrentsortedset.client.command.RemCommand;
import com.concurrentsortedset.client.command.SizeCommand;

public class ClientSimulator {
	/**
	 * 	<SIZE> <set1> [0]
		<ADD> <set1> <k1> <1>
		<GET> <set1> <k1> [1]
		<SIZE> <set1> [1]
		<ADD> <set1> <k1> <2>
		<GET> <set1> <k1> [2]
		<ADD> <set1> <k2> <3>
		<GETRANGE> <set1> <-1> <3> <3> [k2] [3] [-1]
		<ADD> <set2> <k3> <1>
		<GETRANGE> <set1> <set2> <-1> <0> <INT_MAX> [k3] [1] [k1] [2] [k2] [3] [-1]
		<REM> <set1> <k2>
		<GETRANGE> <set1> <set2> <-1> <0> <INT_MAX> [k3] [1] [k1] [2] [-1]
	 */
	public static void main(String[] args) throws Exception {
		ClientCommand command;
		command = new SizeCommand(1);
		command.doWork();
		System.out.println("EXPECTED: 0");
		command = new AddCommand(1, 1, 1);
		command.doWork();
		command = new GetCommand(1, 1);
		command.doWork();
		System.out.println("EXPECTED: 1");
		command = new SizeCommand(1);
		command.doWork();
		System.out.println("EXPECTED: 1");
		command = new AddCommand(1, 1, 2);
		command.doWork();
		command = new GetCommand(1, 1);
		command.doWork();
		System.out.println("EXPECTED: 2");
		command = new AddCommand(1, 2, 3);
		command.doWork();
		List<Integer> sets = new ArrayList<Integer>();
		sets.add(1);
		command = new GetRangeCommand(sets, 3, 3);
		command.doWork();
		command = new AddCommand(2, 3, 1);
		command.doWork();
		sets.add(2);
		command = new GetRangeCommand(sets, 0, Integer.MAX_VALUE);
		command.doWork();
		command = new RemCommand(1, 2);
		command.doWork();
		command = new GetRangeCommand(sets, 0, Integer.MAX_VALUE);
		command.doWork();
	}
}
