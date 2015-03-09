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
//			expandAxis(cells, x,y,  1,  0);
//			expandAxis(cells, x,y,  0,  1);
//			expandAxis(cells, x,y, -1,  0);
//			expandAxis(cells, x,y,  0, -1);
//			expandDiagonal(cells, x-1, y-1, -1, -1);
//			expandDiagonal(cells, x+1, y-1,  1, -1);
//			expandDiagonal(cells, x-1, y+1, -1,  1);
//			expandDiagonal(cells, x+1, y+1,  1,  1);
			doDiagonal(cells, x, y, -1, -1);
			doDiagonal(cells, x, y,  1, -1);
			doDiagonal(cells, x, y, -1,  1);
			doDiagonal(cells, x, y,  1,  1);
			
		} else if (dX == 0 || dY == 0) {	// One is zero, other is not
			// Axis aligned
			doAxis(cells, x, y, dX, dY);
//			getForcedNeighbor(cells, x,y,dX,dY);
//			expandAxis(cells, x, y, dX, dY);
			
		} else {
			// Directed diagonal
			doDiagonal(cells, x, y, dX, dY);
//			expandShallowDiagonal(cells, x, y, dX, dY);
//			expandDiagonal(cells, x+dX,y+dY,dX,dY);
		}
		
	}
	
	private boolean doDiagonal(Set<Cell> cells, int x, int y, int dX, int dY) {
		// cells should always be valid
		boolean foundCells = false;
		// Add the cells that are directly connected to this cell
		if (isValidCell(x+dX, y)) {
			foundCells = doAxis(cells, x, y, dX, 0) || foundCells;
		}
		if (isValidCell(x, y+dY)) {
			foundCells = doAxis(cells, x, y, 0, dY) || foundCells;
		}
		
		x+= dX;
		y+= dY;
		boolean foundCellsDeep = false;
		// Start checking the layered diagonal areas
		while (isValidCell(x, y) && isValidCell(x-dX, y) && isValidCell(x, y-dY) && !foundCellsDeep) {
			foundCellsDeep = foundCellsDeep || checkAxis(x, y, dX, 0);
			foundCellsDeep = foundCellsDeep || checkAxis(x, y, 0, dY);
			if (foundCellsDeep) {
				cells.add(cellGrid.getCell(x, y));
				foundCells = true;
			}
			x += dX;
			y += dY;
		}
		
		return foundCells;
	}
	
	private void expandDiagonal(Set<Cell> cells, int x, int y, int dX, int dY) {
		// This one expands horizontal and vertical
		// dX and dY should both not be zero
		
		if (dX == 0 || dY == 0) {
			throw new IllegalArgumentException("dX and dY may not be zero while expanding diagonally.");
		}
		boolean result = false;
		while (!cellGrid.isOutOfBounds(x, y) && cellGrid.isWalkableCell(x, y) && (cellGrid.isWalkableCell(x-dX, y) || cellGrid.isWalkableCell(x, y-dY)) && !result) {
			result = expandAxis(null, x, y, dX, 0);
			result = expandAxis(null, x, y, 0, dY) || result;
			if (result) {	// If they don't find anything, continue on to the next place
				cells.add(cellGrid.getCell(x, y));
			}
			x += dX;
			y += dY;
		}		
	}
	
	private boolean expandShallowDiagonal(Set<Cell> cells, int x, int y, int dX, int dY) {
//		Set<Cell> cells = new HashSet<Cell>();
		boolean result = false;
		if (!cellGrid.isOutOfBounds(x, y) && cellGrid.isWalkableCell(x, y)) {
//			result = getForcedNeighbor(cells, x, y, dX, dY);
			result = expandAxis(cells, x, y, dX, 0) || result;
			result = expandAxis(cells, x, y, 0, dY) || result;
		}
//		return cells;
		return result;
	}
	
	private boolean doAxis(Set<Cell> cells, int x, int y, int dX, int dY) {
		boolean result = false;
		// Two parts, first takes care of the steps that turn, namely the forced neighbors
		// Second part takes care of running along the axis, adding any cells that will also branch off, but not adding the branches
		result = getForcedNeighbor(cells, x, y, dX, dY);
		// The direct steps off the axis have been completed, now the steps along the axis
		result = runAxis(cells, x, y, dX, dY) || result;
		return result;
	}
	
	// This should be the only way runAxis is accessed where the cells parameter is null
	private boolean checkAxis(int x, int y, int dX, int dY) {
		return runAxis(null, x, y, dX, dY);
	}
	
	private boolean runAxis(Set<Cell> cells, int x, int y, int dX, int dY) {
		// dX and dY will not both be zero
		boolean foundCells = false;
		while (isValidCell(x, y)) {
			// Check if we found the goal
			if (cellGrid.getCell(x, y).equals(agent.getGoal())) {
				if (cells != null) {
					cells.add(agent.getGoal());		// I'm just grabbing it from the agent, even though it is the same
				}
				foundCells = true;
				//break;
			}
			// Check if there are any cells that have a forced neighbor, but don't add the neighbors
			if (isValidCell(x+dX, y+dY) && getForcedNeighbor(x, y, dX, dY)) {
				// We found one, add the current cell to the set
				if (cells != null) {
					cells.add(cellGrid.getCell(x, y));
				}
				foundCells = true;
				//break;				// It's an experiment, to see what happens
			}
			x += dX;
			y += dY;
		}
		return foundCells;
	}

	private boolean isValidCell(int x, int y) {
		return !cellGrid.isOutOfBounds(x, y) && cellGrid.isWalkableCell(x, y);
	}
	
	private boolean expandAxis(Set<Cell> cells, int x, int y, int dX, int dY) {
		// dX and dY can not both be non zero
		boolean result = false;
		while(!cellGrid.isOutOfBounds(x, y) && cellGrid.getCell(x, y).getWalkable()) {
			if (cellGrid.getCell(x, y).equals(agent.getGoal())) {
				if (cells != null) {
					cells.add(cellGrid.getCell(x, y));
				}
				result = true;
				break;
			}
			if ( ! cellGrid.isOutOfBounds(x + dX , y + dY) && cellGrid.isWalkableCell(x+dX, y+dY)) {
				if (getForcedNeighbor(null, x,y,dX,dY)) {		// Was previously null for cells
					if (cells != null) {
						cells.add(cellGrid.getCell(x, y));
					}
					result = true;
				}
//				cells.addAll(getForcedNeighbor(x, y, dX, dY));
			}
			// Remember, one of dY or dX will be zero, but not both
			y += dY;
			x += dX;
		}
//		return cells;
		return result;
	}
	
	private boolean getForcedNeighbor(int x, int y, int dX, int dY) {
		return getForcedNeighbor(null, x, y, dX, dY);
	}
	
	private boolean getForcedNeighbor(Set<Cell> cells, int x, int y, int dX, int dY) {
		
		int lhX = 0;
		int lhY = 0;
		if (dX == 0) {
			lhX = 1;	// It's moving up or down, so I need to look left or right as well
		} else {
			lhY = 1;	
		}
		boolean result = false;
		//result = getForcedNeighborInternal(cells, x, y, dX+lhX, dY+lhY) || result;
		if (! cellGrid.isOutOfBounds(x + dX + lhX, y + dY + lhY) && cellGrid.isWalkableCell(x + dX + lhX, y + dY + lhY) && cellGrid.cutsCorners(x, y, dX + lhX, dY + lhY)) {
			if (cells != null) {
				cells.add(cellGrid.getCell(x + dX + lhX, y + dY + lhY));
			}
			result = true;
		}
		
		//result = getForcedNeighborInternal(cells, x, y, dX-lhX, dY-lhY) || result;
		if (! cellGrid.isOutOfBounds(x + dX - lhX, y + dY - lhY) && cellGrid.isWalkableCell(x + dX - lhX, y + dY - lhY) && cellGrid.cutsCorners(x, y, dX - lhX, dY - lhY)) {
			if (cells != null) {
				cells.add(cellGrid.getCell(x + dX - lhX, y + dY - lhY));
			}
			result = true;
		}
		return result;
		
	}
	
	private boolean getForcedNeighborInternal(Set<Cell> cells, int x, int y, int delX, int delY) {
		boolean result = false;
		if (! cellGrid.isOutOfBounds(x + delX, y + delY) && cellGrid.isWalkableCell(x + delX, y + delY) && cellGrid.cutsCorners(x, y, delX, delY)) {
			if (cells != null) {
				cells.add(cellGrid.getCell(x + delX, y + delY));
			}
			result = true;
		}
		return result;
	}
	
//	private boolean getForcedNeighborDiag(Set<Cell> cells, int x, int y, int dX, int dY) {
//		
//		// both dX and dY are non zero
//		boolean result = false;
//		if (! cellGrid.isOutOfBounds(x-dX, y-dY) && cellGrid.cutsCorners(x-dX, y-dY, dX, dY)) {
//			// This is weird, I don't think that both if blocks should ever trigger at the same time
//			if (!cellGrid.isWalkableCell(x, y-dY)) {
//				cells.add(cellGrid.getCell(x+dX, y-dY));
//				result = true;
//			}
//			if (!cellGrid.isWalkableCell(x-dX, y)) {
//				cells.add(cellGrid.getCell(x-dX, y+dY));
//				result = true;
//			}
//		}
//			
//		return result;
//	}
}
