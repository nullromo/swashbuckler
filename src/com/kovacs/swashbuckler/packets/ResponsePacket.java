package com.kovacs.swashbuckler.packets;

import java.io.Serializable;

/*
 * This packet carries plans, pirates, decisions, and every game object that is
 * needed. It is always sent to the server from the client in response to a
 * request packet.
 */
public class ResponsePacket<T> extends Packet implements Serializable
{
	private static final long serialVersionUID = 3265445637744986077L;

	/*
	 * The object being sent (payload).
	 */
	private T object;

	public ResponsePacket(T object)
	{
		this.object = object;
	}

	public T getObject()
	{
		return object;
	}

	public Class<? extends Object> getType()
	{
		return object.getClass();
	}
}