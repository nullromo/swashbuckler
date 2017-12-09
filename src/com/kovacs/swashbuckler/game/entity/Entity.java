package com.kovacs.swashbuckler.game.entity;

import java.io.Serializable;
import java.util.ArrayList;
import com.kovacs.swashbuckler.Utility.Direction;
import com.kovacs.swashbuckler.game.BoardCoordinate;

/*
 * This class represents an objct on the game board.
 */
public class Entity implements Serializable
{
	private static final long serialVersionUID = -441542423900716801L;

	/*
	 * All the types of entities.
	 */
	public enum EntityType
	{
		BALCONY, BROKEN_GLASS, CHAIR, DAGGER, MUG, PIRATE, SHELF, STAIRS, SWORD, TABLE, WINDOW
	};

	/*
	 * This entity's type
	 */
	public EntityType type;

	/*
	 * An array of the coordinates that this entity takes up. Most entities take
	 * up a single square, but tables, shelves, and carpets can take up more
	 * than one.
	 */
	public ArrayList<BoardCoordinate> coordinates;

	public Entity(EntityType type, BoardCoordinate... boardCoordinates)
	{
		this.type = type;
		coordinates = new ArrayList<>();
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

	/*
	 * Returns true if this entity touches the given coordinate
	 */
	public boolean touches(BoardCoordinate coordinate)
	{
		for (BoardCoordinate c : coordinates)
		{
			if (c.equals(coordinate))
			{
				return true;
			}
		}
		return false;
	}
	
	@Override public String toString()
	{
		return type.toString();
	}
}