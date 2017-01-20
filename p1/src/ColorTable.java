import java.awt.Color;
import java.util.Random;

/**
 * @author Chuck Jia
 * 
 * A ColorTable represents a dictionary of frequency counts, keyed on Color.
 * It is a simplification of Map<Color, Integer>. The size of the key space
 * can be reduced by limiting each Color to a certain number of bits per channel.
 */

/**
 * TODO
 * 
 * Implement this class, including whatever data members you need and all of the
 * public methods below. You may create any number of private methods if you find
 * them to be helpful. Replace all TODO comments with appropriate javadoc style 
 * comments. Be sure to document all data fields and helper methods you define.
 */

public class ColorTable {
  /**
   * Counts the number of collisions during an operation.
   */
  private static int numCollisions = 0;

  /**
   * Returns the number of collisions that occurred during the most recent get or
   * put operation.
   */
  public static int getNumCollisions() {
    return numCollisions;
  }


  /**
   * TODO
   * 
   * Constructs a color table with a starting capacity of initialCapacity. Keys in
   * the color key space are truncated to bitsPerChannel bits. The collision resolution
   * strategy is specified by passing either Constants.LINEAR or Constants.QUADRATIC for
   * the collisionStrategy parameter. The rehashThrehold specifies the maximum tolerable load 
   * factor before triggering a rehash.
   * 
   * @throws RuntimeException if initialCapacity is not in the range [1..Constants.MAX_CAPACITY]
   * @throws RuntimeException if bitsPerChannel is not in the range [1..8]
   * @throws RuntimeException if collisionStrategy is not one of Constants.LINEAR or Constants.QUADRATIC
   * @throws RuntimeException if rehashThreshold is not in the range (0.0..1.0] for a
   *                             linear strategy or (0.0..0.5) for a quadratic strategy
   */
  
  private TableElem[] table;
  private int bitsPerChannel;
  private int collisionStrategy;
  private double rehashThreshold;
  
  public ColorTable(int initialCapacity, int bitsPerChannel, int collisionStrategy, double rehashThreshold) { 
    this.table = new TableElem[initialCapacity];    
    this.bitsPerChannel = bitsPerChannel;
    this.collisionStrategy = collisionStrategy;
    this.rehashThreshold = rehashThreshold; 
  }

  /**
   * TODO
   * 
   * Returns the number of bits per channel used by the colors in this table.
   */
  
  public int getBitsPerChannel() {
    return this.bitsPerChannel;
  }

  
  public int lookup(Color color, TableElem[] table){
	  
	  int colorkey = Util.pack(color, bitsPerChannel);
	  int start = colorkey % table.length;
	  int i = start;
	  int k = 0;
			
	  while(table[i] != null && table[i].key != colorkey){
		  k++;
		  if(collisionStrategy == Constants.LINEAR){
			  i = (start + k) % table.length;
		  }else{
			  i = (start + k*k) % table.length;
		  }
	  }
		
	  return i; 
  }
  

  /**
   * TODO
   * 
   * Returns the frequency count associated with color. Note that colors not
   * explicitly represented in the table are assumed to be present with a
   * count of zero. Uses Util.pack() as the hash function.
   */
  
  
  public long get(Color color) {
	  int i = lookup(color, table);
	  
	  if(table[i].key == Util.pack(color, bitsPerChannel)){
		return table[i].value;  
	  }else{
		return 0;
	  }
	
  }

  /**
   * TODO
   * 
   * Associates the count with the color in this table. Do nothing if count is less than
   * or equal to zero. Uses Util.pack() as the hash function.
   */
  public void put(Color color, long count) {
	  
      if(count > 0){
          int i = lookup(color, table);
  	      if(table[i] == null){  // Questions? What to do if color is not originally in
  	    	  table[i] = new TableElem(Util.pack(color, bitsPerChannel), count);
  	      }else{
  		      table[i].value = count;
  	      }
      }
      
	  if(getLoadFactor() >= rehashThreshold){
		  rehash();
	  }
  }

  /**
   * TODO
   * 
   * Increments the frequency count associated with color. Note that colors not
   * explicitly represented in the table are assumed to be present with a
   * count of zero.
   */
  public void increment(Color color) {
	  int i = lookup(color, table);
	  
	  if(table[i] == null){
		  table[i] = new TableElem(Util.pack(color, bitsPerChannel), 1);
	  }else{
		  table[i].value++; 
	  }
	  
	  if(getLoadFactor() >= rehashThreshold){
		  rehash();
	  }
  }

  /**
   * TODO
   * 
   * Returns the load factor for this table.
   */
  public double getLoadFactor() {
	double n = table.length;
		
    return (getSize()/n);
  }

  /**
   * TODO
   * 
   * Returns the size of the internal array representing this table.
   */
  public int getCapacity() {
    return table.length;
  }

  /**
   * TODO
   * 
   * Returns the number of key/value associations in this table.
   */
  public int getSize() {
	  int n = 0;
	  for(int i = 0; i < table.length; i++){
	      if(table[i] != null)
		      n++;
	  }
      return n;
  }

  /**
   * TODO
   * 
   * Returns true iff this table is empty.
   */
  public boolean isEmpty() {
	boolean result = true;
	if(getSize() > 0)
		result = false;
    return result;
  }

  /**
   * TODO
   * 
   * Increases the size of the array to the smallest prime greater than double the 
   * current size that is of the form 4j + 3, and then moves all the key/value 
   * associations into the new array. 
   * 
   * Hints: 
   * -- Make use of Util.isPrime().
   * -- Multiplying a positive integer n by 2 could result in a negative number,
   *    corresponding to integer overflow. You should detect this possibility and
   *    crop the new size to Constants.MAX_CAPACITY.
   * 
   * @throws RuntimeException if the table is already at maximum capacity.
   */
  private void rehash() { 
	int currentSize = getCapacity(); 
	int j = 2 * currentSize + 1;
	
	while((!(Util.isPrime(j) && (j % 4 == 3))) && j < Constants.MAX_CAPACITY){
		j++;
	}
	
    TableElem[] newTable = new TableElem[j];
    
    for(int i = 0; i < currentSize; i++){
    	if(table[i] != null){
    		int k = lookup(Util.unpack(table[i].key, bitsPerChannel), newTable);
    		if(newTable[k] == null){ // for robustness, might have problems later
    			newTable[k] = new TableElem(table[i].key, table[i].value);
    		}
    	}
    }
    
    table = newTable;
  }

  /**
   * TODO
   * 
   * Returns an Iterator that marches through each color in the key color space and
   * returns the sequence of frequency counts.
   */
  public Iterator iterator() {
    return new Iterator(){
    	int curr = 0;
    	int end = table.length -1;
    	
    	public long next(){
    		while(table[curr] == null){
    			curr++;
    		}
    		curr++;
    		return table[curr-1].value;
    	}
    	
    	public boolean hasNext(){
    		return curr < end;    		
    	}
    };
  }

  /**
   * TODO
   * 
   * Returns a String representation of this table.
   */
  public String toString() {
	  String tableString = ""; 
	  for(int i = 0; i < table.length; i++){
		  if(table[i] != null){
			  tableString = tableString + Integer.toString(i) + ":" + Integer.toString(table[i].key) + "," + Long.toString(table[i].value) + "; ";
		  }
	  }
	  
	  if(tableString != null && tableString.substring(tableString.length()-2).equals("; "))
    	  tableString = tableString.substring(0, tableString.length()-2);
		  
    return tableString;
  }

  /**
   * TODO
   * 
   * Returns the count in the table at index i in the array representing the table.
   * The sole purpose of this function is to aid in writing the unit tests.
   */
  public long getCountAt(int i) { 
    return table[i].value;
  }

  /**
   * Simple testing.
   */
  public static void main(String[] args) {
    ColorTable table = new ColorTable(3, 6, Constants.QUADRATIC, .49);
    
    int[] data = new int[] { 32960, 4293315, 99011, 296390 };
    for (int i = 0; i < data.length; i++){ 
      table.increment(new Color(data[i]));
    }
    
    System.out.println("capacity: " + table.getCapacity()); // Expected: 7
    System.out.println("size: " + table.getSize());         // Expected: 3
    
    
    /* The following automatically calls table.toString().
       Notice that we only include non-zero counts in the String representation.
       
       Expected: [3:2096,2, 5:67632,1, 6:6257,1]
       
       This shows that there are 3 keys in the table. They are at positions 3, 5, and 6.
       Their color codes are 2096, 67632, and 6257. The code 2096 was incremented twice.
       You do not have to mimic this format exactly, but strive for something compact
       and readable.
       */
    System.out.println(table);  
  }
}