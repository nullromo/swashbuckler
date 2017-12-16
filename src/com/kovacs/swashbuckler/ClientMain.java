package com.kovacs.swashbuckler;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import com.kovacs.swashbuckler.game.Board;
import com.kovacs.swashbuckler.game.Order;
import com.kovacs.swashbuckler.game.Plan;
import com.kovacs.swashbuckler.game.entity.Pirate;
import com.kovacs.swashbuckler.game.entity.Pirate.Dexterity;
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
 * This is the main class for the client-side application. It will respond to
 * all data coming from the server by updating the game state visible to the
 * user and sending responses back to the server.
 */
public class ClientMain
{
	/*
	 * This is the single instance of the main class.
	 */
	public static ClientMain main;

	/*
	 * The I/O wrapper for the client's connection
	 */
	public Connection connection;

	/*
	 * Holds the value that tells what turn the game is on (1-MAX_TURNS).
	 */
	// TODO: this should increment at the end of each turn.
	public int currentTurn = 1;

	/*
	 * Holds a list of the pirate names that this client controls.
	 */
	private ArrayList<String> ownedPirateNames;

	/*
	 * Keeps track of which pirates still need their plans. Resets to an empty
	 * list every new turn.
	 */
	private ArrayList<String> plannedPirateNames;

	/*
	 * Holds the history of each pirate's plans. Maps pirate names to arrays of
	 * their plans.
	 */
	public HashMap<String, Plan[]> planHistory;

	/*
	 * Keeps track of the plans that are about to be submitted.
	 */
	private Order[] planInProgress;

	/*
	 * The gui for the client
	 */
	public ClientGUI gui;

	/*
	 * An aesthetic thing so that the text looks right.
	 */
	private boolean firstPirate = true;

	private ClientMain()
	{
		ownedPirateNames = new ArrayList<>();
		Socket socket = new Socket();
		try
		{
			InetAddress addr = InetAddress
					.getByName("localhost"/* "68.6.102.228" */);
			socket.connect(new InetSocketAddress(addr, 8004));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		connection = new Connection(socket);
		System.out.println("Connected to " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort()
				+ "\n         from " + socket.getLocalSocketAddress().toString().substring(1));
		gui = new ClientGUI();
		planHistory = new HashMap<>();
		planInProgress = new Order[8];
	}

	/*
	 * The main method sets up the connection and kicks off the main loop
	 */
	public static void main(String[] args)
	{
		main = new ClientMain();
		System.out.println("Starting...");

		Plan[] asdf = new Plan[15];
		asdf[1] = Plan.buildPlan(1, "ale", Order.BLOCK, Order.BLOCK, Order.BLOCK);
		asdf[2] = Plan.buildPlan(2, "ale", Order.SLASH, Order.JUMP_DOWN, Order.REST);
		asdf[3] = Plan.buildPlan(3, "ale", Order.PICK_UP_DAGGER, Order.THROW_CHAIR, Order.REST);
		asdf[4] = Plan.buildPlan(4, "ale", Order.THROW_SWORD, Order.THROW_MUG, Order.THROW_DAGGER);
		asdf[5] = Plan.buildPlan(5, "ale", Order.MOVE_FORWARD_LEFT, Order.MOVE_BACK_RIGHT, Order.MOVE_FORWARD_RIGHT);
		main.planHistory.put("ale", asdf);

		main.run();
	}

	/*
	 * The main loop that runs and responds to all server communications
	 */
	private void run()
	{
		connection.write(new NewConnectionPacket());
		while (true)
		{
			if (connection.hasData())
			{
				handleRequest(connection.nextPacket());
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
	}

	/*
	 * The main switch that takes care of incoming requests from the server.
	 */
	private void handleRequest(Packet packet)
	{
		if (packet instanceof RequestPacket)
		{
			handleRequestPacket((RequestPacket) packet);
		}
		else if (packet instanceof InvalidPirateNamePacket)
		{
			Pirate pirate = ((InvalidPirateNamePacket) packet).getPirate();
			gui.writeMessage("\"" + pirate.getName() + "\" is already taken. Please choose another pirate name.");
			ownedPirateNames.remove(pirate.getName());
			String newName = Utility.getValidName();
			pirate.setName(newName);
			ownedPirateNames.add(newName);
			connection.write(new ResponsePacket<Pirate>(pirate));
		}
		else if (packet instanceof PirateAcceptedPacket)
		{
			planHistory.put(((PirateAcceptedPacket) packet).getPirate().getName(), new Plan[ServerMain.MAX_TURNS]);
		}
		else if (packet instanceof PlanAcceptedPacket)
		{
			Plan plan = ((PlanAcceptedPacket) packet).getPlan();
			planHistory.get(plan.getPirateName())[plan.getTurn()] = plan;
			plannedPirateNames.add(plan.getPirateName());
		}
		else if (packet instanceof MessagePacket)
		{
			// TODO: There should be a separate thread that handles messages. It
			// is important that messages get handled in real time while the
			// user is deciding to do things.
			gui.writeMessage(((MessagePacket) packet).getMessage());
		}
		else if (packet instanceof NextTurnPacket)
		{
			currentTurn++;
			plannedPirateNames = new ArrayList<>();
			gui.writeMessage("=== Begin turn " + currentTurn + " ===");
			gui.writeMessage("Plan your turn.");
		}
		else if (packet instanceof BoardPacket)
		{
			Board board = ((BoardPacket) packet).getBoard();
			gui.board = board;
		}
		else
			Utility.typeError(packet.getClass());
	}

	/*
	 * Handles requests for objects that have been made by the server.
	 */
	private void handleRequestPacket(RequestPacket packet)
	{
		if (packet.type == Pirate.class)
		{
			gui.writeMessage("Please enter your "
					+ (ServerMain.PIRATES_PER_PLAYER == 1 ? "" : firstPirate ? "first " : "next ") + "pirate's name.");
			firstPirate = false;
			String name = Utility.getValidName();
			int strength = Utility.rollDie() + Utility.rollDie() + Utility.rollDie();
			int endurance = Utility.rollDie() + Utility.rollDie() + Utility.rollDie();
			int constitution = strength + endurance;
			int expertise = Utility.rollDie() + Utility.rollDie() + Utility.rollDie();
			int dexterityRoll = Utility.rollDoubleDigit();
			Dexterity dexterity = dexterityRoll == 66 ? Dexterity.AMBIDEXTROUS
					: dexterityRoll >= 62 ? Dexterity.LEFT_HANDED : Dexterity.RIGHT_HANDED;
			gui.writeMessage(name + "'s strength is " + strength);
			gui.writeMessage(name + "'s endurance is " + endurance);
			gui.writeMessage(name + "'s constitution is " + constitution);
			gui.writeMessage(name + "'s expertise is " + expertise);
			gui.writeMessage(name + " is " + dexterity);
			gui.writeMessage("You may now distribute " + name
					+ "'s constitution points among his/her body parts (head, body, right arm, left arm). Each part requires at least 1 point.");
			gui.writeMessage("Enter the number of constituion points to assign to " + name + "'s head. (up to "
					+ (constitution - 3) + ").");
			int head = Utility.getInt(constitution - 3);
			gui.writeMessage("Enter the number of constituion points to assign to " + name + "'s body. (up to "
					+ (constitution - head - 2) + ").");
			int body = Utility.getInt(constitution - head - 2);
			gui.writeMessage("Enter the number of constituion points to assign to " + name + "'s left arm. (up to "
					+ (constitution - head - body - 1) + ").");
			int leftArm = Utility.getInt(constitution - head - body - 1);
			gui.writeMessage(name + "'s remaining consitution points have been added to his/her right arm.");
			int rightArm = constitution - head - body - leftArm;
			Pirate pirate = new Pirate(head, leftArm, rightArm, body, strength, endurance, constitution, expertise,
					dexterity, name);
			ownedPirateNames.add(name);
			connection.write(new ResponsePacket<Pirate>(pirate));
		}
		else
			Utility.typeError(packet.type);
	}

	/*
	 * Called when the user submits the planned plan for the current turn for
	 * the selected pirate.
	 */
	public void submitPlan()
	{
		String pirateName = gui.getSelectedPirate().getName();
		// Plan plan = Plan.buildPlan(currentTurn, pirateName, );
		// connection.write(new ResponsePacket<Plan>(plan));
	}

	/*
	 * Updates the in-progress plan based on what was selected in a menu.
	 */
	public void updatePlanInProgress(int step, Order order)
	{
		if (planHistory.get(gui.getSelectedPirate().getName())[currentTurn - 1].getCarryOverRests() >= step)
			return;
		for (int i = 0; i < step - 1; i++)
			if (planInProgress[i] != null && planInProgress[i].getRests() + i + 1 >= step)
				return;
		planInProgress[step - 1] = order;
		for (int i = 0; i < order.getRests(); i++)
			planInProgress[step + i] = Order.REST;
		for (int i = step + order.getRests(); i < 8; i++)
			planInProgress[i] = null;
	}

	public Order[] getPlanInProgress()
	{
		return planInProgress;
	}
}