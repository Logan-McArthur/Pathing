package agentpathing.behaviors;
import java.util.Set;

import agentpathing.Cell;
import agentpathing.Grid;
import agentpathing.agents.Agent;

// The class is meant to change how cells are retrieved from the Grid
public abstract class SearchBehavior {
	
	protected Grid cellGrid;
	protected Agent agent;
	
	public SearchBehavior(Grid grid) {
		cellGrid = grid;
	}
	
	public void setAgent(Agent agnt) {
		agent = agnt;
	}
	
	public Set<Cell> getAdjacentCells(Cell center, int dX, int dY) {
		return getAdjacentCells(center.getX(),center.getY(), dX, dY);
	}
	
	public abstract Set<Cell> getAdjacentCells(int x, int y, int dX, int dY);
	
}
