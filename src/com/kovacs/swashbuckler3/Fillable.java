package com.kovacs.swashbuckler3;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import com.kovacs.swashbuckler.Utility;

/*
 * This class is basically a map. An unfilled map entry can either be an
 * instance of the appropriate class, or it can be a Class corresponding to the
 * value that needs to be filled in.
 */
public class Fillable
{
	/*
	 * Map that contains all the fillable mappings.
	 */
	private HashMap<String, Object> map = new HashMap<>();

	public Fillable(ArrayList<SimpleEntry<String, Object>> items)
	{
		for (SimpleEntry<String, Object> item : items)
			map.put(item.getKey(), item.getValue());
	}

	/*
	 * Fills in the item corresponding to a string with an object.
	 */
	public Fillable fill(String s, Object o)
	{
		Object m = map.get(s);
		if (m == null)
			throw new RuntimeException("You cannot fill an atribute which does not already exist in a fillable.");
		if (!m.equals(o.getClass()) && !m.getClass().equals(o.getClass()))
			throw new RuntimeException("You cannot fill a request for <" + Utility.getSimpleClassNameIfClass(m)
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

	@Override
	public String toString()
	{
		return map.toString();
	}
}