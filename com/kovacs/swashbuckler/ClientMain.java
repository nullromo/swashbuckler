package com.kovacs.swashbuckler;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
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
	public static ClientMain main = new ClientMain();

	/*
	 * The I/O wrapper for the client's connection
	 */
	public Connection connection;

	/*
	 * Holds the value that tells what turn the game is on (1-MAX_TURNS).
	 */
	// TODO: this should increment at the end of each turn.
	private int currentTurn = 0;

	/*
	 * Holds a list of the pirates that this client controls.
	 */
	private ArrayList<Pirate> pirates = new ArrayList<>();

	/*
	 * Holds the history of each pirate's plans. Maps pirate names to arrays of
	 * their plans.
	 */
	private HashMap<String, Plan[]> planHistory = new HashMap<>();

	/*
	 * The gui for the client
	 */
	public ClientGUI gui = new ClientGUI();

	/*
	 * An aesthetic thing so that the text looks right.
	 */
	private boolean firstPirate = true;

	/*
	 * The main method sets up the connection and kicks off the main loop
	 */
	public static void main(String[] args)
	{
		System.out.println("Starting...");
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
		main.connection = new Connection(socket);
		System.out.println("Connected to " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort()
				+ "\n         from " + socket.getLocalSocketAddress().toString().substring(1));
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
			String newName = Utility.getValidName();
			pirate.setName(newName);
			connection.write(new ResponsePacket<Pirate>(pirate));
		}
		else if (packet instanceof PirateAcceptedPacket)
		{
			Pirate pirate = ((PirateAcceptedPacket) packet).getPirate();
			gui.writeMessage(pirate.getName() + " has joined the game.");
			pirates.add(pirate);
			planHistory.put(pirate.getName(), new Plan[ServerMain.MAX_TURNS]);
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
		}
		else if (packet instanceof BoardPacket)
		{
			gui.board = ((BoardPacket) packet).getBoard();
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
			gui.writeMessage("Please enter your " + (firstPirate ? "first" : "next") + " pirate's name.");
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
			connection.write(new ResponsePacket<Pirate>(pirate));
		}
		else if (packet.type == Plan.class)
		{
			gui.writeMessage("Plan your turn.");
			// TODO: unfinished
			gui.keyboardInput.nextLine();
			Plan plan = null;
			do
			{
				// TODO: this is where the scanner/inout device takes in the
				// plan.
				plan = Plan.buildPlan(currentTurn, pirates.get(0).getName(), Order.BLOCK, Order.BLOCK,
						Order.THROW_CHAIR);
				// TODO: keep planning until all plans are made, sending each
				// one in as it's made. When the last one comes back as
				// validated, then it's done.
			}
			while (plan == null);
			gui.writeMessage(plan.toString());
			connection.write(new ResponsePacket<Plan>(plan));
		}
		else
			Utility.typeError(packet.type);
	}
}