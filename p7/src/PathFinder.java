import java.awt.Dimension;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * TODO
 * 
 * Most of the work for this project involves implementing the
 * connectAllWires() method in this class. Feel free to create
 * any helper methods that you deem necessary. 
 * 
 * Your goal is to come up with an efficient algorithm that will 
 * find a layout that connects all the wires (if one exists) while
 * attempting to minimize the overall wire length.
 */

public class PathFinder {

	/**
	 * TODO
	 * 
	 * Lays out a path connecting each wire on the chip, and then 
	 * returns a map that associates a wire id numbers to the paths
	 * corresponding to the connected wires on the grid. If it is 
	 * not possible to connect the endpoints of a wire, then there
	 * should be no association for the wire id# in the result.
	 */
	public static Map<Integer, Path> connectAllWires(Chip chip) {
		int n = chip.wires.size();
		if (n <= 4)
			return connectAllWires_AllOrders(chip);
		if (n <= 8) 
			return connectAllWires_EarlyStop(chip);
		return connectAllWires_Fast(chip);
	}
	

	/**
	 * Connects all wires by running Dijkstra through all possible wiring 
	 * orders and return the best solution (the one with the most connected 
	 * wires or if multiple wires have the same largest number of wire 
	 * connected, the one with the shortest wiring length).
	 * 
	 * The method finds the (to some extent) optimal solution, but it works 
	 * slow (O(n!) times of wiring) when number of wires is large.
	 * @param chip The chip
	 * @return A map between wire id and a wire path.
	 * @author Chuck Jia
	 */
	public static Map<Integer, Path> connectAllWires_AllOrders(Chip chip) {
		Map<Integer, Path> layout = new HashMap<>();
		/*
		 * Sort the wire order according to the Manhattan distance between the
		 * two wire ends. This will help find the optimal solution earlier and 
		 * thus reduces the chance of having better solutions in later steps. 
		 * As a result, it improves efficiency by reducing the number of the 
		 * copy-over steps in permuteAllOrders() when better solutions are found. 
		 */
		List<Wire> wires = new LinkedList<>(chip.wires);
		wires.sort((x, y) -> x.separation() - y.separation());
		int n = wires.size(); // Number of wires
		int[] a = new int[n]; // Array for permutation of wiring orders 
		for (int i = 0; i < n; i++)
			a[i] = i;		
		// Run Dijkstra through every possible wiring order
		permuteAllOrders(a, 0, chip, wires, layout);
		// Layout paths on the grid. Used for displaying path in the GUI
		layoutPaths(chip, layout); 
		// Sort to return the wires back to their original order
		if (layout.size() != n)
			for (Wire wire : wires) 
				if (!layout.containsKey(wire.wireId)) 
					System.out.println("Cannot connect wire #" + wire.wireId);
		return layout;
	}
	

	/**
	 * Connects all wires by running Dijkstra through all possible wiring 
	 * orders until having all wires connected or exhausted all possible orders.
	 * 
	 * The method stops immediately once a solution with all wires connected is 
	 * found. Otherwise, the algorithm finishes when all wiring orders have
	 * been calculated, in which case the algorithm takes the best solution (the 
	 * one with the most connected wires or if multiple wires have the same largest
	 * number of wire connected, the one with the shortest wiring length).
	 * The method guarantees a solution to connect all wires if such a solution
	 * exists. The method works best for medium sized chips with medium number 
	 * of wires.
	 * @param chip The chip
	 * @return A map between wire id and a wire path.
	 * @author Chuck Jia
	 */
	public static Map<Integer, Path> connectAllWires_EarlyStop(Chip chip) {
		Map<Integer, Path> layout = new HashMap<>();
		/*
		 * Sort the wire order according to the Manhattan distance between the
		 * two wire ends. This will help find the optimal solution earlier. Also, 
		 * if no early stopping is achieved, this reduces the chance of having 
		 * better solutions in later steps. As a result, it improves efficiency 
		 * by reducing the number of the copy-over steps in permuteAllOrders() 
		 * when better solutions are found. 
		 */
		List<Wire> wires = new LinkedList<>(chip.wires);
		wires.sort((x, y) -> x.separation() - y.separation());
		int n = wires.size(); // Number of wires
		boolean[] go = { true }; // Marker for early stopping
		int[] a = new int[n]; // Array for permutation of wiring orders 
		for (int i = 0; i < n; i++)
			a[i] = i;
		// Run Dijkstra through every possible wiring order and stop immediately when
		// a solution that connects all wires is found
		permuteWithEarlyStop(a, 0, chip, wires, layout, go);
		layoutPaths(chip, layout);
		// Sort to return the wires back to their original order
		if (layout.size() != n)
				for (Wire wire : wires) 
					if (!layout.containsKey(wire.wireId)) 
						System.out.println("Cannot connect wire #" + wire.wireId);
		return layout;
	}


	/**
	 * Connects all wires by running Dijkstra. 
	 * 
	 * The order of the wiring is pre-calculated by sorting the wires according
	 * to the Manhattan distance between wire ends to find better solutions. 
	 * @param chip The chip
	 * @return A map between wire id and a wire path.
	 * @author Chuck Jia
	 */
	public static Map<Integer, Path> connectAllWires_Fast(Chip chip) {
		Map<Integer, Path> layout = new HashMap<>();
		List<Wire> wires = chip.wires;
		/*
		 * Sort the wire order according to the Manhattan distance between the
		 * two wire ends. This will increase the chance of finding better 
		 * solutions. 
		 */
		chip.wires.sort((x, y) -> x.separation() - y.separation());
		for (Wire wire : wires) 
			if (!dijkstra(wire, chip, layout))
				System.out.println("Cannot connect wire #" + wire.wireId);	
		// Sort to return the wires back to their original order
		wires.sort((x, y) -> x.wireId - y.wireId);
		return layout;
	}
		

	/**
	 * This helper function recursively permute over all possible wiring
	 * orders and executes the Dijkstra algoritm for every order.
	 * @param a Array used for permutation
	 * @param start Permutation start position
	 * @param chip The chip
	 * @param layout A map between wire id and a wire path.
	 * @author Chuck Jia
	 */
	private static void permuteAllOrders(int[] a, int start, Chip chip, List<Wire> wires, Map<Integer, Path> layout) {
		int n = a.length;
		if (start == n - 1) {
			Map<Integer, Path> newLayout = new HashMap<>();
			chip.layout(); // Clear the previous layout. Used as markers
			for (int i = 0; i < n; i++)
				dijkstra(wires.get(a[i]), chip, newLayout);
			if (newLayout.size() > layout.size()) { // If new layout connects more wires
				layout.clear();
				layout.putAll(newLayout);
			} else if (layout.size() == newLayout.size()) { // If new and previous layouts connects same number of wires
				if (totalWireUsage(newLayout) < totalWireUsage(layout)) { // If new layouts uses shorter wires
					layout.clear();
					layout.putAll(newLayout);
				}
			}
			return;
		}

		for (int i = start; i < n; i++) {
			swap(a, start, i);
			permuteAllOrders(a, start + 1, chip, wires, layout);
			swap(a, start, i);
		}

	}


	/**
	 * This helper function recursively permute over all possible wiring
	 * orders and executes the Dijkstra algorithm for every order until 
	 * a solution wiring all wires is found. Otherwise it exhausts all 
	 * possible orders and selects the best solution.
	 * @param a Array used for permutation
	 * @param start Permutation start position
	 * @param chip The chip
	 * @param layout A map between wire id and a wire path.
	 * @author Chuck Jia
	 */
	private static void permuteWithEarlyStop(int[] a, int start, Chip chip, List<Wire> wires, Map<Integer, Path> layout, boolean[] go) {
		int n = a.length;
		if (start == n - 1) {
			Map<Integer, Path> newLayout = new HashMap<>();
			chip.layout(); // Clear the previous layout. Used as markers
			for (int i = 0; i < n; i++) 
				dijkstra(wires.get(a[i]), chip, newLayout);

			if (newLayout.size() > layout.size()) { // If new layout connects more wires
				layout.clear();
				layout.putAll(newLayout);
			} else if (layout.size() == newLayout.size()) { // If new and previous layouts connects same number of wires
				if (totalWireUsage(newLayout) < totalWireUsage(layout)) { // If new layouts uses shorter wires
					layout.clear();
					layout.putAll(newLayout);
				}
			}

			if (layout.size() == chip.wires.size()) // If a solution connecting all wires is found
				go[0] = false;
			return;
		}

		for (int i = start; i < n && go[0]; i++) {
			swap(a, start, i);
			permuteWithEarlyStop(a, start + 1, chip, wires, layout, go);
			swap(a, start, i);
		}
	}
	

	/**
	 * Helper method for all the permutation methods
	 * @param a The array whose elements are to be swapped
	 * @param i The index of the first of the pair to be swapped
	 * @param j The index of the second of the pair to be swapped
	 */
	protected static void swap(int[] a, int i, int j) {
		int t = a[i];
		a[i] = a[j];
		a[j] = t;
	}
	

	/**
	 * Layout paths on a chip
	 * @param chip The chip
	 * @param layout A map between wire id and a wire path.
	 */
	public static void layoutPaths (Chip chip, Map<Integer, Path> layout) {
		chip.layout();
		for (int id : layout.keySet()){
			Path path = layout.get(id);
			for (Coord coord : path)
				chip.grid.put(coord, id);
		}
	}
	

	/**
	 * Runs the Dijkstra algorithm on a chip for one wire.
	 * @param wire The wire to be connected.
	 * @param chip The chip.
	 * @param layout A map between wire id and a wire path.
	 * @return True if wire is successfully connected, false if failed.
	 */	
	public static boolean dijkstra(Wire wire, Chip chip, Map<Integer, Path> layout) {		
		Map<Coord, Integer> grid = chip.grid;
		Dimension dim = chip.dim;
		int width = dim.width, height = dim.height, id = wire.wireId;
		Coord from = wire.from, to = wire.to;

		int[][] dist = new int[width][height]; // Holds the minimum distance from "from"
		Coord[][] parent = new Coord[width][height]; // Holds the parent coordinates along the shortest path
		boolean[][] visited = new boolean[width][height]; // Holds markers for checking if a coord's minimal distance is decided

		Comparator<Coord> comp = (a, b) -> dist[a.getX()][a.getY()] - dist[b.getX()][b.getY()];
		Heap candidates = new Heap(comp, dim);

		// Populates dist and others with default values. Parent does not need pre-population.
		for (int[] a : dist)
			Arrays.fill(a, Integer.MAX_VALUE);

		grid.put(to, Constants.FREE); // Temporarily set to improve efficiency and will be reset later
		dist[from.getX()][from.getY()] = 0;

		Coord p = from;
		candidates.insert(p);
		while (!p.equals(to)) {
			for (Coord q : p.neighbors(dim)){ // Do nothing if y is occupied b/c y wouldn't be in others
				int qX = q.getX(), qY = q.getY();
				if (grid.get(q) == Constants.FREE && !visited[qX][qY]) {
					int alt = dist[p.getX()][p.getY()] + 1;
					if (alt < dist[qX][qY]) {
						dist[qX][qY] = alt;
						parent[qX][qY] = p;
						candidates.update(q); // If q exists in heap then update the heap. Otherwise update is same with insert
					}
				}
			}
			if (candidates.isEmpty())
				break;
			p = candidates.delete();
			visited[p.getX()][p.getY()] = true;
		}

		Path path = new Path(wire); // "from" is added to path from here
		if (parent[to.getX()][to.getY()] != null) {
			Coord q = to;
			while (!q.equals(from)){
				path.add(1, q); // Add to position 1
				grid.put(q, id);
				q = parent[q.getX()][q.getY()];
			}
			layout.put(id, path);
			return true;
		} else if (from.equals(to)) {
			layout.put(id, path);
			grid.put(to, id);
			return true; 
		} else {
			grid.put(to, id);
			return false;
		}
	}
	

	/**
	 * TODO
	 * 
	 * Returns the sum of the lengths of all non-null paths in the given layout.
	 */
	public static int totalWireUsage(Map<Integer, Path> layout) {
		int ans = 0;
		for (Path p : layout.values())
			ans += p.length();
		return ans;
	}
}