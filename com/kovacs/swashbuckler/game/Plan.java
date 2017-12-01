package com.kovacs.swashbuckler.game;

/*
 * This data object represents a single turn for a single pirate.
 */
public class Plan
{
	/*
	 * The turn number of the plan (1-15).
	 */
	private int turn;

	/*
	 * Data that holds the orders assigned in this plan
	 */
	private Order[] orders;

	/*
	 * Builds a plan out of the given set of orders. If the plan is not valid,
	 * returns null. This method does not guarantee that the plan is valid given
	 * the current game state, only that it is generally valid under no
	 * assumptions.
	 */
	// TODO: it is the server's job to handle validation of carry-over steps of
	// required rest.
	// TODO: it is the server's job to determine illegal actions based on
	// character state and validate plans against these conditions. The client
	// should be warned and asked to resubmit their plan in any case where the
	// pre-resolution plan is invalid.
	// TODO: it is the server's job to handle carry-over rest
	public static Plan buildPlan(int turn, Order... orders)
	{
		int stepsSum = 0;
		for (int i = 0; i < orders.length; i++)
		{
			if (i == orders.length - 1)
				if (stepsSum + 1 > 6)
					return null;
			stepsSum += orders[i].getRests() + 1;
		}
		Order[] steps = new Order[6];
		int stepsIndex = 0;
		for (Order order : orders)
		{
			steps[stepsIndex] = order;
			for (int i = 0; i < order.getRests(); i++)
			{
				if (stepsIndex >= 5)
					break;
				stepsIndex++;
				steps[stepsIndex] = Order.REST;
			}
			if (stepsIndex >= 5)
				break;
			stepsIndex++;
		}
		return new Plan(turn, steps);
	}

	private Plan(int turn, Order[] orders)
	{
		this.turn = turn;
		this.orders = orders;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder("Turn " + turn + ": | ");
		for (Order order : orders)
		{
			sb.append(order.getAbbreviation()).append(" | ");
		}
		return sb.toString();
	}
}