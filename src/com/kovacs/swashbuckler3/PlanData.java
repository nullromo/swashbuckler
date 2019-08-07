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

	/*
	 * Number of initial rest steps that are carried over into this plan.
	 */
	private final int requiredInitialRests;

	public PlanData(PlanDataArgs args)
	{
		for (int i = 0; i < 6; i++)
			orders[i] = Order.UNPLANNED;
		this.requiredInitialRests = args.initialRests;
	}

	public PlanData()
	{
		this(new PlanDataArgs(0));
	}

	@Override
	protected Request createRequestInternal(Player player)
	{
		ArrayList<SimpleEntry<String, Object>> requestedItems = new ArrayList<>();
		for (int i = 0; i < 6; i++)
		{
			requestedItems.add(new SimpleEntry<String, Object>("Step " + (i + 1),
					i + 1 <= requiredInitialRests ? Order.REST : Order.UNPLANNED));
		}
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
	public static Request createRequest(Player player, RequestArgs args)
	{
		return Requestable.createRequest(PlanData.class, player, args);
	}
	
	public static Request createRequest(Player player)
	{
		return Requestable.createRequest(PlanData.class, player, PlanDataArgs.defaultArgs());
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

	public Order[] getOrders()
	{
		return orders;
	}

	public int getInitialRests()
	{
		return requiredInitialRests;
	}
}