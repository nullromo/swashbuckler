package com.kovacs.swashbuckler.packets;

import com.kovacs.swashbuckler.Connection;

/*
 * The base class for client-server communication packets.
 */
// TODO: many packets and entities seem to be repetitive and the only thing that
// matters is their type. I should probably change it to a single class and use
// generics for the data type.
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