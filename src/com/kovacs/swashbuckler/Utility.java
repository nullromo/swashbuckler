package com.kovacs.swashbuckler;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;
import com.kovacs.swashbuckler.game.BoardCoordinate;

/*
 * Contains general utilities and things that aren't really specific to a single
 * class or are used all over.
 */
public class Utility
{
	/*
	 * Throws an exception if the given number is negative.
	 */
	public static void assertPositive(int i)
	{
		if (i < 0)
			throw new RuntimeException("This number cannot be negative.");
	}

	/*
	 * Sleeps the calling thread.
	 */
	public static void sleep(int millis)
	{
		try
		{
			Thread.sleep(millis);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}

	/*
	 * Tells whether a character is a printable character in the game or not.
	 */
	public static boolean isPrintableCharacter(char character)
	{
		if (Character.isLetterOrDigit(character))
			return true;
		switch (character)
		{
			case ' ':
			case '!':
			case '@':
			case '#':
			case '$':
			case '%':
			case '^':
			case '&':
			case '*':
			case '(':
			case ')':
			case '-':
			case '_':
			case '=':
			case '+':
			case '[':
			case ']':
			case '{':
			case '}':
			case '\\':
			case '|':
			case ';':
			case ':':
			case '\'':
			case '"':
			case ',':
			case '<':
			case '.':
			case '>':
			case '/':
			case '?':
			case '`':
			case '~':
				return true;
			default:
				return false;
		}
	}

	/*
	 * Throws a runtime exception because of an unimplemented feature or
	 * unrecognized operation/type.
	 */
	public static void typeError(Object... objects)
	{
		StringBuilder sb = new StringBuilder("Unrecognized or unimplemented object type");
		for (Object object : objects)
		{
			sb.append(" ").append(object.toString());
		}
		throw new RuntimeException(sb.toString());
	}

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
	public static <T> T randomElement(T arr[])
	{
		return arr[randInt(0, arr.length - 1)];
	}

	/*
	 * Returns the direction from a to b. Returns null if a and b are not
	 * adjacent.
	 */
	public static Direction directionTo(BoardCoordinate a, BoardCoordinate b)
	{
		for (Direction direction : Direction.values())
			if (a.next(direction) != null && a.next(direction).equals(b))
				return direction;
		return null;
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
	 * Reads from a scanner until a valid name is entered.
	 */
	public static String getValidName()
	{
		String name = null;
		do
		{
			ClientMain.main.gui.writeMessage("(Your pirate's name must be one or two words made of letters only.)");
			System.out.println("waiting for line.");
			name = ClientMain.main.gui.keyboardInput.nextLine();
			System.out.println("sd");
		}
		while (!validName(name));
		return name;
	}

	/*
	 * Tells whether or not a name is valid
	 */
	private static boolean validName(String name)
	{
		Pattern validNamePattern = Pattern.compile("[a-zA-Z]+( )?([a-zA-Z]+)?");
		return validNamePattern.matcher(name).matches();
	}

	/*
	 * Tells whether a string is profane or not.
	 */
	public static boolean isProfane(String ss)
	{
		String s = ss.toLowerCase();
		for (String p : profanityList)
		{
			if (s.contains(p))
				return true;
		}
		return false;
	}

	/*
	 * List of disallowed words.
	 */
	private static ArrayList<String> profanityList = new ArrayList<>();
	static
	{
		for (String s : new String[] { "shit", "fuck", "ass", "bitch", "tits", "nigger", "anus", "hole", "titty",
				"tittie", "whore", "cunt", "penis", "dick", "cock", "wank", "crap", "poop", "turd", "twat", "jizz",
				"cum", "semen", "pecker", "stiffy", "erection", "damn", "butt", "gay", "slut", "queer", "fag", "urine",
				"piss", "pee", "masturbat", "fap", "fart" })
			profanityList.add(s);
	}

	/*
	 * Takes a non-zero integer from the standard input
	 */
	public static int getInt(int max)
	{
		int i = 0;
		while (i == 0)
		{
			try
			{
				i = Integer.parseInt(ClientMain.main.gui.keyboardInput.nextLine());
			}
			catch (NumberFormatException e)
			{
				ClientMain.main.gui.writeMessage("Please enter an integer.");
			}
			if (i <= 0)
			{
				ClientMain.main.gui.writeMessage("Must be greater than 0.");
				i = 0;
			}
			else if (i > max)
			{
				ClientMain.main.gui.writeMessage("That's too high.");
				i = 0;
			}
		}
		return i;
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
	public static Direction[] cardinalDirections = { Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST };

	/*
	 * Represents each relevant compass direction.
	 */
	public enum Direction
	{
		NORTH, SOUTH, EAST, WEST, NORTHEAST, NORTHWEST, SOUTHEAST, SOUTHWEST;

		public Direction opposite()
		{
			switch (this)
			{
				case EAST:
					return WEST;
				case NORTH:
					return SOUTH;
				case NORTHEAST:
					return SOUTHWEST;
				case NORTHWEST:
					return SOUTHEAST;
				case SOUTH:
					return NORTH;
				case SOUTHEAST:
					return NORTHWEST;
				case SOUTHWEST:
					return NORTHEAST;
				case WEST:
					return EAST;
				default:
					throw new RuntimeException("Unreachable code.");
			}
		}
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