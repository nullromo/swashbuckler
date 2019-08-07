package com.kovacs.swashbuckler3;

/*
 * Arguments for PlanData.
 */
public class PlanDataArgs extends RequestArgs
{
	/*
	 * Default version.
	 */
	private static final PlanDataArgs defaultArgs = new PlanDataArgs(0);

	/*
	 * Override.
	 */
	public static RequestArgs defaultArgs()
	{
		return defaultArgs;
	}

	/*
	 * Arg 1.
	 */
	int initialRests;

	public PlanDataArgs(int initialRests)
	{
		this.initialRests = initialRests;
	}
}