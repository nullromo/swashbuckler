package com.kovacs.swashbuckler3;

import java.util.ArrayList;
import com.kovacs.swashbuckler.Utility;
import com.kovacs.swashbuckler3.Request.RequestStatus;

/*
 * This class is instantiated by the engine. It handles communication between
 * the engine and the Player threads.
 */
public class InformationRequester
{
	/*
	 * A reference to the list of all players from the engine.
	 */
	private ArrayList<Player> players;

	/*
	 * A list of all the requests that are to be filled.
	 */
	private ArrayList<Request> requests = new ArrayList<>();

	/*
	 * Tells whether the information requester has been flushed. It must be
	 * flushed after each round of use.
	 */
	private boolean flushed = true;

	public InformationRequester(int numPlayers)
	{
		players = new ArrayList<>();
		for (int i = 0; i < numPlayers; i++)
			players.add(new Player());
		for (Player p : players)
			p.start();
	}

	/*
	 * Closes the InformationRequester and shuts down all the player threads.
	 */
	public void close()
	{
		for (Player p : players)
			p.stop();
	}

	/*
	 * Sets up a given request for filling.
	 */
	public void request(Request request)
	{
		assertFlushed();
		requests.add(request);
	}

	/*
	 * Collects all responses and returns when everything has been filled and
	 * validated. This is how the engine gathers results.
	 */
	public void collect()
	{
		assertFlushed();
		flushed = false;
		for (Request request : requests)
			request.send();
		while (!isComplete())
		{
			Utility.sleep(10);
			for (Player player : players)
			{
				if (!player.hasResponse())
					continue;
				Request r = player.take();
				System.out.println("InformationRequester got request: " + r);
				if (r == null)
					throw new RuntimeException("InformationRequester got a null request from a player.");
				check(r);
				player.put(r);
			}
		}
	}

	/*
	 * Mutates the request according to what was wrong. Sets it to FILLED if
	 * everything is okay.
	 */
	private void check(Request request)
	{
		request.errorMessage = "";
		if (request.getTarget() instanceof PirateData)
		{
			PirateData p = PirateData.parseRequest(request);
			if (p.getBody() <= 0 || p.getHead() <= 0 || p.getLeftArm() <= 0 || p.getRightArm() <= 0)
				request.errorMessage += "Each area must have a positive number of hit points. ";
			if (Math.abs((p.getLeftArm() - p.getRightArm())) > 1)
				request.errorMessage += "The difference between the left and right arms cannot exceed 1 hit point. ";
			if (p.getBody() + p.getHead() + p.getLeftArm() + p.getRightArm() != p.getConstitution())
				request.errorMessage += "The sum of all hit point areas must equal the pirate's constitution ("
						+ p.getConstitution() + "). ";
			if (!p.nameValid())
				request.errorMessage += "Pirate names must be between 2 and 32 letter-only characters, and must consist of one or two non-profane words. ";
		}
		if (request.errorMessage.equals(""))
			request.requestStatus = RequestStatus.FILLED;
		else
		{
			request.requestStatus = RequestStatus.ERROR;
			request.getFillable().reset();
		}
	}

	/*
	 * Flushes the InformationRequester.
	 */
	public void flush()
	{
		flushed = true;
		requests = new ArrayList<>();
	}

	/*
	 * Asserts that the InformationRequester is flushed.
	 */
	private void assertFlushed()
	{
		if (!flushed)
			throw new RuntimeException("The InformationRequester must be flushed before it is used again.");
	}

	/*
	 * Returns true if all the requests are filled and all players are done
	 * responding.
	 */
	private boolean isComplete()
	{
		for (Request r : requests)
			if (!r.isComplete())
				return false;
		for (Player p : players)
			if (p.hasResponse())
				return false;
		return true;
	}

	public ArrayList<Request> getRequests()
	{
		return requests;
	}

	public ArrayList<Player> getPlayers()
	{
		return players;
	}
}