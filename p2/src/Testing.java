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
  
  @Test // Test fullyFlooded()
  public void testFullyFlooded(){
	  Board myboard = new Board(5);
	  myboard.testSetFull();
	  assertTrue( myboard.fullyFlooded() );
  }
  
  @Test // Test flood1() and suggest()
  public void testFlood1(){
	  Board myboard = new Board(10);
	  myboard.testMyBoard1();
	  myboard.flood1(WaterColor.RED);
	  assertTrue( myboard.fullyFlooded() );
	  
	  Board myboard2 = new Board(8);
	  myboard2.testMyBoard1();
	  myboard2.flood1(WaterColor.RED);
	  assertTrue( myboard2.fullyFlooded() );
	  
	  Board myboard3 = new Board(8);
	  myboard3.testMyBoard2();
	  myboard3.flood1(WaterColor.BLUE);
	  assertTrue( myboard3.suggest().equals(WaterColor.RED) );
	  myboard3.flood1(WaterColor.YELLOW);
	  assertTrue( myboard3.suggest().equals(WaterColor.RED) );
	  myboard3.flood1(WaterColor.RED);
	  assertTrue( myboard3.fullyFlooded() );
  }
  
  
  @Test // Test flood2() and suggest()
  public void testFlood2(){
	  Board myboard = new Board(10);
	  myboard.testMyBoard1();
	  myboard.flood2(WaterColor.RED);
	  assertTrue( myboard.fullyFlooded() );
	  
	  Board myboard2 = new Board(8);
	  myboard2.testMyBoard1();
	  myboard2.flood2(WaterColor.RED);
	  assertTrue( myboard2.fullyFlooded() );
	  
	  Board myboard3 = new Board(8);
	  myboard3.testMyBoard2();
	  myboard3.flood2(WaterColor.BLUE);
	  assertTrue( myboard3.suggest().equals(WaterColor.RED) );
	  myboard3.flood2(WaterColor.YELLOW);
	  assertTrue( myboard3.suggest().equals(WaterColor.RED) );
	  myboard3.flood2(WaterColor.RED);
	  assertTrue( myboard3.fullyFlooded() );
  }
  
  
  @Test // Test flood3() and suggest()
  public void testFlood3(){
	  Board myboard = new Board(10);
	  myboard.testMyBoard1();
	  myboard.flood3(WaterColor.RED);
	  assertTrue( myboard.fullyFlooded() );
	  
	  Board myboard2 = new Board(8);
	  myboard2.testMyBoard1();
	  myboard2.flood3(WaterColor.RED);
	  assertTrue( myboard2.fullyFlooded() );
	  
	  Board myboard3 = new Board(8);
	  myboard3.testMyBoard2();
	  myboard3.flood3(WaterColor.BLUE);
	  assertTrue( myboard3.suggest().equals(WaterColor.RED) );
	  myboard3.flood3(WaterColor.YELLOW);
	  assertTrue( myboard3.suggest().equals(WaterColor.RED) );
	  myboard3.flood3(WaterColor.RED);
	  assertTrue( myboard3.fullyFlooded() );
  }
  
  
  @Test // Test neighbors() and onBoard()
  public void testNeighbors(){
	  Coord c = new Coord(1, 2);
	  Coord up = new Coord(0, 2);
	  Coord down = new Coord(2, 2);
	  Coord left = new Coord(1, 1);
	  Coord right = new Coord(1, 3);
	  assertTrue(c.neighbors(10).contains(up));
	  assertTrue(c.neighbors(10).contains(down));
	  assertTrue(c.neighbors(10).contains(left));
	  assertTrue(c.neighbors(10).contains(right));
	  
	  assertFalse(c.neighbors(3).contains(right));
	  
	  Coord c2 = new Coord(7, 8);
	  assertTrue(c.onBoard(5));
	  assertFalse(c2.onBoard(5));
  }
  
  
  @Test // Test hashCode()
  public void testHashcode(){
	  Coord c = new Coord(9, 10);
	  assertTrue(c.hashCode() == 9 * ((int) (Math.pow(2, 15)) - 1) + 10 );
	  
	  Coord c1 = new Coord(3, 7);
	  assertTrue(c1.hashCode() == 3 * ((int) (Math.pow(2, 15)) - 1) + 7 );
  }

}