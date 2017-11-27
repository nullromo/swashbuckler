package com.kovacs.swashbuckler.game.entity;

import java.io.Serializable;
import com.kovacs.swashbuckler.game.BoardCoordinate;

public class Table extends Entity implements Serializable
{
	private static final long serialVersionUID = -5218162275675104346L;

	public Table(BoardCoordinate... boardCoordinates)
	{
		super(boardCoordinates);
	}
}