package com.kovacs.swashbuckler;

import java.awt.Font;
import java.awt.List;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JWindow;
import com.kovacs.swashbuckler.game.Order;
import com.kovacs.swashbuckler.game.OrderType;

public class Menu extends JWindow implements ItemListener
{
	private static final long serialVersionUID = -7234566321060273518L;

	/*
	 * The list of options on the menu.
	 */
	private List options;

	/*
	 * The menu's location relative to the game window.
	 */
	private int x, y;

	/*
	 * The step that this menu is talking about.
	 */
	private int step;

	public Menu()
	{
		options = new List(4, false);
		options.setFont(new Font("courier", Font.PLAIN, 12));
		add(options);
		setAlwaysOnTop(true);
		setVisible(false);
		options.addItemListener(this);
	}

	public enum MenuType
	{
		PLOT, ACTIONS, MOVEMENT, SWORDPLAY
	}

	/*
	 * Moves the menu to the right location, resizes it, and makes it appear.
	 */
	public void refresh(int x, int y, MenuType menuType)
	{
		options.removeAll();
		switch (menuType)
		{
			case ACTIONS:
				for (Order order : Order.values())
					if (order.getOrderType() == OrderType.ACTION)
						options.add(order.getName());
				break;
			case MOVEMENT:
				for (Order order : Order.values())
					if (order.getOrderType() == OrderType.MOVEMENT)
						options.add(order.getName());
				break;
			case PLOT:
				options.add("Actions");
				options.add("Swordplay");
				options.add("Movement");
				break;
			case SWORDPLAY:
				for (Order order : Order.values())
					if (order.getOrderType() == OrderType.SWORDPLAY)
						options.add(order.getName());
				break;
			default:
				throw new RuntimeException("Unreachable code.");
		}
		options.add("Cancel");
		this.x = x;
		this.y = y;
		setLocation(ClientMain.main.gui.frame.getX() + x, ClientMain.main.gui.frame.getY() + y);
		setSize(155, 16 * options.getItemCount());
		setVisible(true);
	}

	@Override
	public void itemStateChanged(ItemEvent e)
	{
		String option = ((List) (e.getSource())).getSelectedItem();
		switch (option)
		{
			case "Actions":
				refresh(x, y, MenuType.ACTIONS);
				return;
			case "Swordplay":
				refresh(x, y, MenuType.SWORDPLAY);
				return;
			case "Movement":
				refresh(x, y, MenuType.MOVEMENT);
				return;
			default:
				ClientMain.main.updatePlanInProgress(step, Order.valueOf(option.replace(' ', '_').toUpperCase()));
				break;
		}
		setVisible(false);
	}

	/*
	 * Sets the variables for the square that this menu is talking about.
	 */
	public void setStep(int step)
	{
		this.step = step;
	}
}