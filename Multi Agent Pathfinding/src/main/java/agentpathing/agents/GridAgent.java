package agentpathing.agents;

import java.util.Set;

import agentpathing.Cell;
import agentpathing.behaviors.SearchBehavior;

public class GridAgent extends Agent {

	private SearchBehavior behavior;

	public GridAgent(SearchBehavior behave, Cell start, Cell end) {
		super(start, end);

		behavior = behave;
		behavior.setAgent(this);
	}

	@Override
	protected int getStepCost(Cell start, Cell end) {
		boolean vertical = start.getX() == end.getX();
		boolean horizontal = start.getY() == end.getY();
		int multiplier = Math.max(Math.abs(end.getX()-start.getX()), Math.abs(end.getY() - start.getY()));
		if ((vertical && !horizontal) || (!vertical && horizontal)) {
			return 10 * multiplier;
		} else {
			return 14 * multiplier;
		}
	}

	@Override
	protected int getGoalCost(Cell cell) {
		// Straight line heuristic
		int dx = getGoal().getX() - cell.getX();
		int dy = getGoal().getY() - cell.getY();
		dx*=10;
		dy*=10;
		return (int) Math.sqrt(dx*dx+dy*dy);
	}

	protected int getUnitDirectionX(PathStep step) {
		if (step.previous == null) {
			return 0;
		}
		return getUnitDirectionX(step.node, step.previous.node);
	}
	protected int getUnitDirectionY(PathStep step) {
		if (step.previous == null) {
			return 0;
		}
		return getUnitDirectionY(step.node, step.previous.node);
	}

	protected int getUnitDirectionX(Cell end, Cell start) {
		int dX = end.getX() - start.getX();
		if (dX > 0) {
			dX = 1;
		} else if (dX < 0) {
			dX = -1;
		}
		return dX;
	}

	public int getUnitDirectionY(Cell end, Cell start) {
		int dY = end.getY() - start.getY();
		if (dY > 0) {
			dY = 1;
		} else if (dY < 0) {
			dY = -1;
		}
		return dY;
	}

	@Override
	protected Set<Cell> getConnectedCells(PathStep center) {
		int dX = getUnitDirectionX(center);
		int dY = getUnitDirectionY(center);

		return behavior.getAdjacentCells(center.node, dX, dY);	// Consider initializing size to the number of links in center
	}

}
