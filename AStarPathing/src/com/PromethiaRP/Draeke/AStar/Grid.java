package com.PromethiaRP.Draeke.AStar;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
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

public class Grid extends BasicGame{

	public static void main(String args[]) {
		try {
			AppGameContainer app = new AppGameContainer(new Grid("Title"));
			
			app.setTargetFrameRate(60);
			app.setDisplayMode(800, 600, false);
			app.start();
		} catch (SlickException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private Cell[][] cells = new Cell[20][15];
	
	private Cell start;// = new Cell(2,15);
	private Cell goal;// = new Cell(39,14);
	
	private Point currentCell;// = start;
	
	private List<Cell> openCells = new ArrayList<Cell>();
	private List<Cell> closedCells = new ArrayList<Cell>();
	private List<Cell> finishedPath = new ArrayList<Cell>();
	
	public Grid(String title) {
		super(title);
		// TODO Auto-generated constructor stub
	}
	boolean finished = false;
	@Override
	public void render(GameContainer container, Graphics grafix) throws SlickException {
		// TODO Auto-generated method stub
		int x;
		int y;
		
		if (closedCells.contains(goal)) {
			finished = true;
			Point c = currentCell;
			Cell d;
			while ((d = cells[c.x][c.y].parent) != null) {
				finishedPath.add(cells[c.x][c.y]);
				c.x = d.x;
				c.y = d.y;
			}
			
			
		}
		
		{
			grafix.setColor(Color.gray);
			Point p = new Point(currentCell.x, currentCell.y);
			Cell cc;
			while ((cc = cells[p.x][p.y].parent) != null) {
				grafix.fillRect(cc.x * cc.width, cc.y * cc.height, cc.width, cc.height);
				p.x = cc.x;
				p.y = cc.y;
			}
		}
		
		if (finished) {
			
			grafix.setColor(Color.red);
			for (Cell cs : finishedPath) {
				grafix.fillRect(cs.x * 40, cs.y * 40, cs.width, cs.height);
			}
		}
		
		grafix.setColor(Color.blue);
		int mx = (Mouse.getX()-(Mouse.getX()%40));
		int my = (container.getHeight()-(Mouse.getY()+40-(Mouse.getY()%40)));
		grafix.drawRect(mx,my, 40, 40);
		
				
		for (int i = 0; i < openCells.size(); i++) {
			x = openCells.get(i).x * 40;
			y = openCells.get(i).y * 40;
			grafix.setColor(Color.green);
			grafix.fillRect(x, y, 40, 40);
			grafix.setColor(Color.blue);
			grafix.drawRect(x, y, 40, 40);
		}
		for (int i = 0; i < cells.length; i++ ) {
			for (int j = 0; j < cells[i].length; j++) {
				if(cells[i][j].walkable) {
					continue;
				}
				grafix.setColor(Color.white);
				grafix.fillRect(cells[i][j].x*40, cells[i][j].y*40, 40, 40);
				grafix.setColor(Color.orange);
				grafix.drawRect(cells[i][j].x*40, cells[i][j].y*40, 40, 40);
			
			}
		}
		
		grafix.setColor(Color.cyan);
		grafix.fillRect(currentCell.x*40, currentCell.y*40, 40, 40);
		
		grafix.setColor(Color.yellow);
		grafix.fillRect(goal.x*40, goal.y*40, 40, 40);
		
		int mcx = (int)(mx/40);
		int mcy = (int)(my/40);
		if(openCells.contains(cells[mcx][mcy])) {
			if(cells[mcx][mcy].parent == null) {
				return;
			}
			grafix.drawRect(cells[mcx][mcy].parent.x*40,cells[mcx][mcy].parent.y*40, 40, 40);
		}

	}

	@Override
	public void init(GameContainer arg0) throws SlickException {
		// TODO Auto-generated method stub
		for (int i = 0; i < cells.length; i++) {
			for (int j = 0; j < cells[i].length; j++) {
				cells[i][j] = new Cell(i, j);
//				Random rand = new Random();
//				if (rand.nextInt(7) == 1) {
//					cells[i][j].walkable = false;
//				}
				//cells[i][j].goalCost = getGoalCost(i, j);
			}
		}
		start = cells[2][10];
		goal = cells[19][7];
		for (int i = 0; i < cells.length; i++) {
			for (int j = 0; j < cells[i].length; j++) {
				//cells[i][j] = new Cell(i, j);
				if(cells[i][j] == null) {
					throw new Error();
				}
				cells[i][j].goalCost = getGoalCost(i, j);
			}
		}
		currentCell = new Point(start.x, start.y);
		cells[currentCell.x][currentCell.y].pathCost = 0;
		openCells.add(cells[currentCell.x][currentCell.y]);
	}
	
	boolean running = false;
	boolean stepping = false;
	boolean locked = false;
	boolean setWalkable = false;
	boolean drawingObstructions = false;
	@Override
	public void update(GameContainer arg0, int arg1) throws SlickException {
		// TODO Auto-generated method stub
		//if (iter.hasNext()) {
		//	currentCell = iter.next();
		//}
		Input in = arg0.getInput();
		
		if (in.isMousePressed(0) && !drawingObstructions) {
			int mx = Math.round((Mouse.getX()-(Mouse.getX()%40))/40);
			int my = Math.round((arg0.getHeight()-(Mouse.getY()+40-(Mouse.getY()%40)))/40);
			setWalkable = !cells[mx][my].walkable;
			cells[mx][my].toggleWalkable();
			drawingObstructions = true;
			//cells[Math.round((Mouse.getX()-(Mouse.getX()%20))/20)][Math.round((arg0.getHeight()-Mouse.getY()-(Mouse.getY()%20))/20)].walkable = false;
			
		} else if (in.isMouseButtonDown(0)) {
			int mx = Math.round((Mouse.getX()-(Mouse.getX()%40))/40);
			int my = Math.round((arg0.getHeight()-(Mouse.getY()+40-(Mouse.getY()%40)))/40);
			cells[mx][my].walkable = setWalkable;
		} else {
			drawingObstructions = false;
		}
//		if (Mouse.isButtonDown(0)) {
//			cells[(int)(Mouse.getX()-(Mouse.getX()%20))/20][(int)(Mouse.getY()-(Mouse.getY()%20))/20].walkable = false;
//		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
			running = true;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_RETURN) && locked) {
			locked = true;
		} else if (Keyboard.isKeyDown(Keyboard.KEY_RETURN) && !locked) {
			stepping = true;
			locked = true;
		} else {
			locked = false;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_BACK)) {
			running = false;
			stepping = false;
			finished = false;
			openCells.clear();
			closedCells.clear();
			finishedPath.clear();
		}
		if (!(running||stepping)) {
			return;
		}
		if (finished) {
			return;
		}
		

		int minDistance = 1000000000;
		int minCell = 0;
		for (int k = 0; k < openCells.size(); k++) {
			if (openCells.get(k).getTotalCost() <= minDistance) {
				minDistance = openCells.get(k).getTotalCost();
				minCell = k;
			}
		}
		
		currentCell.x = openCells.get(minCell).x;
		currentCell.y = openCells.get(minCell).y;
		
		int x = currentCell.x;
		int y = currentCell.y;
		for (int i = -1; i < 2; i++ ) {
			for (int j = -1; j < 2; j++) {
				try{
					if(x+i < 0) {
						continue;
					}
					if(y+j <0) {
						continue;
					}
					if(x+i > cells.length-1){
						continue;
					}
					if(y+j > cells[x+i].length-1) {
						continue;
					}
					
				if(!(
					(openCells.contains(cells[x+i][y+j]))
					||
					(closedCells.contains(cells[x+i][y+j]))
					)
					&&
					(cells[x+i][y+j].walkable)
					) {
					if ((Math.abs(i) == Math.abs(j)) && ( i != 0 )  ) {
						if (!cells[x+i][y].walkable) {
							continue;
						}
						if (!cells[x][y+j].walkable) {
							continue;
						}
					}
						
					openCells.add(cells[x+i][y+j]);
					cells[x+i][y+j].parent = cells[x][y];
					cells[x+i][y+j].pathCost = cells[x][y].pathCost+cells[x+i][y+j].getStepCost();
					
				}
				if (cells[x][y].parent == null) {
					continue;
				}
//				if ((cells[x][y].parent.pathCost < cells[x+i][y+j].pathCost)&& closedCells.contains(cells[x+i][y+j]) && !openCells.contains(cells[x+i][y+j])){
//					cells[x][y].parent = cells[x+i][y+j];
//				}
				
//				Cell pathCell;
//				while ((pathCell = cells[x][y].parent) != null) {
//					fixPath(pathCell);
//				}
				
//				for (Cell c: closedCells) {
//					if (Math.abs(c.x - currentCell.x) <=1 && Math.abs(c.y-currentCell.y) <=1) {
//						if (c.pathCost < cells[x][y].pathCost){
//							cells[x][y].parent = c;
//						}
//					}
//				}
//				for (int jj = -1; jj < 2; jj++) {
//					for (int kk = -1; kk < 2; kk++) {
//						if(x+jj < 0 || y+kk < 0) {
//							continue;
//						}
//						if(x+jj > cells.length){ 
//							continue;
//						}
//						if(y+kk > cells[x].length) {
//							continue;
//						}
//						if (cells[x+jj][y+kk].pathCost < cells[x][y].parent.pathCost) {
//							cells[x][y].parent = cells[x+jj][y+kk];
//						}
//					}
//				}
				
				}catch(ArrayIndexOutOfBoundsException e) {
					e.printStackTrace();
				}
			}
		}
		openCells.remove(cells[x][y]);
		closedCells.add(cells[x][y]);
		
//		for (Cell p : openCells) {
//			if (p.getTotalCost() <= minDistance) {
//				minDistance = p.getTotalCost();
//				currentCell = p;
//			}
//		}
		stepping = false;
	}

	public void fixPath(Cell cel) {
		for (Cell c: closedCells) {
			if (Math.abs(c.x - cel.x) <=1 && Math.abs(c.y-cel.y) <=1) {
				if (c.pathCost < cells[cel.x][cel.y].pathCost){
					cells[cel.x][cel.y].parent = c;
				}
			}
		}
	}
	
//	public int getStepCost(int x, int y) {
//		int dx = (int)Math.abs(x);
//		int dy = (int)Math.abs(y);
//		if ((dx %2== 1) && (dy%2== 1)) {
//			return 14;
//		} else {
//			return 10;
//		}
////		if((x%2 == 0)||(y%2==0)) {
////			return 10;
////		} else {
////			return 14;
////		}
//	}
	
//	public int getStartCost(int x, int y) {
//		int dx = start.x - x;
//		int dy = start.y - y;
//		int num = dx * 10 + dy * 10;
//		return num;
//	}
	
	public int getGoalCost(int x, int y) {
		int dx = Math.abs(goal.x - x);
		int dy = Math.abs(goal.y - y);
		int num = dx * 10 + dy * 10;
		return num;
	}
	class Cell {
		public Cell parent = null;
		public int x;
		public int y;
		public int width = 40;
		public int height = 40;
		public int pathCost = 0;
		public int goalCost = 0;
		public boolean walkable = true;
		public int getTotalCost() {
			return pathCost+goalCost;
		}
		
		public Cell(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
		public void toggleWalkable() {
			walkable = !walkable;
		}
		
		public int getStepCost() {
			boolean horiz = false;
			boolean vert = false;
			if(Math.abs(x-parent.x)==0) {
				vert = true;
			}
			if(Math.abs(y-parent.y)==0) {
				horiz = true;
			}
			if ( (horiz && !vert) || (vert && !horiz)) {
				return 10;
			} else {
				return 14;
			}
		}
//		@Override
//		public boolean equals(Object o) {
//			if (o instanceof Cell) {
//				Cell cel = (Cell) o;
//				if (cel.x == this.x) {
//					if (cel.y == this.y) {
//						return true;
//					}
//				}
//			}
//			return false;
//		}
	}
}
