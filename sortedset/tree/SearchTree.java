package com.concurrentsortedset.sortedset.tree;

import java.util.List;

public interface SearchTree {
	
	public void add(Element e);
	
	public void remove(Element e);
	
	public void getRange(List<Element> elements_in_range, int lower, int upper); 
}
