package com.kovacs.swashbuckler3;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;

/*
 * Represents one packet of information that the engine needs from a player.
 */
public class Request implements WindowListener, ActionListener
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
	 * True if the request has been sent and no reply has been received.
	 */
	private boolean pending;

	/*
	 * The gui for this request.
	 */
	GUIRequest gui;

	public Request(Player player, Fillable fillable)
	{
		this.player = player;
		this.fillable = fillable;
	}

	/*
	 * Returns true if the required information has been filled.
	 */
	public boolean isComplete()
	{
		return fillable.isFilled();
	}

	/*
	 * Sends the request to the player.
	 */
	public void send()
	{
		pending = true;
		gui = new GUIRequest(player.toString());
		gui.addWindowListener(this);
		gui.submitButton.addActionListener(this);
		gui.create(fillable);
	}

	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		for (JPanel p : gui.neededItems)
		{
			String name = ((JLabel) p.getComponent(0)).getText().toLowerCase().replace(' ', '_');
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
		gui.dispatchEvent(new WindowEvent(gui, WindowEvent.WINDOW_CLOSING));
		pending = false;
	}

	@Override
	public void windowActivated(WindowEvent e)
	{
	}

	@Override
	public void windowClosed(WindowEvent e)
	{
	}

	@Override
	public void windowClosing(WindowEvent e)
	{
		pending = false;
	}

	@Override
	public void windowDeactivated(WindowEvent e)
	{
	}

	@Override
	public void windowDeiconified(WindowEvent e)
	{
	}

	@Override
	public void windowIconified(WindowEvent e)
	{
	}

	@Override
	public void windowOpened(WindowEvent e)
	{
	}

	public boolean isPending()
	{
		return pending;
	}
}