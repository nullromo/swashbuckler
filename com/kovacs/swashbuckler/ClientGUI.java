package com.kovacs.swashbuckler;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;
import java.util.LinkedList;
import java.util.Queue;
import javax.swing.JFrame;
import com.kovacs.swashbuckler.game.Board;
import com.kovacs.swashbuckler.game.BoardCoordinate;
import com.kovacs.swashbuckler.game.entity.Entity;

/*
 * The class that controls graphical interactions on the client side.
 */
public class ClientGUI extends Canvas
{
	private static final long serialVersionUID = 495654254622340673L;

	/*
	 * The scale factor for text on the events log.
	 */
	private static final int TEXT_SCALE_FACTOR = 4;

	/*
	 * The dimensions of the window in virtual pixels.
	 */
	public static final double VIRTUAL_WIDTH = 1280.0 / 4, VIRTUAL_HEIGHT = 720.0 / 4;

	/*
	 * Default colors for things. //TODO: these will probably go away
	 * eventually.
	 */
	private static final Color backgroundColor = Color.BLACK, foregroundColor = new Color(0x666666);

	/*
	 * A list of recent messages to display.
	 */
	public LinkedList<String> messageHistory;

	/*
	 * A queue of messages that have not been added to the output window, but
	 * have been written to the gui.
	 */
	private Queue<Character> pendingCharacters;

	/*
	 * The maximum number of messages that can fit on the screen.
	 */
	private static final int MAX_MESSAGES = 82;

	/*
	 * The main window of the gui.
	 */
	private JFrame frame;

	/*
	 * The mouse input adapter.
	 */
	private MouseInput mouseInput = new MouseInput();

	/*
	 * The keyboard input adapter.
	 */
	public KeyboardInput keyboardInput = new KeyboardInput();

	/*
	 * An instance of a board that is given to the client by the server on board
	 * updates.
	 */
	public Board board;

	/*
	 * The thread that adds the characters to the output slowly.
	 */
	private Thread characterAdder;

	public ClientGUI()
	{
		messageHistory = new LinkedList<>();
		for (int i = 0; i < MAX_MESSAGES; i++)
			messageHistory.add("");
		pendingCharacters = new LinkedList<>();
		frame = new JFrame("Swashbuckler");
		frame.add(this);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		setPreferredSize(new Dimension((int) VIRTUAL_WIDTH, (int) VIRTUAL_HEIGHT));
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		addMouseListener(mouseInput);
		addMouseMotionListener(mouseInput);
		addKeyListener(keyboardInput);
		new Thread()
		{
			@Override
			public void run()
			{
				while (true)
				{
					draw();
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
		}.start();
		resize(4);
		startCharacterAdder();
	}

	/*
	 * Creates the graphics object and draws everything on it.
	 */
	public void draw()
	{
		BufferStrategy bs = getBufferStrategy();
		if (bs == null)
		{
			createBufferStrategy(2);
			requestFocus();
			return;
		}
		Graphics graphics = bs.getDrawGraphics();
		Graphics2D g = (Graphics2D) graphics;

		AffineTransform at = new AffineTransform();
		double scale = getWidth() / (double) VIRTUAL_WIDTH;
		at.scale(scale, scale);
		g.setTransform(at);

		g.setColor(backgroundColor);
		g.fillRect(0, 0, (int) VIRTUAL_WIDTH, (int) VIRTUAL_HEIGHT);
		drawPlanHistory(g);
		drawEvents(g);
		if (board != null)
			drawBoard(g);
		drawOtherStuff(g);

		at = new AffineTransform();
		at.scale(scale / TEXT_SCALE_FACTOR, scale / TEXT_SCALE_FACTOR);
		g.setTransform(at);

		g.setColor(backgroundColor);
		for (int i = 0; i < MAX_MESSAGES; i++)
			TextDrawer.drawText(g, messageHistory.get(i), 3 * TEXT_SCALE_FACTOR, (6 + 2 * i) * TEXT_SCALE_FACTOR, 1);
		TextDrawer.drawText(g, keyboardInput.keyboardInput, 3 * TEXT_SCALE_FACTOR, 173 * TEXT_SCALE_FACTOR, 1);

		bs.show();
		g.dispose();
	}

	/*
	 * Prints a message to the output area and shifts/aligns everything
	 * accordingly.
	 */
	public void writeMessage(String message)
	{
		while (message.length() > 54)
		{
			int endIndex = 54;
			while (!Character.isWhitespace(message.charAt(endIndex)))
				endIndex--;
			writeMessage(message.substring(0, endIndex));
			message = " " + message.substring(endIndex);
		}
		pendingCharacters.add('\n');
		for (int i = 0; i < message.length(); i++)
			pendingCharacters.add(message.charAt(i));
		if (!characterAdder.isAlive())
			startCharacterAdder();
	}

	/*
	 * TODO: comment here. Change the method name. Do something.
	 */
	private void drawOtherStuff(Graphics g)
	{
		;
	}

	/*
	 * Draws the event window.
	 */
	private void drawEvents(Graphics2D g)
	{
		g.setPaint(new GradientPaint(0, 0, backgroundColor, 0, 80, foregroundColor));
		g.fillRect(2, 5, 82, 166);
		g.setColor(foregroundColor);
		g.fillRect(2, 172, 82, 3);
	}

	/*
	 * Draws the right-side area (plot window).
	 */
	private void drawPlanHistory(Graphics g)
	{
		g.setColor(foregroundColor);
		g.fillRect(247, 26, 71, 149);
		g.setColor(backgroundColor);
		for (int i = 0; i < 7; i++)
			g.fillRect(249 + i * 11, 28, 1, 145);
		for (int i = 0; i < 16; i++)
			g.fillRect(249, 28 + i * 9, 67, 2);
		g.fillRect(249, 172, 67, 1);
		for (int i = 0; i < 6; i++)
			g.drawRect(250 + i * 11, 29, 10, 9);
	}

	/*
	 * Draws the board.
	 */
	private void drawBoard(Graphics g)
	{
		g.setColor(foregroundColor);
		g.fillRect(86, 5, 159, 170);
		g.setColor(backgroundColor);
		for (int i = 0; i < 15; i++)
			g.fillRect(88 + i * 11, 7, 1, 166);
		for (int i = 0; i < 16; i++)
			g.fillRect(88, 7 + i * 11, 155, 1);
		for (Entity entity : board.allEntities(Entity.class))
			for (BoardCoordinate coordinate : entity.coordinates)
			{
				switch (entity.type)
				{
					case CHAIR:
						g.setColor(Color.DARK_GRAY);
						break;
					case PIRATE:
						g.setColor(Color.GREEN);
						break;
					case MUG:
						g.setColor(Color.BLUE);
						break;
					case DAGGER:
						g.setColor(Color.RED);
						break;
					case SHELF:
						g.setColor(Color.YELLOW);
						break;
					case STAIRS:
						g.setColor(Color.MAGENTA);
						break;
					case BALCONY:
						g.setColor(Color.CYAN);
						break;
					case WINDOW:
						g.setColor(Color.LIGHT_GRAY);
						break;
					case TABLE:
						g.setColor(Color.PINK);
						break;
					case SWORD:
						g.setColor(Color.WHITE);
						break;
					case BROKEN_GLASS:
						g.setColor(Color.BLACK);
						break;
					default:
						throw new RuntimeException("Unreachable code.");
				}
				g.fillOval((coordinate.number - 1) * 11 + 89, (coordinate.letter - 'a') * 11 + 8, 10, 10);
			}
	}

	/*
	 * Resizes the window. Scale of 1 sets it to the default.
	 */
	private void resize(double scale)
	{
		setPreferredSize(new Dimension((int) (scale * VIRTUAL_WIDTH), (int) (scale * VIRTUAL_HEIGHT)));
		setSize(new Dimension((int) (scale * VIRTUAL_WIDTH), (int) (scale * VIRTUAL_HEIGHT)));
		frame.pack();
	}

	/*
	 * Adds a single character to the output window. Messages are written in and
	 * stored in a queue, then popped off one letter at a time into the output.
	 */
	private void addCharacterToOutput()
	{
		try
		{
			char nextChar = pendingCharacters.poll();
			if (nextChar == '\n')
			{
				messageHistory.poll();
				messageHistory.add("");
			}
			else
			{
				messageHistory.set(MAX_MESSAGES - 1, messageHistory.get(MAX_MESSAGES - 1).concat(nextChar + ""));
			}
		}
		catch (NullPointerException e)
		{
			System.err.println("Null pointer from the character thread.");
			try
			{
				characterAdder.join();
			}
			catch (InterruptedException e1)
			{
				e1.printStackTrace();
			}
		}
	}

	/*
	 * Restarts the character adder thread.
	 */
	private void startCharacterAdder()
	{
		characterAdder = new Thread()
		{
			@Override
			public void run()
			{
				while (!pendingCharacters.isEmpty())
				{
					addCharacterToOutput();
					try
					{
						Thread.sleep(4);
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}
			}
		};
		characterAdder.start();
	}
}