package com.kovacs.swashbuckler.game.entity;

import java.io.Serializable;
import com.kovacs.swashbuckler.game.BoardCoordinate;

public class Dagger extends Entity implements Serializable
{
	private static final long serialVersionUID = -7179420929298655539L;

	public Dagger(BoardCoordinate... boardCoordinates)
	{
		super(boardCoordinates);
	}
}