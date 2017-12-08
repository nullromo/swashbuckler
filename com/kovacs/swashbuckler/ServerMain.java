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
	/*
	 * The number of players in a game.
	 */
	private static final int NUM_PLAYERS = 1;

	/*
	 * The number of pirates each player controls.
	 */
	private static final int PIRATES_PER_PLAYER = 2;

	/*
	 * The number of turns in a game.
	 */
	public static final int MAX_TURNS = 15;

	/*
	 * This is the single instance of the main class.
	 */
	public static ServerMain main = new ServerMain();

	/*
	 * This is the list of all clients' I/O interfaces.
	 */
	private ArrayList<Connection> connections = new ArrayList<Connection>();

	/*
	 * This should turn to false on shutdown.
	 */
	private boolean running = true;

	/*
	 * The main board that represents the game
	 */
	private Board board = new Board();

	/*
	 * Holds all plans for all pirates. Maps pirate names to arrays of their
	 * plans.
	 */
	private HashMap<String, Order[][]> planHistory = new HashMap<>();

	/*
	 * The thread that accepts clients.
	 */
	private Thread clientAcceptThread;

	/*
	 * The socket that the server runs on.
	 */
	private static ServerSocket serverSocket;

	/*
	 * Tells whether or not the game is still accepting more clients.
	 */
	private static boolean acceptingClients = true;

	/*
	 * The gui for the server.
	 */
	private ServerGUI gui = new ServerGUI();

	/*
	 * The program starts off by launching a client-accepting thread and a
	 * response-accepting thread, and then running the run method.
	 */
	public static void main(String[] args)
	{
		// TODO: This thread just runs and accepts clients all the time. It
		// needs to start games properly and such.
		main.clientAcceptThread = new Thread()
		{
			@Override
			public void run()
			{
				try
				{
					serverSocket = new ServerSocket(8004);
					while (true)
					{
						if (acceptingClients)
						{
							main.gui.write("Waiting for connections...");
							Socket s = serverSocket.accept();
							main.gui.write("A client has connected from " + s.getInetAddress().getHostAddress() + ":"
									+ s.getPort());
							main.connections.add(new Connection(s));
						}
					}
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		};
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
			packet.getConnection()
					.write(new MessagePacket("To play, you will need to create " + PIRATES_PER_PLAYER + " pirates."));
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
		}
		else if (packet.getType() == Plan.class)
		{
			Plan plan = (Plan) packet.getObject();
			String errorMessage = isPlanInvalid(plan);
			if (errorMessage == null)
			{
				addPlan(plan);
				packet.getConnection().write(new MessagePacket("Planned [" + plan + "] for " + plan.getPirateName()));
				checkPlanningDone();
			}
			else
			{
				packet.getConnection()
						.write(new MessagePacket("Invalid plan for " + plan.getPirateName() + ": " + errorMessage));
				packet.getConnection().write(new RequestPacket(Plan.class));
			}
		}
		else
			Utility.typeError(packet.getType());
	}

	/*
	 * Adds the plan to the plan history.
	 */
	private void addPlan(Plan plan)
	{
		Order[][] pirateHistory = planHistory.get(plan.getPirateName());
		pirateHistory[plan.getTurn()] = plan.getOrders();
		if (plan.getCarryOverRests() > 0)
		{
			Order[] carryOver = new Order[6];
			for (int i = 0; i < plan.getCarryOverRests(); i++)
				carryOver[i] = Order.REST;
			pirateHistory[plan.getTurn() + 1] = carryOver;
		}
	}

	/*
	 * Returns null if the plan is okay and an error message if it isn't.
	 */
	private String isPlanInvalid(Plan plan)
	{
		int restsCarriedOver = 0;
		for (Order order : planHistory.get(plan.getPirateName())[plan.getTurn()])
			if (order == Order.REST)
				restsCarriedOver++;
			else if (order != null)
				// TODO: this can be eliminated once it is tested.
				throw new RuntimeException("Non-rest order carried over");
			else
				break;
		for (int i = 0; i < restsCarriedOver; i++)
			if (plan.getOrders()[i] != Order.REST)
				return "Plan must start with " + restsCarriedOver + " required step"
						+ (restsCarriedOver == 1 ? "" : "s") + " of rest carried over from last turn.";
		return null;
	}

	/*
	 * Places a newly-received pirate onto the game board.
	 */
	private void placePirate(Pirate pirate)
	{
		// TODO: Catch the infinite loop possibility where the pirate doesn't
		// ever get placed.
		BoardCoordinate coordinate = Board.randomPiratePosition();
		while (board.occupied(coordinate))
			coordinate = Board.randomPiratePosition();
		pirate.coordinates.add(coordinate);
		board.add(pirate);
		writeAll(new PirateAcceptedPacket(pirate));
		writeAll(new BoardPacket(board));
		planHistory.put(pirate.getName(), new Order[MAX_TURNS][6]);
		gui.write(board);
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
			writeAll(new RequestPacket(Plan.class));
		}
	}

	/*
	 * Checks to see if all clients have submitted valid plans for all pirates.
	 */
	private void checkPlanningDone()
	{
		// TODO: check it and start the resolution phase. Also want to have a
		// currentTurn variable so the game knows what turn it's on.
		writeAll(new NextTurnPacket());
		writeAll(new RequestPacket(Plan.class));
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