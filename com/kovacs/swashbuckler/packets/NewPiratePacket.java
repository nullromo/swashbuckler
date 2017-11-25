package com.kovacs.swashbuckler.packets;

import java.io.Serializable;
import com.kovacs.swashbuckler.game.entity.Pirate;

/*
 * Sent after the user has determined a pirate's stats.
 */
public class NewPiratePacket extends Packet implements Serializable
{
	private static final long serialVersionUID = 229060587255056161L;
	
	/*
	 * The new pirate that is being created.
	 */
	private Pirate pirate;

	public NewPiratePacket(Pirate pirate)
	{
		this.pirate = pirate;
	}
	
	public Pirate getPirate()
	{
		return pirate;
	}
}