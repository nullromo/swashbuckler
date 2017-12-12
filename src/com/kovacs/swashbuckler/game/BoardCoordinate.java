package com.kovacs.swashbuckler.game;

import java.io.Serializable;
import com.kovacs.swashbuckler.Utility;
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

	/*
	 * Picks a random square for a pirate to go.
	 */
	public static BoardCoordinate randomPiratePosition()
	{
		switch (Utility.rollDoubleDigit())
		{
			case 11:
				return new BoardCoordinate('b', 2);
			case 12:
				return new BoardCoordinate('b', 5);
			case 13:
				return new BoardCoordinate('b', 8);
			case 14:
				return new BoardCoordinate('b', 11);
			case 15:
				return new BoardCoordinate('b', 14);
			case 16:
				return new BoardCoordinate('d', 1);
			case 21:
				return new BoardCoordinate('d', 4);
			case 22:
				return new BoardCoordinate('d', 7);
			case 23:
				return new BoardCoordinate('d', 10);
			case 24:
				return new BoardCoordinate('d', 13);
			case 25:
				return new BoardCoordinate('f', 3);
			case 26:
				return new BoardCoordinate('f', 6);
			case 31:
				return new BoardCoordinate('f', 9);
			case 32:
				return new BoardCoordinate('f', 12);
			case 33:
				return new BoardCoordinate('f', 14);
			case 34:
				return new BoardCoordinate('h', 1);
			case 35:
				return new BoardCoordinate('h', 4);
			case 36:
				return new BoardCoordinate('h', 7);
			case 41:
				return new BoardCoordinate('h', 10);
			case 42:
				return new BoardCoordinate('h', 13);
			case 43:
				return new BoardCoordinate('j', 2);
			case 44:
				return new BoardCoordinate('j', 5);
			case 45:
				return new BoardCoordinate('j', 8);
			case 46:
				return new BoardCoordinate('j', 11);
			case 51:
				return new BoardCoordinate('j', 14);
			case 52:
				return new BoardCoordinate('l', 3);
			case 53:
				return new BoardCoordinate('l', 6);
			case 54:
				return new BoardCoordinate('l', 9);
			case 55:
				return new BoardCoordinate('l', 12);
			case 56:
				return new BoardCoordinate('l', 14);
			case 61:
				return new BoardCoordinate('n', 2);
			case 62:
				return new BoardCoordinate('n', 5);
			case 63:
				return new BoardCoordinate('n', 7);
			case 64:
				return new BoardCoordinate('n', 10);
			case 65:
				return new BoardCoordinate('n', 12);
			case 66:
				return new BoardCoordinate('n', 14);
			default:
				throw new RuntimeException("Unreachable code.");
		}
	}

	/*
	 * Returns a random coordinate on the board.
	 */
	public static BoardCoordinate random()
	{
		int number = Utility.randInt(1, 14);
		char letter = (char) ('a' + Utility.randInt(0, 14));
		return new BoardCoordinate(letter, number);
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + letter;
		result = prime * result + number;
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BoardCoordinate other = (BoardCoordinate) obj;
		if (letter != other.letter)
			return false;
		if (number != other.number)
			return false;
		return true;
	}
}