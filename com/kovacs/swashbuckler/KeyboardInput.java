package com.kovacs.swashbuckler;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyboardInput implements KeyListener
{
	/*
	 * Holds the contents of the next line to send in once the enter key has
	 * been pressed.
	 */
	private String nextLine;
	
	/*
	 * Stores the characters entered before enter is pressed.
	 */
	public String keyboardInput = "";

	@Override
	public void keyPressed(KeyEvent e)
	{
		System.out.println("Pressed key: " + e.getKeyChar());
		if (e.getKeyCode() == KeyEvent.VK_ENTER)
		{
			nextLine = keyboardInput;
			ClientMain.main.gui.messageHistory.add(nextLine);
			ClientMain.main.gui.messageHistory.poll();
			keyboardInput = "";
		}
		else if (Utility.isPrintableCharacter(e.getKeyChar()))
		{
			if (keyboardInput.length() < 50)
				keyboardInput += e.getKeyChar();
		}
		else if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE)
			keyboardInput = keyboardInput.substring(0, keyboardInput.length() > 0 ? keyboardInput.length()-1 : 0);
	}
	
	@Override
	public void keyReleased(KeyEvent e)
	{
	}

	@Override
	public void keyTyped(KeyEvent e)
	{
	}

	/*
	 * Blocks until another line is sent in.
	 */
	public String nextLine()
	{
		while (nextLine == null)
			try
			{
				// System.out.println(nextLine + " " + keyboardInput);
				Thread.sleep(1);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		String temp = nextLine;
		nextLine = null;
		return temp;
	}
}