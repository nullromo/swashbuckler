package com.kovacs.swashbuckler.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import com.kovacs.swashbuckler.Connection;
import com.kovacs.swashbuckler.Utility;
import com.kovacs.swashbuckler.game.BoardCoordinate;
import com.kovacs.swashbuckler.game.entity.Entity;
import com.kovacs.swashbuckler.game.entity.Pirate;
import com.kovacs.swashbuckler.packets.MessagePacket;
import com.kovacs.swashbuckler.packets.NewConnectionPacket;
import com.kovacs.swashbuckler.packets.Packet;
import com.kovacs.swashbuckler.packets.InvalidPirateNamePacket;
import com.kovacs.swashbuckler.packets.RequestPacket;
import com.kovacs.swashbuckler.packets.ResponsePacket;

/*
 * This is the main server-side application. It hosts and array list of
 * connection objects and routes all information to each client. It updates the
 * clients with the appropriate data and game actions, while requesting
 * information from clients and handling the outcomes. It is responsible for
 * handling all game logic.
 */
public class ServerMain
{
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
	 * The program starts off by launching a client-accepting thread and a
	 * response-accepting thread, and then running the run method.
	 */
	public static void main(String[] args)
	{
		// TODO: This thread just runs and accepts clients all the time. It
		// needs to start games properly and such.
		new Thread()
		{
			@Override
			public void run()
			{
				try
				{
					ServerSocket serverSocket = new ServerSocket(8004);
					while (main.running)
					{
						System.out.println("Waiting for connections...");
						Socket s = serverSocket.accept();
						System.out.println("A client has connected from " + s.getInetAddress().getHostAddress() + ":"
								+ s.getPort());
						main.connections.add(new Connection(s));
					}
					serverSocket.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}.start();
		// System.out.println("External IP: " + Utility.getExternalIPAddress());
		main.run();
	}

	/*
	 * The main loop that is responsible for routing.
	 */
	private void run()
	{
		while (true)
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
	}

	/*
	 * The main switch that takes care of incoming packets from clients.
	 */
	private void handleResponse(Packet packet)
	{
		if (packet instanceof NewConnectionPacket)
		{
			packet.getConnection().write(new MessagePacket("Welcome to Swashbuckler."));
			packet.getConnection().write(new MessagePacket("To play, you will need to create 2 pirates."));
			packet.getConnection().write(new RequestPacket(Pirate.class));
			packet.getConnection().write(new RequestPacket(Pirate.class));
		}
		else if (packet instanceof ResponsePacket)
		{
			handleObjectPacket((ResponsePacket<?>) packet);
		}
		else
			Utility.typeError(packet.getClass());
	}

	/*
	 * Handles incoming objects.
	 */
	private void handleObjectPacket(ResponsePacket<?> packet)
	{
		if (packet.getType() == Pirate.class)
		{
			Pirate pirate = (Pirate) packet.getObject();
			for (Entity entity : board.getEntities())
			{
				if (entity instanceof Pirate)
				{
					if (((Pirate) entity).getName().equals(pirate.getName()))
					{
						packet.getConnection().write(new InvalidPirateNamePacket(pirate));
						return;
					}
				}
			}
			placePirate(pirate);
		}
		else
			Utility.typeError(packet.getType());
	}

	/*
	 * Places a newly recieved pirate onto the game board.
	 */
	private void placePirate(Pirate pirate)
	{
		// TODO: Catch the infinite loop possibility.
		BoardCoordinate coordinate = Board.randomPiratePosition();
		while (board.occupied(coordinate))
			coordinate = Board.randomPiratePosition();
		pirate.coordinates.add(coordinate);
		board.add(pirate);
		writeAll(new MessagePacket(pirate.getName() + " has joined the game."));
		System.out.println(board);
		checkGameStart();
	}

	/*
	 * Checks to see if the right amount of pirates have been created
	 */
	private void checkGameStart()
	{
		// TODO: check to start the game and fire off a packet to all clients.
	}

	/*
	 * Writes the same packet to all clients.
	 */
	private void writeAll(Packet packet)
	{
		for (Connection c : connections)
		{
			c.write(packet);
		}
	}
}