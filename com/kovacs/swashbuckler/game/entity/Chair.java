package com.kovacs.swashbuckler.game.entity;

import java.io.Serializable;
import com.kovacs.swashbuckler.game.BoardCoordinate;

public class Chair extends Entity implements Serializable
{
	private static final long serialVersionUID = 6796385433280051150L;

	public Chair(BoardCoordinate... boardCoordinates)
	{
		super(boardCoordinates);
	}
}