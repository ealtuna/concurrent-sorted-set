package com.concurrentsortedset.test;

import java.util.LinkedList;
import java.util.List;

import com.concurrentsortedset.sortedset.tree.Element;
import com.concurrentsortedset.sortedset.tree.RedBlackTree;

public class RedBlackPropertiesTest {

	public static void main(String[] args) {
		RedBlackTree tree = new RedBlackTree();
		tree.add(new Element(12,28));
		tree.add(new Element(13,38));
		tree.add(new Element(14,7));
		tree.add(new Element(15,12));
		tree.add(new Element(16,15));
		tree.add(new Element(17,20));
		tree.add(new Element(18,35));
		tree.add(new Element(19,39));
		tree.add(new Element(20,3));
		tree.add(new Element(1,26));
		tree.add(new Element(2,17));
		tree.add(new Element(3,41));
		tree.add(new Element(4,14));
		tree.add(new Element(5,21));
		tree.add(new Element(6,30));
		tree.add(new Element(7,47));
		tree.add(new Element(8,10));
		tree.add(new Element(9,16));
		tree.add(new Element(10,19));
		tree.add(new Element(11,23));
		
		List<Element> list = new LinkedList<Element>();
		tree.getRange(list, 17, 30);
		for (Element element : list)
			System.out.println(element.key + "->" + element.score);
	}

}
