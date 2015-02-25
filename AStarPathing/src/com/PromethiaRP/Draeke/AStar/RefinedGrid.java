package com.PromethiaRP.Draeke.AStar;

import java.awt.Point;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

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
	private List<Point> openCells = new ArrayList<Point>();
	private List<Point> closedCells = new ArrayList<Point>();



	private static int m_Width = 40;
	private static int m_Height = 30;

	private Point startCell;
	private Point goalCell;

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

		startCell = new Point(10,15);
		goalCell = new Point(30,15);

		openCells.add(startCell);
	}

	public static void main(String args[]) {
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

		{
			for (Point pt : openCells) {
				drawCell(grafix, cells[pt.x][pt.y], Color.green, Color.blue);
			}
		}


		{
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
		}

		{
			if(isFinished()) {
				Cell c = cells[goalCell.x][goalCell.y];
				do {
					fixPaths(c.getX(), c.getY());
					fixPaths(c.getX(), c.getY());
					drawParentLine(grafix,c, Color.cyan);
				} while( (c = c.getParent())!= null );
			}
		}

		{
			Point pt = getCellMousePosition();
			Cell c = cells[pt.x][pt.y];
			do {
				drawParentLine(grafix,c, Color.gray);
			} while( (c = c.getParent())!= null );
			grafix.setColor(Color.white);
			grafix.drawString("( " + pt.x + ", " + pt.y + ")", 10, 10);
			Cell cll = cells[pt.x][pt.y];
			grafix.drawString("Total cost: " + (cll.getPathCost()+getGoalCost(cll.getX(), cll.getY())), 10, 25);
			grafix.drawString("Path cost: " + cll.getPathCost(), 10, 40);
		}
		drawCell(grafix, cells[startCell.x][startCell.y],Color.green,Color.red);
		drawCell(grafix, cells[goalCell.x][goalCell.y], Color.red, Color.orange);

		//		{
		//			if (isFinished()) {
		//				Cell pathCell = cells[goalCell.x][goalCell.y];
		//				do {
		//					drawCell(grafix, pathCell, Color.red, Color.magenta);
		//				} while ( (pathCell = pathCell.m_parent) != null); 
		//				System.out.println("");
		//			}
		//		}
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
		int minIndex = openCells.size()-1;
		if (minIndex == -1) {
			running = false;
			return;
		}
		int minX = openCells.get(minIndex).x;
		int minY = openCells.get(minIndex).y;
		//int minCost = getGoalCost(minX, minY);
		int minCost = cells[minX][minY].getPathCost()+getGoalCost(minX,minY);
		{
			int currentX;
			int currentY;
			int currentCost;
			for (int i = openCells.size()-1; i>=0; i--) {
				currentX = openCells.get(i).x;
				currentY = openCells.get(i).y;
				currentCost = cells[currentX][currentY].getPathCost()+getGoalCost(currentX,currentY);
				if (minCost > currentCost) {
					minIndex = i;
					minX = currentX;
					minY = currentY;
					minCost = currentCost;
				}
			}

		}
		//for (Point pt : closedCells){

		//}
		// You now have the cell with the minimum path cost
		// Add the surrounding cells to the open cell list
		addAccessibleCells(cells[minX][minY]);
		//while (fixPaths(minX, minY));
		fixPaths(minX, minY);
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
		for (Point c : closedCells) {
			fixPaths(c.x, c.y);
		}
		return finalize;
	}

	public boolean fixPaths(int x, int y) {
		boolean fix = false;
		Cell cel = cells[x][y];
		Direction min = null;
		int minCost = cel.getPathCost();
		for (Direction d : Direction.values()) {
			if (checkOutOfBounds(d, x, y)) {
				continue;
			}
			if (cells[x+d.getDeltaX()]
					[y+d.getDeltaY()].getPathCost() < minCost) {
				if (!closedCells.contains(new Point(x+d.getDeltaX(), y +d.getDeltaY()))) {
					continue;
				}
				if (cutsCorners(d, x, y)) {
					continue;
				}
				min = d;
				minCost = cells[x+d.getDeltaX()][y+d.getDeltaY()].getPathCost();
				//				cel.m_parent = cells[x+d.getDeltaX()][y+d.getDeltaY()];
				fix = true;

			}
		}
		if (fix) {
			cel.setParent(cells[x+min.getDeltaX()][y+min.getDeltaY()]);	
		}
		return fix;
	}

	private boolean checkOutOfBounds(Direction dir, int x, int y) {
		if (x+dir.getDeltaX() < 0 || x+dir.getDeltaX() > cells.length-1){
			return true;
		}
		if (y+dir.getDeltaY() < 0 || y+dir.getDeltaY() > cells[0].length-1){
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
		
		closedCells.add(new Point(x, y));
		openCells.remove(new Point(x, y));
		for (Direction dir : Direction.values()) {
			Point pt = new Point(x+dir.getDeltaX(), y+dir.getDeltaY());
			if (checkOutOfBounds(dir, pt.x, pt.y)){
				continue;
			}
			boolean walk = cells[pt.x][pt.y].getWalkable();
			if ( openCells.contains(pt)) {
				continue;
			}
			if (closedCells.contains(pt)){
				fixPaths(pt.x, pt.y);
				continue;
			}
			if (walk) {
				if (!dir.isAxisAligned() && cutsCorners(dir, x, y)) {
					continue;
				}
				openCells.add(pt);
				cells[pt.x][pt.y].setParent(currentCell);
			}
			
			
		}

	}

	public int getGoalCost(int x, int y) {
		int dx = Math.abs(goalCell.x - x);
		int dy = Math.abs(goalCell.y - y);
		int num = dx * 10 + dy * 10;
		return num;
	}

	private Point getCellMousePosition(){
		int mx = Math.round((Mouse.getX()-(Mouse.getX()%CELLWIDTH))/CELLWIDTH);
		int my = Math.round((containerHeight-(Mouse.getY()+((int)CELLHEIGHT/2)-(Mouse.getY()%CELLHEIGHT)))/CELLHEIGHT);
		return new Point(mx,my);
	}


	
}