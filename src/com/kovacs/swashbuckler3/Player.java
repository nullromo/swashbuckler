package com.kovacs.swashbuckler3;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import com.kovacs.swashbuckler.Utility;

/*
 * Represents a human player in a game. This is a server-side class that handles
 * passing information to and from the user.
 */
public class Player implements Runnable
{
	/*
	 * Gives each player a unique ID number.
	 */
	private static int nextID;

	/*
	 * The unique ID of this player.
	 */
	private final int ID = ++nextID;

	@Override
	public String toString()
	{
		return "Player " + ID;
	}

	/*
	 * List of pirates this player controls.
	 */
	private ArrayList<PirateData> pirates = new ArrayList<>();

	/*
	 * Whether this thread is running or not.
	 */
	private boolean running;
	
	/*
	 * Communication channel between this player and the engine.
	 */
	private BlockingQueue<Request> in = new LinkedBlockingQueue<>();
	private BlockingQueue<Request> out = new LinkedBlockingQueue<>();

	/*
	 * Stops the client from running and cleans up.
	 */
	public void stop()
	{
		running = false;
	}

	/*
	 * Starts the client running.
	 */
	public void start()
	{
		new Thread(this).start();
	}

	@Override
	public void run()
	{
		running = true;
		while (running)
		{
			Utility.sleep(10);
			// If there are no requests in the queue, do nothing.
			if (in.isEmpty())
				continue;
			Request r = accept();
			if (r == null)
				throw new RuntimeException("Null request received by player.");
			System.out.println(this + " got an unfilled request: " + r);
			r.createGUI();
		}
	}

	/*
	 * Way for the player to send something.
	 */
	public synchronized void send(Request request)
	{
		try
		{
			out.put(request);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}

	/*
	 * Way for the player to receive something.
	 */
	private synchronized Request accept()
	{
		Request request = null;
		try
		{
			request = in.take();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		return request;
	}

	/*
	 * Way to send something to the player.
	 */
	public synchronized void put(Request request)
	{
		try
		{
			in.put(request);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}

	/*
	 * Way to grab responses from the player.
	 */
	public synchronized Request take()
	{
		Request request = null;
		try
		{
			request = out.take();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		return request;
	}

	/*
	 * Tells whether or not there is anything in the output queue.
	 */
	public boolean hasResponse()
	{
		return !out.isEmpty();
	}

	public void addPirate(PirateData p)
	{
		pirates.add(p);
	}

	// TODO: Connection information goes here
}