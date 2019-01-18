package com.kovacs.swashbuckler3;

import com.kovacs.swashbuckler.game.entity.Pirate;

/*
 * Represents a human player in a game
 */
public class Player
{
	/*
	 * List of pirates this player controls.
	 */
	public Pirate[] pirates;

	public Player(Pirate... pirates)
	{
		this.pirates = pirates;
	}

	// TODO: Connection information goes here
}