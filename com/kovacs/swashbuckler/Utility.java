package com.kovacs.swashbuckler;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import com.kovacs.swashbuckler.game.BoardCoordinate;

/*
 * Contains general utilities and things that aren't really specific to a single
 * class or are used all over.
 */
public class Utility
{
	/*
	 * Shuffles a list.
	 */
	public static <T> void shuffle(List<T> list)
	{
		Collections.shuffle(list, rand);
	}
	
	/*
	 * Returns a element from the given array.
	 */
	public static <T> T random(T arr[])
	{
		return arr[randInt(0, arr.length-1)];
	}
	
	/*
	 * Returns a random direction.
	 */
	public static Direction randomDirection()
	{
		return Direction.values()[rand.nextInt(Direction.values().length)];
	}
	
	/*
	 * Seedable random object
	 */
	private static Random rand = new Random();

	/*
	 * Returns a random double between 0 and 1.
	 */
	public static double rand()
	{
		return rand.nextDouble();
	}

	/*
	 * Returns a random integer between the two given values
	 */
	public static int randInt(int min, int max)
	{
		return rand.nextInt(max - min + 1) + min;
	}

	/*
	 * Takes a non-zero integer from the standard input
	 */
	public static int getInt(Scanner scanner, int max)
	{
		int i = 0;
		while (i == 0)
		{
			try
			{
				i = scanner.nextInt();
			}
			catch (InputMismatchException e)
			{
				System.out.println("Please enter an integer.");
			}
			if (i <= 0)
			{
				System.out.println("Must be greater than 0.");
				i = 0;
			}
			else if (i > max)
			{
				System.out.println("That's too high.");
				i = 0;
			}
			scanner.nextLine();
		}
		return i;
	}

	/*
	 * Picks a random square on the board. TODO: add checking to this and change
	 * it to randomEmptySquare or something.
	 */
	public static BoardCoordinate randomBoardCoordinate()
	{
		switch (rollDoubleDigit())
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
	 * Simulates a 6-sided die roll
	 */
	public static int rollDie()
	{
		return rand.nextInt(6) + 1;
	}

	/*
	 * Simulates a double roll
	 */
	public static int rollDoubleDigit()
	{
		return rollDie() * 10 + rollDie();
	}
	
	/*
	 * A list of just the cardinal direcitons.
	 */
	public static Direction[] cardinalDirections = {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};

	/*
	 * Represents each relevant compass direction.
	 */
	public enum Direction
	{
		NORTH, SOUTH, EAST, WEST, NORTHEAST, NORTHWEST, SOUTHEAST, SOUTHWEST
	}
	
	// returns the external IP address of this computer. Super sketchy. I wrote
	// this a long time ago.
	public static String getExternalIPAddress()
	{
		String s = "";
		try
		{
			URL url = new URL("http://whatsmyip.net");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			DataInputStream reader = new DataInputStream(connection.getInputStream());
			for (int i = 0; i < 6904; i++)
				s += (char) reader.read();
		}
		catch (UnknownHostException e)
		{
			e.printStackTrace();
			return "unavailable because there is no internet.";
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		int indexOfA = -1, indexOfIP = 0;
		while (true)
		{
			for (int i = indexOfA + 1; i < s.length(); i++)
			{
				if (s.charAt(i) == 'A')
				{
					indexOfA = i;
					break;
				}
			}
			if (s.substring(indexOfA, indexOfA + 37).equals("Address is <input type=\"text\" value=\""))
			{
				indexOfIP = indexOfA + 37;
				break;
			}
		}
		return s.substring(indexOfIP, s.indexOf('"', indexOfIP));
	}
}