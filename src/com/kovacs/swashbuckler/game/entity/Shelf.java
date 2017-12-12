package com.kovacs.swashbuckler.game.entity;

import java.awt.image.BufferedImage;
import java.io.Serializable;
import com.kovacs.swashbuckler.Images;
import com.kovacs.swashbuckler.Utility;
import com.kovacs.swashbuckler.Utility.Direction;
import com.kovacs.swashbuckler.game.BoardCoordinate;

public class Shelf extends Entity implements Serializable
{
	private static final long serialVersionUID = 2833163871523422344L;

	/*
	 * Directions so that the correct image can be drawn.
	 */
	private Direction wall;

	public Shelf(EntityType type, Direction wall, BoardCoordinate... boardCoordinates)
	{
		super(type, boardCoordinates);
		this.wall = wall;
	}

	@Override
	public BufferedImage getImage()
	{
		throw new RuntimeException(
				"Shelves must be supplied with coordinates in order to determine their draw images.");
	}

	/*
	 * Determines which shelf image should be drawn.
	 */
	public BufferedImage getDrawImage(BoardCoordinate coordinate)
	{
		Direction direction = null;
		for (BoardCoordinate neighborCoordinate : coordinates)
		{
			Direction neighborDirection = Utility.directionTo(coordinate, neighborCoordinate);
			if (neighborDirection != null)
				direction = neighborDirection;
		}
		if (wall == Direction.EAST)
		{
			if (direction == Direction.SOUTH)
				return Images.shelf_E_top;
			else
				return Images.shelf_E_bottom;
		}
		else if (wall == Direction.WEST)
		{
			if (direction == Direction.SOUTH)
				return Images.shelf_W_top;
			else
				return Images.shelf_W_bottom;
		}
		else if (wall == Direction.NORTH)
		{
			if (direction == Direction.EAST)
				return Images.shelf_N_left;
			else
				return Images.shelf_N_right;
		}
		else
		{
			if (direction == Direction.EAST)
				return Images.shelf_S_left;
			else
				return Images.shelf_S_right;
		}
	}
}