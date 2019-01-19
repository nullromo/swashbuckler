package com.kovacs.swashbuckler3;

import java.util.HashMap;
import java.util.Set;

/*
 * This class is a hash map that only allows fields to be filled once. Any
 * uninitialized fields are instances of a Class object corresponding to the
 * information needed. Once they are filled they cannot be modified. The
 * fillable is marked as filled when none of the fields are Classes.
 */
public class Fillable
{
	/*
	 * Map that contains all the fillable mappings.
	 */
	private HashMap<String, Object> map = new HashMap<>();

	public Fillable(RequestedItem... items)
	{
		for (RequestedItem item : items)
			map.put(item.name, item.value);
	}

	/*
	 * Fills in the item corresponding to a string with an object.
	 */
	public Fillable fill(String s, Object o)
	{
		if (map.get(s) == null)
			throw new RuntimeException("You cannot fill an atribute which does not already exist in a fillable.");
		if (!(map.get(s) instanceof Class<?>))
			throw new RuntimeException("You cannot fill an already-filled attribute of a Fillable.");
		if (!map.get(s).equals(o.getClass()))
			throw new RuntimeException("You cannot fill a request for <" + ((Class<?>) map.get(s)).getSimpleName()
					+ "> with an instance of <" + o.getClass().getSimpleName() + ">.");
		map.put(s, o);
		return this;
	}

	/*
	 * Returns the object that has been filled for a given string.
	 */
	public Object get(String s)
	{
		return map.get(s);
	}

	/*
	 * Returns the set of all keys in the fillable.
	 */
	public Set<String> getKeys()
	{
		return map.keySet();
	}

	/*
	 * Returns true if the entire fillable is filled.
	 */
	public boolean isFilled()
	{
		for (Object o : map.values())
			if (o instanceof Class<?>)
				return false;
		return true;
	}

	@Override
	public String toString()
	{
		return map.toString();
	}
}