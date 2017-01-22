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

public class ColorTable{
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
	  // Throw exceptions if any of the arguments are out of range
	  if( !(initialCapacity >= 1 && initialCapacity <= Constants.MAX_CAPACITY) )
		  throw new RuntimeException("Invalid value for starting capacity!");
	  if( !(bitsPerChannel >= 1 && bitsPerChannel <= 8) )
		  throw new RuntimeException("Invalid value for bits per channel!");
	  if( !(collisionStrategy == Constants.LINEAR || collisionStrategy == Constants.QUADRATIC) )
		  throw new RuntimeException("Invalid value for collision strategy!");
	  if( collisionStrategy == Constants.LINEAR && !( rehashThreshold > 0 && rehashThreshold <= 1 ))
		  throw new RuntimeException("Invalid value for rehash threshold!");
	  if( collisionStrategy == Constants.QUADRATIC && !( rehashThreshold > 0 && rehashThreshold < 0.5 ))
		  throw new RuntimeException("Invalid value for rehash threshold!");
	  
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

  /* Helper Method
   * "lookup" returns the index of color in table, or if the color is not in table, returns the 
   * index where color can be correctly put in table. 
   * Two arguments color and table are used due to usage in "rehash".
   */
  
  public int lookup(Color color, TableElem[] table){ 
	  int colorkey = Util.pack(color, bitsPerChannel);
	  int start = colorkey % table.length;
	  int i = start;
	  int k = 0;
	  numCollisions = 0;

	  while(table[i] != null && table[i].key != colorkey){
		  k++;

		  if(collisionStrategy == Constants.LINEAR){
			  i = (start + k) % table.length;
		  }else{ // This is the case of quadratic probing. 
			  i = (start + k*k) % table.length;
		  }

		  numCollisions++; // Count the number of collisions
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
	  
	  if(table[i] == null)
		  return 0;

	  return table[i].value;  
  }
  

  /**
   * TODO
   * 
   * Associates the count with the color in this table. Do nothing if count is less than
   * or equal to zero. Uses Util.pack() as the hash function.
   */
  
  public void put(Color color, long count) {	  
	  if(count > 0){ // Do nothing if count is not positive
		  int i = lookup(color, table);
		  if(table[i] == null){ // If color is not yet in table
			  table[i] = new TableElem(Util.pack(color, bitsPerChannel), count);
		  }else{
			  table[i].value = count;
		  }
	  }
      
	  // The length of table changed. Rehash if needed.
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

	  if(table[i] == null){ //If color is not table, then add it to table
		  table[i] = new TableElem(Util.pack(color, bitsPerChannel), 1);
	  }else{
		  table[i].value++; 
	  }

	  // The length of table changed. Rehash if needed.
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
	  int tableLength = getCapacity(); // The length of table
	  double tableElemNum = getSize(); // The number of key/value associations in table
	  int j = 2 * tableLength + 1;
      
	  // In the following while loop, we make sure that a number j is found that satisfies all the conditions
	  // (1) j is prime, (2) j%4==3, (3) new size j makes load less than the rehash threshold
	  while( ( !(Util.isPrime(j) && (j % 4 == 3) && (tableElemNum/j < rehashThreshold)) ) && j <= Constants.MAX_CAPACITY){
		  j += 2; // Only need to traverse odd numbers
	  }
	  
	  // Throw exception if the j we found is too large
	  // If in the while loop j reaches MAX_CAPACITY (and thus stops the loop), we consider it to be too large
	  if(j >= Constants.MAX_CAPACITY)
		  throw new RuntimeException("Hash table size is too large!");
		  
	  TableElem[] newTable = new TableElem[j];

	  for(int i = 0; i < tableLength; i++){
		  if(table[i] != null){ // Traverse all key/value associations in table
			  int k = lookup(Util.unpack(table[i].key, bitsPerChannel), newTable);
			  if(newTable[k] == null){
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
		  private int r = 0;
		  private int g = 0;
		  private int b = 0;
		  // From one color to the next, the increment is 2 to the power of 8-bitsPerChannel
		  private int inc = (int) Math.pow(2, 8 - bitsPerChannel);

		  public boolean hasNext(){
			  if((r < 255 || g < 255 || b < 255) && r <= 255 && g <= 255 && b <= 255){
				  return true;
			  }
			  return false;
		  }

		  public long next(){
			  Color color = new Color(r,g,b);
			  if(b + inc < 256){
				  b = b + inc;
			  }else{ 
				  if(g + inc < 256){
					  b = 0;
					  g = g + inc;
				  }else{
					  b = 0;
					  g = 0;
					  r = r + inc;
				  }
			  }
			  return get(color);
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
	  
	  // The following is to delete the last ";" in the final string
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
	  if(table[i] == null){
		  return 0;
	  }
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
