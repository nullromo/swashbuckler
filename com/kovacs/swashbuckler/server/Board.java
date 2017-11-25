package com.kovacs.swashbuckler.server;

import java.util.ArrayList;
import com.kovacs.swashbuckler.game.BoardCoordinate;
import com.kovacs.swashbuckler.game.entity.Entity;

/*
 * This class represents the game board.
 */
public class Board
{
	/*
	 * This list contains all the entities on the board.
	 */
	private ArrayList<Entity> entities = new ArrayList<>();

	public Board()
	{
		entities.add(new Entity(new BoardCoordinate('c', 9)));
	}

	@Override
	public String toString()
	{
		char[][] board = new char[14][14];
		for (Entity e : entities)
		{
			for (BoardCoordinate bc: e.coordinates)
			{
				board[bc.letter-97][bc.number-1] = 'e';
			}
		}
		StringBuilder sb = new StringBuilder();
		for (char[] ca: board)
		{
			for(char c: ca)
			{
				sb.append(c==0?'.':c).append(" ");
			}
			sb.append("\n");
		}
		return sb.toString();
	}
}