import java.util.HashMap;

/**
 * TODO: Complete the implementation of this class.
 */

public class FrequencyTable extends HashMap<Character, Integer> {
	/**
	 * Constructs an empty table.
	 */
	public FrequencyTable() {
		super();
	}

	/**
	 * TODO: Make use of get() and put().
	 * 
	 * Constructs a table of character counts from the given text string.
	 */
	public FrequencyTable(String text) {
		int n = text.length();

		for (int i = 0; i < n; i++){ // No need to check if key is in hashmap because of our override
			char key = text.charAt(i);
			this.put(key, this.get(key) + 1);
		}
	}

	/**
	 * TODO
	 * 
	 * Returns the count associated with the given character. In the case that
	 * there is no association of ch in the map, return 0.
	 */
	@Override
	public Integer get(Object ch) {
		if (!this.containsKey(ch))
			return 0;
		else
			return super.get(ch);
	}
}
