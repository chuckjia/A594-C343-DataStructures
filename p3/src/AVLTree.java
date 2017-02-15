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
    //Node q = findPivotES(p);
    
    // Case 1: Whole tree is not overweight/no pivots
    //if (q == null) 
    	//return p;
    
    // Case 2: LL
    
    
    
    
    return p;
    
  }
  
  // Find overweight nodes with early stop
  // @return null if no one found, i.e. the tree is not overweight
  
  private Node findPivotES(Node p){ 
	  while (p != null && !p.isOverWeight()){
		  if (p.left.height == p.right.height)
			  return null;
		  p = p.parent;
	  }
	  if (p == null)
		  return null;
	  return p;
  }

  // Find overweight nodes
  // @return null if no one found, i.e. the tree is not overweight
  private Node findPivot(Node p){
	  while (p != null && !p.isOverWeight())
		  p = p.parent;
	  
	  if (p == null)
		  return null;
	  return p;
  }
  
  private Node ll(Node k1){
	  Node k2 = k1.left;
	  Node b = k1.right;
	  if (k1.parent == null){
		  // Modify k2
		  root = k2;
		  k2.parent = null;
	  }
	  return null;
  }

}









