package com.concurrentsortedset.sortedset;

import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

import com.concurrentsortedset.sortedset.tree.Element;
import com.concurrentsortedset.sortedset.tree.RedBlackTree;
import com.concurrentsortedset.sortedset.tree.SearchTree;

public class SortedSetState {
	public HashMap<Integer, Element> elements_table = new HashMap<Integer, Element>();
	public SearchTree search_tree = new RedBlackTree();
	public ReentrantLock write_lock = new ReentrantLock();
}
