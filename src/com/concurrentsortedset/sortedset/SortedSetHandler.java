package com.concurrentsortedset.sortedset;

import com.concurrentsortedset.sortedset.tree.Element;
import com.concurrentsortedset.sortedset.tree.background.BackgroundProcceser;

public class SortedSetHandler {
	
	private SortedSetState sorted_set_state;
	
	public SortedSetHandler(SortedSetState sorted_set_state) {
		this.sorted_set_state = sorted_set_state;
	}
	
	public void add(BackgroundProcceser background_processer, int key, int score)
	{
		sorted_set_state.write_lock.lock();//lock
		Element element = new Element(key, score);
		Element stored_element;
		stored_element = sorted_set_state.elements_table.get(key);
		sorted_set_state.elements_table.put(key, element);
		sorted_set_state.write_lock.unlock();//unlock
		if (stored_element != null)
		{
			background_processer.processRemove(sorted_set_state.search_tree, stored_element);
		}
		background_processer.processAdd(sorted_set_state.search_tree, element);
	}
	
	public void remove(BackgroundProcceser background_processer, int key)
	{
		sorted_set_state.write_lock.lock();//lock
		Element element = sorted_set_state.elements_table.remove(key);
		sorted_set_state.write_lock.unlock();//unlock
		if (element == null) return;
		background_processer.processRemove(sorted_set_state.search_tree, element);
	}
	
	public int size()
	{
		sorted_set_state.write_lock.lock();
		sorted_set_state.write_lock.unlock();
		int size = sorted_set_state.elements_table.size();
		return size;
	}
	
	public int get(int key)
	{
		sorted_set_state.write_lock.lock();
		sorted_set_state.write_lock.unlock();
		Element element = sorted_set_state.elements_table.get(key);
		if (element == null) return -1;
		return  element.score;
	}
}
