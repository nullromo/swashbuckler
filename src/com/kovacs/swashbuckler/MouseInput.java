package com.kovacs.swashbuckler;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import com.kovacs.swashbuckler.Menu.MenuType;
import com.kovacs.swashbuckler.game.BoardCoordinate;
import com.kovacs.swashbuckler.game.entity.Entity;
import com.kovacs.swashbuckler.game.entity.Entity.EntityType;
import com.kovacs.swashbuckler.game.entity.Pirate;

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
		ClientMain.main.gui.menu.close();
		if (ClientMain.main.currentTurn == 0)
			return;
		System.out.println("Released mouse at " + mouseX + ", " + mouseY);
		// handle board clicks
		if (356 <= mouseX && mouseX <= 968 && 32 <= mouseY && mouseY <= 688)
		{
			ClientMain.main.deselectPirate();
			int number = (mouseX - 356) / 44 + 1;
			char letter = (char) ((mouseY - 32) / 44 + 'a');
			System.out.println("Clicked square " + number + "" + letter);
			for (Entity entity : ClientMain.main.gui.board.getEntities(new BoardCoordinate(letter, number)))
			{
				System.out.println("  " + entity);
				if (entity.type == EntityType.PIRATE)
					ClientMain.main.selectPirate((Pirate) entity);
			}
		}
		if (ClientMain.main.getSelectedPirate() != null)
		{
			if (!ClientMain.main.getPlanInProgress().isLocked())
			{
				// handle planning clicks
				if (1001 <= mouseX && mouseX <= 1260 && 157 <= mouseY && mouseY <= 688)
				{
					int step = (mouseX - 999) / 44 + 1;
					int turn = (mouseY - 153) / 36 + 1;
					System.out.println("Clicked plot window turn " + turn + ", step " + step);
					if (turn != ClientMain.main.currentTurn)
						return;
					ClientMain.main.gui.menu.setStep(step);
					ClientMain.main.gui.menu.refresh(mouseX, mouseY, MenuType.PLOT);
				}
				// handle plan submit clicks
				if (988 <= mouseX && mouseX <= 1112 && 64 <= mouseY && mouseY <= 96)
				{
					ClientMain.main.submitPlan();
				}
			}
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