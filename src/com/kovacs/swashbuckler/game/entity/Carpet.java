package com.kovacs.swashbuckler.game.entity;

import java.awt.image.BufferedImage;
import java.io.Serializable;
import com.kovacs.swashbuckler.Images;
import com.kovacs.swashbuckler.game.BoardCoordinate;

public class Carpet extends Entity implements Serializable
{
	private static final long serialVersionUID = 8132672763661671297L;

	/*
	 * Tells which image to use. If it's not northSouth, then it's eastWest.
	 */
	private boolean northSouth;

	public Carpet(EntityType type, boolean northSouth, BoardCoordinate... boardCoordinates)
	{
		super(type, boardCoordinates);
		this.northSouth = northSouth;
	}

	@Override
	public BufferedImage getImage()
	{
		return northSouth ? Images.carpet_NS : Images.carpet_EW;
	}
}