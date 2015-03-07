package com.PromethiaRP.Draeke.AStar;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;


public class RefinedGrid extends BasicGame{

	private Cell[][] cells;
	private Heap<Cell> openCells = new Heap<Cell>();	// openCells should be sorted
	private Set<Cell> closedCells = new HashSet<Cell>();	// closedCells does not need to be



	private static int m_Width = 40;
	private static int m_Height = 30;

	private Cell startCell;
	private Cell goalCell;

	private final int CELLWIDTH = 20;
	private final int CELLHEIGHT = 20;

	private int containerHeight;
	public RefinedGrid(String title, int width, int height) {
		super(title);
		cells = new Cell[width][height];
		m_Width = width;
		m_Height = height;
	}

	private void initializeGrid() {
		for (int i = 0; i < cells.length; i++) {
			for (int j = 0; j < cells[i].length; j++) {
				cells[i][j] = new Cell(i, j, CELLWIDTH, CELLHEIGHT);
			}
		}

		startCell = cells[10][15];
		goalCell = cells[30][15];

		startCell.setGoalCost(getGoalCost(startCell.getX(),startCell.getY()));
		openCells.insert(startCell);
	}

	public static void main(String args[]) {
		//System.setProperty("java.library.path", "build\\natives\\windows");
		try {
			AppGameContainer app = new AppGameContainer(new RefinedGrid("Title", m_Width, m_Height));

			app.setTargetFrameRate(60);
			app.setDisplayMode(800, 600, false);
			app.setShowFPS(false);
			app.start();
		} catch (SlickException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void drawCell(Graphics grafix, Cell cel, Color base, Color outline) {
		int x = cel.getAdjustedX();
		int y = cel.getAdjustedY();
		int width = cel.getWidth();
		int height = cel.getHeight();
		grafix.setColor(base);
		grafix.fillRect(x, y, width, height);
		grafix.setColor(outline);
		grafix.drawRect(x, y, width, height);
	}

	public void drawParentLine(Graphics grafix, Cell cel, Color line) {
		if(cel.getParent() == null) {
			return;
		}
		grafix.setColor(line);

		grafix.drawLine(cel.getAdjustedX() + (int)(cel.getWidth()/2), cel.getAdjustedY() + (int)(cel.getHeight()/2),
				cel.getParent().getAdjustedX() + (int)(cel.getWidth()/2), cel.getParent().getAdjustedY() + (int)(cel.getHeight()/2));
	}

	@Override
	public void render(GameContainer container, Graphics grafix) throws SlickException {
		// TODO Auto-generated method stub

		
			for (Cell pt : openCells) {
				drawCell(grafix, pt, Color.green, Color.blue);
			}
			for (Cell cll : closedCells) {
				drawCell(grafix, cll, Color.lightGray, Color.gray);
			}


		
			for (int i = 0; i < cells.length; i++ ) {

				for (int j = 0; j < cells[i].length; j++ ) {
					if (cells[i][j].getWalkable()) {
						drawParentLine(grafix, cells[i][j],Color.red);
						continue;
					} else {
						drawCell(grafix, cells[i][j], Color.white, Color.orange);
					}
				}
			}
		

		{
			if(isFinished()) {
				Cell c = goalCell;
				do {
//					fixPaths(c.getX(), c.getY());
//					fixPaths(c.getX(), c.getY());
					drawParentLine(grafix,c, Color.cyan);
				} while( (c = c.getParent())!= null );
			}
		}

		{
			Point pt = getCellMousePosition();
			Cell c = cells[pt.x][pt.y];
			float lineWidth = grafix.getLineWidth();
			grafix.setLineWidth(2 * lineWidth);
			do {
				drawParentLine(grafix,c, Color.blue);
			} while( (c = c.getParent())!= null );
			grafix.setLineWidth(lineWidth);
			grafix.setColor(Color.white);
			grafix.drawString("( " + pt.x + ", " + pt.y + ")", 10, 10);
			Cell cll = cells[pt.x][pt.y];
			grafix.drawString("Total cost: " + (cll.getTotalCost()), 10, 25);
			grafix.drawString("Path cost: " + cll.getPathCost(), 10, 40);
			grafix.drawString("Goal cost: " + getGoalCost(this.getCellMousePosition()), 10, 55);
			grafix.drawString("Heap top: " + openCells.peek().getTotalCost(), 10, 70);
		}
		drawCell(grafix, startCell, Color.green,Color.red);
		drawCell(grafix, goalCell, Color.red, Color.orange);

		//		{
		//			if (isFinished()) {
		//				Cell pathCell = cells[goalCell.x][goalCell.y];
		//				do {
		//					drawCell(grafix, pathCell, Color.red, Color.magenta);
		//				} while ( (pathCell = pathCell.m_parent) != null); 
		//				System.out.println("");
		//			}
		//		}
		drawCell(grafix,openCells.peek(),Color.white,Color.blue);
		
	}

	@Override
	public void init(GameContainer container) throws SlickException {
		// TODO Auto-generated method stub
		initializeGrid();
		containerHeight = container.getHeight();
	}

	private boolean running = false;
	private boolean stepping = false;
	private boolean drawingObstructions = false;
	private boolean setWalkable = false;
	@Override
	public void update(GameContainer container, int delta) throws SlickException {
		// Check to see if we are running
		Input in = container.getInput();
		if (in.isMousePressed(0) && !drawingObstructions) {
			Point pt = getCellMousePosition();
			//			int mx = Math.round((Mouse.getX()-(Mouse.getX()%CELLWIDTH))/CELLWIDTH);
			//			int my = Math.round((container.getHeight()-(Mouse.getY()+((int)CELLHEIGHT/2)-(Mouse.getY()%CELLHEIGHT)))/CELLHEIGHT);

			setWalkable = !cells[pt.x][pt.y].getWalkable();
			drawingObstructions = true;
			//cells[Math.round((Mouse.getX()-(Mouse.getX()%20))/20)][Math.round((arg0.getHeight()-Mouse.getY()-(Mouse.getY()%20))/20)].walkable = false;

		} else if (in.isMouseButtonDown(0)) {
			//			int mx = Math.round((Mouse.getX()-(Mouse.getX()%CELLWIDTH))/CELLWIDTH);
			//			int my = Math.round((container.getHeight()-(Mouse.getY()+((int)CELLHEIGHT/2)-(Mouse.getY()%CELLHEIGHT)))/CELLHEIGHT);
			Point pt = getCellMousePosition();
			cells[pt.x][pt.y].setWalkable(setWalkable);
		} else {
			drawingObstructions = false;
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
			running = true;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_RETURN) && !stepping) {
			stepping = true;
		} else if (Keyboard.isKeyDown(Keyboard.KEY_RETURN) && stepping){
			return;
		} else if (!Keyboard.isKeyDown(Keyboard.KEY_RETURN)) {
			stepping = false;
		}
		if (!running && !stepping) {
			return;
		}


		Cell cll = openCells.deleteMin();
		
		// You now have the cell with the minimum path cost
		// Add the surrounding cells to the open cell list
		addAccessibleCells(cll);
		//while (fixPaths(minX, minY));
//		fixPaths(cll.getX(), cll.getY());
		if (isFinished()) {
			running = false;
			stepping = false;
			//finalizePath();
		}
		//stepping = false;
	}

	public boolean isFinished() {

		return closedCells.contains(goalCell);
	}

	public boolean finalizePath() {
		boolean finalize = false;
//		for (Cell cll : closedCells) {
//			fixPaths(cll);
//		}
		return finalize;
	}



	private boolean checkOutOfBounds(int x, int y) {
		if (x < 0 || x > cells.length-1){
			return true;
		}
		if (y < 0 || y > cells[0].length-1){
			return true;
		}
		return false;
	}

	private boolean cutsCorners(Direction dir, int x, int y) {
		Direction clockwise = dir.getClockwiseTurn();
		Direction counterClockwise = dir.getCounterClockwiseTurn();

		return !cells[x+clockwise.getDeltaX()][y+clockwise.getDeltaY()].getWalkable() ||
				!cells[x+counterClockwise.getDeltaX()][y+counterClockwise.getDeltaY()].getWalkable();
	}

	private void addAccessibleCells(Cell currentCell) {
		int x = currentCell.getX();
		int y = currentCell.getY();
		
		closedCells.add(currentCell);

		for (Direction dir : Direction.values()) {
		
			int dx = dir.getDeltaX();
			int dy = dir.getDeltaY();
			if (checkOutOfBounds(x+dx, y+dy)){
				continue;
			}
			Cell cll = cells[x+dx][y+dy];
			boolean walk = cll.getWalkable();
			if (closedCells.contains(cll) || !walk) {
				continue;
			}
			if (!dir.isAxisAligned() && cutsCorners(dir, x, y)) {
				continue;
			}
			
			cll.setGoalCost(getGoalCost(x+dx,y+dy));
			
			if ( !openCells.contains(cll)) {
				cll.setParent(currentCell);
				openCells.insert(cll);
			} else if (cll.isBetterPath(currentCell)) {
				//System.out.println("Can be better");
				cll.setParent(currentCell);
				openCells.buildHeap();
				// Check if the path cost for that cell is better if goes through currentCell
			}
			

				
		
			
		}

	}

	public int getGoalCost(Point pt) {
		return getGoalCost(pt.x,pt.y);
	}
	public int getGoalCost(int x, int y) {
		int dx = Math.abs(goalCell.getX() - x)*10;
		int dy = Math.abs(goalCell.getY() - y)*10;
		int num = (int) Math.sqrt(dx*dx+dy*dy);
		return num;
	}

	private Point getCellMousePosition(){
		int mx = Math.round((Mouse.getX()-(Mouse.getX()%CELLWIDTH))/CELLWIDTH);
		int my = Math.round((containerHeight-(Mouse.getY()+((int)CELLHEIGHT/2)-(Mouse.getY()%CELLHEIGHT)))/CELLHEIGHT);
		return new Point(mx,my);
	}


	
}