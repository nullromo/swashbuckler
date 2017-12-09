package com.kovacs.swashbuckler.packets;

import java.io.Serializable;

/*
 * This packet is a request from the server to the client that asks for a
 * specific type of object. It is used to request the client to send pirates,
 * plans, decisions, and other game objects needed.
 */
public class RequestPacket extends Packet implements Serializable
{
	private static final long serialVersionUID = -6972542971370931041L;
	
	public Class<?> type;

	public RequestPacket(Class<?> type)
	{
		this.type = type;
	}
}