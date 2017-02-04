import java.awt.Color;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A Board represents the current state of the game. Boards know their dimension, 
 * the collection of tiles that are inside the current flooded region, and those tiles 
 * that are on the outside.
 * 
 * @author Chuck Jia
 */

public class Board {
  private Map<Coord, Tile> inside, outside;
  private Map<Coord, Tile> newinside;  // For undo move
  private int size;
  private WaterColor prevColor; // For undo move
  private Map<WaterColor, Integer> colorCounts = new HashMap<>(); // For suggestion
  
  /**
   * Constructs a square game board of the given size, initializes the list of 
   * inside tiles to include just the tile in the upper left corner, and puts 
   * all the other tiles in the outside list.
   */
  
  public Board(int size) {
	  // A tile is either inside or outside the current flooded region.
	  inside = new HashMap<>();
	  outside = new HashMap<>();

	  newinside = new HashMap<>();// For undo move

	  resetColorCount(); // For suggestions

	  this.size = size;
	  for (int y = 0; y < size; y++)
		  for (int x = 0; x < size; x++) {
			  Coord coord = new Coord(x, y);
			  outside.put(coord, new Tile(coord));
		  }
	  // Move the corner tile into the flooded region and run flood on its color.
	  Tile corner = outside.remove(Coord.ORIGIN);
	  inside.put(Coord.ORIGIN, corner);
	  flood(corner.getColor());
  }  
  
  private void resetColorCount(){ // For suggestions
	  colorCounts.put(WaterColor.BLUE, 0); 
	  colorCounts.put(WaterColor.RED, 0);
	  colorCounts.put(WaterColor.CYAN, 0);
	  colorCounts.put(WaterColor.PINK, 0);
	  colorCounts.put(WaterColor.YELLOW, 0);
  }
  
  private WaterColor nextColor(){ // For suggestions
	  int max = 0;
	  WaterColor color = WaterColor.BLUE;
	  for ( WaterColor k : colorCounts.keySet() ) {
		  if ( colorCounts.get(k) > max ){
			  max = colorCounts.get(k);
			  color = k;
		  } 
	  }
	  
	  return color;
  }

  
  /**
   * Returns the tile at the specified coordinate.
   */ 
  public Tile get(Coord coord) {
    if (outside.containsKey(coord))
      return outside.get(coord);
    return inside.get(coord);
  }
  
  /**
   * Returns the size of this board.
   */
  public int getSize() {
    return size;
  }
  
  /**
   * TODO
   * 
   * Returns true iff all tiles on the board have the same color.
   */
  public boolean fullyFlooded() {
	  if(outside.size() == 0)
		  return true;
	  return false;
  }
  
  public WaterColor getPrevColor(){ // For undo move
	  return prevColor;
  }
  
  public void unflood(WaterColor color){ // For undo move
	  for(Coord k : newinside.keySet()){
		  outside.put(k, newinside.get(k));
		  inside.remove(k);
	  }
	  for(Coord k : inside.keySet())
		  inside.get(k).setColor(prevColor);
  }
  
  /**
   * TODO
   * 
   * Updates this board by changing the color of the current flood region 
   * and extending its reach.
   */
  
  public void flood(WaterColor color) {
	  flood1(color);
  }

  /**
   * TODO
   * 
   * Explore a variety of algorithms for handling a flood. Use the same interface 
   * as for flood above, but add an index so your methods are named flood1,
   * flood2, ..., and so on. Then, use the batchTest() tool in Driver to run game
   * simulations and then display a graph showing the run times of all your different 
   * flood functions. Do not delete your flood code attempts. For each variety of 
   * flood, including the one above that you eventually settle on, write a comment
   * that describes your algorithm in English. For those implementations that you
   * abandon, state your reasons. 
   *    
   */
  
  /*
   * My algorithm for flood1:
   * 
   * Flood1() is a recursive method.
   * 
   * Each time flood1() is called, I start to check from the tile ORIGIN. 
   * (1) If the tile is in "inside", then it will be set to the new color, and 
   * then we repeat the procedures on its 4 neighbors; 
   * (2) If it is not in the inside, but has the same color with the new color, 
   * then it will be added to "inside" and removed from "outside", and then we 
   * repeat the procedures on its 4 neighbors; 
   * (3) if not the previous 2 cases, then just return (void).
   * 
   * During this process, I keeped a hashmap "visited" to keep track of which tiles 
   * have been visited, so that next time I do not need to repeat the checks.
   * 
   * Added feature to the game: "undo move"
   * I added another hashmap "newinside", which keeps track of the tiles that are 
   * newly added to inside from the last step. In this way, with the function
   * unflood(), I was able to add the feature "undo move" to our game. Simply clicking  
   * "Game -> Undo move" in the menu would undo the last move.
   * 
   * */
  
  public void flood1(WaterColor color) {
	  HashSet<Coord> visited = new HashSet<>();
	  newinside.clear(); // For undo move
	  prevColor = get(Coord.ORIGIN).getColor(); // For undo move
	  floodNeighbor(Coord.ORIGIN, color, visited);
  }
  
  // flood1 helper
  public void floodNeighbor(Coord coord, WaterColor newColor, HashSet<Coord> visited){

	  visited.add(coord);
	  if ( inside.containsKey(coord) ){
		  get(coord).setColor(newColor);
	  } else if (get(coord).getColor() == newColor) {
		  inside.put(coord, get(coord));
		  outside.remove(coord);
		  newinside.put(coord, get(coord)); // For undo move
	  } else {
		  return;
	  }
	  
	  List<Coord> nb = coord.neighbors(size);
	  for (Coord p : nb){
		  if (p != null && !visited.contains(p)){
			  floodNeighbor(p, newColor, visited);
		  }
	  }
  }
  
  /*
  * Why I choose flood1 instead of flood2:
  * 
  * The earlier version flood2 would flood the area first by ONLY checking the colors
  * of the tiles, in a second step, resweep the flooded area and update inside. The
  * complexities of flood2 and flood1 are similar. But, by combining the color change
  * and updating "inside" in just ONE sweep, some running time has been saved. 
  * For example, in flood2, for each tile, (roughly speaking) we calculate its 
  * neighbor coordinates TWICE, while in flood1, we only need to do ONCE. Therefore, 
  * flood1 is more efficient than flood2. Thus, I chose to use flood1.
  * 
  * */
  
  public void flood2(WaterColor color) {
	  HashSet<Coord> visited = new HashSet<>();
	  prevColor = get(Coord.ORIGIN).getColor();
	  floodNeighbor2(Coord.ORIGIN, prevColor, color, visited);
	  visited.clear();
	  newinside.clear(); // For undo move
	  updateInside2(Coord.ORIGIN, color, visited);
  }
  
  // flood2 helper #1
  public void floodNeighbor2(Coord coord, WaterColor currColor, WaterColor newColor, HashSet<Coord> visited){

	  visited.add(coord);
	  if ( get(coord).getColor() == currColor ){
		  get(coord).setColor(newColor);
		  List<Coord> nb = coord.neighbors(size);
		  for (int i = 0; i < 4; i++){
			  Coord p = nb.get(i);
			  if (p != null && !visited.contains(p)){
				  floodNeighbor2(p, currColor, newColor, visited);
			  }
		  }
	  }
  }

  // flood2 helper #2
  public void updateInside2(Coord coord, WaterColor newColor, HashSet<Coord> visited){
	  visited.add(coord);
	  if ( !inside.containsKey(coord) ){
		  if ( get(coord).getColor() == newColor ){
			  inside.put(coord, get(coord));
			  outside.remove(coord);
			  newinside.put(coord, get(coord)); // For undo move
		  }else{
			  return;
		  }
	  }
	  List<Coord> nb = coord.neighbors(size);
	  for (int i = 0; i < 4; i++){
		  Coord p = nb.get(i);
		  if (p != null && !visited.contains(p)){
			  updateInside2(p, newColor, visited);
		  }
	  }
  }
  
  /*
   * Flood3: Non-recursive method
   * 
   * Why I choose flood1 instead of flood3:
   * Flood3() is a NON-recursive method. To update the set "inside", I keep sweeping "inside" 
   * and for each of the tiles, I check if any of its neighbors should be updated to "inside". 
   * But in order to efficiently update the set "inside", I had to keep a new hashset "ninside"
   * to track the one I just added to "inside". This way, next time I sweep, I do not need to 
   * sweep the whole "inside" set, and instead I just need to sweep the "ninside" part.
   * 
   * Although the running time should be comparable to flood1(), I feel that the "ninside" set
   * is an extra cost on storage. Also, the recursive algorithm of flood1 is much simpler to 
   * write. Thus, I decided to use flood1 instead of flood3.
   * 
   * Note: this method flood3 has not been updated to work with the "undo move" feature.
   * 
   * */
  
  public void flood3(WaterColor color) {
	  for (Coord k : inside.keySet()){
		  get(k).setColor(color);
	  }
	  HashSet<Coord> ninside = new HashSet<>();
	  for (Coord k : inside.keySet()){
		  ninside.add(k);
	  }	  
	  
	  while(ninside.size() != 0)
		  ninside = flood3Helper(color, ninside);
  }
  
  // flood3 helper: Updating the inside set
  public HashSet<Coord> flood3Helper(WaterColor currColor, HashSet<Coord> ninside){
	  HashSet<Coord> ninsidetemp = new HashSet<>();
	  for (Coord k : ninside){
		  ninsidetemp.add(k);
	  }
	  for (Coord k : ninsidetemp){
		  ninside.remove(k);
		  List<Coord> nb = k.neighbors(size);
		  for (Coord p : nb){
			  if (p != null && !inside.containsKey(p) ){
				  if (get(p).getColor() == currColor){
					  inside.put(p, get(p));
					  outside.remove(p);
					  ninside.add(p);
				  } 					  
			  }
		  }
	  }
	  return ninside; 
  }

  
  
  /**
   * TODO
   * 
   * Returns the "best" GameColor for the next move. 
   * 
   * Modify this comment to describe your algorithm. Possible strategies to pursue 
   * include maximizing the number of tiles in the current flooded region, or maximizing
   * the size of the perimeter of the current flooded region.
   * 
   * 
   * My algorithm for suggest:
   * 
   * IDEA: The method sugguest() counts the number of tiles of each color that are adjacent to 
   * the flooded area and gives the suggestion of the color with the most count.
   * 
   * This is a recursive method. I start from the tile ORIGIN. 
   * (1) If the tile is in "inside", then I repeat the procedures on its neighbors
   * (2) If the tile is not in "inside", then I note its color and increment this color's
   *    count (stored in colorCounts).
   * Again, a hashset "visited" is used to keep the process efficient.
   *  
   * A recursive helper function countNeighbor is used here.
   */
  
  // Helper function for suggest()
  private void countNeighbor( Coord coord, WaterColor colortry, HashSet<Coord> visited ){
	  visited.add(coord);
	  if ( !inside.containsKey(coord) ) {
		  if ( get(coord).getColor() == colortry ){
			  int x = colorCounts.get(colortry) + 1;
			  colorCounts.put(colortry, x);
		  } else {
			  return;
		  }
	  }
	  
	  List<Coord> nb = coord.neighbors(size);
	  for (Coord p : nb){
		  if (p != null && !visited.contains(p)){
			  countNeighbor(p, colortry, visited);
		  }
	  }
  }
  
  
  public WaterColor suggest() {	  
	  resetColorCount();
	  
	  for ( WaterColor c : colorCounts.keySet() ){
		  HashSet<Coord> visited = new HashSet<>();
		  countNeighbor( Coord.ORIGIN, c, visited );
	  }
	  
	  return nextColor();
  }
  
  
  /**
   * Returns a string representation of this board. Tiles are given as their
   * color names, with those inside the flooded region written in uppercase.
   */ 
  public String toString() {
    StringBuilder ans = new StringBuilder();
    for (int y = 0; y < size; y++) {
      for (int x = 0; x < size; x++) {
        Coord curr = new Coord(x, y);
        WaterColor color = get(curr).getColor();
        ans.append(inside.containsKey(curr) ? color.toString().toUpperCase() : color);
        ans.append("\t");
      }
      ans.append("\n");
    }
    return ans.toString();
  }
  
  
  // Following are some methods to provide instances of board for testing  
  
  public void testSetFull(){ // For testing fullyFlooded
	  for( Coord p : outside.keySet() ){
		  get(p).setColor(WaterColor.BLUE);
		  inside.put(p, get(p));
	  }
	  outside.clear();
  }
  
  public void testMyBoard1(){ // For testing. Build an instance of a board
	  for( Coord p : inside.keySet() )
		  get(p).setColor(WaterColor.BLUE);
	  for( Coord p : outside.keySet() )
		  get(p).setColor(WaterColor.RED);	  
  }
  
 
  public void testMyBoard2(){ // For testing. Build an instance of a board
	  for (Coord p : outside.keySet())
		  get(p).setColor(WaterColor.BLUE);
	  for( Coord p : inside.keySet() ){
		  get(p).setColor(WaterColor.BLUE);
		  outside.put(p, get(p));
	  }
	  inside.clear();
	  
	  get(Coord.ORIGIN).setColor(WaterColor.CYAN);
	  inside.put(Coord.ORIGIN, get(Coord.ORIGIN));
	  outside.remove(Coord.ORIGIN);	  

	  int bsize = getSize();
	  for( int i = 0; i < bsize; i++){
		  Coord coord = new Coord(i, bsize - 1);
		  Coord coord2 = new Coord(bsize - 1, i);
		  get(coord).setColor(WaterColor.YELLOW);
		  get(coord2).setColor(WaterColor.RED);
	  }

  }
  
  
  /**
   * Simple testing.
   */
  public static void main(String... args) {
    // Print out boards of size 1, 2, ..., 5
    int n = 5;
    for (int size = 1; size <= n; size++) {
      Board someBoard = new Board(size);
      System.out.println(someBoard);
    }
  }
}






