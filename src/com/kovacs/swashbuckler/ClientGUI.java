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
import com.kovacs.swashbuckler.game.entity.Chair;
import com.kovacs.swashbuckler.game.entity.Entity;
import com.kovacs.swashbuckler.game.entity.Table;

/*
 * The class that controls graphical interactions on the client side.
 */
public class ClientGUI extends Canvas
{
	private static final long serialVersionUID = 495654254622340673L;

	/*
	 * The dimensions of the window in virtual pixels.
	 */
	public static final double VIRTUAL_WIDTH = 1280.0, VIRTUAL_HEIGHT = 720.0;

	/*
	 * The maximum length of a message in the output area.
	 */
	public static final int MAX_MESSAGE_LENGTH = 54;

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
		resize(1);
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

		bs.show();
		g.dispose();
	}

	/*
	 * Prints a message to the output area and shifts/aligns everything
	 * accordingly.
	 */
	public void writeMessage(String message)
	{
		while (message.length() > MAX_MESSAGE_LENGTH)
		{
			int endIndex = MAX_MESSAGE_LENGTH;
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
		g.setPaint(new GradientPaint(0, 0, backgroundColor, 0, 320, foregroundColor));
		g.fillRect(8, 20, 328, 664);
		g.setColor(foregroundColor);
		g.fillRect(8, 688, 328, 12);
		g.setColor(backgroundColor);
		for (int i = 0; i < MAX_MESSAGES; i++)
			TextDrawer.drawText(g, messageHistory.get(i), 12, 24 + 8 * i, 1);
		TextDrawer.drawText(g, keyboardInput.keyboardInput, 12, 692, 1);
	}

	/*
	 * Draws the right-side area (plot window).
	 */
	private void drawPlanHistory(Graphics g)
	{
		g.setColor(foregroundColor);
		g.fillRect(988, 104, 284, 596);
		g.setColor(backgroundColor);
		for (int i = 0; i < 7; i++)
			g.fillRect(996 + i * 44, 112, 4, 580);
		for (int i = 0; i < 16; i++)
			g.fillRect(996, 112 + i * 36, 268, 8);
		g.fillRect(996, 688, 268, 4);
		for (int i = 0; i < 6; i++)
			g.drawRect(1000 + i * 44, 116, 40, 36);
	}

	/*
	 * Draws the board.
	 */
	private void drawBoard(Graphics g)
	{
		// g.setColor(foregroundColor);
		// g.fillRect(344, 20, 636, 680);
		g.drawImage(Images.tavernFloor, 344, 20, 636, 680, null);
		g.setColor(backgroundColor);
		for (int i = 0; i < 15; i++)
			g.fillRect(353 + i * 44, 29, 2, 662);
		for (int i = 0; i < 16; i++)
			g.fillRect(353, 29 + i * 44, 618, 2);
		for (Entity entity : board.allEntities(Entity.class))
			for (BoardCoordinate coordinate : entity.coordinates)
			{
				int xDrawPosition = (coordinate.number - 1) * 44 + 355;
				int yDrawPosition = (coordinate.letter - 'a') * 44 + 31;
				switch (entity.type)
				{
					case CHAIR:
						g.setColor(Color.DARK_GRAY);
						g.drawImage(((Chair) entity).getImage(), xDrawPosition, yDrawPosition, 42, 42, null);
						break;
					case PIRATE:
						g.setColor(Color.GREEN);
						g.fillOval(xDrawPosition, yDrawPosition, 42, 40);
						break;
					case MUG:
						g.setColor(Color.BLUE);
						g.drawImage(Images.mug, xDrawPosition, yDrawPosition, 42, 42, null);
						break;
					case DAGGER:
						g.setColor(Color.RED);
						g.fillOval(xDrawPosition, yDrawPosition, 42, 42);
						break;
					case SHELF:
						g.setColor(Color.YELLOW);
						g.fillOval(xDrawPosition, yDrawPosition, 42, 42);
						break;
					case STAIRS:
						break;
					case BALCONY:
						break;
					case WINDOW:
						break;
					case TABLE:
						g.setColor(new Color(0x4D320D));
						g.drawImage(((Table) entity).getDrawImage(coordinate), xDrawPosition, yDrawPosition, 42, 42,
								null);
						break;
					case SWORD:
						g.setColor(Color.WHITE);
						g.fillOval(xDrawPosition, yDrawPosition, 42, 42);
						break;
					case BROKEN_GLASS:
						g.setColor(Color.BLACK);
						g.fillOval(xDrawPosition, yDrawPosition, 42, 42);
						break;
					default:
						throw new RuntimeException("Unreachable code.");
				}
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