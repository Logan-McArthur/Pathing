package agentpathing;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

import agentpathing.behaviors.AStarSearch;
import agentpathing.behaviors.JumpSearch;


public class RefinedGrid extends BasicGame{
	public static void main(String args[]) {
		try {
			int windowWidth = 800;
			int windowHeight = 600;
			int cellsWide = 200;
			int cellsTall = 150;
			AppGameContainer app = new AppGameContainer(
					new RefinedGrid("Title",cellsWide,cellsTall, (windowWidth / cellsWide), (windowHeight / cellsTall) ));

			app.setTargetFrameRate(60);
			app.setDisplayMode(800, 600, false);
			app.setShowFPS(false);
			app.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
	Grid cellGrid;
	List<Agent> agents = new ArrayList<Agent>();
	List<Color> agentColors = new ArrayList<Color>();
	private final int CELLWIDTH;// = 20;
	private final int CELLHEIGHT;// = 20;

	private int containerHeight;
	public RefinedGrid(String title, int width, int height, int cellWidth, int cellHeight) {
		super(title);
		CELLWIDTH = cellWidth;
		CELLHEIGHT = cellHeight;
		cellGrid = new Grid(width, height);
		createAgents(4);
		Color[] colors = new Color[]{Color.cyan,Color.yellow,Color.magenta,Color.pink};
		for (Color col : colors) {
			agentColors.add(col);
		}
	}

	private Cell getRandomCell() {
		Random rand = new Random();
		int x = rand.nextInt(cellGrid.getMapWidth());
		int y = rand.nextInt(cellGrid.getMapHeight());
		return cellGrid.getCell(x, y);
	}
	
	private void createAgents(int number) {
		for ( ; number > 0; number--) {
//			agents.add(new Agent(new JumpSearch(cellGrid), cellGrid.getCell(5, 15), cellGrid.getCell(35,15)));
			agents.add(new Agent(new JumpSearch(cellGrid),getRandomCell(), getRandomCell()));
		}
	}
	
	

	public void drawCell(Graphics grafix, Cell cel, Color base, Color outline) {
		int width = CELLWIDTH;
		int height = CELLHEIGHT;
		int x = cel.getX() * width;
		int y = cel.getY() * height;
		grafix.setColor(base);
		grafix.fillRect(x, y, width, height);
		grafix.setColor(outline);
		grafix.drawRect(x, y, width, height);
	}
	
	public void drawCellSet(Graphics grafix, Set<Cell> cells, Color base, Color outline) {
		for (Cell cll : cells) {
			drawCell(grafix, cll, base, outline);
		}
	}
	
	public void drawParentLine(Graphics grafix, List<Cell> cells, Color line) {
		grafix.setColor(line);
		for (int i = 1; i < cells.size(); i++) {
			grafix.drawLine(cells.get(i).getX()*CELLWIDTH + (int)(CELLWIDTH/2), cells.get(i).getY()*CELLHEIGHT + (int)(CELLHEIGHT/2),
					cells.get(i-1).getX()*CELLWIDTH + (int)(CELLWIDTH/2), cells.get(i-1).getY()*CELLHEIGHT + (int)(CELLHEIGHT/2));
		}
	}

	@Override
	public void render(GameContainer container, Graphics grafix) throws SlickException {

		// drawOpenCells green body, blue outline
		// drawClosedCells lightgray body, gray outline
		
		// Not drawing all cells like that, especially if there is an unknown amount of agents
		
		// Loop through all cells, if it is walkable draw the parent line, if not draw white body with orange outline
		
		// drawFinalParentLine cyan
		
		// drawMouseHoveredParentLine blue with double thickness
		
		
		drawCellSet(grafix, cellGrid.getUnwalkableCells(), Color.white, Color.orange);
		
		for (int i = 0; i < agents.size(); i++) {
			drawCellSet(grafix,agents.get(i).getOpenCells() , agentColors.get(i),Color.black);
			drawCellSet(grafix,agents.get(i).getClosedCells() , Color.gray,Color.black);
			
			
			
		}
		
		for (int i = 0; i < agents.size(); i++) {
			if (agents.get(i).isFinished())
				drawParentLine(grafix, agents.get(i).getFinishedLine(), agentColors.get(i));
			else {
				drawParentLine(grafix, agents.get(i).getClosestLine(), agentColors.get(i));
			}
		}
		
		for (int i = 0; i < agents.size(); i++) {
			drawCell(grafix, agents.get(i).getStart(),Color.green,agentColors.get(i));
			drawCell(grafix, agents.get(i).getGoal(), Color.red, agentColors.get(i));
		}
		// diagnostic stuff
		// total cost for hovered cell
		// path cost for hovered cell
		// goal cost for hovered cell
		// top of heap, the total cost
		
		// drawStartCell green body with red outline
		// drawGoalCell red body with orange outline

		// drawCurrentConsideredCell white body with blue outline		
	}

	@Override
	public void init(GameContainer container) throws SlickException {
		
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

			setWalkable = !cellGrid.getCell(pt.x, pt.y).getWalkable();
			drawingObstructions = true;

		} else if (in.isMouseButtonDown(0)) {
			Point pt = getCellMousePosition();
			cellGrid.getCell(pt.x, pt.y).setWalkable(setWalkable);
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
		if (Keyboard.isKeyDown(Keyboard.KEY_DELETE)) {
			for (Agent agnt : agents) {
				agnt.reset();
			}
		}
		if (!running && !stepping) {
			return;
		}

		for (Agent agnt : agents) {
			agnt.nextStep();
		}

		if (isFinished()) {
			running = false;
			stepping = false;
		}
	}

	public boolean isFinished() {
		boolean result = true;
		for (Agent agnt : agents) {
			result = result && agnt.isFinished();
		}
		return result;
	}

	private Point getCellMousePosition(){
		int mx = Math.round((Mouse.getX()-(Mouse.getX()%CELLWIDTH))/CELLWIDTH);
		int my = Math.round((containerHeight-(Mouse.getY()+((int)CELLHEIGHT/2)-(Mouse.getY()%CELLHEIGHT)))/CELLHEIGHT);
		return new Point(mx,my);
	}


	
}