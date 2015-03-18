package agentpathing.agents;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

import agentpathing.Cell;

public abstract class Agent {

	private Queue<PathStep> openCells = new PriorityQueue<PathStep>();	// openCells should be sorted
	private Set<PathStep> closedCells = new HashSet<PathStep>();	// closedCells does not need to be

	private PathStep pathFront;
	private PathStep pathEnd;
	
	private Cell goalCell;
	private Cell startCell;
	
	private boolean noPath = false;
	
	public Agent(Cell start, Cell end) {
		startCell = start;
		goalCell = end;
		pathFront = new PathStep(start, getGoalCost(start));
		pathEnd = null;
		openCells.add(pathFront);
		
	}
	
	public void reset() {
		openCells.clear();
		closedCells.clear();
		pathEnd = null;
		pathFront = new PathStep(startCell, getGoalCost(startCell));
		openCells.add(pathFront);
		noPath = false;
	}
	
	public boolean isFinished() {
		return pathEnd != null || noPath;
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
		if (current == null) {
			noPath = true;
			return;
		}
		closedCells.add(current);
		if (current.node.equals(goalCell)) {
			pathEnd = current;
		}
		
		Set<Cell> considerSet = getConnectedCells(current);
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
	
//	protected void removeFromClosed(Set<Cell> cells) {
//		// Including the cells already in openCells
//		// Remove the ones in closedCells
//		//cells.removeAll(closedCells);
//		int size = cells.size();
//		for (Cell cll : cells) {
//			PathStep stp = getStepFromCollection(closedCells, cll);
//			if (stp != null) {
//				cells.remove(stp);
//			}
//		}
//		if (size != cells.size()) {
//			System.out.println("NOT EQUAL");
//		}
//	}
	
	private int getStepCost(PathStep start, PathStep end) {
		return getStepCost(start.node,end.node);
	}
	
	protected abstract int getStepCost(Cell start, Cell end);

	protected abstract int getGoalCost(Cell cell);

//	
	
	// Do not include Cells cut off by corners
	protected abstract Set<Cell> getConnectedCells(PathStep center);

	
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
	
	public class PathStep implements Comparable<PathStep>{

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof PathStep) {
				PathStep ps = (PathStep) obj;
				return node.equals(ps.node);
			}
			return false;
		}

		protected Cell node;
		protected PathStep previous;
		
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
			return pathCost + goalCost - (compare.pathCost + compare.goalCost);
//			Integer ours = new Integer(pathCost+goalCost);
//			Integer theirs = new Integer(compare.pathCost + compare.goalCost);
//			return ours.compareTo(theirs);
		}
		
		public boolean isBetterPath(PathStep possiblePrev, int step) {
			// get cost of possiblePrev and add it to the cost from possiblePrev to this
			// get current cost
//			int possibleCost = possiblePrev.pathCost + step + getTurnCost(possiblePrev);
			int possibleCost = possiblePrev.pathCost + step;
			return possibleCost < pathCost;
		}
		
		public void setPrevious(PathStep prev, int stepCost, int goalCost) {
			if (prev == null) {
				pathCost = stepCost;
			} else {
//				pathCost = prev.pathCost + Math.max(Math.abs(prev.node.getX()-node.getX()), Math.abs(prev.node.getY()-node.getY()))*stepCost + getTurnCost(prev);
				pathCost = prev.pathCost + stepCost;

			}
			this.goalCost = goalCost;
			this.previous = prev;
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
