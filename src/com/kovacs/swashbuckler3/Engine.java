package com.kovacs.swashbuckler3;

import java.util.ArrayList;
import java.util.Arrays;
import com.kovacs.swashbuckler.game.entity.Pirate;
import com.kovacs.swashbuckler3.PirateData.Dexterity;

/*
 * Core engine of the game. This runs on the server side. It contains all the
 * information and does all the work specifically related to running the game
 * itself.
 */
public class Engine implements Runnable
{
	/*
	 * The number of turns in a game.
	 */
	public static final int MAX_TURNS = 15;

	/*
	 * Number of pirates each player controls.
	 */
	public static final int PIRATES_PER_PLAYER = 1;

	/*
	 * Keeps track of the current game turn and step.
	 */
	private int currentTurn, currentStep;

	/*
	 * List of all players in the game.
	 */
	private ArrayList<Player> players;

	/*
	 * Temporary main method to run the engine for testing TODO: remove this
	 * eventually.
	 */
	public static void main(String[] args)
	{
		Engine e = new Engine();
		e.setup();
		new Thread(e).start();
		e.cleanup();
	}

	public Engine()
	{
		players = new ArrayList<>();
		// TODO: somehow acquire players
		players.add(new Player());
	}

	/*
	 * Performs setup actions, like gathering players, initializing the board,
	 * etc.
	 */
	private void setup()
	{
		for (Player p : players)
		{
			for (int i = 0; i < PIRATES_PER_PLAYER; i++)
			{
				Request pirateData = new PirateData().createRequest(p);
				Request[] filledPirateData = InformationRequester.request(pirateData);
			}

		}
		// TODO: do something with the filled requests
	}

	/*
	 * Performs cleanup actions, like freeing resources, displaying endgame
	 * messages, resetting, etc.
	 */
	private void cleanup()
	{
		System.exit(0);
	}

	/*
	 * Runs the game. One completion of this function constitutes one entire
	 * game from start to finish.
	 */
	@Override
	public void run()
	{
		for (currentTurn = 1; currentTurn <= MAX_TURNS; currentTurn++)
		{
			acquirePlans();
			for (currentStep = 1; currentStep <= 6; currentStep++)
				resolveStep();
		}
		System.out.println("Game completed");
	}

	/*
	 * Acquires all plans from all players.
	 */
	private void acquirePlans()
	{
		// Request[] plans = InformationRequester.request();
		// TODO: do something with the plans
		System.out.println("Plans aquired for turn " + currentTurn + ".");
	}

	/*
	 * Resolves an individual step of the game.
	 */
	private void resolveStep()
	{
		// while(there are actions to resolve)
		// {
		// pick next action
		// Request[] decisions = InformationRequester.request()z;
		// TODO: do something with the decisions
		// }
		// resolve action
		System.out.println("Step " + currentStep + " of turn " + currentTurn + " resolved.");
	}
}