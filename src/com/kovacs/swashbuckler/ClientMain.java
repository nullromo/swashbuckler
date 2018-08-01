package com.kovacs.swashbuckler;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
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
	public int currentTurn = 0;

	/*
	 * Holds a list of the pirate names that this client controls.
	 */
	private ArrayList<String> ownedPirateNames;

	/*
	 * Holds the history of each pirate's plans. Maps pirate names to arrays of
	 * their plans.
	 */
	public HashMap<String, Plan[]> planHistory;

	/*
	 * The current pirate that is selected for planning.
	 */
	private Pirate selectedPirate;

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
		planHistory = new HashMap<>();
	}

	/*
	 * The main method sets up the connection and kicks off the main loop
	 */
	public static void main(String[] args)
	{
		main = new ClientMain();
		main.gui = new ClientGUI();
		System.out.println("Starting...");
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
			Plan[] emptyPlanHistory = new Plan[ServerMain.MAX_TURNS];
			for (int i = 0; i < ServerMain.MAX_TURNS; i++)
				emptyPlanHistory[i] = Plan.createUnplannedPlan(i);
			planHistory.put(((PirateAcceptedPacket) packet).getPirate().getName(), emptyPlanHistory);
		}
		else if (packet instanceof PlanAcceptedPacket)
		{
			Plan plan = ((PlanAcceptedPacket) packet).getPlan();
			planHistory.get(plan.getPirateName())[plan.getTurn()] = plan;
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
	// TODO: (long-term) make the pirate creation into a gui pop-up
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
					+ "'s constitution points among their body parts (head, body, right arm, left arm). Each part requires at least 1 point.");
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
		else if (packet.type == Plan.class)
		{
			throw new RuntimeException(
					"Need to figure out how the server requests specific plans when an invalid one is submitted.");
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
		String pirateName = selectedPirate.getName();
		List<Order> plannedOrders = new ArrayList<>(Arrays.asList(getPlanInProgress().getOrders())).stream()
				.filter(o -> o != null && o != Order.UNPLANNED).collect(Collectors.toList());
		for (int i = 0; i < getPlanInProgress().getCarryOverRests(); i++)
			plannedOrders.add(Order.REST);
		Plan plan = Plan.buildPlan(currentTurn, pirateName, plannedOrders);
		if (plan == null)
		{
			gui.writeMessage("You must submit a valid plan. All steps must be filled out.");
			return;
		}
		int restsCarriedOver = planHistory.get(pirateName)[currentTurn - 1].getCarryOverRests();
		for (int i = 0; i < restsCarriedOver; i++)
			if (plannedOrders.get(i) != Order.REST)
			{
				gui.writeMessage("Plan must start with " + restsCarriedOver + " required step"
						+ (restsCarriedOver == 1 ? "" : "s") + " of rest carried over from last turn.");
				System.err.println("This code should never have executed.");
				return;
			}
		connection.write(new ResponsePacket<Plan>(plan));
	}

	/*
	 * Updates the in-progress plan based on what was selected in a menu.
	 */
	public void updatePlanInProgress(int step, Order order)
	{
		// don't allow changing of carry-over rests
		if (currentTurn != 1 && planHistory.get(selectedPirate.getName())[currentTurn - 1].getCarryOverRests() >= step)
			return;
		Order[] ordersInProgress = getPlanInProgress().getOrders();
		// don't allow changing of previously planned orders' required rest
		for (int i = 0; i < step - 1; i++)
			if (ordersInProgress[i] != null && ordersInProgress[i].getRests() + i + 1 >= step)
				return;
		// assign the desired order to the orders in progress
		ordersInProgress[step - 1] = order;
		// erase everything after the desired order
		for (int i = step + order.getRests(); i < 6; i++)
			ordersInProgress[i] = Order.UNPLANNED;
		planHistory.get(selectedPirate.getName())[currentTurn + 1] = Plan.createUnplannedPlan(currentTurn + 1);
		getPlanInProgress().resetCarryOverRests();
		// assign required rest steps for the desired order
		for (int i = 0; i < order.getRests(); i++)
		{
			try
			{
				ordersInProgress[step + i] = Order.REST;
			}
			catch (ArrayIndexOutOfBoundsException e)
			{
				getPlanInProgress().addCarryOverRest();
				planHistory.get(selectedPirate.getName())[currentTurn + 1].getOrders()[i + step - 6] = Order.REST;
			}
		}
	}

	/*
	 * Selects a pirate and does all associated things. Called when a player
	 * clicks on a pirate.
	 */
	public void selectPirate(Pirate pirate)
	{
		if (ownedPirateNames.contains(pirate.getName()))
			selectedPirate = pirate;
		// TODO: more goes here probably.
	}

	/*
	 * Un-selects the currently selected pirate.
	 */
	public void deselectPirate()
	{
		selectedPirate = null;
		// TODO: more might go here.
	}

	public Plan getPlanInProgress()
	{
		if (selectedPirate == null)
			return null;
		return planHistory.get(selectedPirate.getName())[currentTurn];
	}

	public ArrayList<String> getOwnedPirateNames()
	{
		return ownedPirateNames;
	}

	public Pirate getSelectedPirate()
	{
		return selectedPirate;
	}
}