package com.kovacs.swashbuckler.game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import com.kovacs.swashbuckler3.Engine;
import com.kovacs.swashbuckler3.Order;

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
	 * Tells whether the plan has been submitted and accepted by the server
	 */
	private boolean locked = false;

	/*
	 * Generates an unplanned plan. Used for initializing plan histories.
	 */
	public static Plan createUnplannedPlan(int turn)
	{
		return expandAndBuildPlan(turn, "", Order.UNPLANNED, Order.UNPLANNED, Order.UNPLANNED, Order.UNPLANNED,
				Order.UNPLANNED, Order.UNPLANNED);
	}

	/*
	 * Builds a plan out of the given set of orders. The orders do not include
	 * mandatory rests. Rests are expanded before the plan is validated. If the
	 * plan is not valid, returns null. This method does not guarantee that the
	 * plan is valid given the current game state, only that it is generally
	 * valid under no assumptions.
	 */
	// TODO: it is the server's job to determine illegal actions based on
	// character state and validate plans against these conditions. The client
	// should be warned and asked to resubmit their plan in any case where the
	// pre-resolution plan is invalid. This is for security reasons. In other
	// words, if somebody is trying to cheat, the server should recognize this.
	public static Plan expandAndBuildPlan(int turn, String pirateName, Order... orders)
	{
		ArrayList<Order> expandedPlan = new ArrayList<>();
		for (Order order : orders)
		{
			expandedPlan.add(order);
			for (int i = 0; i < order.getRests(); i++)
				expandedPlan.add(Order.REST);
		}
		return buildPlan(turn, pirateName, expandedPlan);
	}

	/*
	 * Builds a new plan out of orders that already include mandatory rests.
	 * Same as expandAndBuildPlan, but with the rests already included.
	 */
	public static Plan buildPlan(int turn, String pirateName, List<Order> expandedPlan)
	{
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
		return new Plan(turn, pirateName, builtPlan, turn == Engine.MAX_TURNS ? 0 : carryOverRests);
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

	public void addCarryOverRest()
	{
		carryOverRests++;
	}

	public void resetCarryOverRests()
	{
		carryOverRests = 0;
	}

	public int getTurn()
	{
		return turn;
	}

	public Order[] getOrders()
	{
		return orders;
	}

	public void lock()
	{
		locked = true;
	}

	public boolean isLocked()
	{
		return locked;
	}
}