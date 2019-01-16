package com.kovacs.swashbuckler3;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Set;

/*
 * This class is a hash map that only allows fields to be filled once. Any
 * uninitialized fields are null, but once they are filled they cannot be
 * modified. The fillable is marked as filled when none of the fields are null.
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
	 * Creates Fillables from classes.
	 */
	public static class FillableFactory
	{
		/*
		 * Creates a Fillable based on the fields of a given class.
		 */
		public static Fillable createFillable(Class<?> c)
		{
			Field[] fields = c.getDeclaredFields();
			RequestedItem[] requestedItems = new RequestedItem[fields.length];
			for (int i = 0; i < fields.length; i++)
				requestedItems[i] = fieldToItem(fields[i]);
			return new Fillable(requestedItems);
		}

		/*
		 * Converts a Field object into a new RequestedItem object.
		 */
		private static RequestedItem fieldToItem(Field f)
		{
			String name = f.getName();
			Class<?> type = f.getType();
			// If the initial field was of a primitive type, then the reflection
			// will return that primitive type. This is a problem, because we
			// need the boxed type. In order to get the boxed type from the
			// primitive type, we create an array of length 1 of that primitive
			// type. This array will initialize to something like [0]. Then
			// extracting the first element of that array will auto-box the
			// primitive 0 into a boxed version, like Integer(0). From there
			// getClass() will give us the boxed class for the corresponding
			// primitive type.
			if (type.isPrimitive())
				type = Array.get(Array.newInstance(type, 1), 0).getClass();
			return new RequestedItem(name, type);
		}
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