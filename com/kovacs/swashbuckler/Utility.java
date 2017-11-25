package com.kovacs.swashbuckler;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

/*
 * Contains general utilities and things that aren't really specific to a single
 * class or are used all over.
 */
public class Utility
{
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