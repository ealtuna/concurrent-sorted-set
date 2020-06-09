package com.concurrentsortedset.sortedset.tree;

import java.util.List;

public class RedBlackTree implements SearchTree {

	private Node root;
	private Node nill;
	
	public RedBlackTree() {
		nill=new Node();
		root=nill;
	}
	
	private void updateAddedRangeUp(Node updated)
	{
		Node current = updated;
		while (current.parent!=nill)
		{
			if (current.min<current.parent.min) current.parent.min=current.min;
			else if (current.max>current.parent.max) current.parent.max=current.max;
			else return;
			current=current.parent;
		}
	}
	
	private void updatedDeletedRangeUp(Node deleted)
	{
		Node current = deleted;
		while (current!=nill)
		{
			int new_min;
			if (current.left==nill)
			{
				new_min=current.score;
			}else
			{
				new_min=current.left.min;
			}
			int new_max;
			if (current.rigth==nill)
			{
				new_max=current.score;
			}else
			{
				new_max=current.rigth.max;
			}
			if (new_min==current.min && new_max==current.max) return;
			current.min=new_min;
			current.max=new_max;
			current=current.parent;
		}
	}
	
	public void add(Element e)
	{
		Node past = nill;
		Node current = root;
		while (current!=nill)
		{
			past=current;
			if (e.score==current.score)
			{
				current.keys.put(e.key, e);
				return;
			}
			if (e.score<current.score)
			{
				current=current.left;
			}else
			{
				current=current.rigth;
			}
		}
		Node new_node = new Node(e);
		new_node.parent=past;
		if (past==nill)
		{
			root=new_node;
		}else if (new_node.score<past.score) {
			past.left=new_node;
		}else {
			past.rigth=new_node;
		}
		new_node.left=nill;
		new_node.rigth=nill;
		updateAddedRangeUp(new_node);
		insertFixup(new_node);
	}
	
	private void insertFixup(Node node)
	{
		Node z = node;
		while (z.parent.color==Node.RED)
		{
			if (z.parent==z.parent.parent.left)
			{
				Node y = z.parent.parent.rigth;
				if (y.color==Node.RED)
				{
					z.parent.color=Node.BLACK;
					y.color=Node.BLACK;
					z.parent.parent.color=Node.RED;
					z=z.parent.parent;
				}else
				{
					if (z==z.parent.rigth)
					{
						z=z.parent;
						leftRotate(z);
					}
					z.parent.color=Node.BLACK;
					z.parent.parent.color=Node.RED;
					rightRotate(z.parent.parent);
				}	
			}else
			{
				Node y = z.parent.parent.left;
				if (y.color==Node.RED)
				{
					z.parent.color=Node.BLACK;
					y.color=Node.BLACK;
					z.parent.parent.color=Node.RED;
					z=z.parent.parent;
				}else
				{
					if (z==z.parent.left)
					{
						z=z.parent;
						rightRotate(z);
					}
					z.parent.color=Node.BLACK;
					z.parent.parent.color=Node.RED;
					leftRotate(z.parent.parent);
				}	
			}
		}
		root.color=Node.BLACK;
	}

	@Override
	public void remove(Element e) {
		Node z = root;
		while (z!=null && z.score!=e.score)
		{
			if (e.score<z.score) z=z.left;
			else z=z.rigth;
		}
		if (z==nill) return;
		if (z.keys.size()>1)
		{
			z.keys.remove(e.key);
			return;
		}
		int y_original_color = z.color;
		Node x;
		if (z.left==nill)
		{
			x=z.rigth;
			Node parent = z.parent;
			transplant(z, z.rigth);
			updatedDeletedRangeUp(parent);
		}else if (z.rigth==nill)
		{
			x=z.left;
			Node parent = z.parent;
			transplant(z, z.left);
			updatedDeletedRangeUp(parent);
		}else
		{
			Node y = minimun(z.rigth);
			Node minimun_parent=y.parent;
			y_original_color = y.color;
			x=y.rigth;
			if (y.parent==z)
			{
				x.parent=y;
			}else
			{
				transplant(y, y.rigth);
				y.rigth=z.rigth;
				y.rigth.parent=y;
			}
			transplant(z, y);
			y.left=z.left;
			y.left.parent=y;
			y.color=z.color;
			updatedDeletedRangeUp(minimun_parent);
		}
		if (y_original_color==Node.BLACK)
		{
			deleteFixup(x);
		}
	}
	
	private void deleteFixup(Node node)
	{
		Node x=node;
		while (x!=root && x.color==Node.BLACK)
		{
			if (x==x.parent.left)
			{
				Node w = x.parent.rigth;
				if (w.color==Node.RED)
				{
					w.color=Node.BLACK;
					x.parent.color=Node.RED;
					leftRotate(x.parent);
					w=x.parent.rigth;
				}
				if (w.left.color==Node.BLACK && w.rigth.color==Node.BLACK)
				{
					w.color=Node.RED;
					x=x.parent;
				}else
				{
					if (w.rigth.color==Node.BLACK)
					{
						w.left.color=Node.BLACK;
						w.rigth.color=Node.BLACK;
						w.color=Node.RED;
						rightRotate(w);
						w=x.parent.rigth;
					}
					w.color=x.parent.color;
					x.parent.color=Node.BLACK;
					w.rigth.color=Node.BLACK;
					leftRotate(x.parent);
					x=root;
				}	
			}else
			{
				Node w = x.parent.left;
				if (w.color==Node.RED)
				{
					w.color=Node.BLACK;
					x.parent.color=Node.RED;
					rightRotate(x.parent);
					w=x.parent.left;
				}
				if (w.rigth.color==Node.BLACK && w.left.color==Node.BLACK)
				{
					w.color=Node.RED;
					x=x.parent;
				}else
				{
					if (w.left.color==Node.BLACK)
					{
						w.rigth.color=Node.BLACK;
						w.left.color=Node.BLACK;
						w.color=Node.RED;
						leftRotate(w);
						w=x.parent.left;
					}
					w.color=x.parent.color;
					x.parent.color=Node.BLACK;
					w.left.color=Node.BLACK;
					rightRotate(x.parent);
					x=root;
				}
			}
		}
		x.color=Node.BLACK;
	}
	
	private void transplant(Node old, Node updated)
	{
		if (old.parent==nill)
		{
			root=updated;
		}else if (old==old.parent.left)
		{
			old.parent.left=updated;
		}else old.parent.rigth=updated;
		updated.parent=old.parent;
	}
	
	public void leftRotate(Node node)
	{
		Node rigth_child = node.rigth;
		node.rigth = rigth_child.left;
		if (rigth_child.left!=nill)
		{
			rigth_child.left.parent=node;
		}
		rigth_child.parent=node.parent;
		if (node.parent==nill)
		{
			root=rigth_child;
		}else if (node==node.parent.left)
		{
			node.parent.left=rigth_child;
		}else
		{
			node.parent.rigth=rigth_child;
		}
		rigth_child.left=node;
		node.parent=rigth_child;
		updatedDeletedRangeUp(node);
	}
	
	public void rightRotate(Node node)
	{
		Node left_child = node.left;
		node.left = left_child.rigth;
		if (left_child.rigth!=nill)
		{
			left_child.rigth.parent=node;
		}
		left_child.parent=node.parent;
		if (node.parent==nill)
		{
			root=left_child;
		}else if (node==node.parent.rigth)
		{
			node.parent.rigth=left_child;
		}else
		{
			node.parent.left=left_child;
		}
		left_child.rigth=node;
		node.parent=left_child;
		updatedDeletedRangeUp(node);
	}
	
	private Node minimun(Node node)
	{
		Node current = node;
		while (current.left!=nill)
			current=current.left;
		return current;
	}
	
	public Node getNode(Element element) throws Exception
	{
		Node current=root;
		while (current!=nill)
		{
			if (current.score==element.score) return current;
			if (element.score<current.score) current = current.left;
			else current = current.rigth;
		}
		throw new Exception("Element not found");
	}

	@Override
	public void getRange(List<Element> elements_in_range, int lower, int upper) {
		getRange(elements_in_range, lower, upper, root);
	}
	
	private void getRange(List<Element> elements_in_range, int lower, int upper, Node current)
	{
		if (current==nill) return;
		if (current.left.min<=upper || current.left.max>=lower) getRange(elements_in_range, lower, upper, current.left);
		if (current.score>=lower && current.score<=upper)
		{
			elements_in_range.addAll(current.keys.values());
		}
		if (current.rigth.min<=upper || current.rigth.max>=lower) getRange(elements_in_range, lower, upper, current.rigth);
	}

}
