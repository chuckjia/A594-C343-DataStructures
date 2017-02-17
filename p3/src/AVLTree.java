import java.util.function.BiPredicate;

/**
 * TODO: This is your second major task.
 *
 * This class implements a generic height-balanced binary search tree, 
 * using the AVL algorithm. Beyond the constructor, only the insert()
 * method needs to be implemented. All other methods are unchanged.
 */

public class AVLTree<K> extends BinarySearchTree<K> {

  /**
   * Creates an empty AVL tree as a BST organized according to the
   * lessThan predicate.
   */
  public AVLTree(BiPredicate<K, K> lessThan) {
    super(lessThan);
  }

  /**
   * TODO
   * 
   * Inserts the given key into this AVL tree such that the ordering 
   * property for a BST and the balancing property for an AVL tree are
   * maintained.
   */
  
  public Node insert(K key) {
	  Node p = super.insert(key);
	  Node q = findPivotES(p); // Using the early stop version of findPivot

	  // Case: whole tree is not overweight/ has no pivots
	  if (q == null) 
		  return p;

	  do{
		  // Rotations
		  if (q.balFactor() < 0){ // L
			  if (q.left.balFactor() < 0) // LL rotation 
				  q = rotateLL(q);
			  else
				  q = rotateLR(q); // LR rotation
		  } else { // R
			  if (q.right.balFactor() > 0)
				  q = rotateRR(q); // RR rotation
			  else
				  q = rotateRL(q); // RL rotation
		  }
		  // After the first pivot, findPivot mainly deals with height fixing
		  q = findPivot(q); 
	  } while (q != null);

	  return p;
  }
  
  /* 
   * Find overweight nodes with early stop
   * For use when no rotations occur. This optimizes the stopping time
   * @return null if no one found, i.e. the tree is not overweight
   */
  private Node findPivotES(Node p){ 
	  p = p.parent;
	  while (p != null && !p.isOverWeight()){
		  if (p.balFactor() == 0) // As long as p is balanced we are done
			  return null;
		  p = p.parent;
		  // No need to fix heights, as they are fixed in BST.insert
	  }
	  if (p == null)
		  return null;
	  return p;
  }

  /* 
   * Find overweight nodes
   * Heights of nodes are fixed along the traversal
   * @return null if no one found, i.e. the tree is not overweight
   */
  private Node findPivot(Node p){
	  p = p.parent;
	  while (p != null && !p.isOverWeight()){
		  p.fixHeight();
		  p = p.parent;
	  }
	  
	  if (p == null)
		  return null;
	  return p;
  }
  
  
  /*
   * Performs single rotation type LL
   * */
  private Node rotateLL(Node k1){
	  Node k2 = k1.left;
	  Node b = k2.right;

	  // Attach k2 and its parent 
	  if (k1.parent == null){ // Case 1: k1 is root
		  root = k2;
		  k2.parent = null;		  
	  } else { // Case 2: k1 is not root	
		  Node k0 = k1.parent;
		  if (k0.left == k1)
			  k0.left = k2;
		  else
			  k0.right = k2;

		  k2.parent = k0;
	  }

	  // Modify k2's children
	  k2.right = k1;

	  // Modify k1
	  k1.parent = k2;
	  k1.left = b;

	  // Modify b
	  if (b != null)
		  b.parent = k1;

	  k1.fixHeight();
	  k2.fixHeight();
	  return k2;
  }
  
  
  /*
   * Performs single rotation type RR
   * */
  
  private Node rotateRR(Node k1){
	  Node k2 = k1.right;
	  Node b = k2.left;

	  // Attach k2 and its parent
	  if (k1.parent == null){ // Case 1: k1 is root
		  root = k2;
		  k2.parent = null;
	  } else { // Case 2: k1 is not root
		  Node k0 = k1.parent;
		  if (k0.left == k1)
			  k0.left = k2;
		  else
			  k0.right = k2;

		  k2.parent = k0;
	  }

	  // Modify k2's children	  
	  k2.left = k1;

	  // Modify k1
	  k1.right = b;
	  k1.parent = k2;

	  // Modify b
	  if (b != null)
		  b.parent = k1;

	  k1.fixHeight();
	  k2.fixHeight();
	  return k2;
  }
  
  
  /*
   * Performs double rotation type LR
   * */
  private Node rotateLR(Node k1){
	  Node k2 = k1.left;
	  Node k3 = k2.right;
	  Node b = k3.left;
	  Node c = k3.right;

	  // Attach k3 and its parent
	  if (k1.parent == null){ // Case 1: k1 is root
		  root = k3;
		  k3.parent = null;
	  } else { // Case 2: k1 is not root
		  // Modify k3's parent
		  Node k0 = k1.parent;
		  if (k0.left == k1)
			  k0.left = k3;
		  else
			  k0.right = k3;

		  k3.parent = k0;
	  }

	  // Modify k3's children
	  k3.left = k2;
	  k3.right = k1;

	  // Modify k2
	  k2.right = b;
	  k2.parent = k3;

	  // Modify k1
	  k1.left = c;
	  k1.parent = k3;

	  // Modify b
	  if (b != null)
		  b.parent = k2;

	  // Modify c
	  if (c != null)
		  c.parent = k1;

	  k1.fixHeight();
	  k2.fixHeight();
	  k3.fixHeight();

	  return k3;
  }
  
  
  /*
   * Performs double rotation type RL
   * */
  private Node rotateRL(Node k1){
	  Node k2 = k1.right;
	  Node k3 = k2.left;
	  Node b = k3.left;
	  Node c = k3.right;

	  // Attach k3 and its parent
	  if (k1.parent == null){ // Case 1: k1 is root
		  root = k3;
		  k3.parent = null;
	  } else { // Case 2: k1 is not root
		  // Modify k3's parent
		  Node k0 = k1.parent;
		  if (k0.left == k1)
			  k0.left = k3;
		  else
			  k0.right = k3;

		  k3.parent = k0;
	  }

	  // Modify k3's children
	  k3.left = k1;
	  k3.right = k2;

	  // Modify k2
	  k2.left = c;
	  k2.parent = k3;

	  // Modify k1
	  k1.right = b;
	  k1.parent = k3;

	  // Modify b
	  if (b != null)
		  b.parent = k1;

	  // Modify c
	  if (c != null)
		  c.parent = k2;

	  k1.fixHeight();
	  k2.fixHeight();
	  k3.fixHeight();

	  return k3;
  }
}









