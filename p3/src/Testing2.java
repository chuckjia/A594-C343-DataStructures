/**
 * To test with JUnit, add JUnit to your project. To do this, go to
 * Project->Properties. Select "Java Build Path". Select the "Libraries"
 * tab and "Add Library". Select JUnit, then JUnit 4.
 */

import static org.junit.Assert.*;

import java.util.*;
import java.util.function.BiPredicate;

import org.junit.Test;
import java.util.Random;


/*
 * Here we provide some additional testing, as the methods getBefore and getAfter were not 
 * thoroughly tested in testing.java.
 * */
public class Testing2 {

  @Test
  public void getBeforeBST() {
	  BinarySearchTree<Integer> bst = new BinarySearchTree<>((Integer x, Integer y) -> x < y);
	  int[] a = new int[] { 4, 8, 0, 2, 6, 10 };
	  for (Integer key : a)
		  bst.insert(key);
	  int n = a.length;	  
	  int key;
	  
	  assertTrue(bst.root.data.equals(4));
	  assertTrue(bst.root.getBefore().data.equals(2));
	  key = 2;
	  bst.remove(key);
	  n--;
	  assertTrue(bst.root.getBefore().data.equals(0));
	  assertEquals(n, bst.size());
	  
	  key = 0;
	  bst.remove(key);
	  n--;
	  assertNull(bst.root.getBefore());
	  assertEquals(n, bst.size());	  
  }
  
  @Test
  public void getAfterBST() {
	  BinarySearchTree<Integer> bst = new BinarySearchTree<>((Integer x, Integer y) -> x < y);
	  int[] a = new int[] { 4, 8, 0, 2, 6, 10 };
	  for (Integer key : a)
		  bst.insert(key);
	  int n = a.length;	  
	  int key;
	  
	  assertTrue(bst.root.data.equals(4));
	  assertTrue(bst.root.getAfter().data.equals(6));
	  key = 6;
	  bst.remove(key);
	  n--;
	  assertTrue(bst.root.getAfter().data.equals(8));
	  assertEquals(n, bst.size());
	  
	  key = 8;
	  bst.remove(key);
	  n--;
	  assertTrue(bst.root.getAfter().data.equals(10));
	  assertEquals(n, bst.size());
	  
	  key = 10;
	  bst.remove(key);
	  n--;
	  assertNull(bst.root.getAfter());
	  assertEquals(n, bst.size());	  
  }
  

  /**********************************************************************************
   * When you've reached this point, you're ready to replace the BST with AVL in 
   * the Driver.
   **********************************************************************************/

  private <K> int countDirtyNodes(BinarySearchTree<K>.Node p) {
    if (p == null)
      return 0;
    return (p.dirty ? 1 : 0) + countDirtyNodes(p.left) + countDirtyNodes(p.right); 
  }

  private <K> boolean verifyHeights(BinarySearchTree<K>.Node p) {
    if (p == null)
      return true;
    int h1 = p.left == null ? 0 : p.left.height;
    int h2 = p.right == null ? 0 : p.right.height;
    return p.height == 1 + Math.max(h1, h2) &&
        verifyHeights(p.left) && verifyHeights(p.right);
  }

  private <K> boolean verifyBFs(BinarySearchTree<K>.Node p) {
    if (p == null)
      return true;
    int h1 = p.left == null ? 0 : p.left.height;
    int h2 = p.right == null ? 0 : p.right.height;
    return Math.abs(h1 - h2) <= 1 &&
        verifyBFs(p.left) && verifyBFs(p.right);
  }

  private <K> boolean verifyParentPointers(BinarySearchTree<K>.Node root) {
    if (root == null)
      return true;
    if (root.parent != null)
      return false;
    return verifyParentPointersHelper(root, root.left) && 
        verifyParentPointersHelper(root, root.right);
  }
  
  private <K> boolean verifyParentPointersHelper(BinarySearchTree<K>.Node p, BinarySearchTree<K>.Node q) {
    if (q == null)
      return true;
    if (q.parent != p)
      return false;
    return 
        verifyParentPointersHelper(q, q.left) && verifyParentPointersHelper(q, q.right);
  }

  private <K> 
  boolean verifyOrderingProperty(BinarySearchTree<K>.Node p, BiPredicate<K, K> lessThan) {
    if (p == null) return true;
    K key = p.data;
    return allLessThan(p.left, key, lessThan) && allGreaterThan(p.right, key, lessThan) &&
        verifyOrderingProperty(p.left, lessThan) && verifyOrderingProperty(p.right, lessThan);
  }

  private <K>
  boolean allGreaterThan(BinarySearchTree<K>.Node p, K x, BiPredicate<K, K> lessThan) {
    if (p == null) return true;
    return !lessThan.test(p.data, x) &&
        allGreaterThan(p.left, x, lessThan) && allGreaterThan(p.right, x, lessThan);
  }

  private <K>
  boolean allLessThan(BinarySearchTree<K>.Node p, K x, BiPredicate<K, K> lessThan) {
    if (p == null) return true;
    return lessThan.test(p.data, x) &&
        allLessThan(p.left, x, lessThan) && allLessThan(p.right, x, lessThan);
  }

}
