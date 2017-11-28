package com.kovacs.swashbuckler.game.entity;

import java.io.Serializable;
import com.kovacs.swashbuckler.game.BoardCoordinate;

public class Shelf extends Entity implements Serializable
{
	private static final long serialVersionUID = -1420685841604605700L;

	public Shelf(BoardCoordinate... boardCoordinates)
	{
		super(boardCoordinates);
	}
}