import java.io.File;
import java.util.Map;

import javax.swing.SwingUtilities;

/**
 * This is the main entry point for the application. When run, a GUI will
 * appear showing the chip with the wires connected.
 * 
 * Features of the GUI:
 *    1. Row and column numbers are displayed, in grey along the sides of the chip.
 * 2. Wires are displayed in different colors to separate each other. You are encouraged to run
 *    the medium sized chips and enjoy the beautiful board displayed!
 * 3. Wire ends are displayed in bold font.
 * 4. User Interaction: When you rest your pointer on a cell, the tip text will tell you 
 *    what kind of cell this is (free or obstacle). If it is along a wire, it will tell you
 *    the wire ID and the length of the wire. If it is one of the wire ends, it will also
 *    tell you if it is the start or the other end.
 * 5. Finally but not least: my design considers your screen size. Running on different screens,
 *    my board and font size will change accordingly to deliever a astheticaly pleasing look.
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
