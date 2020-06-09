package com.concurrentsortedset.test;

import com.concurrentsortedset.sortedset.tree.Element;
import com.concurrentsortedset.sortedset.tree.RedBlackTree;

public class TreeConstructionTest {
	public static void main(String[] args) throws Exception {
		RedBlackTree tree = new RedBlackTree();
		tree.add(new Element(2,4));
		tree.add(new Element(1,6));
		tree.add(new Element(3,7));
		tree.add(new Element(4,1));
		tree.add(new Element(5,2));
		tree.add(new Element(6,2));
		tree.add(new Element(7,5));
		tree.leftRotate(tree.getNode(new Element(2,4)));
		tree.rightRotate(tree.getNode(new Element(1,6)));
		tree.remove(new Element(2,4));
		tree.remove(new Element(7,5));
		tree.remove(new Element(1,6));
		tree.remove(new Element(6,2));
	}
}
