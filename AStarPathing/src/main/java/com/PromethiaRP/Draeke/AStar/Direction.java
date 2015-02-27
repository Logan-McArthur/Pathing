package com.PromethiaRP.Draeke.AStar;

import java.security.InvalidParameterException;

enum Direction {
	NORTH(0,-1),
	NORTH_EAST(1,-1),
	EAST(1,0),
	SOUTH_EAST(1,1),
	SOUTH(0,1),
	SOUTH_WEST(-1,1),
	WEST(-1,0),
	NORTH_WEST(-1,-1);
	
	private final int X, Y;
	
	Direction(int x, int y) {
		X = x;
		Y = y;
	}
	
	
	public boolean isFluidTurn(Direction dir) {
		int myord = this.ordinal();
		int dirord = dir.ordinal();
		return Math.abs(myord - dirord) <= 1;
	}
	
	public int getDeltaX() {
		return X;
	}
	public int getDeltaY() {
		return Y;
	}
	
	public static Direction getDirection(int dX, int dY) {
		if (Math.abs(dX) > 1 || Math.abs(dY) > 1) {
			throw new InvalidParameterException("Invalid parameters passed into Direction.getDirection(int, int)");
		}
		for ( Direction dir : Direction.values()) {
			if (dir.getDeltaX() == dX) {
				if (dir.getDeltaY() == dY) {
					return dir;
				}
			}
		}
		return null;
	}
	
	public Direction getClockwiseTurn() {
		return Direction.values()[(this.ordinal()+1) % (Direction.values().length)];
	}
	// (a % b + b) % b
	public Direction getCounterClockwiseTurn() {
		return Direction.values()[(((this.ordinal()-1) % (Direction.values().length) +Direction.values().length)) % Direction.values().length];
	}
	
	public boolean isAxisAligned() {
		return this.ordinal()%2==0;
	}
}