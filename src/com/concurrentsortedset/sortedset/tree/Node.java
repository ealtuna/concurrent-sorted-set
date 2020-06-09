package com.concurrentsortedset.sortedset.tree;

import java.util.HashMap;

public class Node {
	
	public static final int RED=0;
	public static final int BLACK=1;
	public int score;
	public HashMap<Integer, Element> keys;
	
	public Node parent;
	
	public Node left;
	public Node rigth;
	
	public int min;
	public int max;
	
	public int color;
	
	public Node(Element e) {
		parent=null;
		left=null;
		rigth=null;
		score=e.score;
		keys=new HashMap<Integer, Element>();
		keys.put(e.key, e);
		min=max=score;
		color=RED;
	}
	
	public Node()
	{
		score=-1;
		parent=this;
		left=this;
		rigth=this;
		color=BLACK;
	}
}
