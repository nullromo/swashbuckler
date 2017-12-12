package com.kovacs.swashbuckler.packets;

import com.kovacs.swashbuckler.Connection;

/*
 * The base class for client-server communication packets.
 */
public class Packet
{
	/*
	 * A reference to the connection that this packet was sent from.
	 */
	private Connection connection;

	/*
	 * Tags this packet with the correct connection so that the server can tell
	 * where it came from.
	 */
	public void tag(Connection connection)
	{
		this.connection = connection;
	}

	public Connection getConnection()
	{
		return connection;
	}
}