package com.kovacs.swashbuckler;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

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
	}

	/*
	 * Reads and returns the next object in the stream. Blocks if there are no
	 * objects.
	 */
	public Object read()
	{
		Object result = null;
		try
		{
			result = reader.readObject();
		}
		catch (ClassNotFoundException | IOException e)
		{
			e.printStackTrace();
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
			writer.writeObject(object);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

	}

	/*
	 * Returns true if there is an object waiting to be read in the input
	 * stream.
	 */
	public boolean hasData()
	{
		try
		{
			return reader.available() > 0;
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return false;
	}
}