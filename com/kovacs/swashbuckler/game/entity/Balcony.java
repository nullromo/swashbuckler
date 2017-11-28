package com.kovacs.swashbuckler.game.entity;

import java.io.Serializable;
import com.kovacs.swashbuckler.game.BoardCoordinate;

public class Balcony extends Entity implements Serializable
{
	private static final long serialVersionUID = 7629939162551444947L;

	public Balcony(BoardCoordinate... boardCoordinates)
	{
		super(boardCoordinates);
	}
}