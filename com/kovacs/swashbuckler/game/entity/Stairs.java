package com.kovacs.swashbuckler.game.entity;

import java.io.Serializable;
import com.kovacs.swashbuckler.game.BoardCoordinate;

public class Stairs extends Entity implements Serializable
{
	private static final long serialVersionUID = 5317234951904788700L;

	public Stairs(BoardCoordinate... boardCoordinates)
	{
		super(boardCoordinates);
	}
}