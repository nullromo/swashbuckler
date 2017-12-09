package com.kovacs.swashbuckler.game;

import java.io.Serializable;
import com.kovacs.swashbuckler.Utility.Direction;

/*
 * This class represents a single square on the board (like e4 or g12)
 */
public class BoardCoordinate implements Serializable
{
	private static final long serialVersionUID = 1890916500095788408L;

	/*
	 * The row on the board
	 */
	public char letter;

	/*
	 * The column on the board
	 */
	public int number;

	public BoardCoordinate(char letter, int number)
	{
		this.letter = letter;
		this.number = number;
	}

	@Override
	public String toString()
	{
		return Character.toUpperCase(letter) + "" + number;
	}

	/*
	 * Returns a new board coordinate 1 over from this in the specified
	 * direction. Returns null if that coordinate is invalid.
	 */
	public BoardCoordinate next(Direction direction)
	{
		char newLetter;
		int newNumber;
		switch (direction)
		{
			case NORTH:
				newLetter = (char) (letter - 1);
				newNumber = number;
				break;
			case SOUTH:
				newLetter = (char) (letter + 1);
				newNumber = number;
				break;
			case EAST:
				newLetter = letter;
				newNumber = number + 1;
				break;
			case WEST:
				newLetter = letter;
				newNumber = number - 1;
				break;
			case NORTHEAST:
				newLetter = (char) (letter - 1);
				newNumber = number + 1;
				break;
			case NORTHWEST:
				newLetter = (char) (letter - 1);
				newNumber = number - 1;
				break;
			case SOUTHEAST:
				newLetter = (char) (letter + 1);
				newNumber = number + 1;
				break;
			case SOUTHWEST:
				newLetter = (char) (letter + 1);
				newNumber = number - 1;
				break;
			default:
				throw new RuntimeException("Unreachable code");

		}
		if (newLetter < 'a' || newLetter > 'o' || newNumber < 1 || newNumber > 14)
			return null;
		return new BoardCoordinate(newLetter, newNumber);
	}

	@Override
	public boolean equals(Object o)
	{
		return o instanceof BoardCoordinate && ((BoardCoordinate) o).letter == this.letter
				&& ((BoardCoordinate) o).number == this.number;
	}
}