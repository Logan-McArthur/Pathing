package com.PromethiaRP.Draeke.AStar;

import java.util.ArrayList;
import java.util.List;

public class BinaryHeap {

	private List<Integer> heap = new ArrayList<Integer>();
	
	public BinaryHeap() {
		
	}
	
	private void swap(int a, int b) {
		Integer tempA = heap.get(a);
		Integer tempB = heap.get(b);
		heap.remove(a);
		heap.set(a, tempB);
		heap.remove(b);
		heap.set(b, tempA);
		
	}
	
	public void insert(int value) {
		
	}
	
	public void remove(int value) {
		
	}
	
	private void sort() {
		
	}
	
	private int getParent(int index) {
		return 0;
	}
	
	public int getFirst() {
		return heap.get(0).intValue();
	}
	
	public int getLast() {
		return heap.get(heap.size()-1).intValue();
	}
}
