package com.kovacs.swashbuckler;

import java.awt.BasicStroke;
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
import com.kovacs.swashbuckler.game.Plan;
import com.kovacs.swashbuckler.game.entity.Entity;
import com.kovacs.swashbuckler.game.entity.Entity.EntityType;
import com.kovacs.swashbuckler.game.entity.Pirate;
import com.kovacs.swashbuckler.game.entity.Shelf;
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
	public static final int MAX_MESSAGE_LENGTH_LARGE = 27;
	public static final int MAX_MESSAGE_LENGTH_SMALL = 54;

	/*
	 * Keeps track of the text size setting.
	 */
	// TODO: make it so that the user can adjust this.
	private static final boolean largeText = false;

	/*
	 * Default colors for things. //TODO: these will probably go away
	 * eventually.
	 */
	private static final Color backgroundColor = Color.BLACK, foregroundColor = new Color(0x666666),
			redTint = new Color(255, 0, 0, 100), greenTint = new Color(0, 255, 0, 100),
			yellowTint = new Color(255, 255, 0, 100);

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
	private static final int MAX_MESSAGES_LARGE = 47;
	private static final int MAX_MESSAGES_SMALL = 82;

	/*
	 * The main window of the gui.
	 */
	public JFrame frame;

	/*
	 * The mouse input adapter.
	 */
	private MouseInput mouseInput;

	/*
	 * The keyboard input adapter.
	 */
	public KeyboardInput keyboardInput;

	/*
	 * An instance of a board that is given to the client by the server on board
	 * updates.
	 */
	public Board board;

	/*
	 * The thread that adds the characters to the output slowly.
	 */
	private Thread characterAdder;

	/*
	 * The thing that pops up when you click.
	 */
	public Menu menu;

	/*
	 * Keeps track of how big the window is.
	 */
	private double scale;

	public ClientGUI()
	{
		messageHistory = new LinkedList<>();
		for (int i = 0; i < (largeText ? MAX_MESSAGES_LARGE : MAX_MESSAGES_SMALL); i++)
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
		menu = new Menu();
		keyboardInput = new KeyboardInput();
		mouseInput = new MouseInput();
		addMouseListener(mouseInput);
		addMouseMotionListener(mouseInput);
		addKeyListener(keyboardInput);
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
		while (message.length() > (largeText ? MAX_MESSAGE_LENGTH_LARGE : MAX_MESSAGE_LENGTH_SMALL))
		{
			int endIndex = largeText ? MAX_MESSAGE_LENGTH_LARGE : MAX_MESSAGE_LENGTH_SMALL;
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
		g.fillRect(8, 20, 328, (largeText ? 658 : 664));
		g.setColor(foregroundColor);
		g.fillRect(8, (largeText ? 682 : 688), 328, (largeText ? 18 : 12));
		g.setColor(backgroundColor);
		for (int i = 0; i < (largeText ? MAX_MESSAGES_LARGE : MAX_MESSAGES_SMALL); i++)
			TextDrawer.drawText(g, messageHistory.get(i), 12, (largeText ? 18 : 24) + (largeText ? 14 : 8) * i,
					largeText ? 2 : 1);
		TextDrawer.drawText(g, keyboardInput.keyboardInput, 12, largeText ? 686 : 692, largeText ? 2 : 1);
	}

	/*
	 * Draws the right-side area (plot window).
	 */
	private void drawPlanHistory(Graphics g)
	{
		// TODO: only draw the clickable stuff and only allow clicks if the game
		// is ready for that type of click.
		if (ClientMain.main.getSelectedPirate() == null)
			return;
		// TODO: decide on and standardize how everyone knows what pirates exist
		// and where their plan histories are stored.
		if (!ClientMain.main.getOwnedPirateNames().contains(ClientMain.main.getSelectedPirate().getName()))
			return;
		// TODO: control access to things so that I don't have to keep typing
		// ClientMain.main.gui and such
		// draw background
		g.setColor(foregroundColor);
		g.fillRect(988, 104, 284, 596);
		// tint future turns red
		g.setColor(redTint);
		g.fillRect(1000, 156 + ClientMain.main.currentTurn * 36, 260, 532 - ClientMain.main.currentTurn * 36);
		// tint the current turn
		if (ClientMain.main.getPlanInProgress().isLocked())
			g.setColor(greenTint);
		else
			g.setColor(yellowTint);
		g.fillRect(1000, 120 + ClientMain.main.currentTurn * 36, 260, 28);
		// draw all the boxes
		g.setColor(backgroundColor);
		for (int i = 0; i < 7; i++)
			g.fillRect(996 + i * 44, 112, 4, 580);
		for (int i = 0; i < 16; i++)
			g.fillRect(996, 112 + i * 36, 268, 8);
		g.fillRect(996, 688, 268, 4);
		for (int i = 0; i < 6; i++)
			g.drawRect(1000 + i * 44, 116, 40, 36);
		for (int i = 1; i <= 6; i++)
			TextDrawer.drawText(g, "" + i, 1010 + (i - 1) * 44, 124, 4);
		// fill in the plan history
		for (int turn = 0; turn < ServerMain.MAX_TURNS; turn++)
		{
			if (ClientMain.main.getSelectedPirate() == null)
				return;
			Plan plan = ClientMain.main.planHistory.get(ClientMain.main.getSelectedPirate().getName())[turn];
			if (plan == null)
				continue;
			for (int i = 0; i < plan.getOrders().length; i++)
				TextDrawer.drawText(g, plan.getOrders()[i].getAbbreviation(), 1003 + i * 44, 128 + 36 * turn, 2);
		}
		// draw the submit button
		g.setColor(foregroundColor);
		g.fillRect(988, 64, 124, 32);
		g.setColor(backgroundColor);
		TextDrawer.drawText(g, "Submit", 998, 72, 3);
	}

	/*
	 * Draws the board.
	 */
	private void drawBoard(Graphics g)
	{
		if (board == null)
			return;
		g.drawImage(Images.tavernFloor, 344, 20, 636, 680, null);
		for (Entity entity : board.allEntities(Entity.class))
			for (BoardCoordinate coordinate : entity.coordinates)
			{
				int xDrawPosition = (coordinate.number - 1) * 44 + 355;
				int yDrawPosition = (coordinate.letter - 'a') * 44 + 31;
				if (entity.type == EntityType.TABLE)
					g.drawImage(((Table) entity).getDrawImage(coordinate), xDrawPosition, yDrawPosition, 42, 42, null);
				else if (entity.type == EntityType.SHELF)
					g.drawImage(((Shelf) entity).getDrawImage(coordinate), xDrawPosition, yDrawPosition, 42, 42, null);
				else if (entity.type == EntityType.PIRATE && ClientMain.main.getSelectedPirate() == (Pirate) entity)
				{
					((Graphics2D) g).setStroke(new BasicStroke(2));
					g.setColor(Color.YELLOW);
					g.drawRect(xDrawPosition - 1, yDrawPosition - 1, 44, 44);
					g.drawImage(entity.getImage(), xDrawPosition, yDrawPosition, 42, 42, null);
				}
				else
					g.drawImage(entity.getImage(), xDrawPosition, yDrawPosition, 42, 42, null);
			}
	}

	/*
	 * Resizes the window. Scale of 1 sets it to the default.
	 */
	public void resize(double scale)
	{
		if (scale < .1)
			scale = .1;
		this.scale = scale;
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
				messageHistory.set((largeText ? MAX_MESSAGES_LARGE : MAX_MESSAGES_SMALL) - 1, messageHistory
						.get((largeText ? MAX_MESSAGES_LARGE : MAX_MESSAGES_SMALL) - 1).concat(nextChar + ""));
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

	public double getScale()
	{
		return scale;
	}
}