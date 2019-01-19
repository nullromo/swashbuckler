package com.kovacs.swashbuckler.game.entity;

import java.awt.image.BufferedImage;
import java.io.Serializable;
import com.kovacs.swashbuckler.Images;
import com.kovacs.swashbuckler.game.BoardCoordinate;
import com.kovacs.swashbuckler3.PirateData;
import com.kovacs.swashbuckler3.PirateData.Dexterity;

public class Pirate extends Entity implements Serializable
{
	private static final long serialVersionUID = -1096156252334655485L;

	// TODO: this is a mess right now.
	public PirateData pirateData = new PirateData();

	public Pirate(int head, int leftArm, int rightArm, int body, int strength, int endurance, int constitution,
			int expertise, Dexterity dexterity, String name, BoardCoordinate... boardCoordinates)
	{
		super(EntityType.PIRATE, boardCoordinates);
	}

	// TODO: change this, obviously.
	@Override
	public BufferedImage getImage()
	{
		return Images.pirate;
	}

	public String getName()
	{
		return pirateData.getName();
	}
}