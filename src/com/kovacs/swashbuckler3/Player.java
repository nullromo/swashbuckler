package com.kovacs.swashbuckler3;

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
	// TODO: maybe don't have this be public.
	public PirateData[] pirates = new PirateData[Engine.PIRATES_PER_PLAYER];

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
	// TODO: maybe don't have this be public.
	public BlockingQueue<Request> in = new PriorityBlockingQueue<>();
	public BlockingQueue<Request> out = new PriorityBlockingQueue<>();

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
		Request r = null;
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
			try
			{
				r = in.take();
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			if (r.requestStatus == RequestStatus.UNFILLED)
			{
				// TODO: this is where the gui pops up and the user fills out
				// the request.
				// TODO: then the request is sent back via the out queue.
				waitingForResponse = true;
			}
			else if (r.requestStatus == RequestStatus.ERROR)
			{
				// TODO: this is where the gui says "here's what went wrong" and
				// then the user refills the request and it's sent back via the
				// out queue.
				waitingForResponse = true;
			}
			else if (r.requestStatus == RequestStatus.FILLED)
			{
				// TODO: this is where the gui closes.
				waitingForResponse = false;
			}
		}
	}

	// TODO: Connection information goes here
}