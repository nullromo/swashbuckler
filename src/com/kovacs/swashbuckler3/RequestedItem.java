package com.kovacs.swashbuckler3;

/*
 * A requested item is just a single piece of a fillable. It is simply a pair of
 * <name, class> that will need to be filled in. If the value field is a class,
 * then it means this is waiting to be filled with an instance of that class. If
 * it is a non-class object, then it is already filled.
 */
public class RequestedItem
{
	public String name;
	public Object value;

	public RequestedItem(String name, Object value)
	{
		this.name = name;
		this.value = value;
	}
}