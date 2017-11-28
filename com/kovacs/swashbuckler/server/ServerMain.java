package com.kovacs.swashbuckler.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import com.kovacs.swashbuckler.Connection;
import com.kovacs.swashbuckler.game.BoardCoordinate;
import com.kovacs.swashbuckler.game.entity.Pirate;
import com.kovacs.swashbuckler.packets.MessagePacket;
import com.kovacs.swashbuckler.packets.NewConnectionPacket;
import com.kovacs.swashbuckler.packets.NewPiratePacket;
import com.kovacs.swashbuckler.packets.Packet;

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
		System.out.println(board);
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
			packet.getConnection().write(new NewConnectionPacket());
		}
		else if (packet instanceof NewPiratePacket)
		{
			Pirate pirate = ((NewPiratePacket) packet).getPirate();
			// TODO: Catch the infinite loop possibility.
			BoardCoordinate coordinate = Board.randomPiratePosition();
			while (board.occupied(coordinate))
				coordinate = Board.randomPiratePosition();
			pirate.coordinates.add(coordinate);
			board.add(pirate);
			writeAll(new MessagePacket(((NewPiratePacket) packet).getPirate().getName() + " has joined the game."));
			System.out.println(board);
		}
		else
			System.out.println("Unrecognized or unimplemented packet type: " + packet.getClass());
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