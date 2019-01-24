package com.kovacs.swashbuckler3;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import com.kovacs.swashbuckler3.PirateData.Dexterity;

/*
 * A pop-up for filling out requests.
 */
// TODO: this will eventually probably go away or be massively changed once the
// graphics phase begins.
public class GUIRequest extends JFrame
{
	private static final long serialVersionUID = 395704055704493561L;

	/*
	 * The main panel in the GUI.
	 */
	JPanel panel;

	/*
	 * The button that submits the response.
	 */
	JButton submitButton = new JButton("Submit");

	/*
	 * An extra message for the viewer.
	 */
	JLabel message = new JLabel();

	/*
	 * List of all the panels (containing [JLabel, Thing] where Thing is the
	 * target object) that need to be filled out.
	 */
	List<JPanel> neededItems = new ArrayList<>();

	public GUIRequest(String name)
	{
		super(name);
	}

	public GUIRequest()
	{
		this("");
	}

	/*
	 * Adds all the stuff to the GUI and shows it on the screen.
	 */
	public void create(Fillable fillable)
	{
		panel = new JPanel();
		for (String s : fillable.getKeys())
		{
			JPanel component = createComponent(s, fillable.get(s));
			if (component != null)
				panel.add(component);
		}
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		message.setForeground(Color.RED);
		panel.add(message);
		panel.add(submitButton);
		add(panel);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		pack();
		setVisible(true);
	}

	/*
	 * Creates a GUI component allowing the user to fill in a specific fillable
	 * item.
	 */
	private JPanel createComponent(String name, Object o)
	{
		JPanel box = new JPanel();
		box.setLayout(new GridLayout());
		box.add(new JLabel(name));
		if (o instanceof Class<?>)
			neededItems.add(box);
		if (o.equals(Integer.class))
		{
			box.add(new JSpinner());
		}
		else if (o instanceof Integer)
		{
			JSpinner v = new JSpinner(
					new SpinnerNumberModel(((Integer) o).intValue(), Integer.MIN_VALUE, Integer.MAX_VALUE, 0));
			box.add(v);
		}
		else if (o.equals(String.class))
		{
			box.add(new JTextField());
		}
		else if (o.equals(Dexterity.class))
		{
			box.add(new JComboBox<>(Dexterity.values()));
		}
		else if (o instanceof Dexterity)
		{
			box.add(new JComboBox<Dexterity>(new Dexterity[] { (Dexterity) o }));
		}
		else
			throw new RuntimeException("Unsupported GUI Fillable type: " + o);
		return box;
	}

	/*
	 * Changes the message displayed to the viewer.
	 */
	public void setMessage(String s)
	{
		message.setText(s);
	}
}