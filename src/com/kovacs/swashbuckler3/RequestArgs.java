package com.kovacs.swashbuckler3;

/*
 * A wrapper for arguments for requestable constructors.
 */
public abstract class RequestArgs
{
	/*
	 * RequestArgs instances must define a default form.
	 */
	public static RequestArgs defaultArgs()
	{
		throw new RuntimeException("Subclasses of RequestArgs must implement a defaultArgs method.");
	}
}