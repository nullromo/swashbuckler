package com.kovacs.swashbuckler.packets;

import java.io.Serializable;
import com.kovacs.swashbuckler.game.Board;

public class BoardPacket extends Packet implements Serializable
{
	private static final long serialVersionUID = -7955191022388260660L;

	private Board board;

	public BoardPacket(Board board)
	{
		this.board = board;
	}

	public Board getBoard()
	{
		return board;
	}
}