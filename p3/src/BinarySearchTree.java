import java.util.LinkedList;
import java.util.List;
import java.util.function.BiPredicate;

//import BinarySearchTree.Node;
//import java.util.Iterator;

/**
 * TODO: This is your first major task.
 * 
 * This class implements a generic unbalanced binary search tree (BST).
 */

public class BinarySearchTree<K> implements Tree<K> {
  
  /**
   * A Node is a Location, which means that it can be the return value
   * of a search on the tree.
   */
  
  class Node implements Location<K> { 
    protected K data;
    protected Node left, right;
    protected Node parent;     // the parent of this node
    protected int height;      // the height of the subtree rooted at this node
    protected boolean dirty;   // true iff the key in this node has been removed

    /**
     * Constructs a leaf node with the given key.
     */
    public Node(K key) {
      this(key, null, null);
    }
    
    /**
     * TODO
     * 
     * Constructs a new node with the given values for fields.
     */
    public Node(K data, Node left, Node right) {
    	this.data = data;
    	this.left = left;
    	this.right = right;
    	this.parent = null;
    	this.dirty = false;
    	fixHeight();
    }

    /**
     * Return true iff this node is a leaf in the tree.
     */
    protected boolean isLeaf() {
      return left == null && right == null;
    }
    
    /**
     * TODO
     * 
     * Performs a local update on the height of this node. Assumes that the 
     * heights in the child nodes are correct. This function *must* run in 
     * O(1) time.
     */
    protected void fixHeight() {
    	if ( left == null && right == null ){
    		height = 1;
    		return;
    	}

    	if(left != null && right != null){
    		height = ((left.height > right.height) ? left.height : right.height) + 1;
    		return;
    	}

    	if (left == null) // Make sure no null pointers exist
    		height = right.height + 1;
    	else
    		height = left.height + 1;
    }
    
    /**
     * TODO
     * 
     * Returns the data in this node.
     */
    public K get() {
      return data;
    }

    /**
     * TODO
     * 
     * Returns the location of the node containing the inorder predecessor
     * of this node.
     */
    
    public Node getBefore() {
    	Node p = getBeforeHelper(this); // Assume "this" is not null
    	// If the one found by the helper is dirty, we keep searching until
    	// one that is not dirty is found or we hit null
    	while (p != null && p.dirty) // Keep searching if the node found is dirty
    		p = getBeforeHelper(p);    		
    	return p;
    }
    
    /*
     * getBefore helper function
     * It searches and returns the predecessor of thenode in the tree.
     * The returned node might be dirty. This is fixed in getBefore
     */
    private Node getBeforeHelper(Node thenode){
    	// Case 1: The node has left subtree
    	if (thenode.left != null){ 
    		Node p = thenode.left; // Find the right most node in the left tree
    		while (p.right != null)
    			p = p.right;
    		return p;
    	}
    	
    	// Case 2: The node does not have left subtree
    	Node p = thenode; // Walk up and find the first node who's a right child 
    	while (p.parent != null && p.parent.right != p)
    		p = p.parent;
    	return p.parent;
    }


    /**
     * TODO
     * 
     * Returns the location of the node containing the inorder successor
     * of this node.
     */    
    
    public Node getAfter() {
    	Node p = getAfterHelper(this);
    	while (p != null && p.dirty) // Keep searching if the node found is dirty
    		p = getAfterHelper(p);
    	return p;    	
    }
    
    private Node getAfterHelper(Node thenode){
    	
    	// Case 1: The node has right subtree
    	if ( thenode.right != null ){ 
    		Node p = thenode.right;
    		while ( p.left != null ) // Find the left most node in the right tree
    			p = p.left;
    		return p;
    	}
    	
    	// Case 2: The node has no right subtree, and the node is either
    	//     the right child of its parent, or it has no parent
    	Node p = thenode;
    	while (p.parent != null && p.parent.left != p) // Walk up 
    		p = p.parent; // Stops at the first encounter of a left child
    	return p.parent; // Return the left child's parent
    }
    

    // Check if node is overweight
    public boolean isOverWeight(){ 
    	int b = balFactor();
    	if (b > 1 || b < -1)
    		return true;
    	return false;
    }
    
    
    // Calculate the balance factor of the node
    public int balFactor(){ 
    	if (left != null && right != null)
    		return right.height - left.height;    		
    	
    	if (left != null && right == null)
    		return - left.height;
    	
    	if (right != null && left == null)
    		return right.height;
    	
    	return 0;
    }
  }
  
  protected Node root;
  protected int n;
  protected BiPredicate<K, K> lessThan;
  
  /**
   * Constructs an empty BST, where the data is to be organized according to
   * the lessThan relation.
   */
  public BinarySearchTree(BiPredicate<K, K> lessThan) {
    this.lessThan = lessThan;
  }
  
  /**
   * TODO
   * 
   * Looks up the key in this tree and, if found, returns the (possibly dirty)
   * location containing the key.
   */
  public Node search(K key) {
	  Node p = root;
	  while (p != null && !p.data.equals(key)){ // Stops if p is null or key is found
		  if (lessThan.test(key, p.data))
			  p = p.left;
		  else
			  p = p.right;
	  }
	  return p;
  } 

  /**
   * TODO
   * 
   * Returns the height of this tree. Runs in O(1) time!
   */
  public int height() {
	  if (root == null)
		  return 0;
	  return root.height;
  }
  
  /**
   * TODO
   * 
   * Clears all the keys from this tree. Runs in O(1) time!
   */
  public void clear() {
	  root = null;
	  n = 0;
  }

  /**
   * Returns the number of keys in this tree.
   */
  public int size() {
    return n;
  }
  
  /**
   * TODO
   * 
   * Inserts the given key into this BST, as a leaf, where the path
   * to the leaf is determined by the predicate provided to the tree
   * at construction time. The parent pointer of the new node and
   * the heights in all node along the path to the root are adjusted
   * accordingly.
   * 
   * Note: we assume that all keys are unique. Thus, if the given
   * key is already present in the tree, nothing happens.
   * 
   * Returns the location where the insert occurred (i.e., the leaf
   * node containing the key).
   */
  public Node insert(K key) {
	  
	  if (root == null){ // Case when tree is empty
		  n++;
		  root = new Node(key);
		  return root;
	  }
	  
	  return insertSearch(key, root);
  }
  
  
  /* 
   * insert() helper function
   * It searches for the location where key should be inserted and inserts the key
   * @parameter p is the node where we start the search
   * @return the location of insertion (as the pointer to the node)
   */
  
  private Node insertSearch(K key, Node p){
	  if (p.data.equals(key)){ // Key already exists in p but is dirty
		  if ( p.dirty ){
			  n++;
			  p.dirty = false;
			  return p; 
		  } else 
		  return p; // Key already exists in p and is not dirty. Do nothing
	  }

	  Node result = null;
	  if (lessThan.test(key, p.data)){ // Key < p.data
		  if (p.left == null){ // No left child, insert there
			  n++;
			  p.left = new Node(key);
			  p.left.parent = p;
			  result = p.left;
		  }else
			  result = insertSearch(key, p.left);
	  }else{ // Key > p.data
		  if (p.right == null){ // No right child, insert there
			  n++;
			  p.right = new Node(key);
			  p.right.parent = p;
			  result = p.right;
		  }else
			  result = insertSearch(key, p.right);
	  }
	  
	  // Height fix needs to be in helper as it fixes all the parents 
	  // during the recursion
	  p.fixHeight(); 
	  return result;
  }
  
  /**
   * TODO
   * 
   * Returns true iff the given key is in this BST.
   */
  public boolean contains(K key) {
    Node p = search(key);
    if (p == null || p.dirty) // Assuming that all keys are unique
    	return false;
    return true;
  }

  /**
   * TODO
   * 
   * Removes the key from this BST. If the key is not in the tree,
   * nothing happens. Implement the removal using lazy deletion.
   */
  public void remove(K key) {
	  Node p = search(key);
	  if (p == null || p.dirty) // Do nothing if p is already deleted or does not exist
		  return;
	  n--;
	  p.dirty = true;
  }
  
  /**
   * TODO
   * 
   * Clears out all dirty nodes from this BST.
   * 
   * Use the following algorithm:
   * (1) Let ks be the list of keys in this tree. 
   * (2) Clear this tree.
   * (2) For each key in ks, insert it into this tree.
   */
  public void rebuild() {
	  List<K> ks = keys();
	  clear();
	  for (K key : ks)
		  insert(key);
	  /* 
	   *  // 2nd method: iterator method
	   * Iterator<K> it = ks.iterator();
	   * while (it.hasNext())
	   * 	insert(it.next());
	   */
  }
    
  /**
   * TODO
   * 
   * Returns a sorted list of all the keys in this tree.
   */
  public List<K> keys() {
	  LinkedList<K> ls = new LinkedList<>();
	  
	  if (root != null){
		  Node p = root;
		  
		  while (p.left != null) // Find the left most node in the tree (might be dirty)
			  p = p.left;
		  
		  // If the node found in the last step is dirty, then find the one after
		  // getAfter() guarantees that it returns one that is not dirty 
		  if (p.dirty) // Here p is never null because at the end of while loop, p.left == null
			  p = p.getAfter();
		  
		  /*
		   * For finding the left most node, we could use an algorithm that keeps using getBefore 
		   * until hitting null and return the last node. However, in that way, we essentially 
		   * traverse the same route with length O(h). Therefore, it is not more efficient. Also, 
		   * a null pointer check at the beginning would be needed if we choose that way, makes it
		   *  harder to implement. 
		   * */

		  while (p != null){
			  ls.addLast(p.data);
			  p = p.getAfter();
		  }
	  }
	  
	  return ls;
  }


  /**
   * TODO
   * 
   * Returns a textual representation of this BST.
   */
  public String toString(){	
	  String result = "";
	  if (root != null)
		  result = toStringHelper(root);
	  if(result != "") // Delete the last comma if result is not empty
		  result = result.substring(0,result.length()-2);
	  result = "(" + result + ")"; // Add parentheses
	  return result;
  }
  
  // Helper function for toString
  // @return the string typed representation of the subtree with root at key
  private String toStringHelper(Node p){
	  String result = p.data.toString() + ", ";
	  if (p.dirty)
		  result = "";
	  
	  // Concatenate the strings from left and right child trees
	  if (p.left != null){
		  result = toStringHelper(p.left) + result;
	  }
	  if (p.right != null)
		  result = result + toStringHelper(p.right);
	  return result;
  }
  
  // Returns a string typed result representing the whole tree, including dirty nodes
  // This function is mainly for testing purposes
  public String toStringWithDirty(){	
	  String result = toStringWithDirtyHelper(root);
	  if(result != "") // Delete the last comma if result is not empty
		  result = result.substring(0,result.length()-2);
	  result = "(" + result + ")"; // Add parentheses
	  return result;
  }
  
  // Helper function for toStringWithDirty
  private String toStringWithDirtyHelper(Node p){
	  String result = p.data.toString() + ", ";
	  if (p.left != null)
		  result = toStringHelper(p.left) + result;
	  if (p.right != null)
		  result = result + toStringHelper(p.right);
	  return result;
  }
}
