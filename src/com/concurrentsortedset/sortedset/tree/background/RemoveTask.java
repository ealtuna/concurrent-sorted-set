package com.concurrentsortedset.sortedset.tree.background;

import com.concurrentsortedset.sortedset.tree.Element;
import com.concurrentsortedset.sortedset.tree.SearchTree;

public class RemoveTask implements BackgroundTask {

	Element element;
	SearchTree search_tree;
	
	public RemoveTask(SearchTree search_tree, Element element) {
		this.element=element;
		this.search_tree=search_tree;
	}
	
	@Override
	public void execute() {
		search_tree.remove(element);
	}

}
