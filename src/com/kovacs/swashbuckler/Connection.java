package com.kovacs.swashbuckler;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
import com.kovacs.swashbuckler.packets.Packet;

/*
 * Wrapper class for I/O streams.
 */
public class Connection
{
	/*
	 * The output stream that lets this connection send data.
	 */
	private ObjectOutputStream writer;

	/*
	 * A FIFO queue of the responses from this client.
	 */
	private Queue<Packet> responses = new LinkedList<>();

	/*
	 * The input stream that lets this connection receive data.
	 */
	private ObjectInputStream reader;

	public Connection(Socket socket)
	{
		try
		{
			writer = new ObjectOutputStream(socket.getOutputStream());
			reader = new ObjectInputStream(socket.getInputStream());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		new Thread()
		{
			@Override
			public synchronized void run()
			{
				while (true)
				{
					responses.add((Packet) read());
				}
			}
		}.start();
	}

	/*
	 * Reads and returns the next object in the stream. Blocks if there are no
	 * objects.
	 */
	private Object read()
	{
		Object result = null;
		try
		{
			result = reader.readUnshared();
		}
		catch (ClassNotFoundException | IOException e)
		{
			e.printStackTrace();
			System.exit(1);
		}
		return result;
	}

	/*
	 * Writes an object to the output stream.
	 */
	public void write(Object object)
	{
		try
		{
			writer.writeUnshared(object);
			writer.reset();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			System.exit(1);
		}

	}

	/*
	 * Returns the first item in the queue.
	 */
	public Packet nextPacket()
	{
		return responses.poll();
	}

	/*
	 * Returns true if there is an object waiting to be read in the input queue.
	 */
	public boolean hasData()
	{
		return !responses.isEmpty();
	}
}