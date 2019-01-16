package com.kovacs.swashbuckler3;

/*
 * This class is instantiated by the engine when information is needed. The
 * needed information is defined in terms of a list of fillable questions. The
 * general paradigm for gathering information is defined by the 3-step process
 * in the collect() function.
 */
public class InformationRequester
{
	/*
	 * List of requests to be filled.
	 */
	private Request[] requests;

	/*
	 * This is how the engine makes new requests.
	 */
	public static Request[] request(Request... requests)
	{
		return new InformationRequester(requests).collect();
	}

	private InformationRequester(Request... requests)
	{
		this.requests = requests;
	}

	/*
	 * Collects all responses and returns when everything has been filled and
	 * validated. This is how the engine gathers results.
	 */
	private Request[] collect()
	{
		while (!isComplete())
		{
			sendRequests();
			waitForResponse();
			checkResponse();
		}
		return requests;
	}

	/*
	 * Creates all required requests and sends them to the players.
	 */
	private void sendRequests()
	{
		for (Request r : requests)
			if (!r.isComplete() && !r.isPending())
				r.send();
	}

	/*
	 * Blocks until a response comes back for any of the sent requests.
	 */
	private void waitForResponse()
	{
	}

	/*
	 * Validates responses for legality and correctness.
	 */
	private void checkResponse()
	{
	}

	/*
	 * Returns true if all the requests are filled.
	 */
	private boolean isComplete()
	{
		for (Request r : requests)
			if (!r.isComplete())
				return false;
		return true;
	}
}