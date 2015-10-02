package agentpathing;

public class Cell {
	
	// TODO: Consider adding a count of how many links the cell has
	
	private int m_X, m_Y;
	
	private boolean m_walkable = true;

	public Cell(int x, int y) {
		m_X = x;
		m_Y = y;
	}

	
	public void setWalkable(boolean walk) {
		m_walkable = walk;
	}
	public boolean getWalkable() {
		return m_walkable;
	}

	public int getX() {
		return m_X;
	}
	public int getY() {
		return m_Y;
	}
	
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Walkable: " + m_walkable + ", ");
		builder.append("X: " + m_X + ", ");
		builder.append("Y: " + m_Y + ", ");
		return builder.toString();
	}

}
