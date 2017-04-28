import java.io.File;
import java.util.Map;

import javax.swing.SwingUtilities;

/**
 * This is the main entry point for the application. When run, a GUI will
 * appear showing the cache table for a pair of randomly generated DNA
 * strands.
 * 
 * Features of the GUI:
 * 
 */

public class Driver {

	public static void main(String... args) {
		System.out.println(Constants.TITLE);
		System.out.println("Start the GUI...");
		
		String fileNameNoExt = "medium_01";
		File file = new File(Constants.INPUTS_FOLDER + "\\" + fileNameNoExt + ".in");
		Chip chip = new Chip(file);
		Map<Integer, Path> paths = PathFinder.connectAllWires(chip);
		SwingUtilities.invokeLater(() -> new GUI(chip, paths, fileNameNoExt + ".in"));
	} 
}
