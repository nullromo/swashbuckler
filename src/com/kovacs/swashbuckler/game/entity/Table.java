package com.kovacs.swashbuckler.game.entity;

import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import com.kovacs.swashbuckler.Images;
import com.kovacs.swashbuckler.Utility;
import com.kovacs.swashbuckler.Utility.Direction;
import com.kovacs.swashbuckler.game.BoardCoordinate;

public class Table extends Entity implements Serializable
{
	private static final long serialVersionUID = -3510355668738377995L;

	/*
	 * A tag so that the game can tell which tables are connected to which other
	 * tables.
	 */
	private int tag;

	public Table(EntityType type, int tableTag, BoardCoordinate... boardCoordinates)
	{
		super(type, boardCoordinates);
		this.tag = tableTag;
	}

	/*
	 * Returns the appropriate image to draw based on the coordinate's position
	 * relative to it's other connected parts.
	 */
	public BufferedImage getDrawImage(BoardCoordinate coordinate)
	{
		List<Direction> neighborDirections = new ArrayList<>();
		for (BoardCoordinate connectedCoordinate : coordinates)
			neighborDirections.add(Utility.directionTo(coordinate, connectedCoordinate));
		if (neighborDirections.contains(Direction.NORTH) && neighborDirections.contains(Direction.SOUTH))
			return Images.table_NS;
		else if (neighborDirections.contains(Direction.EAST) && neighborDirections.contains(Direction.WEST))
			return Images.table_EW;
		else if (neighborDirections.contains(Direction.EAST))
			return isLong() ? Images.table_E_long : Images.table_E_short;
		else if (neighborDirections.contains(Direction.WEST))
			return isLong() ? Images.table_W_long : Images.table_W_short;
		else if (neighborDirections.contains(Direction.NORTH))
			return isLong() ? Images.table_N_long : Images.table_N_short;
		else if (neighborDirections.contains(Direction.SOUTH))
			return isLong() ? Images.table_S_long : Images.table_S_short;
		else
		{
			System.out.println(coordinates);
			System.out.println(isLong());
			System.out.println();
			throw new RuntimeException("Impossible state.");
		}
	}

	/*
	 * Disable the superclass getImage method.
	 */
	@Override
	public BufferedImage getImage()
	{
		throw new RuntimeException("Tables must be supplied with coordinates in order to determine their draw images.");
	}

	/*
	 * Returns true if the table is 3 long and false if it's 2 long.
	 */
	private boolean isLong()
	{
		return coordinates.size() == 3;
	}

	public int getTag()
	{
		return tag;
	}

	@Override
	public String toString()
	{
		return super.toString() + " " + tag;
	}
}