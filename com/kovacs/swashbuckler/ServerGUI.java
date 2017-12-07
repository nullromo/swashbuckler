package com.kovacs.swashbuckler;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.text.DefaultCaret;

public class ServerGUI
{
	/*
	 * The frame that holds the server gui.
	 */
	private JFrame frame;

	/*
	 * The text box where all the messages show up.
	 */
	private JTextArea output;

	public ServerGUI()
	{
		frame = new JFrame("Swashbuckler Server");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		output = new JTextArea();
		output.setEditable(false);
		output.setFont(new Font("courier new", Font.BOLD, 14));
		output.setBackground(Color.BLACK);
		output.setForeground(Color.GREEN);
		((DefaultCaret) output.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		JScrollPane pane = new JScrollPane(output);
		pane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		pane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		pane.setPreferredSize(new Dimension(590, 390));
		frame.add(pane);
		frame.pack();
		// frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	/*
	 * Writes a string to the console window.
	 */
	public void write(Object object)
	{
		output.append(object.toString() + "\n");
		// System.out.println(object.toString());
	}
}