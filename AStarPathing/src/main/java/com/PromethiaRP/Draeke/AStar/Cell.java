package com.PromethiaRP.Draeke.AStar;

public class Cell implements Comparable<Cell> {
	private int m_X, m_Y;
	private int m_Width, m_Height;
	private Cell m_parent = null;
	private int m_travelCost = 0;
	private int m_goalCost = 0;
	//private boolean m_isGoal = false;
	//private boolean m_isStart = false;
	private boolean m_walkable = true;

	public Cell(int x, int y, int width, int height) {
		m_X = x;
		m_Y = y;
		m_Width = width;
		m_Height = height;
	}

	public void setWalkable(boolean walk) {
		m_walkable = walk;
	}
	public boolean getWalkable() {
		return m_walkable;
	}

	public void setParent(Cell cel) {
		m_parent = cel;
	}
	public Cell getParent() {
		return m_parent;
	}

	public int getWidth() {
		return m_Width;
	}
	public int getHeight() {
		return m_Height;
	}
	public int getX() {
		return m_X;
	}
	public int getY() {
		return m_Y;
	}
	public int getAdjustedX() {
		return m_X * m_Width;
	}
	public int getAdjustedY() {
		return m_Y * m_Height;
	}

	public int getTotalCost() {
		return getPathCost() + m_goalCost;
	}

	public int getPathCost() {
		if (m_parent == null) {
			return 1;
		}
		return m_parent.getPathCost()+getStepCost();
	}

	public int getTurnCost() {
		if (m_parent == null) {
			return 0;
		}
		if (m_parent.m_parent == null) {
			return 0;
		}
		Direction dir = Direction.getDirection(m_X-m_parent.m_X, m_Y-m_parent.m_Y);
		Direction dir2 = Direction.getDirection(m_parent.m_X-m_parent.m_parent.m_X, m_parent.m_Y-m_parent.m_parent.m_Y);
		if (dir == dir2) {
			return 0;
		}else if (dir.isFluidTurn(dir2)) {
			return 4;
		} else {
			return 9;
		}

	}

	public int getStepCost() {
		boolean horiz = false;
		boolean vert = false;

		if(Math.abs(m_X-m_parent.getX())==0) {
			vert = true;
		}
		if(Math.abs(m_Y-m_parent.getY())==0) {
			horiz = true;
		}
		if ( (horiz && !vert) || (vert && !horiz)) {
			return 10 + m_travelCost + getTurnCost();
		} else {
			return 14 + m_travelCost + getTurnCost();
		}
	}

	@Override
	public int compareTo(Cell arg) {
		
		return  getTotalCost() - arg.getTotalCost();
	}
}
