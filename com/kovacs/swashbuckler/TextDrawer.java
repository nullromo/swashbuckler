package com.kovacs.swashbuckler;

import java.awt.Graphics;

/*
 * Creates text out of rectangles. Exposes only 1 method that draws on a
 * graphics object.
 */
public class TextDrawer
{
	/*
	 * Internal fields for the text drawer to use.
	 */
	private static Graphics _g;
	private static String _text;
	private static int _x, _y;
	private static int _size;

	/*
	 * Makes the given text appear at (x,y) with a particular size.
	 */
	public static void drawText(Graphics g, String text, int x, int y, int size)
	{
		_g = g;
		_text = text.toUpperCase();
		_y = y;
		_size = size;
		_x = x;

		for (int i = 0; i < _text.length(); i++)
		{
			_x = x + i * 6 * size;
			drawLetter(i);
		}
	}

	/*
	 * Draws an individual letter.
	 */
	private static void drawLetter(int index)
	{
		// If the offset is 0, then the letter is 5 virtual pixels wide. If it
		// is _size/2, then the letter is 4 virtual pixels wide.
		boolean narrowChar = false;
		char letter = _text.charAt(index);
		switch (letter)
		{
			case 'A':
				fillRectangle(narrowChar, 2, 0, 1, 2);// top
				fillRectangle(narrowChar, 1, 1, 1, 2);// up left
				fillRectangle(narrowChar, 3, 1, 1, 2);// up right
				fillRectangle(narrowChar, 0, 2, 1, 3);// left
				fillRectangle(narrowChar, 4, 2, 1, 3);// right
				fillRectangle(narrowChar, 2, 3, 1, 1);// middle
				break;
			case 'B':
				narrowChar = true;
				fillRectangle(narrowChar, 0, 0, 1, 5);// left
				fillRectangle(narrowChar, 1, 0, 2, 1);// top
				fillRectangle(narrowChar, 1, 2, 2, 1);// mid
				fillRectangle(narrowChar, 1, 4, 2, 1);// bottom
				fillRectangle(narrowChar, 3, 1, 1, 1);// top nib
				fillRectangle(narrowChar, 3, 3, 1, 1);// bottom nib
				break;
			case 'C':
				narrowChar = true;
				fillRectangle(narrowChar, 0, 0, 4, 1);
				;// top
				fillRectangle(narrowChar, 0, 1, 1, 3);// left
				fillRectangle(narrowChar, 0, 4, 4, 1);// bottom
				break;
			case 'D':
				narrowChar = true;
				fillRectangle(narrowChar, 0, 0, 3, 1);// top
				fillRectangle(narrowChar, 0, 1, 1, 3);// left
				fillRectangle(narrowChar, 0, 4, 3, 1);// bottom
				fillRectangle(narrowChar, 3, 1, 1, 3);// right
				break;
			case 'E':
				narrowChar = true;
				fillRectangle(narrowChar, 0, 0, 1, 5);// left
				fillRectangle(narrowChar, 1, 0, 3, 1);// top
				fillRectangle(narrowChar, 1, 2, 2, 1);// middle
				fillRectangle(narrowChar, 1, 4, 3, 1);// bottom
				break;
			case 'F':
				narrowChar = true;
				fillRectangle(narrowChar, 0, 0, 1, 5);// left
				fillRectangle(narrowChar, 1, 0, 3, 1);// top
				fillRectangle(narrowChar, 1, 2, 2, 1);// middle
				break;
			case 'G':
				narrowChar = true;
				fillRectangle(narrowChar, 0, 0, 4, 1);// top
				fillRectangle(narrowChar, 0, 1, 1, 3);// left
				fillRectangle(narrowChar, 0, 4, 4, 1);// bottom
				fillRectangle(narrowChar, 3, 2, 1, 3);// right
				fillRectangle(narrowChar, 2, 2, 1, 1);// nib
				break;
			case 'H':
				narrowChar = true;
				fillRectangle(narrowChar, 0, 0, 1, 5);// left
				fillRectangle(narrowChar, 1, 2, 2, 1);// middle
				fillRectangle(narrowChar, 3, 0, 1, 5);// right
				break;
			case 'I':
				fillRectangle(narrowChar, 1, 0, 3, 1);// top
				fillRectangle(narrowChar, 2, 1, 1, 3);// middle
				fillRectangle(narrowChar, 1, 4, 3, 1);// bottom
				break;
			case 'J':
				fillRectangle(narrowChar, 1, 0, 3, 1);// top
				fillRectangle(narrowChar, 2, 1, 1, 3);// middle
				fillRectangle(narrowChar, 0, 4, 3, 1);// bottom
				break;
			case 'K':
				narrowChar = true;
				fillRectangle(narrowChar, 0, 0, 1, 5);// left
				fillRectangle(narrowChar, 1, 2, 2, 1);// middle
				fillRectangle(narrowChar, 3, 0, 1, 2);// top right
				fillRectangle(narrowChar, 3, 3, 1, 2);// bottom right
				break;
			case 'L':
				narrowChar = true;
				fillRectangle(narrowChar, 0, 0, 1, 5);// left
				fillRectangle(narrowChar, 1, 4, 3, 1);// bottom
				break;
			case 'M':
				fillRectangle(narrowChar, 0, 0, 1, 5);// column 1
				fillRectangle(narrowChar, 1, 0, 1, 2);// column 2
				fillRectangle(narrowChar, 2, 1, 1, 2);// column 3
				fillRectangle(narrowChar, 3, 0, 1, 2);// column 4
				fillRectangle(narrowChar, 4, 0, 1, 5);// column 5
				break;
			case 'N':
				fillRectangle(narrowChar, 0, 0, 1, 5);// column 1
				fillRectangle(narrowChar, 1, 0, 1, 2);// column 2
				fillRectangle(narrowChar, 2, 2, 1, 1);// column 3
				fillRectangle(narrowChar, 3, 3, 1, 1);// column 4
				fillRectangle(narrowChar, 4, 0, 1, 5);// column 5
				break;
			case 'O':
				narrowChar = true;
				fillRectangle(narrowChar, 0, 0, 4, 1);// top
				fillRectangle(narrowChar, 0, 1, 1, 3);// left
				fillRectangle(narrowChar, 3, 1, 1, 3);// right
				fillRectangle(narrowChar, 0, 4, 4, 1);// bottom
				break;
			case 'P':
				narrowChar = true;
				fillRectangle(narrowChar, 0, 0, 1, 5);// left
				fillRectangle(narrowChar, 1, 0, 3, 1);// top
				fillRectangle(narrowChar, 3, 0, 1, 3);// right
				fillRectangle(narrowChar, 1, 2, 3, 1);// bottom
				break;
			case 'Q':
				fillRectangle(narrowChar, 0, 0, 4, 1);// top
				fillRectangle(narrowChar, 0, 1, 1, 3);// left
				fillRectangle(narrowChar, 3, 1, 1, 3);// right
				fillRectangle(narrowChar, 0, 4, 5, 1);// bottom
				fillRectangle(narrowChar, 2, 3, 1, 1);// nib
				break;
			case 'R':
				narrowChar = true;
				fillRectangle(narrowChar, 0, 0, 1, 5);// left
				fillRectangle(narrowChar, 1, 0, 2, 1);// top
				fillRectangle(narrowChar, 3, 1, 1, 2);// right
				fillRectangle(narrowChar, 1, 2, 2, 1);// bottom
				fillRectangle(narrowChar, 2, 3, 1, 1);// nib 1
				fillRectangle(narrowChar, 3, 4, 1, 1);// nib 2
				break;
			case 'S':
				narrowChar = true;
				fillRectangle(narrowChar, 0, 0, 4, 1);// top
				fillRectangle(narrowChar, 0, 1, 1, 1);// left
				fillRectangle(narrowChar, 0, 2, 4, 1);// middle
				fillRectangle(narrowChar, 3, 3, 1, 1);// right
				fillRectangle(narrowChar, 0, 4, 4, 1);// bottom
				break;
			case 'T':
				fillRectangle(narrowChar, 0, 0, 5, 1);// top
				fillRectangle(narrowChar, 2, 1, 1, 4);// middle
				break;
			case 'U':
				narrowChar = true;
				fillRectangle(narrowChar, 0, 0, 1, 5);// left
				fillRectangle(narrowChar, 1, 4, 3, 1);// bottom
				fillRectangle(narrowChar, 3, 0, 1, 5);// right
				break;
			case 'V':
				fillRectangle(narrowChar, 0, 0, 1, 2);// column 1
				fillRectangle(narrowChar, 1, 2, 1, 2);// column 2
				fillRectangle(narrowChar, 2, 4, 1, 1);// column 3
				fillRectangle(narrowChar, 3, 2, 1, 2);// column 4
				fillRectangle(narrowChar, 4, 0, 1, 2);// column 5
				break;
			case 'W':
				fillRectangle(narrowChar, 0, 0, 1, 5);// column 1
				fillRectangle(narrowChar, 1, 3, 1, 2);// column 2
				fillRectangle(narrowChar, 2, 2, 1, 2);// column 3
				fillRectangle(narrowChar, 3, 3, 1, 2);// column 4
				fillRectangle(narrowChar, 4, 0, 1, 5);// column 5
				break;
			case 'X':
				narrowChar = true;
				fillRectangle(narrowChar, 0, 0, 1, 2);// top left
				fillRectangle(narrowChar, 3, 0, 1, 2);// top right
				fillRectangle(narrowChar, 0, 3, 1, 2);// bottom left
				fillRectangle(narrowChar, 3, 3, 1, 2);// bottom right
				fillRectangle(narrowChar, 1, 2, 2, 1);// middle
				break;
			case 'Y':
				fillRectangle(narrowChar, 0, 0, 1, 2);// column 1
				fillRectangle(narrowChar, 1, 2, 1, 1);// column 2
				fillRectangle(narrowChar, 2, 3, 1, 2);// column 3
				fillRectangle(narrowChar, 3, 2, 1, 1);// column 4
				fillRectangle(narrowChar, 4, 0, 1, 2);// column 5
				break;
			case 'Z':
				fillRectangle(narrowChar, 0, 0, 4, 1);// row 1
				fillRectangle(narrowChar, 2, 1, 2, 1);// row 2
				fillRectangle(narrowChar, 1, 2, 2, 1);// row 3
				fillRectangle(narrowChar, 0, 3, 2, 1);// row 4
				fillRectangle(narrowChar, 0, 4, 4, 1);// row 5
				break;
			case ':':
				fillRectangle(narrowChar, 2, 1, 1, 1);// top
				fillRectangle(narrowChar, 2, 3, 1, 1);// bottom
				break;
			case '0':
				fillRectangle(narrowChar, 1, 0, 3, 1);// top
				fillRectangle(narrowChar, 1, 4, 3, 1);// bottom
				fillRectangle(narrowChar, 0, 1, 1, 3);// left
				fillRectangle(narrowChar, 4, 1, 1, 3);// right
				fillRectangle(narrowChar, 3, 1, 1, 1);// left slash
				fillRectangle(narrowChar, 2, 2, 1, 1);// middle slash
				fillRectangle(narrowChar, 1, 3, 1, 1);// right slash
				break;
			case '1':
				fillRectangle(narrowChar, 1, 0, 1, 1);// top
				fillRectangle(narrowChar, 2, 0, 1, 5);// middle
				fillRectangle(narrowChar, 1, 4, 3, 1);// bottom
				break;
			case '2':
				narrowChar = true;
				fillRectangle(narrowChar, 0, 0, 4, 1);// top
				fillRectangle(narrowChar, 0, 3, 1, 1);// left
				fillRectangle(narrowChar, 0, 2, 4, 1);// middle
				fillRectangle(narrowChar, 3, 1, 1, 1);// right
				fillRectangle(narrowChar, 0, 4, 4, 1);// bottom
				break;
			case '3':
				narrowChar = true;
				fillRectangle(narrowChar, 3, 0, 1, 5);// right
				fillRectangle(narrowChar, 0, 0, 3, 1);// top
				fillRectangle(narrowChar, 1, 2, 2, 1);// middle
				fillRectangle(narrowChar, 0, 4, 3, 1);// bottom
				break;
			case '4':
				narrowChar = true;
				fillRectangle(narrowChar, 0, 0, 1, 3);// left
				fillRectangle(narrowChar, 1, 2, 3, 1);// horizontal
				fillRectangle(narrowChar, 2, 1, 1, 4);// vertical
				break;
			case '5':
				fillRectangle(narrowChar, 1, 0, 3, 1);// top
				fillRectangle(narrowChar, 1, 1, 1, 1);// left
				fillRectangle(narrowChar, 1, 2, 3, 1);// middle
				fillRectangle(narrowChar, 3, 3, 1, 1);// right
				fillRectangle(narrowChar, 1, 4, 3, 1);// bottom
				break;
			case '6':
				narrowChar = true;
				fillRectangle(narrowChar, 1, 0, 3, 1);// top
				fillRectangle(narrowChar, 0, 0, 1, 5);// left
				fillRectangle(narrowChar, 1, 2, 3, 1);// middle
				fillRectangle(narrowChar, 3, 3, 1, 1);// right
				fillRectangle(narrowChar, 1, 4, 3, 1);// bottom
				break;
			case '7':
				narrowChar = true;
				fillRectangle(narrowChar, 0, 0, 4, 1);// top
				fillRectangle(narrowChar, 3, 1, 1, 1);// right
				fillRectangle(narrowChar, 2, 2, 1, 1);// middle
				fillRectangle(narrowChar, 1, 3, 1, 2);// bottom
				break;
			case '8':
				narrowChar = true;
				fillRectangle(narrowChar, 0, 0, 1, 5);// left
				fillRectangle(narrowChar, 3, 0, 1, 5);// right
				fillRectangle(narrowChar, 1, 0, 2, 1);// top
				fillRectangle(narrowChar, 1, 2, 2, 1);// middle
				fillRectangle(narrowChar, 1, 4, 2, 1);// bottom
				break;
			case '9':
				narrowChar = true;
				fillRectangle(narrowChar, 0, 0, 1, 3);// left
				fillRectangle(narrowChar, 3, 0, 1, 5);// right
				fillRectangle(narrowChar, 1, 0, 2, 1);// top
				fillRectangle(narrowChar, 1, 2, 2, 1);// middle
				fillRectangle(narrowChar, 0, 4, 3, 1);// bottom
				break;
			case '?':
				fillRectangle(narrowChar, 0, 0, 1, 2);// left
				fillRectangle(narrowChar, 1, 0, 4, 1);// top
				fillRectangle(narrowChar, 4, 1, 1, 2);// right
				fillRectangle(narrowChar, 2, 2, 2, 1);// middle
				fillRectangle(narrowChar, 2, 4, 1, 1);// bottom
				break;
			case '#':
				fillRectangle(narrowChar, 1, 0, 1, 5);// left
				fillRectangle(narrowChar, 3, 0, 1, 5);// right
				fillRectangle(narrowChar, 0, 1, 5, 1);// top
				fillRectangle(narrowChar, 0, 3, 5, 1);// bottom
				break;
			case '!':
				fillRectangle(narrowChar, 2, 0, 1, 3);// top
				fillRectangle(narrowChar, 2, 4, 1, 1);// bottom
				break;
			case '$':
				fillRectangle(narrowChar, 0, 0, 5, 1);// top
				fillRectangle(narrowChar, 0, 1, 1, 1);// left
				fillRectangle(narrowChar, 0, 2, 5, 1);// middle
				fillRectangle(narrowChar, 4, 3, 1, 1);// right
				fillRectangle(narrowChar, 0, 4, 5, 1);// bottom
				fillRectangle(narrowChar, 2, 1, 1, 1);// slash 1
				fillRectangle(narrowChar, 2, 3, 1, 1);// slash 2
				break;
			case '/':
				narrowChar = true;
				for (int i = 1; i < 5; i++)
					fillRectangle(narrowChar, i, 4 - i, 1, 2);
				break;
			case ' ':
				break;
			case '-':
				fillRectangle(narrowChar, 0, 2, 5, 1);// middle
				break;
			case '.':
				narrowChar = true;
				fillRectangle(narrowChar, 1, 4, 1, 1);// dot
				break;
			case ',':
				narrowChar = true;
				fillRectangle(narrowChar, 1, 4, 1, 1);// dot
				fillRectangle(narrowChar, 0, 5, 1, 1);// dot
				break;
			case '\'':
				narrowChar = true;
				fillRectangle(narrowChar, 1, 0, 1, 2);// dot
				break;
			case '(':
				narrowChar = true;
				fillRectangle(narrowChar, 1, 0, 1, 5);// vertical
				fillRectangle(narrowChar, 2, -1, 1, 1);// top
				fillRectangle(narrowChar, 2, 5, 1, 1);// bottom
				break;
			case ')':
				narrowChar = true;
				fillRectangle(narrowChar, 2, 0, 1, 5);// vertical
				fillRectangle(narrowChar, 1, -1, 1, 1);// top
				fillRectangle(narrowChar, 1, 5, 1, 1);// bottom
				break;
			case '[':
				narrowChar = true;
				fillRectangle(narrowChar, 1, -1, 1, 7);// vertical
				fillRectangle(narrowChar, 2, -1, 1, 1);// top
				fillRectangle(narrowChar, 2, 5, 1, 1);// bottom
				break;
			case ']':
				narrowChar = true;
				fillRectangle(narrowChar, 2, -1, 1, 7);// vertical
				fillRectangle(narrowChar, 1, -1, 1, 1);// top
				fillRectangle(narrowChar, 1, 5, 1, 1);// bottom
				break;
			case '=':
				fillRectangle(narrowChar, 0, 1, 4, 1);// top
				fillRectangle(narrowChar, 0, 3, 4, 1);// bottom
				break;
			case '|':
				fillRectangle(narrowChar, 2, -1, 1, 7);// middle
				break;
			case '+':
				fillRectangle(narrowChar, 0, 2, 5, 1);// horizontal
				fillRectangle(narrowChar, 2, 0, 1, 5);// vertical
				break;
			case '@':
				narrowChar = true;
				fillRectangle(narrowChar, 0, 0, 1, 5);// left
				fillRectangle(narrowChar, 1, -1, 3, 1);// top
				fillRectangle(narrowChar, 1, 5, 3, 1);// bottom
				fillRectangle(narrowChar, 1, 1, 3, 1);// middle top
				fillRectangle(narrowChar, 1, 2, 1, 2);// middle left
				fillRectangle(narrowChar, 2, 3, 2, 1);// middle bottom
				fillRectangle(narrowChar, 3, 0, 1, 3);// middle right
				break;
			default:
				System.err.println("INVALID LETTER! -- " + letter);
				System.exit(0);
		}
	}

	/*
	 * Draws an individual rectangle within a letter.
	 */
	private static void fillRectangle(boolean narrowChar, int xx, int yy, int width, int height)
	{
		_g.fillRect(_x + xx * _size + (narrowChar ? _size / 2 : 0), _y + yy * _size, width * _size, height * _size);
	}
}