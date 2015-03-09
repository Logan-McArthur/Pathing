package agentpathing;
import java.util.HashSet;
import java.util.Set;

// Grid should store all of the cells
// Grid does not need to know how big a cell is
// Does Grid need to know how cells connect? I'll say yes
// Grid is something that is used, it shouldn't really call other classes

public class Grid {

	private Cell[][] map;
	
	public Grid(int width, int height) {
		initializeGrid(width, height);
	}
	
	private void initializeGrid(int mapWidth, int mapHeight) {
		map = new Cell[mapWidth][mapHeight];
		
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[i].length; j++) {
				map[i][j] = new Cell(i, j);
			}
		}

	}
	
	public int getMapWidth() {
		return map.length;
	}
	
	public int getMapHeight() {
		return getMapHeight(0);
	}
	
	public int getMapHeight(int column) {
		return map[column].length;
	}
	
	// x and y are the center
	// dx and dy are the direction
	public boolean cutsCorners(int x, int y, int dx, int dy) {
		
		// It's either purely vertical or purely horizontal, so it won't cut corners
		if (dx == 0 || dy == 0) {
			return false;
		}
		// If either the cell to the y when moving x, or the cell to the x when moving y, is not walkable, return false
		return !map[x][y+dy].getWalkable() || !map[x+dx][y].getWalkable();
	}
	
	public boolean isOutOfBounds(int x, int y) {
		if (x < 0 || x > map.length-1){
			return true;
		}
		// I'm using map[x] just in case I want to have it be a staggered matrix
		if (y < 0 || y > map[x].length-1){
			return true;
		}
		return false;
	}
	
	public Cell getCell(int x, int y) {
		if (isOutOfBounds(x,y)) {
			throw new IllegalArgumentException("Requested Cell outside bounds of Grid.");
		}
		return map[x][y];
	}
	
	public boolean isWalkableCell(int x, int y) {
		return map[x][y].getWalkable();
	}
	
	public Set<Cell> getUnwalkableCells() {
		Set<Cell> unwalkables = new HashSet<Cell>();
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[i].length; j++) {
				if (!map[i][j].getWalkable())
					unwalkables.add(map[i][j]);
			}
		}
		return unwalkables;
	}
}
