package com.concurrentsortedset.test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.smartcardio.TerminalFactorySpi;

import com.concurrentsortedset.client.command.AddCommand;
import com.concurrentsortedset.client.command.ClientCommand;
import com.concurrentsortedset.client.command.GetCommand;
import com.concurrentsortedset.client.command.GetRangeCommand;
import com.concurrentsortedset.client.command.SizeCommand;

public class MultipleClientsConcurrentTest {

	abstract class TesterThread extends Thread {
		String last_known_state = "";
		int f_set, l_set;
		
		public TesterThread(int f_set, int l_set) {
			this.f_set=f_set;
			this.l_set=l_set;
		}
	}
	
	class ConcurrentClientTester1 extends TesterThread {
		
		public ConcurrentClientTester1(int f_set, int l_set) {
			super(f_set, l_set);
		}
		
		@Override
		public void run() {
			for (int i=0;i<100;i++)
			{
				try{
					int time = (new Random().nextInt()) % 100;
					if (time<0) time*=-1;
					if (time==0) time++;
					Thread.sleep(time);
					int set = f_set + ((new Random().nextInt()) % (l_set-f_set));
					if (set<0) set*=-1;
					if (set==0) set++;
					ClientCommand command;
					command = new GetCommand(set, i);
					command.doWork();
					command = new SizeCommand(set);
					command.doWork();
					command = new AddCommand(set, i, i);
					command.doWork();
					command = new GetCommand(set, i);
					command.doWork();
					command = new SizeCommand(set);
					command.doWork();
					
				}catch (Exception e)
				{
					e.printStackTrace();
				}
			}
			System.out.println("FINISH");
		}
	}
	
	class ConcurrentClientTester2 extends TesterThread {
		
		
		public ConcurrentClientTester2(int f_set, int l_set) {
			super(f_set, l_set);
		}
		
		@Override
		public void run() {
			for (int i=0;i<100;i++)
			{
				try{
					int time = (new Random().nextInt()) % 100;
					if (time<0) time*=-1;
					if (time==0) time++;
					Thread.sleep(time);
					int set = f_set + ((new Random().nextInt()) % (l_set-f_set));
					if (set<0) set*=-1;
					if (set==0) set++;
					ClientCommand command;
					ArrayList<Integer> sets = new ArrayList<Integer>();
					sets.add(1);
					sets.add(2);
					sets.add(3);
					sets.add(4);
					sets.add(5);
					command = new GetRangeCommand(sets, 0, i);
					last_known_state = "Going to range";
					command.doWork();
					last_known_state = "Done range";
					command = new GetRangeCommand(sets, 0+10, i+10);
					last_known_state = "Going to range";
					command.doWork();
					last_known_state = "Done range";
				}catch (Exception e)
				{
					e.printStackTrace();
				}
			}
			System.out.println("FINISH");
		}
	}
	
	public void test() throws Exception
	{
		List<TesterThread> all_thread = new LinkedList<TesterThread>();
		
		for (int i=0;i<10;i++)
		{
			ConcurrentClientTester1 tester = new ConcurrentClientTester1(1,5);
			all_thread.add(tester);
			tester.start();
		}
		
		for (int i=0;i<12;i++)
		{
			ConcurrentClientTester2 tester = new ConcurrentClientTester2(1,5);
			all_thread.add(tester);
			tester.start();
		}
		
		while (true)
		{
			Thread.sleep(1000);
			int count_alive = 0;
			for (int i=0;i<all_thread.size();i++)
			{
				if (!all_thread.get(i).isAlive())
				{
					all_thread.remove(i--);
				}else count_alive++;
			}
			if (count_alive==0) break;
			System.out.println("Alive: " + count_alive);
			for (TesterThread thread : all_thread)
			{
				System.out.println("LNS: " + thread.last_known_state);
			}
		}
	}
	
	public static void main(String[] args) throws Exception {
		new MultipleClientsConcurrentTest().test();
	}

}
