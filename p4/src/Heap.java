import java.util.List;
import java.util.NoSuchElementException;
import java.util.Comparator;

import java.util.ArrayList;

/**
 * TODO: Complete the implementation of this class.
 * 
 * The keys in the heap must be stored in an array.
 * 
 * There may be duplicate keys in the heap.
 * 
 * The constructor takes an argument that specifies how objects in the 
 * heap are to be compared. This argument is a java.util.Comparator, 
 * which has a compare() method that has the same signature and behavior 
 * as the compareTo() method found in the Comparable interface. 
 * 
 * Here are some examples of a Comparator<String>:
 *    (s, t) -> s.compareTo(t);
 *    (s, t) -> t.length() - s.length();
 *    (s, t) -> t.toLowerCase().compareTo(s.toLowerCase());
 *    (s, t) -> s.length() <= 3 ? -1 : 1;  
 */

public class Heap<E> implements PriorityQueue<E> {
	protected List<E> keys;
	private Comparator<E> comparator;

	/**
	 * TODO
	 * 
	 * Creates a heap whose elements are prioritized by the comparator.
	 */
	public Heap(Comparator<E> comparator) {
		keys = new ArrayList<E>();
		this.comparator = comparator;
	}

	/**
	 * Returns the comparator on which the keys in this heap are prioritized.
	 */
	public Comparator<E> comparator() {
		return comparator;
	}

	/**
	 * TODO
	 * 
	 * Returns the top of this heap. This will be the highest priority key. 
	 * @throws NoSuchElementException if the heap is empty.
	 */
	public E peek() {
		if (size() == 0) // If heap is empty
			throw new NoSuchElementException();

		return keys.get(0);
	}

	/**
	 * TODO
	 * 
	 * Inserts the given key into this heap. Uses siftUp().
	 */
	public void insert(E key) {
		keys.add(key);
		siftUp(size() - 1);
	}

	/**
	 * TODO
	 * 
	 * Removes and returns the highest priority key in this heap.
	 * @throws NoSuchElementException if the heap is empty.
	 */
	public E delete() {
		if (size() == 0) // If empty
			throw new NoSuchElementException();
		if (size() == 1) // If heap has only 1 element, then no need to sift after removal
			return keys.remove(0);
		
		E ans = keys.get(0);
		keys.set(0, keys.remove(size() - 1));
		siftDown(0);
		return ans;
	}

	/**
	 * TODO
	 * 
	 * Restores the heap property by sifting the key at position p down
	 * into the heap.
	 */
	public void siftDown(int p) {
		int leftChild = getLeft(p);
		if (leftChild < size()){ // Do nothing if leftChild is out of the bound
			int minChild = leftChild;
			int rightChild = leftChild + 1;
			if(rightChild < size()){ // Check if rightChild is out of bound
				if ( comparator.compare(keys.get(leftChild), keys.get(rightChild)) > 0 )
					minChild = rightChild; // Check if rightChild is "smaller"
			}
			if ( comparator.compare(keys.get(p), keys.get(minChild)) > 0 ){
				swap(p, minChild);
				siftDown(minChild);
			}
		}
	}

	/**
	 * TODO
	 * 
	 * Restores the heap property by sifting the key at position q up
	 * into the heap. (Used by insert()).
	 */
	public void siftUp(int q) {
		// The first "if" check is not necessary if we change the comparison below to <
		// But we are doing this to align with the choice given in testing.java
		// This does not add the program complexity
		if (q == 0) 
			return; 
		
		int parent = getParent(q);
		if (parent >= 0){ // Check if parent is out of bound
			if (comparator.compare(keys.get(q), keys.get(parent)) <= 0){
				swap(q, parent);
				siftUp(parent);
			}
		}			
	}

	/**
	 * TODO
	 * 
	 * Exchanges the elements in the heap at the given indices in keys.
	 */
	public void swap(int i, int j) {
		E temp = keys.get(i);
		keys.set(i, keys.get(j));
		keys.set(j, temp);
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
	 * TODO
	 * 
	 * Returns the index of the left child of p.
	 */
	public static int getLeft(int p) {
		return 2 * p + 1;
	}

	/**
	 * TODO
	 * 
	 * Returns the index of the right child of p.
	 */
	public static int getRight(int p) {
		return 2 * p + 2;
	}

	/**
	 * TODO
	 * 
	 * Returns the index of the parent of p.
	 */
	public static int getParent(int p) {
		return (p - 1) / 2;
	}
}
