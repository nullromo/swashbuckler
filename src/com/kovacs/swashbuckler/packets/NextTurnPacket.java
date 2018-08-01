package com.kovacs.swashbuckler.packets;

import java.io.Serializable;

/*
 * Tells the clients that it's time to start planning their next turns.
 */
public class NextTurnPacket extends Packet implements Serializable
{
	private static final long serialVersionUID = 5360980999522683250L;
}