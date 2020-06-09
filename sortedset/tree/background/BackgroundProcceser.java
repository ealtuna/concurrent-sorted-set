package com.concurrentsortedset.sortedset.tree.background;

import java.util.List;

import com.concurrentsortedset.sortedset.tree.Element;
import com.concurrentsortedset.sortedset.tree.SearchTree;

public class BackgroundProcceser {
	
	BackgroundProccess background_process = new BackgroundProccess();
	
	public BackgroundProcceser() {
		background_process.start();
	}
	
	public void processAdd(SearchTree search_tree, Element element)
	{
		AddTask task = new AddTask(search_tree, element);
		BackgroundProccess.queue.add(task);
		synchronized (background_process) {
			background_process.notifyAll();
		}
	}
	
	public void processRemove(SearchTree search_tree, Element element)
	{
		RemoveTask task = new RemoveTask(search_tree, element);
		BackgroundProccess.queue.add(task);
		synchronized (background_process) {
			background_process.notifyAll();
		}
	}
	
	public void processRange(List<SearchTree> search_trees, List<Element> in_range, int lower, int upper)
	{
		RangeTask task = new RangeTask(search_trees, in_range, lower, upper);
		task.start();
		BackgroundProccess.queue.add(task);
		synchronized (background_process) {
			background_process.notifyAll();
		}
		try {
			task.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}

