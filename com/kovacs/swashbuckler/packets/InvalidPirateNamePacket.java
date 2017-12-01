package com.kovacs.swashbuckler.packets;

import java.io.Serializable;
import com.kovacs.swashbuckler.game.entity.Pirate;

public class InvalidPirateNamePacket extends Packet implements Serializable
{
	private static final long serialVersionUID = 1631932616300913999L;

	/*
	 * The pirate that was sent and has an already-in-use name.
	 */
	private Pirate pirate;

	public InvalidPirateNamePacket(Pirate pirate)
	{
		this.pirate = pirate;
	}

	public Pirate getPirate()
	{
		return pirate;
	}
}