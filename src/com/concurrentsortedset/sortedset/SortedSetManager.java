package com.concurrentsortedset.sortedset;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.concurrentsortedset.sortedset.tree.Element;
import com.concurrentsortedset.sortedset.tree.SearchTree;
import com.concurrentsortedset.sortedset.tree.background.BackgroundProcceser;

public class SortedSetManager {
	
	private static ConcurrentHashMap<Integer, SortedSetState> sets = new ConcurrentHashMap<Integer, SortedSetState>();
	private static BackgroundProcceser background_processer = new BackgroundProcceser();
	
	public SortedSetManager() {
	}
	
	public void add(int set, int key, int score)
	{
		SortedSetState current_set_state;
		synchronized (sets) {
			current_set_state = sets.get(set);
			if (current_set_state == null)
			{
				System.out.println("[SERVER] Set created: " + set);
				current_set_state = new SortedSetState();
				sets.put(set, current_set_state);
			}
		}
		SortedSetHandler sorted_set_handler = new SortedSetHandler(current_set_state);
		sorted_set_handler.add(background_processer, key, score);
	}
	
	public void remove(int set, int key)
	{
		SortedSetState current_set_state;
		synchronized (sets) {
			current_set_state = sets.get(set);
			if (current_set_state == null) return;
		}
		SortedSetHandler sorted_set_handler = new SortedSetHandler(current_set_state);
		sorted_set_handler.remove(background_processer, key);
	}
	
	public int size(int set)
	{
		SortedSetState current_set_state;
		synchronized (sets) {
			current_set_state = sets.get(set);
			if (current_set_state == null) return 0;
		}
		SortedSetHandler sorted_set_handler = new SortedSetHandler(current_set_state);
		return  sorted_set_handler.size();
	}
	
	public int get(int set, int key)
	{
		SortedSetState current_set_state;
		synchronized (sets) {
			current_set_state = sets.get(set);
			if (current_set_state == null){
				System.out.println("[SERVER] No such set.");
				return -1;
			} 
		}
		SortedSetHandler sorted_set_handler = new SortedSetHandler(current_set_state);
		return sorted_set_handler.get(key);
	}
	
	public List<Element> getRange(List<Integer> sets_id, int lower, int upper)
	{
		List<SearchTree> search_trees = new LinkedList<SearchTree>();
		List<Element> elements_in_range = new LinkedList<Element>();
		synchronized (sets) {
			for (Integer set_id : sets_id)
			{
				SortedSetState current_set = sets.get(set_id);
				if (current_set!=null) {
					search_trees.add(current_set.search_tree);
				}
			}
		}
		if (search_trees.size() > 0)
		{
			background_processer.processRange(search_trees, elements_in_range, lower, upper);
		}
		return elements_in_range;
	}
}
