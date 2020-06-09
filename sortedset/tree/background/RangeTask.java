package com.concurrentsortedset.sortedset.tree.background;

import java.util.List;

import com.concurrentsortedset.sortedset.tree.Element;
import com.concurrentsortedset.sortedset.tree.SearchTree;

public class RangeTask extends Thread implements BackgroundTask  {
	
	List<SearchTree>  search_trees;
	List<Element> in_range;
	int lower, upper;
	
	boolean can_finish=false;
	
	public RangeTask(List<SearchTree>  search_trees, List<Element> in_range, int lower, int upper) {
		this.setName("RangeTask");
		this.search_trees=search_trees;
		this.in_range=in_range;
		this.lower=lower;
		this.upper=upper;
	}
	
	@Override
	public void run() {
		while (!can_finish)
		{
			try {
				synchronized (this) {
					this.wait();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void execute() {
		for (SearchTree search_tree : search_trees)
		{
			search_tree.getRange(in_range, lower, upper);
		}
	}
}
