package com.kovacs.swashbuckler.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import com.kovacs.swashbuckler.Connection;

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
	 * The program starts off by launching a client-accepting thread and then
	 * running the run method.
	 */
	public static void main(String[] args)
	{
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
			for (Connection c : connections)
			{
				if (c.hasData())
					System.out.println(c.read());
			}
		}
	}
}