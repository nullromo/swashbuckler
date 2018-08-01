package com.kovacs.swashbuckler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import com.kovacs.swashbuckler.game.Board;
import com.kovacs.swashbuckler.game.BoardCoordinate;
import com.kovacs.swashbuckler.game.Order;
import com.kovacs.swashbuckler.game.Plan;
import com.kovacs.swashbuckler.game.entity.Entity;
import com.kovacs.swashbuckler.game.entity.Pirate;
import com.kovacs.swashbuckler.packets.BoardPacket;
import com.kovacs.swashbuckler.packets.InvalidPirateNamePacket;
import com.kovacs.swashbuckler.packets.MessagePacket;
import com.kovacs.swashbuckler.packets.NewConnectionPacket;
import com.kovacs.swashbuckler.packets.NextTurnPacket;
import com.kovacs.swashbuckler.packets.Packet;
import com.kovacs.swashbuckler.packets.PirateAcceptedPacket;
import com.kovacs.swashbuckler.packets.PlanAcceptedPacket;
import com.kovacs.swashbuckler.packets.RequestPacket;
import com.kovacs.swashbuckler.packets.ResponsePacket;

/*
 * This is the main server-side application. It hosts and array list of
 * connection objects and routes all information to each client. It updates the
 * clients with the appropriate data and game actions, while requesting
 * information from clients and handling the outcomes. It is responsible for
 * handling all game logic.
 */
// TODO: split this class into smaller ones.
public class ServerMain
{
	// TODO: make a configurable GUI for starting the server, so that the server
	// runner can decide how many pirates, etc.
	/*
	 * The number of players in a game.
	 */
	private static int NUM_PLAYERS = 2;

	/*
	 * The number of pirates each player controls.
	 */
	public static int PIRATES_PER_PLAYER = 1;

	/*
	 * The number of turns in a game.
	 */
	public static final int MAX_TURNS = 15;

	/*
	 * This is the single instance of the main class.
	 */
	public static ServerMain main;

	/*
	 * This is the list of all clients' I/O interfaces.
	 */
	private ArrayList<Connection> connections;

	/*
	 * This should turn to false on shutdown.
	 */
	private boolean running = true;

	/*
	 * The main board that represents the game
	 */
	private Board board;

	/*
	 * Holds all plans for all pirates. Maps pirate names to arrays of their
	 * plans.
	 */
	private HashMap<String, Plan[]> planHistory;

	/*
	 * Keeps track of the current game turn.
	 */
	private int currentTurn;

	/*
	 * The thread that accepts clients.
	 */
	private Thread clientAcceptThread;

	/*
	 * The socket that the server runs on.
	 */
	private ServerSocket serverSocket;

	/*
	 * Tells whether or not the game is still accepting more clients.
	 */
	private boolean acceptingClients = true;

	/*
	 * The gui for the server.
	 */
	private ServerGUI gui;

	private ServerMain()
	{
		connections = new ArrayList<Connection>();
		gui = new ServerGUI();
		planHistory = new HashMap<>();
		board = new Board();
		// TODO: This thread just runs and accepts clients all the time. It
		// needs to start games properly and such.
		clientAcceptThread = new Thread()
		{
			@Override
			public void run()
			{
				try
				{
					serverSocket = new ServerSocket(8004);
					while (acceptingClients)
					{
						gui.write("Waiting for connections...");
						Socket s = serverSocket.accept();
						gui.write("A client has connected from " + s.getInetAddress().getHostAddress() + ":"
								+ s.getPort());
						connections.add(new Connection(s));
					}
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		};
	}

	/*
	 * The program starts off by launching a client-accepting thread and a
	 * response-accepting thread, and then running the run method.
	 */
	public static void main(String[] args)
	{
		main = new ServerMain();
		// main.gui.write("External IP: " + Utility.getExternalIPAddress());
		main.clientAcceptThread.start();
		main.run();
	}

	/*
	 * Stops the client accept thread.
	 */
	private void stopAcceptingClients()
	{
		acceptingClients = false;
	}

	/*
	 * The main loop that is responsible for routing.
	 */
	private void run()
	{
		while (main.running)
		{
			for (int i = 0; i < connections.size(); i++)
			{
				Connection c = connections.get(i);
				if (c.hasData())
				{
					Packet packet = c.nextPacket();
					packet.tag(c);
					handleResponse(packet);
				}
			}
			try
			{
				Thread.sleep(2);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		try
		{
			serverSocket.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/*
	 * The main switch that takes care of incoming packets from clients.
	 */
	private void handleResponse(Packet packet)
	{
		if (packet instanceof NewConnectionPacket)
		{
			if (connections.size() == NUM_PLAYERS - 1)
				stopAcceptingClients();
			packet.getConnection().write(new MessagePacket("Welcome to Swashbuckler."));
			packet.getConnection().write(new MessagePacket("To play, you will need to create " + PIRATES_PER_PLAYER
					+ " pirate" + (PIRATES_PER_PLAYER == 1 ? "" : "s") + "."));
			for (int i = 0; i < PIRATES_PER_PLAYER; i++)
				packet.getConnection().write(new RequestPacket(Pirate.class));
		}
		else if (packet instanceof ResponsePacket)
			handleObjectPacket((ResponsePacket<?>) packet);
		else
			Utility.typeError(packet.getClass());
	}

	/*
	 * Handles incoming objects.
	 */
	private void handleObjectPacket(ResponsePacket<?> packet)
	{
		// TODO: put a lot more logging print lines in the server.
		if (packet.getType() == Pirate.class)
		{
			Pirate pirate = (Pirate) packet.getObject();
			for (Entity entity : board.allEntities(Pirate.class))
				if (((Pirate) entity).getName().equals(pirate.getName()))
				{
					packet.getConnection().write(new InvalidPirateNamePacket(pirate));
					return;
				}
			placePirate(pirate);
			packet.getConnection().write(new PirateAcceptedPacket(pirate));
		}
		else if (packet.getType() == Plan.class)
		{
			Plan plan = (Plan) packet.getObject();
			plan.lock();
			addPlan(plan);
			packet.getConnection().write(new MessagePacket("Planned [" + plan + "] for " + plan.getPirateName()));
			packet.getConnection().write(new PlanAcceptedPacket(plan));
			checkPlanningDone();
		}
		else
			Utility.typeError(packet.getType());
	}

	/*
	 * Adds the plan to the plan history.
	 */
	private void addPlan(Plan plan)
	{
		Plan[] pirateHistory = planHistory.get(plan.getPirateName());
		pirateHistory[plan.getTurn()] = plan;
		if (plan.getCarryOverRests() > 0)
		{
			Order[] carryOver = new Order[plan.getCarryOverRests()];
			for (int i = 0; i < plan.getCarryOverRests(); i++)
				carryOver[i] = Order.REST;
			pirateHistory[plan.getTurn() + 1] = Plan.expandAndBuildPlan(plan.getTurn() + 1, plan.getPirateName(),
					carryOver);
		}
	}

	/*
	 * Places a newly-received pirate onto the game board.
	 */
	private void placePirate(Pirate pirate)
	{
		BoardCoordinate coordinate = BoardCoordinate.randomPiratePosition();
		int tries = 0;
		while (board.occupied(coordinate))
		{
			coordinate = BoardCoordinate.randomPiratePosition();
			if (tries++ > 1000)
				coordinate = BoardCoordinate.random();
		}
		pirate.coordinates.add(coordinate);
		board.add(pirate);
		writeAll(new MessagePacket(pirate.getName() + " has joined the game."));
		writeAll(new BoardPacket(board));
		Plan[] emptyPlanHistory = new Plan[ServerMain.MAX_TURNS];
		for (int i = 0; i < ServerMain.MAX_TURNS; i++)
			emptyPlanHistory[i] = Plan.createUnplannedPlan(i);
		planHistory.put(pirate.getName(), emptyPlanHistory);
		gui.write(board);
		System.out.println(board);
		checkGameStart();
	}

	/*
	 * Checks to see if the right amount of pirates have been created
	 */
	private void checkGameStart()
	{
		if (board.allEntities(Pirate.class).size() == PIRATES_PER_PLAYER * NUM_PLAYERS)
		{
			writeAll(new MessagePacket("All players are ready to begin."));
			writeAll(new NextTurnPacket());
			currentTurn = 1;
		}
	}

	/*
	 * Checks to see if all clients have submitted valid plans for all pirates.
	 */
	private void checkPlanningDone()
	{
		for (Plan[] plans : planHistory.values())
		{
			if (!plans[currentTurn].isLocked())
			{
				System.out.println("Not all pirates have been planned.");
				return;
			}
		}
		System.out.println("All pirates planned. Moving to resolution.");
		//TODO: look into if this is the best way to actually do this or not.
		board = new ResolutionHandler(board, planHistory, currentTurn).resolve();
		writeAll(new NextTurnPacket());
		currentTurn++;
		// writeAll(new RequestPacket(Plan.class));
	}

	/*
	 * Writes the same packet to all clients.
	 */
	private void writeAll(Packet packet)
	{
		for (Connection c : connections)
			c.write(packet);
	}
}