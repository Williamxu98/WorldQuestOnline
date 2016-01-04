package WorldCreator;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.MouseInfo;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.Timer;

import Imports.Images;
import Server.ServerFrame;
import Server.ServerGUI;
import Server.ServerWorld;

public class CreatorWorld extends JPanel implements KeyListener, ActionListener, MouseWheelListener,MouseListener, MouseMotionListener{


	private final int SCROLL_SPEED = 13;

	private char[][] grid = new char[Client.Client.SCREEN_HEIGHT/ServerWorld.TILE_SIZE + 1][Client.Client.SCREEN_WIDTH/ServerWorld.TILE_SIZE+1];	
	private int posY = 200;
	private int posX = 200;

	//Scrolling variables
	private boolean up = false;
	private boolean down = false;
	private boolean right = false;
	private boolean left = false;
	boolean ctrlPressed = false;
	private Timer scrollTimer;

	//Adding/removing tile variables
	private char selectedTile = '-';
	private int[] selectedBlock = null;
	private int[] startingBlock = null;
	private boolean rightClick = false;
	private boolean leftClick = false;
	private boolean addingTile = false;
	private boolean removingTile = false;
	private boolean highlightingArea = false;
	private boolean highlight = false;

	//Variables for changing grid size
	private boolean isNewHeight = false;
	private boolean isNewWidth = false;
	private int newHeight;
	private int newWidth;

	/**
	 * The factor of the scale of the object on the map compared to its actual
	 * height and width (can be changed by scrolling mouse wheel)
	 */
	private double objectFactor;

	/**
	 * The x-coordinate of where the mouse began to be dragged from
	 */
	private int dragSourceX;

	/**
	 * The y-coordinate of where the mouse began to be dragged from
	 */
	private int dragSourceY;

	//File
	String fileName;
	/**
	 * Table to reference objects by character
	 */
	private CreatorObject[] tiles = new CreatorObject[256];

	public CreatorWorld(String fileName) throws NumberFormatException, IOException
	{
		addKeyListener(this);
		addMouseListener(this);
		addMouseWheelListener(this);
		addMouseMotionListener(this);
		setDoubleBuffered(true);
		setBackground(Color.black);

		setLayout(null);
		setFocusable(true);
		requestFocusInWindow();
		setSize(Client.Client.SCREEN_WIDTH,Client.Client.SCREEN_HEIGHT);

		// Set the scale of objects
		objectFactor = ServerFrame.FRAME_FACTOR;

		this.fileName = fileName;

		//Check if the file already exists
		File file = new File(fileName);
		if(file.exists() && !file.isDirectory()) 
			importGrid();
		else
			clearGrid();

		Images.importImages();
		readImages();


		scrollTimer = new Timer(10,this);
		scrollTimer.start();	
	}

	public void importGrid() throws IOException
	{
		BufferedReader br = new BufferedReader(new FileReader(new File("Resources",fileName)));
		String line = br.readLine();
		String[] tokens = line.split(" ");
		grid = new char[Integer.parseInt(tokens[0])][Integer.parseInt(tokens[1])];

		for(int row = 0; row < grid.length;row++)
		{
			line = br.readLine();
			for(int col = 0; col < grid[0].length;col++)
			{
				grid[row][col] = line.charAt(col);
				System.out.print(grid[row][col]);
			}
			System.out.println();

		}
		br.close();
	}

	public void clearGrid()
	{
		for(int row = 0; row < grid.length;row++)
			for(int col = 0; col < grid[row].length;col++)
				grid[row][col] = ' ';
	}

	public char[][] getGrid() {
		return grid;
	}

	public void setGrid(char[][] grid) {
		this.grid = grid;
	}

	public void readImages() throws NumberFormatException, IOException
	{
		BufferedReader br = new BufferedReader(new FileReader("WorldCreator.cfg"));
		int numTiles = Integer.parseInt(br.readLine());
		for(int tile = 0; tile < numTiles;tile++)
		{
			String line = br.readLine();
			tiles[line.charAt(0)] = new CreatorObject(line.charAt(0),line.substring(2),this);
		}
		br.close();
	}

	public void paintComponent(Graphics graphics)
	{
		super.paintComponent(graphics);

		//Create new height and width if necessary
		if(isNewHeight)
		{
			setHeight(newHeight);
			isNewHeight = false;
		}

		if(isNewWidth)
		{
			setWidth(newWidth);
			isNewWidth = false;
		}

		// Draw tiles (draw based on player's position later)
		int startRow = (int) ((posY - ServerGUI.CENTRE_Y - 5) / (ServerWorld.TILE_SIZE / objectFactor));
		if (startRow < 0)
		{
			startRow = 0;
		}
		int endRow = (int) ((ServerGUI.CENTRE_Y + posY + 5) / (ServerWorld.TILE_SIZE / objectFactor));
		if (endRow >= grid.length)
		{
			endRow = grid.length - 1;
		}
		int startColumn = (int) ((posX - ServerGUI.CENTRE_X - 5) / (ServerWorld.TILE_SIZE / objectFactor));
		if (startColumn < 0)
		{
			startColumn = 0;
		}
		int endColumn = (int)((ServerGUI.CENTRE_X + posX + 5) / (ServerWorld.TILE_SIZE / objectFactor));
		if (endColumn >= grid[0].length)
		{
			endColumn = grid[0].length - 1;
		}

		for (int row = startRow; row <= endRow; row++)
		{
			for (int column = startColumn; column <= endColumn; column++)
			{		
				graphics.drawImage(tiles[grid[row][column]].getImage(),
						(int) (ServerGUI.CENTRE_X + column
								* (ServerWorld.TILE_SIZE / objectFactor) - posX),
						(int) (ServerGUI.CENTRE_Y + row
								* (ServerWorld.TILE_SIZE / objectFactor) - posY),
						null);
			}
		}

		if(highlightingArea)
		{
			graphics.setColor(Color.white);

			//If we are trying to selected an area that exceeds the grid, draw a smaller grid
			if(selectedBlock[0] < startRow)
				selectedBlock[0] = startRow;
			else if(selectedBlock[0] > endRow+1)
				selectedBlock[0] = endRow+1;

			if(selectedBlock[1] < startColumn)
				selectedBlock[1] = startColumn;
			else if(selectedBlock[1] > endColumn+1)
				selectedBlock[1] = endColumn+1;

			//Since rectangles can only be drawn from top-left to bottom-right, we need to figure out the starting location and width/height
			int width = (int) ((selectedBlock[1] - startingBlock[1])*(ServerWorld.TILE_SIZE / objectFactor));
			int height = (int) ((selectedBlock[0]-startingBlock[0])*(ServerWorld.TILE_SIZE / objectFactor));
			int startX = (int) (ServerGUI.CENTRE_X + startingBlock[1]
					* (ServerWorld.TILE_SIZE / objectFactor) - posX);
			int startY = (int) (ServerGUI.CENTRE_Y + startingBlock[0]
					* (ServerWorld.TILE_SIZE / objectFactor) - posY);

			//Variables for highlighting
			int startRowInt = startingBlock[0];
			int startColInt = startingBlock[1];
			int numRows = selectedBlock[0] - startingBlock[0];
			int numCols = selectedBlock[1] - startingBlock[1];

			if (width < 0)
			{
				startX += width;
				startColInt += numCols;
				numCols = -numCols;
				width = -width;
			}
			if(height < 0)
			{
				startRowInt += numRows;
				startY += height;
				numRows = -numRows;
				height = - height;
			}

			if(highlight)
			{
				highlight = false;
				highlightingArea = false;
				for(int row = startRowInt; row < startRowInt+numRows;row++)
					for(int col = startColInt; col < startColInt+numCols;col++)
						grid[row][col] = selectedTile;
			}
			else
			{
				graphics.drawRect(startX, startY, width, height);
			}
		}
		else if(selectedTile != '-' && selectedBlock != null && selectedBlock[0] >= startRow && selectedBlock[0] <= endRow && selectedBlock[1] >= startColumn && selectedBlock[1] <= endColumn && objectFactor <= ServerFrame.FRAME_FACTOR*1.1)
		{
			graphics.setColor(Color.white);
			graphics.drawRect((int) (ServerGUI.CENTRE_X + selectedBlock[1]
					* (ServerWorld.TILE_SIZE / objectFactor) - posX),
					(int) (ServerGUI.CENTRE_Y + selectedBlock[0]
							* (ServerWorld.TILE_SIZE / objectFactor) - posY),(int) (ServerWorld.TILE_SIZE / objectFactor) + 1,(int) (ServerWorld.TILE_SIZE / objectFactor) + 1);

		}

		graphics.setColor(Color.white);
		graphics.drawString("Map can only be edited when zoomed in fully", 10, 20);
		graphics.drawString("Select a tile using the mouse", 10, 35);
		graphics.drawString("Place tiles using left click", 10, 50);
		graphics.drawString("Delete tiles using right click", 10, 65);
		graphics.drawString("Use arrow keys to scroll or ctrl + left click to drag the map (left click only when map is not editable)", 10, 80);
		graphics.drawString("Highlight and fill areas of the map with tiles using ctrl + right click", 10, 95);
		graphics.drawString("Tip: Scroll in one direction and hold mouse down to create long straight lines and boxes", 10, 110);
		graphics.drawString("Tip: Zoom out and use mouse drags to quickly access other parts of the map", 10, 125);

	}

	public void update()
	{
		if(up)
			posY -= SCROLL_SPEED;
		else if(down)
			posY += SCROLL_SPEED;

		if(right)
			posX += SCROLL_SPEED;
		else if(left)
			posX -= SCROLL_SPEED;

		repaint();
	}

	public int[] getRowCol(int x, int y)
	{
		int col = (int) ((x - ServerGUI.CENTRE_X + posX)/(ServerWorld.TILE_SIZE / objectFactor));
		int row = (int) ((y - ServerGUI.CENTRE_Y + posY)/(ServerWorld.TILE_SIZE / objectFactor));
		return new int[]{row,col};
	}

	public void save() throws FileNotFoundException
	{
		PrintWriter output = new PrintWriter(new File("Resources",fileName));
		output.println(grid.length+" "+grid[0].length);
		for(int row = 0; row < grid.length;row++)
		{
			for(int col = 0; col < grid[0].length;col++)
				output.print(grid[row][col]);
			output.println();
		}
		output.close();
	}

	public void setHeight(int height)
	{
		char[][] currentGrid = grid.clone();
		grid = new char[height][grid[0].length];

		for(int row = 0; row < grid.length;row++)
			for(int col = 0; col < grid[0].length;col++)
			{
				if(row < currentGrid.length)
					grid[row][col] = currentGrid[row][col];
				else
					grid[row][col] = ' ';
			}
	}

	public void setWidth(int width)
	{
		char[][] currentGrid = grid.clone();
		grid = new char[grid.length][width];

		for(int row = 0; row < grid.length;row++)
			for(int col = 0; col < grid[0].length;col++)
			{
				if(col < currentGrid[0].length)
					grid[row][col] = currentGrid[row][col];
				else
					grid[row][col] = ' ';
			}
	}

	public void setNewHeight(int newHeight) {
		isNewHeight = true;
		this.newHeight = newHeight;
	}

	public void setNewWidth(int newWidth) {
		isNewWidth = true;
		this.newWidth = newWidth;
	}

	public CreatorObject[] getTiles() {
		return tiles;
	}

	public void setTiles(CreatorObject[] tiles) {
		this.tiles = tiles;
	}


	public char getSelectedTile() {
		return selectedTile;
	}

	public void setSelectedTile(char selectedTile) {

		if(this.selectedTile != '-')
			tiles[this.selectedTile].deselect();
		this.selectedTile = selectedTile;
	}

	public void keyPressed(KeyEvent event) {
		if(event.getKeyCode() == KeyEvent.VK_UP)
			up = true;
		else if(event.getKeyCode() == KeyEvent.VK_DOWN)
			down = true;
		else if (event.getKeyCode() == KeyEvent.VK_RIGHT)
			right = true;
		else if(event.getKeyCode() == KeyEvent.VK_LEFT)
			left = true;
		else if(event.getKeyCode() == KeyEvent.VK_CONTROL)
		{
			ctrlPressed = true;
		}
	}

	public void keyReleased(KeyEvent event) {
		if(event.getKeyCode() == KeyEvent.VK_UP)
			up = false;
		else if(event.getKeyCode() == KeyEvent.VK_DOWN)
			down = false;
		else if(event.getKeyCode() == KeyEvent.VK_RIGHT)
			right = false;
		else if(event.getKeyCode() == KeyEvent.VK_LEFT)
			left = false;
		else if(event.getKeyCode() == KeyEvent.VK_CONTROL)
		{
			ctrlPressed = false;
			highlight = true;
		}
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(this.hasFocus())
			selectedBlock = getRowCol((int)(MouseInfo.getPointerInfo().getLocation().getX()- this.getLocationOnScreen().getX()),(int)(MouseInfo.getPointerInfo().getLocation().getY()-this.getLocationOnScreen().getY()));

		if(selectedBlock != null && selectedBlock[0] >= 0 && selectedBlock[0] < grid.length && selectedBlock[1] >= 0 && selectedBlock[1] < grid[0].length)
			if(addingTile && !ctrlPressed)
				grid[selectedBlock[0]][selectedBlock[1]] = selectedTile;
			else if(removingTile && !ctrlPressed)
				grid[selectedBlock[0]][selectedBlock[1]] = ' ';

		update();
		requestFocusInWindow();		
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent event) {
		if(event.getButton() == MouseEvent.BUTTON1)
			leftClick = true;
		else if(event.getButton() == MouseEvent.BUTTON3)
			rightClick = true;

		if (leftClick && (ctrlPressed || objectFactor >= ServerFrame.FRAME_FACTOR*1.2))
		{
			dragSourceX = event.getX();
			dragSourceY = event.getY();
		}
		else if( leftClick && selectedTile != '-' && !ctrlPressed && objectFactor <= ServerFrame.FRAME_FACTOR*1.1)
		{
			addingTile = true;
		}
		else if(rightClick && objectFactor <= ServerFrame.FRAME_FACTOR*1.1 && !ctrlPressed)
		{
			removingTile = true;
		}
		else if(rightClick && objectFactor <= ServerFrame.FRAME_FACTOR*1.1 && ctrlPressed && selectedTile != '-' && selectedBlock[0] >= 0 && selectedBlock[0] < grid.length && selectedBlock[1] >= 0 && selectedBlock[1] < grid[0].length)
		{
			highlightingArea = true;
			highlight = false;
			startingBlock = selectedBlock.clone();
		}
		repaint();
	}

	@Override
	public void mouseReleased(MouseEvent event) {
		if(event.getButton() == MouseEvent.BUTTON1)
			leftClick = false;
		else if(event.getButton() == MouseEvent.BUTTON3)
			rightClick = false;

		addingTile = false;
		removingTile = false;
		highlight = true;
	}

	@Override
	public void mouseDragged(MouseEvent event)
	{
		if((ctrlPressed || objectFactor >= ServerFrame.FRAME_FACTOR*1.2) && leftClick )
		{
			//System.out.println(event.+" "+MouseEvent.BUTTON3);
			posX -= event.getX() - dragSourceX;
			posY -= event.getY() - dragSourceY;
			dragSourceX = event.getX();
			dragSourceY = event.getY();
		}
	}

	@Override
	public void mouseMoved(MouseEvent e)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent scroll)
	{
		int notches = scroll.getWheelRotation();

		if (notches > 0 && objectFactor < ServerFrame.FRAME_FACTOR * 6)
		{
			objectFactor *= (1.1 * notches);
			posX /= 1.1;
			posY /= 1.1;
		}
		else if (notches < 0)
		{
			if (objectFactor / 1.1 >= 1)
			{
				objectFactor /= (1.1 * (-notches));
				posX *= 1.1;
				posY *= 1.1;
			}
			else
			{
				posX *= objectFactor;
				posY *= objectFactor;
				objectFactor = 1;
			}

		}
	}


}
