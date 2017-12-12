package com.kovacs.swashbuckler;

import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

/*
 * This class just statically loads all the images so that they can be accessed
 * from anywhere. There aren't that many and they aren't that large, so this
 * isn't a problem.
 */
public class Images
{
	public static final BufferedImage table_NS = loadImage("table_NS.png"), table_EW = loadImage("table_EW.png"),
			table_S_long = loadImage("table_S_long.png"), table_N_long = loadImage("table_N_long.png"),
			table_E_long = loadImage("table_E_long.png"), table_W_long = loadImage("table_W_long.png"),
			table_S_short = loadImage("table_S_short.png"), table_N_short = loadImage("table_N_short.png"),
			table_E_short = loadImage("table_E_short.png"), table_W_short = loadImage("table_W_short.png"),
			tavernFloor = loadImage("tavern_floor.png"), mug = loadImage("mug.png"), chair_N = loadImage("chair_N.png"),
			chair_S = loadImage("chair_S.png"), chair_W = loadImage("chair_W.png"), chair_E = loadImage("chair_E.png"),
			sword = loadImage("sword.png"), dagger = loadImage("dagger.png"),
			brokenGlass = loadImage("broken_glass.png"), shelf_W_top = loadImage("shelf_W_top.png"),
			shelf_W_bottom = loadImage("shelf_W_bottom.png"), shelf_N_left = loadImage("shelf_N_left.png"),
			shelf_N_right = loadImage("shelf_N_right.png"), shelf_E_top = loadImage("shelf_E_top.png"),
			shelf_E_bottom = loadImage("shelf_E_bottom.png"), shelf_S_right = loadImage("shelf_S_right.png"),
			shelf_S_left = loadImage("shelf_S_left.png"), chandelier = loadImage("chandelier.png"),
			carpet_NS = loadImage("carpet_NS.png"), carpet_EW = loadImage("carpet_EW.png");

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