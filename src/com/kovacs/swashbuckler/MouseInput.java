package com.kovacs.swashbuckler;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import com.kovacs.swashbuckler.game.BoardCoordinate;
import com.kovacs.swashbuckler.game.entity.Entity;

public class MouseInput implements MouseMotionListener, MouseListener
{
	/*
	 * The virtual coordinates of the mouse.
	 */
	private int mouseX, mouseY;

	@Override
	public void mouseClicked(MouseEvent e)
	{
	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
	}

	@Override
	public void mouseExited(MouseEvent e)
	{
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		System.out.println("Mouse at " + mouseX + ", " + mouseY);
		if (356 <= mouseX && mouseX <= 968 && 32 <= mouseY && mouseY <= 688)
		{
			int number = (mouseX - 356) / 44 + 1;
			char letter = (char) ((mouseY - 32) / 44 + 'a');
			System.out.println("Clicked square " + number + "" + letter);
			for (Entity entity : ClientMain.main.gui.board.getEntities(new BoardCoordinate(letter, number)))
				System.out.println("  " + entity);
		}
	}

	@Override
	public void mouseDragged(MouseEvent e)
	{
	}

	@Override
	public void mouseMoved(MouseEvent e)
	{
		mouseX = (int) ((e.getX() + 1) * ClientGUI.VIRTUAL_WIDTH / ClientMain.main.gui.getWidth());
		mouseY = (int) ((e.getY() + 1) * ClientGUI.VIRTUAL_HEIGHT / ClientMain.main.gui.getHeight());
	}
}