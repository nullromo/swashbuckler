package com.kovacs.swashbuckler.game.entity;

import java.awt.image.BufferedImage;
import java.io.Serializable;
import com.kovacs.swashbuckler.Images;
import com.kovacs.swashbuckler.Utility.Direction;
import com.kovacs.swashbuckler.game.BoardCoordinate;

public class Chair extends Entity implements Serializable
{
	private static final long serialVersionUID = -2378133399882880577L;

	/*
	 * The direction of the image that the chair uses based on where it started
	 * out.
	 */
	public Direction direction;

	public Chair(EntityType type, Direction direction, BoardCoordinate... boardCoordinates)
	{
		super(type, boardCoordinates);
		this.direction = direction;
	}

	@Override
	public BufferedImage getImage()
	{
		switch (direction)
		{
			case EAST:
				return Images.chair_E;
			case NORTH:
				return Images.chair_N;
			case SOUTH:
				return Images.chair_S;
			case WEST:
				return Images.chair_W;
			default:
				throw new RuntimeException("Chair cannot face diagonally.");
		}
	}
}