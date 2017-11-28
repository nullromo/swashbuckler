package com.kovacs.swashbuckler.game.entity;

import java.io.Serializable;
import com.kovacs.swashbuckler.game.BoardCoordinate;

public class Sword extends Entity implements Serializable
{
	private static final long serialVersionUID = 1771949615633271240L;

	public Sword(BoardCoordinate... boardCoordinates)
	{
		super(boardCoordinates);
	}
}