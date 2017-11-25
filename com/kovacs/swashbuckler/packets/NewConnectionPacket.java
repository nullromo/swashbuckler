package com.kovacs.swashbuckler.packets;

import java.io.Serializable;

/*
 * Sent by a client when it first connects. Also sent by the server to acknowledge.
 */
public class NewConnectionPacket extends Packet implements Serializable
{
	private static final long serialVersionUID = 8912624105063389371L;	
}