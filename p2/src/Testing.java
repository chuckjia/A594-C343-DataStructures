import static org.junit.Assert.*;

import org.junit.Test;

/**
 * JUnit tests for all TODO methods.
 */

public class Testing {
  
  @Test
  public void testOnBoard() {
    assertFalse(new Coord(3, 4).onBoard(4));
    assertTrue(new Coord(3, 4).onBoard(5));
  }
  
  @Test
  public void testFullyFlooded(){
	  Board myboard = new Board(5);
	  myboard.testSetFull();
	  assertTrue( myboard.fullyFlooded() );
  }
  
  @Test
  public void testFlood1(){
	  Board myboard = new Board(10);
	  myboard.testMyBoard1();
	  myboard.flood1(WaterColor.RED);
	  assertTrue( myboard.fullyFlooded() );
  }
  

}