package com.kovacs.swashbuckler3;

import java.util.ArrayList;
import com.kovacs.swashbuckler.Utility;

/*
 * This class is instantiated by the engine. It handles communication between
 * the engine and the Player threads.
 */
public class InformationRequester
{
	/*
	 * A reference to the list of all players from the engine.
	 */
	private ArrayList<Player> players = new ArrayList<>();

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
				if (!r.message.equals(""))
				{
					r.reset();
					player.put(r);
				}
			}
		}
	}

	/*
	 * Sets the requests's message field to an error message or an empty string
	 * if it passed all checks. Also parses the request.
	 */
	private void check(Request request)
	{
		request.message = "";
		if (request.getTarget() instanceof PirateData)
		{
			PirateData p = PirateData.parseRequest(request);
			if (p.getBody() <= 0 || p.getHead() <= 0 || p.getLeftArm() <= 0 || p.getRightArm() <= 0)
				request.message += "Each area must have a positive number of hit points. ";
			if (Math.abs((p.getLeftArm() - p.getRightArm())) > 1)
				request.message += "The difference between the left and right arms cannot exceed 1 hit point. ";
			if (p.getBody() + p.getHead() + p.getLeftArm() + p.getRightArm() != p.getConstitution())
				request.message += "The sum of all hit point areas must equal the pirate's constitution ("
						+ p.getConstitution() + "). ";
			if (!p.nameValid())
				request.message += "Pirate names must be between 2 and 32 letter-only characters, and must"
						+ " consist of one or two non-profane words. ";
		}
		else if (request.getTarget() instanceof PlanData)
		{
			PlanData p = PlanData.parseRequest(request);
			for (int i = 0; i < 6; i++)
			{
				Order order = p.getOrders()[i];
				if (i < p.getInitialRests() && order != Order.REST)
					request.message += "Because " + p.getInitialRests() + " are carried over, step " + (i + 1)
							+ " must be a rest. ";
				if (order == Order.UNPLANNED)
				{
					request.message += "Step " + (i + 1) + " has not been planned. ";
					break;
				}
				for (int restStep = 0; restStep < order.getRests(); restStep++)
				{
					try
					{
						if (p.getOrders()[i + 1 + restStep] != Order.REST)
							request.message += "Because step " + (i + 1) + " is " + order + ", step "
									+ (i + 2 + restStep) + " must be a rest. ";
					}
					catch (ArrayIndexOutOfBoundsException e)
					{
						;
					}
				}
			}
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
			if (!r.isSubmitted())
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