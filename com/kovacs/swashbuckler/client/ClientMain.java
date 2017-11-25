package com.kovacs.swashbuckler.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;
import com.kovacs.swashbuckler.Connection;

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
			InetAddress addr = InetAddress.getByName("localhost"/*"68.6.102.228"*/);
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
		Scanner scanner = new Scanner(System.in);
		while (true)
		{
			connection.write(scanner.nextLine());
		}
	}
}