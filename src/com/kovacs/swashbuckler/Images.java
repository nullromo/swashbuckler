package com.kovacs.swashbuckler;

import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

/*
 * This class just statically loads all the images so that they can be accessed
 * from anywhere.
 */
public class Images
{
	public static final BufferedImage chair = loadImage("chair.png"), table_S = loadImage("table_S.png"),
			table_NS = loadImage("table_NS.png"), table_N = loadImage("table_N.png"),
			table_E = loadImage("table_E.png"), table_W = loadImage("table_W.png"),
			table_EW = loadImage("table_EW.png"), tavernFloor = loadImage("tavern_floor.png");

	/*
	 * Loads and returns an image
	 */
	public static BufferedImage loadImage(String filepath)
	{
		try
		{
			return ImageIO.read(Utility.class.getResourceAsStream("/" + filepath));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return null;
	}
}