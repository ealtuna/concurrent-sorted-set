package com.concurrentsortedset.test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.concurrentsortedset.sortedset.tree.Element;
import com.concurrentsortedset.sortedset.tree.RedBlackTree;
import com.concurrentsortedset.sortedset.tree.SearchTree;
import com.concurrentsortedset.sortedset.tree.background.BackgroundProcceser;

public class RangeBackgroundProccesTest {

	public static void main(String[] args) {
		BackgroundProcceser process = new BackgroundProcceser();
		SearchTree empty_search_tree = new RedBlackTree();
		process.processAdd(empty_search_tree, new Element(1, 5));
		process.processAdd(empty_search_tree, new Element(2, 10));
		process.processAdd(empty_search_tree, new Element(3, 20));
		List<Element> empty_list = new ArrayList<Element>();
		List<SearchTree> search_trees = new LinkedList<SearchTree>();
		search_trees.add(empty_search_tree);
		process.processRange(search_trees, empty_list, 1, 10);
		process.processRange(search_trees, empty_list, 1, 10);
		process.processRange(search_trees, empty_list, 1, 10);
		process.processRange(search_trees, empty_list, 1, 10);
	}

}
