package agentpathing.behaviors;

import java.util.HashSet;
import java.util.Set;

import agentpathing.Cell;
import agentpathing.Grid;

public class JumpSearch extends SearchBehavior {

	public JumpSearch(Grid grid) {
		super(grid);
	}

	@Override
	public Set<Cell> getAdjacentCells(int x, int y, int dX, int dY) {
		Set<Cell> cells = new HashSet<Cell>();
		
		expand(cells, x, y, dX, dY);
		
		return cells;
	}
	
	private void expand(Set<Cell> cells, int x, int y, int dX, int dY) {
		if (dX == dY && dY == 0) {		// Both zero
			// No preference, expand in all directions
			cells.addAll(expandAxis(x,y,  1,  0));
			cells.addAll(expandAxis(x,y,  0,  1));
			cells.addAll(expandAxis(x,y, -1,  0));
			cells.addAll(expandAxis(x,y,  0, -1));
			cells.addAll(expandDiagonal(x-1, y-1, -1, -1));
			cells.addAll(expandDiagonal(x+1, y-1,  1, -1));
			cells.addAll(expandDiagonal(x-1, y+1, -1,  1));
			cells.addAll(expandDiagonal(x+1, y+1,  1,  1));
			
		} else if (dX == 0 || dY == 0) {	// One is zero, other is not
			// Axis aligned
			Cell cll = getForcedNeighbor(x,y,dX,dY);
			if (cll != null) {
				cells.add(cll);
			}
			cll = getForcedNeighbor(x,y,-dX,-dY);
			if (cll != null) {
				cells.add(cll);
			}
//			cells.addAll(expandAxis(x, y, dX, dY));
			
		} else {
			// Directed diagonal
			cells.addAll(expandShallowDiagonal(x, y, dX, dY));
			cells.addAll(expandDiagonal(x + dX,y + dY,dX,dY));
		}
		
	}
	
	private Set<Cell> expandDiagonal(int x, int y, int dX, int dY) {
		Set<Cell> cells = new HashSet<Cell>();
		// This one expands horizontal and vertical
		// dX and dY should both not be zero
		if (dX == 0 || dY == 0) {
			throw new IllegalArgumentException("dX and dY may not be zero while expanding diagonally.");
		}
		boolean result = false;
		while (!cellGrid.isOutOfBounds(x, y) && !result) {
			result = expandAxis(x, y, dX, 0).size() > 0;
			result = expandAxis(x, y, 0, dY).size() > 0 || result;
			if (result) {	// If they don't find anything, continue on to the next place
				cells.add(cellGrid.getCell(x, y));
			}
			x += dX;
			y += dY;
		}
		
		return cells;
		
	}
	
	private Set<Cell> expandShallowDiagonal(int x, int y, int dX, int dY) {
		Set<Cell> cells = new HashSet<Cell>();
//		if (!cellGrid.isOutOfBounds(x, y)) {
			cells.addAll(expandAxis(x, y, dX, 0));
			cells.addAll(expandAxis(x, y, 0, dY));
//		}
		return cells;
	}
	
	private Set<Cell> expandAxis(int x, int y, int dX, int dY) {
		// dX and dY can not both be non zero
		Set<Cell> cells = new HashSet<Cell>();
		Cell cl = getForcedNeighbor(x, y, dX, dY);
		if (cl != null) {
			cells.add(cl);	// If it's still at the first step, it would make sense for it to consider the forced neighbor too
		}
		while(!cellGrid.isOutOfBounds(x, y) && cellGrid.getCell(x, y).getWalkable()) {
			if (cellGrid.getCell(x, y).equals(agent.getGoal())) {
				cells.add(cellGrid.getCell(x, y));
				return cells;
			}
			if ( ! cellGrid.isOutOfBounds(x + dX , y + dY) && cellGrid.isWalkableCell(x+dX, y+dY)) {
				Cell cll = getForcedNeighbor(x, y, dX, dY);
				if (cll != null) {
					cells.add(cellGrid.getCell(x, y));
				}

			}
			// Remember, one of dY or dX will be zero, but not both
			y += dY;
			x += dX;
		}
		return cells;
	}
	
	private Cell getForcedNeighbor(int x, int y, int dX, int dY) {
		int lhX = 0;
		int lhY = 0;
		if (dX == 0) {
			lhX = 1;	// It's moving up or down, so I need to look left or right as well
		} else {
			lhY = 1;	
		}
		if (! cellGrid.isOutOfBounds(x + dX + lhX, y + dY + lhY) && cellGrid.isWalkableCell(x + dX + lhX, y + dY + lhY) && cellGrid.cutsCorners(x, y, dX + lhX, dY + lhY)) {
			return cellGrid.getCell(x + dX + lhX, y + dY + lhY);
		}
		if (! cellGrid.isOutOfBounds(x + dX - lhX, y + dY - lhY) && cellGrid.isWalkableCell(x + dX - lhX, y + dY - lhY) && cellGrid.cutsCorners(x, y, dX - lhX, dY - lhY)) {
			return cellGrid.getCell(x + dX - lhX, y + dY - lhY);
		}
		return null;
	}
	
}
