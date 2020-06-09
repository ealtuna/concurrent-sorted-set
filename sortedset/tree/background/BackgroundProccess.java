package com.concurrentsortedset.sortedset.tree.background;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class BackgroundProccess extends Thread {
	public static Queue<BackgroundTask> queue = new ConcurrentLinkedQueue<BackgroundTask>();
	
	public BackgroundProccess() {
		this.setName("BackgroundProccess");
	}
	
	@Override
	public void run() {
		while (true)
		{
			while (!queue.isEmpty())
			{
				BackgroundTask task = queue.poll();
				if (task==null) break;
				task.execute();
				if (RangeTask.class.isAssignableFrom(task.getClass()))
				{
					((RangeTask)task).can_finish=true;
					synchronized (task) {
						task.notifyAll();
					}
				}
			}
			synchronized (this) {
				try {
					this.wait(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
