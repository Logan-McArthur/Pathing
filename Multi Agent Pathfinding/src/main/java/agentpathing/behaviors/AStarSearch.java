package agentpathing.behaviors;
import java.util.HashSet;
import java.util.Set;

import agentpathing.Cell;
import agentpathing.Grid;


public class AStarSearch extends SearchBehavior{

	public AStarSearch(Grid grid) {
		super(grid);
	}

	public Set<Cell> getAdjacentCells(int x, int y, int dX, int dY) {
		// dX and dY are not utilized in AStar
		Set<Cell> cells = new HashSet<Cell>();

		for (int dy = -1; dy <= 1; dy++) {
			for (int dx = -1; dx <= 1; dx++) {

				if (cellGrid.isOutOfBounds(x+dx, y+dy)){
					continue;
				}
				if (dx == dy && dx == 0) {
					continue;
				}
				if (cellGrid.cutsCorners(x,y,dx,dy)) {
					continue;
				}
				if (!cellGrid.getCell(x+dx,y+dy).getWalkable()) {
					continue;
				}

				cells.add(cellGrid.getCell(x+dx,y+dy));
			}
		}

		return cells;
	}
}
