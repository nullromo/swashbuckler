package com.kovacs.swashbuckler.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;
import com.kovacs.swashbuckler.Connection;
import com.kovacs.swashbuckler.Utility;
import com.kovacs.swashbuckler.game.BoardCoordinate;
import com.kovacs.swashbuckler.game.entity.Pirate;
import com.kovacs.swashbuckler.game.entity.Pirate.Dexterity;
import com.kovacs.swashbuckler.packets.MessagePacket;
import com.kovacs.swashbuckler.packets.NewConnectionPacket;
import com.kovacs.swashbuckler.packets.NewPiratePacket;
import com.kovacs.swashbuckler.packets.Packet;

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
		if (packet instanceof NewConnectionPacket)
		{
			Scanner scanner = new Scanner(System.in);
			System.out.println("Welcome to Swashbuckler. Please enter your pirate's name.");
			String name = scanner.nextLine();
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
			BoardCoordinate coordinate = Utility.randomBoardCoordinate();
			System.out.println(name + " will be placed on square " + coordinate);
			scanner.close();
			Pirate pirate = new Pirate(head, leftArm, rightArm, body, strength, endurance, constitution, expertise,
					dexterity, name, coordinate);
			System.out.println(pirate.coordinates);
			connection.write(new NewPiratePacket(pirate));
		}
		else if (packet instanceof MessagePacket)
		{
			System.out.println("Server: " + ((MessagePacket) packet).getMessage());
		}
		else
			System.out.println("Unrecognized or unimplemented packet type: " + packet.getClass());
	}
}