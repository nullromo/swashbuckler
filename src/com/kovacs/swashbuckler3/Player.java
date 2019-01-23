package com.kovacs.swashbuckler3;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import com.kovacs.swashbuckler3.Request.RequestStatus;

/*
 * Represents a human player in a game. This is a server-side class that handles
 * forwarding information to the user and forwarding requests from the user.
 */
public class Player implements Runnable
{
	/*
	 * List of pirates this player controls.
	 */
	private ArrayList<PirateData> pirates = new ArrayList<>();

	/*
	 * Whether this thread is running or not.
	 */
	private boolean running;

	/*
	 * Whether the player is waiting for an acknowledgement from the engine.
	 */
	private boolean waitingForResponse;

	/*
	 * Communication channel between this player and the engine.
	 */
	private BlockingQueue<Request> in = new PriorityBlockingQueue<>();
	private BlockingQueue<Request> out = new PriorityBlockingQueue<>();

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
			// if there are no requests in the queue, do nothing.
			if (in.isEmpty())
				continue;
			// if we are waiting to get a FILLED or ERROR back, and there isn't
			// one, then do nothing. Keep in mind that all ERROR and FILLED
			// requests will have higher priority that UNFILLED ones, so they
			// will be moved to the front of the queue if they exist in it at
			// all.
			if (waitingForResponse && in.peek().requestStatus == RequestStatus.UNFILLED)
				continue;
			Request r = accept();
			if (r == null)
				continue;
			if (r.requestStatus == RequestStatus.UNFILLED)
			{
				System.out.println("Player " + this + " got an unfilled request: " + r);
				waitingForResponse = true;
				r.createGUI();
			}
			else if (r.requestStatus == RequestStatus.ERROR)
			{
				System.out.println("Player " + this + " got an error request: " + r);
				waitingForResponse = true;
				r.setGUIEnabled(true);
				r.setGUIMessage(r.message);
			}
			else if (r.requestStatus == RequestStatus.FILLED)
			{
				System.out.println("Player " + this + " got an ack: " + r);
				r.closeGUI();
				waitingForResponse = false;
			}
			try
			{
				Thread.sleep(10);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}

	/*
	 * Way for the player to send something.
	 */
	public void send(Request request)
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
	private Request accept()
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
	public void put(Request request)
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
	public Request take()
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