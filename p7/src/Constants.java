import java.awt.Color;
import java.awt.Font;

public interface Constants {
	public static final String TITLE = "Project 7: Routing Wires on a Chip";
	public static final String INPUTS_FOLDER = "inputs";
	public static final String EXTENSION = ".in";

	public static final int MAX_BOARD_SIZE = 250;
	public static final int FREE = 0;
	public static final int OBSTACLE = -1;

	// Added later
	public static final int HASH_FACTOR = Integer.MAX_VALUE / MAX_BOARD_SIZE; 

	// For the GUI
	public static final Color CELL_COLOR = Color.YELLOW;
	public static final Color PATH_COLOR = new Color(0xffff99);
	public static final Color MENU_COLOR = new Color(0, 0, 200);

	public static final Color LABEL_TEXT_COLOR = Color.GRAY;
	public static final Font LABEL_TEXT_FONT = new Font("Courier", Font.PLAIN, 15);

	public static final Color FREE_CELL_COLOR = new Color(221, 235, 247);
	public static final Color OBSTACLE_COLOR = new Color(208, 206, 206); //new Color(155, 194, 230);

	public static final int CELL_DIM_LARGE = 55;
	public static final int CELL_DIM_MED = 45;
	public static final int CELL_DIM_SMALL = 35;

	public static final Color[] WIRE_COLOR = {
			new Color(255, 230, 153), // Light 2 Yellow
			new Color(244, 176, 132), // Light 3 Orange
			new Color(255, 124, 128), // Light 2 red

			new Color(255, 217, 102), // Light 3 Yellow
			new Color(0, 192, 192),
			
			new Color(252, 228, 214), // Light 1 Orange
			new Color(255, 242, 204), // Light 1 Yellow
			new Color(226, 239, 218), // Light 1 Green
			new Color(198, 224, 180), // Light 2 Green 
			
			new Color(248, 203, 173), // Light 2 Orange
			new Color(169, 208, 142), // Light 3 Green
			Color.PINK,
			new Color(255, 153, 204),
			new Color(244, 176, 132)
	};

	public static final Font WIRE_TEXT_FONT = new Font("Ariel", Font.PLAIN, 16);

	public static final Font WIRE_START_FONT = new Font("Ariel", Font.BOLD, 16);
	public static final Color WIRE_START_TEXT_COLOR = Color.BLACK; // new Color(0, 0, 0);
	public static final Font WIRE_END_FONT = new Font("Ariel", Font.BOLD, 16);
	public static final Color WIRE_END_TEXT_COLOR = Color.BLACK; //new Color(31, 78, 120);
}
