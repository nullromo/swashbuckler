package com.kovacs.swashbuckler3;

/*
 * Arguments for PirateData.
 */
public class PirateDataArgs extends RequestArgs
{
	/*
	 * Default version.
	 */
	private static final PirateDataArgs defaultArgs = new PirateDataArgs();

	/*
	 * Override.
	 */
	public static PirateDataArgs defaultArgs()
	{
		return defaultArgs;
	}
}