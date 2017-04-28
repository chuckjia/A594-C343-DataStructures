import java.util.List;
import java.util.NoSuchElementException;
import java.util.Comparator;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Arrays;

public class Heap implements PriorityQueue<Coord> {
	protected List<Coord> keys;
	private Comparator<Coord> comparator;
	private int[][] posArray; // Store the positions of elements 

	/**
	 * Creates a heap whose elements are prioritized by the comparator.
	 */
	public Heap(Comparator<Coord> comparator) {
		keys = new ArrayList<Coord>();
		this.comparator = comparator;
	}

	
	/**
	 * Creates a heap with preset internal size dim.width * dim.height
	 * @param comparator
	 * @param dim
	 * @author Chuck Jia
	 */
	public Heap(Comparator<Coord> comparator, Dimension dim) {
		int width = dim.width, height = dim.height;
		keys = new ArrayList<Coord>(width * height);
		this.comparator = comparator;
		posArray = new int[width][height];
		for (int[] a : posArray)
			Arrays.fill(a, -1);
	}

	
	/**
	 * Returns the comparator on which the keys in this heap are prioritized.
	 */
	public Comparator<Coord> comparator() {
		return comparator;
	}
	

	/** 
	 * Returns the top of this heap. This will be the highest priority key. 
	 * @throws NoSuchElementException if the heap is empty.
	 */
	public Coord peek() {
		if (size() == 0) // If heap is empty
			throw new NoSuchElementException();
		return keys.get(0);
	}
	

	/**
	 * Inserts the given key into this heap. Uses siftUp().
	 */
	public void insert(Coord coord) {
		keys.add(coord);
		siftUp(size() - 1);
	}
	

	/**
	 * Removes and returns the highest priority key in this heap.
	 * @throws NoSuchElementException if the heap is empty.
	 */
	public Coord delete() {
		// If empty
		if (size() == 0) 
			throw new NoSuchElementException();
		// If heap has only 1 element, then no need to sift after removal
		if (size() == 1){ 
			Coord ans = keys.remove(0);
			posArray[ans.getX()][ans.getY()] = -1; // Setting the only element's position to -1
			return ans;
		}
		// If the heap has at least 2 elements
		Coord ans = keys.get(0);
		posArray[ans.getX()][ans.getY()] = -1;
		keys.set(0, keys.remove(size() - 1)); // Copy the last one to the first position
		siftDown(0);
		return ans;
	}
	

	/**
	 * Delete a certain element from the heap.
	 * If coord is not in this heap, then do nothing
	 * @param coord The Coord to be deleted
	 * @author Chuck Jia
	 */
	public void delete(Coord coord) {
		int loc = posArray[coord.getX()][coord.getY()];
		if (loc != -1){ // Do nothing if coord is not in this heap
			siftUpToTop(loc);
			delete();
		}
	}

	
	/**
	 * Update the heap when the comparator changes.
	 * If coord is not in the heap, then simply add it into the heap.
	 * @param coord The Coord to be updated
	 * @author Chuck Jia
	 */
	public void update(Coord coord) {
		int loc = posArray[coord.getX()][coord.getY()];
		if (loc != -1) { // If coord already exists in heap
			siftUpToTop(loc);
			delete();
		}
		insert(coord);
	}
	

	/**
	 * Restores the heap property by sifting the key at position p down
	 * into the heap.
	 */
	private void siftDown(int p) {
		Coord pElement = keys.get(p);
		int leftChild = getLeft(p);
		if (leftChild < size()){ // Do nothing if leftChild is out of the bound
			int minChild = leftChild;
			int rightChild = leftChild + 1;
			if(rightChild < size()) // Check if rightChild is out of bound
				if (comparator.compare(keys.get(leftChild), keys.get(rightChild)) > 0)
					minChild = rightChild; // Check if rightChild is "smaller"
			Coord minChildElement = keys.get(minChild);
			if (comparator.compare(pElement, minChildElement) > 0) {
				posArray[minChildElement.getX()][minChildElement.getY()] = p;
				swap(p, minChild);
				siftDown(minChild);
			} else
				posArray[pElement.getX()][pElement.getY()] = p; // No switch with minChild, sift-down finished
		} else 
			posArray[pElement.getX()][pElement.getY()] = p; // Already sift down to bottom
	}


	/**
	 * Restores the heap property by sifting the key at position q up
	 * into the heap. (Used by insert()).
	 */
	private void siftUp(int q) {		
		int parent = getParent(q);
		Coord qElement = keys.get(q);
		Coord parentElement = keys.get(parent);
		if (parent >= 0){ // Check if parent is out of bound
			if (comparator.compare(qElement, parentElement) < 0){
				posArray[parentElement.getX()][parentElement.getY()] = q;
				swap(q, parent);
				siftUp(parent);
			} else 
				posArray[qElement.getX()][qElement.getY()] = q;
		}			
	}
	
	
	/**
	 * Sift one existing element to the top. Used by delete(Coord) method.
	 * @param q The index of the element to be sift up.
	 * @author Chuck Jia
	 */
	private void siftUpToTop(int q) {
		if (q == 0) {
			Coord coord = keys.get(q);
			posArray[coord.getX()][coord.getY()] = 0; // Store the position of the element at 0 as 0
			return;
		}

		int parent = getParent(q);
		if (parent >= 0) { // Check if parent is out of bound
			Coord parentElement = keys.get(parent);
			posArray[parentElement.getX()][parentElement.getY()] = q;
			swap(q, parent);
			siftUpToTop(parent);
		}			
	}
	

	/** 
	 * Exchanges the elements in the heap at the given indices in keys.
	 */
	private void swap(int i, int j) {
		Coord temp = keys.get(i);
		keys.set(i, keys.get(j));
		keys.set(j, temp);
	}
	

	public boolean contains(Coord coord) {
		return posArray[coord.getX()][coord.getY()] != -1;
	}
	

	/**
	 * Returns the number of keys in this heap.
	 */
	public int size() {
		return keys.size();
	}
	

	/**
	 * Returns a textual representation of this heap.
	 */
	public String toString() {
		return keys.toString();
	}
	

	/**
	 * Returns the index of the left child of p.
	 */
	private static int getLeft(int p) {
		return 2 * p + 1;
	}
	

	/** 
	 * Returns the index of the parent of p.
	 */
	private static int getParent(int p) {
		return (p - 1) / 2;
	}
}
