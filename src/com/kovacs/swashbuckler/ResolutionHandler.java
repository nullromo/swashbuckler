package com.kovacs.swashbuckler;

import java.util.HashMap;
import com.kovacs.swashbuckler.game.Board;
import com.kovacs.swashbuckler.game.Plan;
import com.kovacs.swashbuckler3.Order;

/*
 * Decides what actions to take to resolve a turn.
 * @formatter:off
 * 1. check priority - get list of actions to be performed (1 or more) and mark as completed
 * 2. check for option responses
 * 3. execute actions
 * 4. report to all players
 * 5. check for re-plot responses
 * 6. update plots
 * 7. continue
 * @formatter:on
 */
public class ResolutionHandler
{
	/*
	 * The board that was given in the beginning of the turn, and will be handed
	 * back at the end.
	 */
	private Board board;
	
	/*
	 * Maps pirate names to thier plans for this turn.
	 */
	HashMap<String, Order[]> plans;
	
	/*
	 * Maps pirate names to their resolution progress.
	 */
	HashMap<String, Integer> resolutionProgress;

	/*
	 * Creates a resolution handler that takes the game's board and the plan
	 * history at the beginning of the turn and, based on which turn it is,
	 * resolves all the planned orders.
	 */
	public ResolutionHandler(Board board, HashMap<String, Plan[]> planHistory, int turn)
	{
		this.board = board;
		for(String name: planHistory.keySet())
		{
			resolutionProgress.put(name, 0);
			plans.put(name, planHistory.get(name)[turn].getOrders());
		}
	}
	
	public Board resolve()
	{
		//TODO: do everything.
		return board;
	}
}