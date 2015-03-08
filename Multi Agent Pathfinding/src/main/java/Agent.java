import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;


public class Agent {

	private Grid grid;
	
	private Queue<PathStep> openCells = new PriorityQueue<PathStep>();	// openCells should be sorted
	private Set<PathStep> closedCells = new HashSet<PathStep>();	// closedCells does not need to be

	private PathStep pathFront;
	private PathStep pathEnd;
	
	private Cell goalCell;
	
	public Agent(Grid grd, Cell start, Cell end) {
		goalCell = end;
		pathFront = new PathStep(start, getGoalCost(start));
		pathEnd = null;
		openCells.add(pathFront);
		grid = grd;
	}
	
	public boolean isFinished() {
		return pathEnd != null;
	}
	
	public void nextStep() {
		if (!isFinished()) {
			addAccessibleCells(openCells.poll());
		}
	}
	
	private List<Cell> getLine(PathStep step) {
		List<Cell> list = new ArrayList<Cell>();
		PathStep current = step;
		while (current != null) {
			list.add(current.node);
			current = current.previous;
		}
		return list;
	}
	
	public List<Cell> getFinishedLine() {
		return getLine(pathEnd);
	}
	public List<Cell> getClosestLine() {
		return getLine(openCells.peek());
	}
	public Set<Cell> getOpenCells() {
		if (openCells.contains(pathFront) && openCells.size() > 1) {
			System.out.println(openCells.peek());
		}
		return getCellsFromCollection(openCells);
	}
	
	public Set<Cell> getClosedCells() {
		return getCellsFromCollection(closedCells);
	}
	
	private Set<Cell> getCellsFromCollection(Collection<PathStep> collection) {
		Set<Cell> cells = new HashSet<Cell>();
		for (PathStep step : collection) {
			cells.add(step.node);
		}
		return cells;
	}
	
	public Cell getGoal() {
		return goalCell;
	}
	public Cell getStart() {
		return pathFront.node;
	}
	
	public void addAccessibleCells(PathStep current) {
		closedCells.add(current);
		if (current.node.equals(goalCell)) {
			pathEnd = current;
		}
		Set<Cell> considerSet = getConnectedCells(current.node);
		considerSet.removeAll(getCellsFromCollection(closedCells));
		for (Cell consider : considerSet) {
		
			PathStep considerPath = getStepFromCell(consider);
			if ( considerPath == null) {
				// It's a new cell, set it up
				PathStep step = new PathStep(current, consider, getStepCost(current.node,consider), getGoalCost(consider));
				if (closedCells.contains(step)) {
					continue;
				}
				if (openCells.contains(step)) {
					System.out.println("This should be impossible.");
				}
				openCells.add(step);
			} else if (considerPath.isBetterPath(current, getStepCost(current, considerPath))) {
				
				considerPath.setPrevious(current, getStepCost(current.node,consider), getGoalCost(considerPath.node));
				// Check if the path cost for that cell is better if goes through currentCell
			}
		}
	}
	
	private int getStepCost(PathStep start, PathStep end) {
		return getStepCost(start.node,end.node);
	}
	
	private int getStepCost(Cell start, Cell end) {
		boolean vertical = start.getX() == end.getX();
		boolean horizontal = start.getY() == end.getY();
		if ((vertical && !horizontal) || (!vertical && horizontal)) {
			return 10;
		} else {
			return 14;
		}
	}
	
	private int getGoalCost(Cell cell) {
		// Straight line heuristic
		int dx = goalCell.getX() - cell.getX();
		int dy = goalCell.getY() - cell.getY();
		dx*=10;
		dy*=10;
		return (int) Math.sqrt(dx*dx+dy*dy);
	}
	
	// Do not include Cells cut off by corners
	private Set<Cell> getConnectedCells(Cell center) {
		
		Set<Cell> cells = grid.getAdjacentCells(center);	// Consider initializing size to the number of links in center
		
		// Including the cells already in openCells
		// Remove the ones in closedCells
		//cells.removeAll(closedCells);
		for (Cell cll : cells) {
			PathStep stp = getStepFromCollection(closedCells, cll);
			if (stp != null) {
				cells.remove(stp);
			}
		}
		return cells;
	}
	
	private PathStep getStepFromCell(Cell cll) {
		PathStep result = getStepFromCollection(openCells, cll);
		return result;
		
	}
	
	private PathStep getStepFromCollection(Collection<PathStep> collection, Cell cll) {
		for (PathStep step : collection) {
			if (step.node.equals(cll)) {
				return step;
			}
		}
		return null;
	}
	
	private class PathStep implements Comparable<PathStep>{

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof PathStep) {
				PathStep ps = (PathStep) obj;
				return node.equals(ps.node);
			}
			return false;
		}

		private Cell node;
		
		private PathStep previous;
		private int pathCost;
		private int goalCost;
		
		public PathStep(Cell nodeCell, int goalCost) {
			this(null, nodeCell, 0, goalCost);
		}
		
		public PathStep(PathStep prev, Cell nodeCell, int stepCost, int goalCost) {
			this.node = nodeCell;
			setPrevious(prev, stepCost, goalCost);
		}
		
		public int compareTo(PathStep compare) {
			// TODO: PathStep.compareTo() must be changed
			Integer ours = new Integer(pathCost+goalCost);
			Integer theirs = new Integer(compare.pathCost + compare.goalCost);
			return ours.compareTo(theirs);
		}
		
		public boolean isBetterPath(PathStep possiblePrev, int step) {
			// get cost of possiblePrev and add it to the cost from possiblePrev to this
			// get current cost
			int possibleCost = possiblePrev.pathCost + step + getTurnCost(possiblePrev);
			return possibleCost < pathCost;
		}
		
		public void setPrevious(PathStep prev, int stepCost, int goalCost) {
			if (prev == null) {
				pathCost = stepCost;
			} else {
				pathCost = prev.pathCost + stepCost + getTurnCost(prev);
			}
			this.goalCost = goalCost;
			this.previous = prev;
		}
		
		public int getTurnCost(PathStep prev) {
			if (prev == null) {		// This is the start point
				return 0;
			}
			if (prev.previous == null) {	// Two points only make a line, a third is necessary
				return 0;
			}
			int cost = 0;
			// Initialize the values, I'll be using them as vectors
			int dx1 = node.getX() - prev.node.getX();
			int dy1 = node.getY() - prev.node.getY();
			int dx2 = prev.node.getX() - prev.previous.node.getX();
			int dy2 = prev.node.getY() - prev.previous.node.getY();

			if (dx1 == dx2 && dy1 == dy2) {		// Straight line
				cost = 0;
			} else if (dx1 == dx2 || dy1 == dy2) {	// Else if will make it so that BOTH pairs are not equal, just one of them
				cost = 7;
			} else {		// They're perpendicular
				cost = 15;
			}
			return cost;
		}
		
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("Cell: [" + node + "]  ");
			if (previous == null) {
				builder.append("Previous: null");
			} else {
				builder.append("Previous: [" + previous.node + "]");
			}
			return builder.toString();
		}
	}
}
