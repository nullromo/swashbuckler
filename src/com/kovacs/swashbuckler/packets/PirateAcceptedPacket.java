package com.kovacs.swashbuckler.packets;

import java.io.Serializable;
import com.kovacs.swashbuckler.game.entity.Pirate;

public class PirateAcceptedPacket extends Packet implements Serializable
{
	private static final long serialVersionUID = 4516358806665441002L;
	/*
	 * The pirate that was accepted.
	 */
	private Pirate pirate;

	public PirateAcceptedPacket(Pirate pirate)
	{
		this.pirate = pirate;
	}

	public Pirate getPirate()
	{
		return pirate;
	}
}