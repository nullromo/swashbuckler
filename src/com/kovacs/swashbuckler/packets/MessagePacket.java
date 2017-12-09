package com.kovacs.swashbuckler.packets;

import java.io.Serializable;

/*
 * This class is used to send messages to clients.
 */
public class MessagePacket extends Packet implements Serializable
{
	private static final long serialVersionUID = -7871414408806307576L;

	/*
	 * The message being sent.
	 */
	private String message;

	public MessagePacket(String message)
	{
		this.message = message;
	}

	public String getMessage()
	{
		return message;
	}
}