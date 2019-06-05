package com.kovacs.swashbuckler3;

import java.util.ArrayList;
import java.util.AbstractMap.SimpleEntry;

/*
 * Contains orders for a turn.
 */
public class PlanData extends Requestable
{
	/*
	 * Contains 6 orders for the turn.
	 */
	private Order[] orders = new Order[6];

	public PlanData()
	{
		for (int i = 0; i < 6; i++)
			orders[i] = Order.UNPLANNED;
	}

	@Override
	protected Request createRequestInternal(Player player)
	{
		ArrayList<SimpleEntry<String, Object>> requestedItems = new ArrayList<>();
		requestedItems.add(new SimpleEntry<String, Object>("Step 1", Order.class));
		requestedItems.add(new SimpleEntry<String, Object>("Step 2", Order.class));
		requestedItems.add(new SimpleEntry<String, Object>("Step 3", Order.class));
		requestedItems.add(new SimpleEntry<String, Object>("Step 4", Order.class));
		requestedItems.add(new SimpleEntry<String, Object>("Step 5", Order.class));
		requestedItems.add(new SimpleEntry<String, Object>("Step 6", Order.class));
		return new Request(player, this, new Fillable(requestedItems));
	}

	@Override
	protected Requestable parseRequestInternal(Request r)
	{
		Fillable source = r.getFillable();
		for (int i = 0; i < 6; i++)
			orders[i] = (Order) source.get("Step " + (i + 1));
		return this;
	}

	/*
	 * Convenience method for creating requests.
	 */
	public static Request createRequest(Player player)
	{
		return Requestable.createRequest(PlanData.class, player);
	}

	/*
	 * Convenience method for parsing requests.
	 */
	public static PlanData parseRequest(Request r)
	{
		return (PlanData) Requestable.parseRequest(r);
	}

	@Override
	public String toString()
	{
		String s = "[" + orders[0];
		for (int i = 1; i < 6; i++)
			s += ", " + orders[i];
		return s + "]";
	}
}