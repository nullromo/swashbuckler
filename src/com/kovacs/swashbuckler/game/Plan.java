package com.kovacs.swashbuckler.game;

import java.io.Serializable;
import java.util.ArrayList;
import com.kovacs.swashbuckler.ServerMain;

/*
 * This data object represents a single turn for a single pirate.
 */
public class Plan implements Serializable
{
	private static final long serialVersionUID = -59384977547432959L;

	/*
	 * The turn number of the plan (1-MAX_TURNS).
	 */
	private int turn;

	/*
	 * The number of steps of rest that this plan bleeds over into the next
	 * turn.
	 */
	private int carryOverRests;

	/*
	 * Holds the name of the pirate this plan applies to
	 */
	private String pirateName;

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
	// TODO: it is the server's job to determine illegal actions based on
	// character state and validate plans against these conditions. The client
	// should be warned and asked to resubmit their plan in any case where the
	// pre-resolution plan is invalid.
	public static Plan buildPlan(int turn, String pirateName, Order... orders)
	{
		ArrayList<Order> expandedPlan = new ArrayList<>();
		for (Order order : orders)
		{
			expandedPlan.add(order);
			for (int i = 0; i < order.getRests(); i++)
				expandedPlan.add(Order.REST);
		}
		if (expandedPlan.size() < 6)
			return null;
		int carryOverRests = 0;
		while (expandedPlan.size() > 6)
		{
			Order lastElement = expandedPlan.get(expandedPlan.size() - 1);
			if (lastElement == Order.REST)
			{
				expandedPlan.remove(expandedPlan.size() - 1);
				carryOverRests++;
			}
			else
				return null;
		}
		Order[] builtPlan = new Order[6];
		for (int i = 0; i < expandedPlan.size(); i++)
			builtPlan[i] = expandedPlan.get(i);
		return new Plan(turn, pirateName, builtPlan, turn == ServerMain.MAX_TURNS ? 0 : carryOverRests);
	}

	private Plan(int turn, String pirateName, Order[] orders, int carryOverRests)
	{
		this.turn = turn;
		this.orders = orders;
		this.pirateName = pirateName;
		this.carryOverRests = carryOverRests;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder("Turn " + turn + ": | ");
		for (Order order : orders)
			sb.append(order.getAbbreviation()).append(" | ");
		return sb.append("+ " + carryOverRests).toString();
	}

	public String getPirateName()
	{
		return pirateName;
	}

	public int getCarryOverRests()
	{
		return carryOverRests;
	}

	public int getTurn()
	{
		return turn;
	}

	public Order[] getOrders()
	{
		return orders;
	}
}