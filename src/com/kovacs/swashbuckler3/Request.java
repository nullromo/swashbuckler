package com.kovacs.swashbuckler3;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;

/*
 * Represents one packet of information that the engine needs from a player.
 */
public class Request implements ActionListener
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

	/*
	 * True once the request has been submitted by the user.
	 */
	private boolean submitted;

	/*
	 * The gui for this request.
	 */
	GUIRequest gui;

	public Request(Player player, Requestable target, Fillable fillable)
	{
		this.player = player;
		this.target = target;
		this.fillable = fillable;
		if (target instanceof PirateData)
			message = "Constitution = " + ((PirateData) target).getConstitution();
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		message = "";
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
				value = ((JTextField) valueHolder).getText();
			else if (valueHolder instanceof JComboBox<?>)
				value = ((JComboBox<?>) valueHolder).getSelectedItem();
			fillable.fill(name, value);
		}
		submitted = true;
		player.send(this);
		closeGUI();
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
		setGUIMessage(message);
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
	 * Should be called when the request was invalid and needs to be sent back
	 * to the user.
	 */
	public void reset()
	{
		submitted = false;
	}

	/*
	 * Returns true if the request has been submitted by the user.
	 */
	public boolean isSubmitted()
	{
		return submitted;
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

	@Override
	public String toString()
	{
		return "<" + player + "," + target.getClass().getSimpleName() + "," + message + "," + fillable + ">";
	}
}