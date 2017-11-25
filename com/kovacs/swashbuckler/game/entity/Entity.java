package com.kovacs.swashbuckler.game.entity;

import java.util.ArrayList;
import com.kovacs.swashbuckler.Utility.Direction;
import com.kovacs.swashbuckler.game.BoardCoordinate;

/*
 * This class represents an objct on the game board.
 */
public class Entity
{
	/*
	 * An array of the coordinates that this entity takes up. Most entities take
	 * up a single square, but tables, shelves, and carpets can take up more
	 * than one.
	 */
	public ArrayList<BoardCoordinate> coordinates = new ArrayList<>();

	public Entity(BoardCoordinate... boardCoordinates)
	{
		for (BoardCoordinate bc : boardCoordinates)
		{
			coordinates.add(bc);
		}
	}

	/*
	 * If the move is valid, this method moves the entity in the specified
	 * direction. If it is not valid, it returns false.
	 */
	public boolean move(Direction direction)
	{
		ArrayList<BoardCoordinate> newCoords = new ArrayList<>();
		for (BoardCoordinate bc : coordinates)
		{
			if (bc.next(direction) == null)
				return false;
			newCoords.add(bc.next(direction));
		}
		coordinates = newCoords;
		return true;
	}
}