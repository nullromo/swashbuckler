package com.kovacs.swashbuckler.game.entity;

import java.io.Serializable;
import com.kovacs.swashbuckler.game.BoardCoordinate;

public class Mug extends Entity implements Serializable
{
	private static final long serialVersionUID = 4026434368361167929L;

	public Mug(BoardCoordinate... boardCoordinates)
	{
		super(boardCoordinates);
	}
}