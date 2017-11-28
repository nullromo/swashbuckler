package com.kovacs.swashbuckler.game.entity;

import java.io.Serializable;
import com.kovacs.swashbuckler.game.BoardCoordinate;

public class Window extends Entity implements Serializable
{
	private static final long serialVersionUID = -3238933136333708319L;

	public Window(BoardCoordinate... boardCoordinates)
	{
		super(boardCoordinates);
	}
}