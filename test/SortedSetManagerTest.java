package com.concurrentsortedset.test;

import java.util.ArrayList;
import java.util.List;

import com.concurrentsortedset.sortedset.SortedSetManager;
import com.concurrentsortedset.sortedset.tree.Element;

public class SortedSetManagerTest {
	
	/**
	 * 	<SIZE> <set1> [0]
		<ADD> <set1> <k1> <1>
		<GET> <set1> <k1> [1]
		<SIZE> <set1> [1]
		<ADD> <set1> <k1> <2>
		<GET> <set1> <k1> [2]
		<ADD> <set1> <k2> <3>
		<GETRANGE> <set1> <-1> <3> <3> [k2] [3] [-1]
		<ADD> <set2> <k3> <1>
		<GETRANGE> <set1> <set2> <-1> <0> <INT_MAX> [k3] [1] [k1] [2] [k2] [3] [-1]
		<REM> <set1> <k2>
		<GETRANGE> <set1> <set2> <-1> <0> <INT_MAX> [k3] [1] [k1] [2] [-1]
	 */
	public static void main(String[] args) {
		SortedSetManager manager = new SortedSetManager();
		int set_size=manager.size(1);
		System.out.println("The size of set is: " + set_size);
		manager.add(1, 1, 1);
		int score_retrieved=manager.get(1, 1);
		System.out.println("The score value of set is: " + score_retrieved);
		set_size=manager.size(1);
		System.out.println("The size of set is: " + set_size);
		manager.add(1, 1, 2);
		score_retrieved=manager.get(1, 1);
		System.out.println("The score value of set is: " + score_retrieved);
		manager.add(1, 2, 3);
		List<Integer> sets = new ArrayList<Integer>();
		sets.add(1);
		List<Element> range = manager.getRange(sets, 3, 3);
		System.out.println("GETRANGE:");
		for (Element e : range)
		{
			System.out.println(e.key + "->" + e.score);
		}
		manager.add(2, 3, 1);
		sets.add(2);
		range = manager.getRange(sets, 0, Integer.MAX_VALUE);
		System.out.println("GETRANGE:");
		for (Element e : range)
		{
			System.out.println(e.key + "->" + e.score);
		}
		manager.remove(1, 2);
		range = manager.getRange(sets, 0, Integer.MAX_VALUE);
		System.out.println("GETRANGE:");
		for (Element e : range)
		{
			System.out.println(e.key + "->" + e.score);
		}
	}
}
