package com.kovacs.swashbuckler.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;
import com.kovacs.swashbuckler.Connection;
import com.kovacs.swashbuckler.Utility;
import com.kovacs.swashbuckler.game.entity.Pirate;
import com.kovacs.swashbuckler.game.entity.Pirate.Dexterity;
import com.kovacs.swashbuckler.packets.MessagePacket;
import com.kovacs.swashbuckler.packets.NewConnectionPacket;
import com.kovacs.swashbuckler.packets.Packet;
import com.kovacs.swashbuckler.packets.InvalidPirateNamePacket;
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
	 * Takes input from stdin. TODO: this will eventually go away.
	 */
	private final Scanner scanner = new Scanner(System.in);

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
			System.out.println("\"" + pirate.getName() + "\" is already taken. Please choose another pirate name.");
			String newName = Utility.getValidName(scanner);
			pirate.setName(newName);
			connection.write(new ResponsePacket<Pirate>(pirate));
		}
		else if (packet instanceof MessagePacket)
		{
			// TODO: There should be a separate thread that handles messages. It
			// is important that messages get handled in real time while the
			// user is deciding to do things.
			System.out.println("Server: " + ((MessagePacket) packet).getMessage());
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
			System.out.println("Please enter your pirate's name.");
			String name = Utility.getValidName(scanner);
			int strength = Utility.rollDie() + Utility.rollDie() + Utility.rollDie();
			int endurance = Utility.rollDie() + Utility.rollDie() + Utility.rollDie();
			int constitution = strength + endurance;
			int expertise = Utility.rollDie() + Utility.rollDie() + Utility.rollDie();
			int dexterityRoll = Utility.rollDoubleDigit();
			Dexterity dexterity = dexterityRoll == 66 ? Dexterity.AMBIDEXTROUS
					: dexterityRoll >= 62 ? Dexterity.LEFT_HANDED : Dexterity.RIGHT_HANDED;
			System.out.println(name + "'s strength is " + strength);
			System.out.println(name + "'s endurance is " + endurance);
			System.out.println(name + "'s constitution is " + constitution);
			System.out.println(name + "'s expertise is " + expertise);
			System.out.println(name + " is " + dexterity);
			System.out.println("You may now distribute " + name
					+ "'s constitution points among his/her body parts (head, body, right arm, left arm). Each part requires at least 1 point.");
			System.out.println("Enter the number of constituion points to assign to " + name + "'s head. (up to "
					+ (constitution - 3) + ").");
			int head = Utility.getInt(scanner, constitution - 3);
			System.out.println("Enter the number of constituion points to assign to " + name + "'s body. (up to "
					+ (constitution - head - 2) + ").");
			int body = Utility.getInt(scanner, constitution - head - 2);
			System.out.println("Enter the number of constituion points to assign to " + name + "'s left arm. (up to "
					+ (constitution - head - body - 1) + ").");
			int leftArm = Utility.getInt(scanner, constitution - head - body - 1);
			System.out.println(name + "'s remaining consitution points have been added to his/her right arm.");
			int rightArm = constitution - head - body - leftArm;
			Pirate pirate = new Pirate(head, leftArm, rightArm, body, strength, endurance, constitution, expertise,
					dexterity, name);
			connection.write(new ResponsePacket<Pirate>(pirate));
		}
		else
			Utility.typeError(packet.type);
	}
}