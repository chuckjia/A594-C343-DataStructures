import static org.junit.Assert.*;

import java.awt.Dimension;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.SwingUtilities;

import org.junit.Test;

/**
 * TODO: Write a comprehensive suite of unit tests!!!!
 * 
 * We include some very simple tests to get you started.
 */

public class Testing {
	
	/*
	 * In the following test, we check the results by individual cells (in addition
	 * to checking by the validateLayout() method) to ensure our methods produce  
	 * the desired result. 
	 * (This is because our program produces the optimal solution for relatively
	 * small chips. Many layout would pass the validateLayout() method, but
	 * they are not the expected result from our algorithm.)  
	 */
	@Test
	public void testDijkstraSmallChip() {
		
		/*
		 *  Case 1: 1 Wire
		 */
		String fileNameNoExt = "other_01";
		Chip chip = new Chip(new File("inputs/" + fileNameNoExt + ".in"));
		Wire wire = chip.wires.get(0);
		Map<Integer, Path> layout = new HashMap<Integer, Path>();
		PathFinder.dijkstra(wire, chip, layout);
		// Path for wire #1 
		Path path = layout.get(1);
		// Testing each cell
		for (int i = 0; i <= 4; i++)
			assertTrue(path.contains(new Coord(0, i)));
		for (int i = 0; i <= 3; i++)
			assertTrue(path.contains(new Coord(i, 4)));
		for (int i = 3; i <= 5; i++)
			assertTrue(path.contains(new Coord(i, 5)));
		// Validate the layout
		assertTrue(validateLayout(layout, chip));

		/*
		 *  Case 2: 2nd Wire
		 */
		wire = chip.wires.get(1);
		PathFinder.dijkstra(wire, chip, layout);
		path = layout.get(2);
		assertTrue(path.contains(new Coord(5, 1)));
		assertTrue(validateLayout(layout, chip));

		/*
		 *  Case 3: Wire that cannot be connected
		 */
		wire = chip.wires.get(2);
		PathFinder.dijkstra(wire, chip, layout);
		path = layout.get(3);
		assertNull(path);

		/*
		 *  Case 4: Wires That Cross Original Order
		 */
		fileNameNoExt = "other_02";
		chip = new Chip(new File("inputs/" + fileNameNoExt + ".in"));
		layout = new HashMap<Integer, Path>();
		// Connect wire #1 first 
		wire = chip.wires.get(0);
		PathFinder.dijkstra(wire, chip, layout);
		path = layout.get(1);
		for (int i = 0; i <= 4; i++)
			assertTrue(path.contains(new Coord(1, i)));
		// Connect wire #2
		wire = chip.wires.get(1);
		PathFinder.dijkstra(wire, chip, layout);
		path = layout.get(2);
		assertNull(path);
		assertTrue(validateLayout(layout, chip));

		/*
		 *  Case 5: Wires That Cross Reverse Order
		 */
		fileNameNoExt = "other_02";
		chip = new Chip(new File("inputs/" + fileNameNoExt + ".in"));
		layout = new HashMap<Integer, Path>();
		// Connect wire #2 First
		wire = chip.wires.get(1);
		PathFinder.dijkstra(wire, chip, layout);
		path = layout.get(2);
		for (int i = 0; i <= 2; i++)
			assertTrue(path.contains(new Coord(i, 1)));
		// Connect wire #1 
		wire = chip.wires.get(0);
		PathFinder.dijkstra(wire, chip, layout);
		path = layout.get(1);
		for (int i = 1; i <= 3; i++)
			assertTrue(path.contains(new Coord(i, 0)));
		for (int i = 0; i <= 2; i++)
			assertTrue(path.contains(new Coord(3, i)));
		for (int i = 1; i <= 3; i++)
			assertTrue(path.contains(new Coord(i, 2)));
		for (int i = 2; i <= 4; i++)
			assertTrue(path.contains(new Coord(1, i)));
		assertTrue(validateLayout(layout, chip));
		
		
		/*
		 * Case 06 : Only Path (Medium Size)
		 */
		fileNameNoExt = "other_03";
		chip = new Chip(new File("inputs/" + fileNameNoExt + ".in"));
		wire = chip.wires.get(0);
		layout = new HashMap<>();
		PathFinder.dijkstra(wire, chip, layout);
		// Path for wire #1 
		path = layout.get(1);
		assertTrue(validateLayout(layout, chip));
	}
	
	
	//@Test
	public void testConnectAllWires_OptimalSoln() {
		/*
		 * Case 1: 3 wires with 1 cannot be connected
		 */
		String fileNameNoExt = "other_01";
		Chip chip = new Chip(new File("inputs/" + fileNameNoExt + ".in"));
		Map<Integer, Path> layout = PathFinder.connectAllWires_OptimalSoln(chip);
		// Path for wire #1 
		Path path = layout.get(1);
		for (int i = 0; i <= 4; i++)
			assertTrue(path.contains(new Coord(0, i)));
		for (int i = 0; i <= 3; i++)
			assertTrue(path.contains(new Coord(i, 4)));
		for (int i = 3; i <= 5; i++)
			assertTrue(path.contains(new Coord(i, 5)));
		path = layout.get(2);
		// Path for wire #2
		assertTrue(path.contains(new Coord(5, 1)));
		// Path for wire #3
		assertNull(layout.get(3));
		assertTrue(validateLayout(layout, chip));

		/*
		 * Case 2: 2 wire may cross
		 */
		fileNameNoExt = "other_02";
		chip = new Chip(new File("inputs/" + fileNameNoExt + ".in"));
		layout = PathFinder.connectAllWires_OptimalSoln(chip);
		// Path for wire #1 
		path = layout.get(1);
		for (int i = 1; i <= 3; i++)
			assertTrue(path.contains(new Coord(i, 0)));
		for (int i = 0; i <= 2; i++)
			assertTrue(path.contains(new Coord(3, i)));
		for (int i = 1; i <= 3; i++)
			assertTrue(path.contains(new Coord(i, 2)));
		for (int i = 2; i <= 4; i++)
			assertTrue(path.contains(new Coord(1, i)));
		assertTrue(validateLayout(layout, chip));
		
		/*
		 * Case 03 : Only Path (Medium Size)
		 */
		fileNameNoExt = "other_03";
		chip = new Chip(new File("inputs/" + fileNameNoExt + ".in"));
		layout = PathFinder.connectAllWires_OptimalSoln(chip);
		// Path for wire #1 
		path = layout.get(1);
		System.out.println(layout);
		assertTrue(validateLayout(layout, chip));
	}


	//@Test
	public void testConnectAllWires_EarlyStop() {
		/*
		 * Case 1: 3 wires with 1 cannot be connected
		 */
		String fileNameNoExt = "other_01";
		Chip chip = new Chip(new File("inputs/" + fileNameNoExt + ".in"));
		Map<Integer, Path> layout = PathFinder.connectAllWires_EarlyStop(chip);
		// Path for wire #1 
		Path path = layout.get(1);
		for (int i = 0; i <= 4; i++)
			assertTrue(path.contains(new Coord(0, i)));
		for (int i = 0; i <= 3; i++)
			assertTrue(path.contains(new Coord(i, 4)));
		for (int i = 3; i <= 5; i++)
			assertTrue(path.contains(new Coord(i, 5)));
		path = layout.get(2);
		// Path for wire #2
		assertTrue(path.contains(new Coord(5, 1)));
		// Path for wire #3
		assertNull(layout.get(3));
		
		/*
		 * Case 2: 2 wire may cross
		 */
		fileNameNoExt = "other_02";
		chip = new Chip(new File("inputs/" + fileNameNoExt + ".in"));
		layout = PathFinder.connectAllWires_EarlyStop(chip);
		// Path for wire #1 
		path = layout.get(1);
		for (int i = 1; i <= 3; i++)
			assertTrue(path.contains(new Coord(i, 0)));
		for (int i = 0; i <= 2; i++)
			assertTrue(path.contains(new Coord(3, i)));
		for (int i = 1; i <= 3; i++)
			assertTrue(path.contains(new Coord(i, 2)));
		for (int i = 2; i <= 4; i++)
			assertTrue(path.contains(new Coord(1, i)));
		assertTrue(validateLayout(layout, chip));
		
		/*
		 * Case 03 : Only Path (Medium Size)
		 */
		fileNameNoExt = "other_03";
		chip = new Chip(new File("inputs/" + fileNameNoExt + ".in"));
		layout = PathFinder.connectAllWires_Fast(chip);
		// Path for wire #1 
		path = layout.get(1);
		assertTrue(validateLayout(layout, chip));
	}


	//@Test
	public void testConnectAllWires_Fast() {
		/*
		 * Case 1: 3 wires with 1 cannot be connected
		 */
		String fileNameNoExt = "other_01";
		Chip chip = new Chip(new File("inputs/" + fileNameNoExt + ".in"));
		Map<Integer, Path> layout = PathFinder.connectAllWires_Fast(chip);
		// Path for wire #1 
		Path path = layout.get(1);
		for (int i = 0; i <= 4; i++)
			assertTrue(path.contains(new Coord(0, i)));
		for (int i = 0; i <= 3; i++)
			assertTrue(path.contains(new Coord(i, 4)));
		for (int i = 3; i <= 5; i++)
			assertTrue(path.contains(new Coord(i, 5)));
		path = layout.get(2);
		// Path for wire #2
		assertTrue(path.contains(new Coord(5, 1)));
		// Path for wire #3
		assertNull(layout.get(3));
		
		/*
		 * Case 2: 2 wire may cross
		 */
		fileNameNoExt = "other_02";
		chip = new Chip(new File("inputs/" + fileNameNoExt + ".in"));
		layout = PathFinder.connectAllWires_EarlyStop(chip);
		// Path for wire #1 
		path = layout.get(1);
		for (int i = 1; i <= 3; i++)
			assertTrue(path.contains(new Coord(i, 0)));
		for (int i = 0; i <= 2; i++)
			assertTrue(path.contains(new Coord(3, i)));
		for (int i = 1; i <= 3; i++)
			assertTrue(path.contains(new Coord(i, 2)));
		for (int i = 2; i <= 4; i++)
			assertTrue(path.contains(new Coord(1, i)));
		assertTrue(validateLayout(layout, chip));
		
		/*
		 * Case 03 : Only Path (Medium Size)
		 */
		fileNameNoExt = "other_03";
		chip = new Chip(new File("inputs/" + fileNameNoExt + ".in"));
		layout = PathFinder.connectAllWires_Fast(chip);
		// Path for wire #1 
		path = layout.get(1);
		System.out.println(layout);
		assertTrue(validateLayout(layout, chip));
	}



	//@Test
	public void chip3Manual() {
		Dimension dim;
		List<Obstacle> obstacles = new LinkedList<>();
		List<Wire> wires = new LinkedList<>();

		// Build the chip described in small_03.in
		dim = new Dimension(7, 6);
		obstacles.add(new Obstacle(1, 1, 1, 4));
		obstacles.add(new Obstacle(1, 4, 3, 4));
		obstacles.add(new Obstacle(3, 2, 3, 4));
		obstacles.add(new Obstacle(3, 2, 5, 2));
		wires.add(new Wire(1, 4, 3, 2, 3));
		Chip chip3 = new Chip(dim, obstacles, wires);

		// Test properties of chip3.
		assertEquals(7, chip3.dim.width);
		assertEquals(6, chip3.dim.height);
		assertEquals(4, chip3.obstacles.size());
		assertEquals(1, chip3.wires.size());
		assertEquals(1, chip3.wires.get(0).wireId);
		assertEquals(4, chip3.wires.get(0).from.x);
		assertEquals(3, chip3.wires.get(0).from.y);
		assertEquals(2, chip3.wires.get(0).to.x);
		assertEquals(3, chip3.wires.get(0).to.y);
	}

	//@Test
	public void chip9Manual() {
		Dimension dim;
		List<Obstacle> obstacles = new LinkedList<>();
		List<Wire> wires = new LinkedList<>();

		// Build the chip described in small_09.in
		dim = new Dimension(4, 5);
		wires.add(new Wire(1, 1, 0, 1, 4));
		wires.add(new Wire(2, 0, 1, 2, 1));
		wires.add(new Wire(3, 0, 2, 2, 2));
		wires.add(new Wire(4, 0, 3, 2, 3));
		Chip chip9 = new Chip(dim, obstacles, wires);

		// TODO: Test properties of chip9.
		assertEquals(4, chip9.dim.width);
		assertEquals(5, chip9.dim.height);
		assertEquals(0, chip9.obstacles.size());
		assertEquals(4, chip9.wires.size());

		assertEquals(1, chip9.wires.get(0).wireId);
		assertEquals(1, chip9.wires.get(0).from.x);
		assertEquals(0, chip9.wires.get(0).from.y);
		assertEquals(1, chip9.wires.get(0).to.x);
		assertEquals(4, chip9.wires.get(0).to.y);

		assertEquals(2, chip9.wires.get(1).wireId);
		assertEquals(0, chip9.wires.get(1).from.x);
		assertEquals(1, chip9.wires.get(1).from.y);
		assertEquals(2, chip9.wires.get(1).to.x);
		assertEquals(1, chip9.wires.get(1).to.y);

		assertEquals(3, chip9.wires.get(2).wireId);
		assertEquals(0, chip9.wires.get(2).from.x);
		assertEquals(2, chip9.wires.get(2).from.y);
		assertEquals(2, chip9.wires.get(2).to.x);
		assertEquals(2, chip9.wires.get(2).to.y);

		assertEquals(4, chip9.wires.get(3).wireId);
		assertEquals(0, chip9.wires.get(3).from.x);
		assertEquals(3, chip9.wires.get(3).from.y);
		assertEquals(2, chip9.wires.get(3).to.x);
		assertEquals(3, chip9.wires.get(3).to.y);
	}

	//@Test
	public void tinyWire() {
		Wire w1 = new Wire(1, 1, 2, 3, 4);
		assertEquals(1, w1.wireId);
		assertEquals(new Coord(1, 2), w1.from);
		assertEquals(new Coord(3, 4), w1.to);
		assertEquals(4, w1.separation());

		Wire w2 = new Wire(2, 3, 4, 1, 2);
		assertEquals(2, w2.wireId);
		assertEquals(new Coord(3, 4), w2.from);
		assertEquals(new Coord(1, 2), w2.to);
		assertEquals(4, w2.separation());
	}

	//@Test
	public void tinyObstacle() {
		Obstacle obs = new Obstacle(5, 5, 5, 5);
		assertTrue(obs.contains(new Coord(5, 5)));
	}

	//@Test
	public void chip3File() {
		Chip chip3 = new Chip(new File("inputs/small_03.in"));
		assertEquals(7, chip3.dim.width);
		assertEquals(6, chip3.dim.height);
		assertEquals(4, chip3.obstacles.size());
		assertEquals(1, chip3.wires.size());
		assertEquals(1, chip3.wires.get(0).wireId);
		assertEquals(4, chip3.wires.get(0).from.x);
		assertEquals(3, chip3.wires.get(0).from.y);
		assertEquals(2, chip3.wires.get(0).to.x);
		assertEquals(3, chip3.wires.get(0).to.y);
	}

	//@Test
	public void chip3Layout() {
		Chip chip3 = new Chip(new File("inputs/small_03.in"));
		System.out.println(chip3);
		System.out.println("From " + chip3.wires.get(0).from + " to " + chip3.wires.get(0).to);
		Map<Integer, Path> layout = PathFinder.connectAllWires(chip3);
		assertNotNull(layout);
		assertEquals(1, layout.size());

		// TODO: Test properties of layout.get(0). // Should it be get(1)? JIA
		Path path = layout.get(1);
		assertEquals(11, path.length());
		assertTrue(path.get(0).equals(new Coord(4, 3)));
		assertTrue(path.get(1).equals(new Coord(5, 3)));
		assertTrue(path.get(2).equals(new Coord(6, 3)));
		assertTrue(path.get(3).equals(new Coord(6, 2)));
		assertTrue(path.get(4).equals(new Coord(6, 1)));
		assertTrue(path.get(5).equals(new Coord(5, 1)));
		assertTrue(path.get(6).equals(new Coord(4, 1)));
		assertTrue(path.get(7).equals(new Coord(3, 1)));
		assertTrue(path.get(8).equals(new Coord(2, 1)));
		assertTrue(path.get(9).equals(new Coord(2, 2)));
		assertTrue(path.get(10).equals(new Coord(2, 3)));
	}

	//@Test
	public void speedTest() {
		String fileNameNoExt = "huge_01";
		File file = new File(Constants.INPUTS_FOLDER + "\\" + fileNameNoExt + ".in");
		Chip chip = new Chip(file);
		PathFinder.connectAllWires(chip);
	}

	/*********************************************************************
	 * Benchmarks: Computes layouts for chips described in input/wire*.in
	 *********************************************************************/

	//@Test
	public void runBenchmarks() {
		// runBenchmarksFor("small");
		// runBenchmarksFor("small_07");		
		// runBenchmarksFor("medium");
		runBenchmarksFor("big");
		// runBenchmarksFor("huge");
	}

	/**
	 * Runs the benchmarks on all filenames starting with the give prefix.
	 */
	public static void runBenchmarksFor(String prefix) {
		System.out.println(String.format("Routing chips %s/%s*%s\n",
				Constants.INPUTS_FOLDER, prefix, Constants.EXTENSION));
		File folder = new File(Constants.INPUTS_FOLDER);
		for (File file : folder.listFiles()) 
			if (file.isFile() && file.getName().startsWith(prefix) &&
					file.getName().endsWith(Constants.EXTENSION)) {
				System.out.println("========== " + file.getName() + " ==========");
				Chip chip = new Chip(file);
				System.out.println("before:\n" + chip);
				Map<Integer, Path> layout = PathFinder.connectAllWires(chip);
				if (layout == null || layout.size() != chip.wires.size())
					System.out.println();
				System.out.println("after:\n" + chip);
				chip.layout();
				if (!validateLayout(layout, chip))
					System.out.println(file.getName());
				assertTrue(validateLayout(layout, chip));
				System.out.println("cost: " + PathFinder.totalWireUsage(layout));
			} 
		System.out.println("==============================");
		System.out.println("Benchmarks completed...\n");
	}

	/**
	 * Returns true iff the given wire layout is legal on the given grid.
	 */
	public static boolean validateLayout(Map<Integer, Path> layout, Chip chip) {
		String msg = "Incorrect %s of path for wire %d, found %s, expected %s.";
		Dimension dim = chip.dim;
		List<Obstacle> obstacles = chip.obstacles;
		List<Wire> wires = chip.wires;
		int numWires = wires.size();
		for (int i = 1; i <= numWires; i++) {
			Path path = layout.get(i);
			if (path != null) {
				Coord start = path.get(0), end = path.get(path.size() - 1);
				if (!start.equals(wires.get(i - 1).from)) {
					System.out.println(String.format(msg, "start", 
							i, start, wires.get(i - 1).from));
					return false;
				}
				if (!end.equals(wires.get(i - 1).to)) {
					System.out.println(String.format(msg, "end", 
							i, start, wires.get(i - 1).to));
					return false;
				}
				Set<Coord> used = new HashSet<>();
				for (int j = 0; j < path.size(); j++) {
					Coord cell = path.get(j);
					// Make sure the cell coordinates are in range for the grid.
					if (!cell.onBoard(dim))
						return false;
					// Make sure none of the wires cross each other.
					if (used.contains(cell)) 
						return false;
					// Make sure that the path consists only of connected neighbors.
					if (j > 0 && !path.get(j - 1).neighbors(dim).contains(cell))
						return false;
					// Make sure that the path doesn't pass through an obstacle.
					for (Obstacle obs : obstacles) {
						if (obs.contains(cell))
							return false;
					}
					used.add(cell);
				}
			}
		}
		return true;
	}
}