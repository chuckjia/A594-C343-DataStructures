import java.util.Comparator;
import java.util.ArrayList;

/**
 * TODO: Complete the implementation of this class.
 * 
 * A HuffmanTree represents a variable-length code such that the shorter the
 * bit pattern associated with a character, the more frequently that character
 * appears in the text to be encoded.
 */

public class HuffmanTree {

	class Node {
		protected char key;
		protected int priority;
		protected Node left, right;

		public Node(int priority, char key) {
			this(priority, key, null, null);
		}

		public Node(int priority, Node left, Node right) {
			this(priority, '\0', left, right);
		}

		public Node(int priority, char key, Node left, Node right) {
			this.key = key;
			this.priority = priority;
			this.left = left;
			this.right = right;
		}

		public boolean isLeaf() {
			return left == null && right == null;
		}
	}

	protected Node root;

	/**
	 * TODO
	 * 
	 * Creates a HuffmanTree from the given frequencies of letters in the
	 * alphabet using the algorithm described in lecture.
	 */
	public HuffmanTree(FrequencyTable charFreqs) {
		Comparator<Node> comparator = (x, y) -> { 
			/**
			 *  TODO: x and y are Nodes
			 *  x comes before y if x's priority is less than y's priority
			 */
			if (x.priority == y.priority)
				return 0;
			if (x.priority < y.priority)
				return -1;
			return 1;				
		};
		PriorityQueue<Node> forest = new Heap<Node>(comparator);
		
		/**
		 * TODO: Complete the implementation of Huffman's Algorithm.
		 * Start by populating forest with leaves.
		 */
		for (char key : charFreqs.keySet())
			forest.insert(new Node(charFreqs.get(key), key));

		while(forest.size() > 1){
			Node node1 = forest.delete();
			Node node2 = forest.delete();
			forest.insert(new Node(node1.priority + node2.priority, node1, node2));
		}
		root = forest.peek(); // If forest is empty, then exception will be thrown
	}

	/**
	 * TODO
	 * 
	 * Returns the character associated with the prefix of bits.
	 * 
	 * @throws DecodeException if bits does not match a character in the tree.
	 */
	public char decodeChar(String bits) {
		int n = bits.length();
		Node pos = root;

		for (int i = 0; i < n; i++){
			int k = Character.getNumericValue(bits.charAt(i));
			if (pos.key != '\0') // If a valid key is found, then break out of for loop
				break;
			if (k != 0 && k != 1) // If we have invalid bits String
				throw new DecodeException(bits);
			if (k == 0){ // Turn left down the path
				if (pos.left == null)
					throw new DecodeException(bits);
				pos = pos.left;
			}
			if (k == 1){ // Turn right down the path
				if (pos.right == null)
					throw new DecodeException(bits);
				pos = pos.right;
			}
		}

		if (pos.key == '\0') // If the one found is not a valid character
			throw new DecodeException(bits);
		return pos.key;
	}

	/**
	 * TODO
	 * 
	 * Returns the bit string associated with the given character. Must
	 * search the tree for a leaf containing the character. Every left
	 * turn corresponds to a 0 in the code. Every right turn corresponds
	 * to a 1. This function is used by CodeBook to populate the map.
	 * 
	 * @throws EncodeException if the character does not appear in the tree.
	 */
	public String lookup(char ch) {
		String ans = lookupHelper(ch, root, "");
		if (ans == null) // Case where the character does not appear in the tree
			throw new EncodeException(ch);
		return ans; 
	}

	private String lookupHelper(char ch, Node pos, String res){
		if (pos == null) // Base case 1
			return null;
		
		if (pos.isLeaf()){ // Base case 2
			if( ch == pos.key )
				return res;
			else
				return null;
		}

		// This return method works assuming that no duplicate characters exist in the tree	
		String ans = lookupHelper(ch, pos.left, res + "0");
		if (ans != null) // If found in left child tree, then no need to search any more
			return ans;
		// If did not find in left child tree, then:
		ans = lookupHelper(ch, pos.right, res + "1");
		return ans;			
	}
}

