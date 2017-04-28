import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;


public class GUI extends JFrame {

	static {
		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			ToolTipManager.sharedInstance().setInitialDelay(0);
			ToolTipManager.sharedInstance().setDismissDelay(1000);
			UIManager.put("ToolTip.background", Constants.PATH_COLOR);
			UIManager.put("ToolTip.border", BorderFactory.createEmptyBorder());
			UIManager.put("MenuItem.foreground", Constants.MENU_COLOR);
			UIManager.put("PopupMenu.border", BorderFactory.createEmptyBorder());
		}
		catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}

	private JLabel xLabel, yLabel;
	private JLabel[][] cells;

	public GUI(Chip chip, Map<Integer, Path> paths, String title) {
		setTitle("p7: " + title);

		// Pad x and y with blanks on left to synchronize indices on the grid
		int numRows = chip.dim.height + 1;
		int numCols = chip.dim.width + 1;
		int[] x = new int[numCols];
		int[] y = new int[numRows];
		for (int i = 0; i < numCols; i++)
			x[i] = i - 1;
		for (int i = 0; i < numRows; i++)
			y[i] = i - 1;

		JPanel grid = new JPanel(new GridLayout(numRows, numCols, 2, 2));
		grid.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

		Map<Coord, Integer> layout = chip.grid;
		Map<Coord, Integer> fromSet = new HashMap<>();
		Map<Coord, Integer> toSet = new HashMap<>();
		for (Wire wire : chip.wires) {
			fromSet.put(wire.from, wire.wireId);
			toSet.put(wire.to, wire.wireId);
		}
		// Set up the look and feel
		Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
		double screenMinEdgeSize = Math.min(screenDim.getWidth(), screenDim.getHeight());
		int gridMaxEdgeSize = Math.min(numCols, numRows) + 2; 
		int textSize = (int) (screenMinEdgeSize/gridMaxEdgeSize/2.2); // Need double calculation
		int labelTextSize = (int) (screenMinEdgeSize/gridMaxEdgeSize/2.3);

		int cellDim = (int) (screenMinEdgeSize/gridMaxEdgeSize);
		if (numCols < 10 && numRows < 10) {
			cellDim = Constants.CELL_DIM_LARGE;
			textSize = 16;
			labelTextSize = 14;
		}
		else if (numCols < 20 && numRows < 20){
			cellDim = Constants.CELL_DIM_MED;
			textSize = 16;
			labelTextSize = 14;
		}
		
		Font labelTextFont = new Font("Courier", Font.PLAIN, labelTextSize);
		Font wireTextFont = new Font("Ariel", Font.PLAIN, textSize);
		Font wireStartFont = new Font("Ariel", Font.BOLD, textSize);
		Font wireEndFont = new Font("Ariel", Font.BOLD, textSize);		
		Color[] wireColors = Constants.WIRE_COLOR;
		int mod = wireColors.length;
		
		cells = new JLabel[numRows][numCols];
		for (int row = 0; row < numRows; row++) {
			for (int col = 0; col < numCols; col++) {
				JLabel cell = new JLabel() {
					@Override
					public Point getToolTipLocation(MouseEvent event) {
						return new Point(0, 0);
					}
				};
				cells[row][col] = cell;
				cell.setOpaque(true);
				cell.setHorizontalAlignment(JLabel.CENTER);
				if (row == 0 || col == 0) {
					if (row >= 1 || col >= 1) {
						// label the rows with chars from x and the columns with chars from y
						cell.setFont(labelTextFont);
						cell.setForeground(Constants.LABEL_TEXT_COLOR);
						cell.setText(row == 0 ? Integer.toString(x[col]) : Integer.toString(y[row]));
					}
				} else {
					Coord coord = new Coord(col - 1, row - 1);
					int res = layout.get(coord);
					if (res == 0){
						cell.setText("");
						cell.setBackground(Constants.FREE_CELL_COLOR);
						cell.setToolTipText("Free Cell");
					} else if (res == -1) {
						cell.setBackground(Constants.OBSTACLE_COLOR);
						cell.setToolTipText("Obstacle");
					} else {
						// Choose style for start and end of wires
						String pathLengthStr = "";
						Path p = paths.get(res);
						if (p != null)
							pathLengthStr = "Length " + Integer.toString(p.length());
						else
							pathLengthStr = "Not Connected";

						if (fromSet.containsKey(coord)) {
							cell.setForeground(Constants.WIRE_START_TEXT_COLOR);
							cell.setFont(wireStartFont);
							cell.setText(Integer.toString(res));
							cell.setBackground(wireColors[res % mod]);
							cell.setToolTipText("Start of Wire " + res + ", " + pathLengthStr);
						} else if (toSet.containsKey(coord)) {
							cell.setForeground(Constants.WIRE_END_TEXT_COLOR);
							cell.setFont(wireEndFont);
							cell.setText(Integer.toString(res));
							cell.setBackground(wireColors[res % mod]);
							cell.setToolTipText("End of Wire " + res + ", " + pathLengthStr);
						} else {
							cell.setFont(wireTextFont);
							cell.setText(Integer.toString(res));
							cell.setBackground(wireColors[res % mod]);
							cell.setToolTipText("Wire " + res + ", " + pathLengthStr);
						}
					}
				}
				grid.add(cell);
			}
		}

		grid.setPreferredSize(new Dimension(numCols * cellDim, numRows * cellDim));
		grid.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10)); // top, left, bottom, right

		JPanel result = new JPanel();
		result.setLayout(new GridLayout(0, 1));
		result.setBorder(BorderFactory.createEmptyBorder(0, 40, 10, 0));

		xLabel = new JLabel();
		yLabel = new JLabel();
		xLabel.setFont(new Font("Courier", Font.PLAIN, 22));
		yLabel.setFont(new Font("Courier", Font.PLAIN, 22));
		result.add(xLabel);
		result.add(yLabel);

		JPanel main = new JPanel(new BorderLayout());
		main.add(grid, BorderLayout.CENTER);

		setContentPane(main);
		pack();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}

	/**
	 * Paint the GUI.
	 */
	public void paint(Graphics g) {
		super.paint(g);
	}
}
