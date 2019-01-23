package com.kovacs.swashbuckler3;

import java.util.ArrayList;
import com.kovacs.swashbuckler3.Request.RequestStatus;

/*
 * This class is instantiated by the engine when information is needed. The
 * needed information is defined in terms of a list of fillable questions. The
 * general paradigm for gathering information is defined by the 3-step process
 * in the collect() function.
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

	public InformationRequester(ArrayList<Player> players)
	{
		this.players = players;
		for (Player p : players)
			p.start();
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
			for (Player player : players)
			{
				if (!player.hasResponse())
					continue;
				Request r = player.take();
				System.out.println("InformationRequester got request: " + r);
				if (r == null)
					continue;
				check(r);
				player.put(r);
			}
		}
	}

	/*
	 * Mutates the request according to what was wrong. Sets it to FILLED if
	 * everything is okay.
	 */
	// TODO: this method.
	private void check(Request request)
	{
		request.message = "";
		if (request.getTarget() instanceof PirateData)
		{
			PirateData pirate = (PirateData) Requestable.parseRequest(request);
			if (pirate.getBody() <= 0 || pirate.getHead() <= 0 || pirate.getLeftArm() <= 0 || pirate.getRightArm() <= 0)
			{
				request.message += "Each area must have a positive number of hit points. ";
				request.requestStatus = RequestStatus.ERROR;
			}
			if (Math.abs((pirate.getLeftArm() - pirate.getRightArm())) > 1)
			{
				request.message += "The difference between the left and right arms cannot exceed 1 hit point. ";
				request.requestStatus = RequestStatus.ERROR;
			}
			if (pirate.getBody() + pirate.getHead() + pirate.getLeftArm() + pirate.getRightArm() != pirate
					.getConstitution())
			{
				request.message += "The sum of all hit point areas must equal the pirate's constitution ("
						+ pirate.getConstitution() + "). ";
				request.requestStatus = RequestStatus.ERROR;
			}
			if(pirate.getName().length() > 32 || pirate.getName().length() < 2)
			{
				request.message += "Pirate's names must be between 2 and 32 characters. ";
				request.requestStatus = RequestStatus.ERROR;
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
	 * Returns true if all the requests are filled.
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
}