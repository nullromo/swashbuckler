package com.kovacs.swashbuckler3;

/*
 * Classes that are requestable are things that the server might need to ask
 * players for.
 */
public abstract class Requestable
{
	/*
	 * This method should create a Fillable for the unfilled fields. The
	 * instance and the Fillable are packaged into a Request and returned.
	 */
	protected abstract Request createRequestInternal(Player player);

	/*
	 * This method takes a request and returns an instance of the implementing
	 * class. It uses the Fillable in the request to fill out the Request's
	 * target. The target is modified and can then be simply extracted from the
	 * request via the getTarget() method.
	 */
	protected abstract Requestable parseRequestInternal(Request r);

	/*
	 * This is how you use the function externally.
	 */
	public static Request createRequest(Class<? extends Requestable> type, Player player)
	{
		Request r = null;
		try
		{
			r = type.newInstance().createRequestInternal(player);
		}
		catch (InstantiationException | IllegalAccessException e)
		{
			e.printStackTrace();
		}
		return r;
	}

	/*
	 * This is how you use the function externally.
	 */
	public static Requestable parseRequest(Request r)
	{
		if (!r.isSubmitted())
			throw new RuntimeException("You cannot parse an incomplete or unsubmitted request.");
		return r.getTarget().parseRequestInternal(r);
	}
}