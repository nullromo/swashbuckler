package com.kovacs.swashbuckler3;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;

/*
 * Represents one packet of information that the engine needs from a player.
 */
public class Request implements ActionListener, Comparable<Request>
{
	/*
	 * The player that needs to fill this request.
	 */
	private Player player;

	/*
	 * The fillable that needs to be filled.
	 */
	private Fillable fillable;

	/*
	 * The target object that this request is trying to build.
	 */
	private Requestable target;

	/*
	 * A way for the engine and the player to tag requests with some extra data.
	 */
	public String message;

	// A request can be UNFILLED, and then a player marks it as FILLED and sends
	// it back. Then if there are issues, the engine marks it as ERROR and
	// unfills the proper portions. Then the player can refill them properly.
	// Finally, once the engine wants to accept the request, it sends back an
	// empty FILLED state request.
	public enum RequestStatus
	{
		ERROR, FILLED, UNFILLED
	};

	/*
	 * The status of this request.
	 */
	public RequestStatus requestStatus = RequestStatus.UNFILLED;

	/*
	 * The gui for this request.
	 */
	GUIRequest gui;

	public Request(Player player, Requestable target, Fillable fillable)
	{
		this.player = player;
		this.target = target;
		this.fillable = fillable;
	}

	@Override
	public int compareTo(Request other)
	{
		return this.requestStatus.ordinal() - other.requestStatus.ordinal();
	}

	/*
	 * Sends the request to the player.
	 */
	public void send()
	{
		player.put(this);
	}

	/*
	 * Creates the GUI.
	 */
	public void createGUI()
	{
		gui = new GUIRequest(player.toString());
		gui.submitButton.addActionListener(this);
		gui.create(fillable);
	}

	/*
	 * Closes the GUI.
	 */
	public void closeGUI()
	{
		gui.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		gui.dispatchEvent(new WindowEvent(gui, WindowEvent.WINDOW_CLOSING));
	}

	/*
	 * Returns true if the required information has been filled.
	 */
	public boolean isComplete()
	{
		return fillable.isFilled();
	}

	public Requestable getTarget()
	{
		return target;
	}

	public Fillable getFillable()
	{
		return fillable;
	}

	public Player getPlayer()
	{
		return player;
	}

	/*
	 * Enables and Disables the GUI.
	 */
	public void setGUIEnabled(boolean enabled)
	{
		gui.setEnabled(enabled);
	}

	/*
	 * Sets the GUI's message for the viewer to see.
	 */
	public void setGUIMessage(String s)
	{
		gui.setMessage(s);
		gui.pack();
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		setGUIEnabled(false);
		for (JPanel p : gui.neededItems)
		{
			String name = ((JLabel) p.getComponent(0)).getText();
			Object valueHolder = p.getComponent(1);
			Object value = null;
			if (valueHolder instanceof JLabel)
				value = ((JLabel) valueHolder).getText();
			else if (valueHolder instanceof JSpinner)
				value = ((JSpinner) valueHolder).getValue();
			else if (valueHolder instanceof JTextField)
			{
				value = ((JTextField) valueHolder).getText();
			}
			fillable.fill(name, value);
		}
		player.send(this);
	}
}