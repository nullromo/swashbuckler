package com.kovacs.swashbuckler.server;

import java.util.ArrayList;
import com.kovacs.swashbuckler.Utility;
import com.kovacs.swashbuckler.Utility.Direction;
import com.kovacs.swashbuckler.game.BoardCoordinate;
import com.kovacs.swashbuckler.game.entity.Chair;
import com.kovacs.swashbuckler.game.entity.Entity;
import com.kovacs.swashbuckler.game.entity.Mug;
import com.kovacs.swashbuckler.game.entity.Table;

/*
 * This class represents the game board.
 */
public class Board
{
	/*
	 * This list contains all the entities on the board.
	 */
	private ArrayList<Entity> entities = new ArrayList<>();

	public Board()
	{
		System.out.println("====initializing game board...====");
		// TODO: initialize the stairs and balcony
		System.out.println("initializing tables...");
		for (int tableNumber = 0; tableNumber < 9; tableNumber++)
		{
			if (Utility.rand() > .75)
				continue;
			// initial table placement is at D4, D8, D12, H4, H8, H12, L4, L8,
			// and L12
			char letter = (char) ((int) 'd' + (tableNumber % 3) * 4);
			int number = 4 + (tableNumber / 3) * 4;
			BoardCoordinate coordinate = new BoardCoordinate(letter, number);
			coordinate = coordinate.next(Utility.randomDirection());
			Table t = new Table(coordinate);
			Direction extensionDirection;
			do
			{
				extensionDirection = Utility.random(Utility.cardinalDirections);
				coordinate = coordinate.next(extensionDirection);
			}
			while (occupied(coordinate));
			t.coordinates.add(coordinate);
			if (Utility.rand() > .5)
			{
				coordinate = coordinate.next(extensionDirection);
				if (coordinate != null && !occupied(coordinate))
					t.coordinates.add(coordinate);
			}
			add(t);
		}

		System.out.println("initializing chairs...");
		ArrayList<BoardCoordinate> potentialChairLocations = new ArrayList<>();
		for (Entity e : entities)
		{
			if (!(e instanceof Table))
				continue;
			for (BoardCoordinate tableCoordinate : e.coordinates)
			{
				for (Direction nextToDirection : Utility.cardinalDirections)
				{
					BoardCoordinate newCoordinate = tableCoordinate.next(nextToDirection);
					if (newCoordinate != null && !occupied(newCoordinate))
						potentialChairLocations.add(newCoordinate);
				}
			}
		}
		Utility.shuffle(potentialChairLocations);
		for (int i = 0; i < potentialChairLocations.size() * 2 / 3; i++)
		{
			add(new Chair(potentialChairLocations.get(i)));
		}

		System.out.println("initializing mugs...");
		ArrayList<BoardCoordinate> potentialMugLocations = new ArrayList<>();
		for (Entity e : entities)
		{
			if (!(e instanceof Table))
				continue;
			for (BoardCoordinate bc : e.coordinates)
			{
				potentialMugLocations.add(bc);
			}
		}
		Utility.shuffle(potentialMugLocations);
		for (int i = 0; i < potentialMugLocations.size() * 2 / 3; i++)
		{
			add(new Mug(potentialMugLocations.get(i)));
		}
		
		
		
		
		/*		
			
			
		//add daggers
		System.out.println("initializing daggers...");
		int daggerNumber=0;
		for(int r=0; r<BOARD_SIZE; r++)
			for(int c=0; c<BOARD_SIZE; c++)
			{
				daggerNumber = spawnDagger(c,r,daggerNumber);
			}
	//spawns a dagger
	private int spawnDagger(int x, int y, int number)
	{
		if(Math.random()>.02)
			return number;
		if(squares[x][y].containsChair() || squares[x][y].containsMug())
			return number;
		Dagger d = new Dagger(x,y,"dagger"+number);
		addElement(d);
		number++;
		return number;
	}
	
			*/
		
		/*
		//add sword
		System.out.println("initializing sword...");
		while(!spawnSword())
			System.out.println("sword location failed");
			
			
	//looks for a place to spawn a sword and then does
	private boolean spawnSword()
	{
		int x=rand.nextInt(BOARD_SIZE);
		int y=rand.nextInt(BOARD_SIZE);
		if(squares[x][y].getElements().isEmpty())
		{
			Sword s = new Sword(x,y,"sword0");
			addElement(s);
			return true;
		}
		return false;
	}
	
			*/
		
		
		
		
		/*
		//add shelves
		System.out.println("initializing shelves...");
		for(int i=0; i<NUMBER_OF_SHELVES; i++)
			spawnShelf(i);
		
	//spawns a shelf
	private void spawnShelf(int wallNumber)
	{
		if(Math.random()>.75)
			return;
		
		Shelf s = null;
		switch(wallNumber)
		{
		case 0:
			s = new Shelf(0,rand.nextInt(13),"shelf"+wallNumber,Wall.LEFT,true);
			break;
		case 1:
			s = new Shelf(rand.nextInt(13),0,"shelf"+wallNumber,Wall.TOP,true);
			break;
		case 2:
			s = new Shelf(13,rand.nextInt(13),"shelf"+wallNumber,Wall.RIGHT,true);
			break;
		case 3:
			s = new Shelf(rand.nextInt(13),13,"shelf"+wallNumber,Wall.BOTTOM,true);
			break;
		}
		addElement(s);
	}
	
	*/
		
		
		/*
	
	//add pirates or something
		//make sure to make a separate list of all the pirates
	////////temporary
		int x,y;
		do
		{
			x = rand.nextInt(14);
			y = rand.nextInt(14);
		}while(!squares[x][y].getElements().isEmpty());
		addElement(new ControllablePirate(x,y,"Kyle Kovacs"));
		
		//addElement(new Pirate(x+1,y,"K Kovacs"));
	////////
		System.out.println("====board fully initialized!====");
	}
	
		 */
	}

	/*
	 * Tells whether a square on the board is occupied
	 */
	public boolean occupied(BoardCoordinate coordinate)
	{
		return !getEntities(coordinate).isEmpty();
	}

	/*
	 * Returns a list of all the entities in a square
	 */
	public ArrayList<Entity> getEntities(BoardCoordinate coordinate)
	{
		ArrayList<Entity> list = new ArrayList<>();
		for (Entity e : entities)
		{
			if (e.touches(coordinate))
			{
				list.add(e);
			}
		}
		return list;
	}

	/*
	 * Adds an entity to the board.
	 */
	public void add(Entity entity)
	{
		this.entities.add(entity);
	}

	@Override
	public String toString()
	{
		char[][] board = new char[15][14];
		for (Entity e : entities)
		{
			for (BoardCoordinate bc : e.coordinates)
			{
				board[bc.letter - 'a'][bc.number - 1] = e.getClass().getSimpleName().charAt(0);
			}
		}
		StringBuilder sb = new StringBuilder();
		for (char[] ca : board)
		{
			for (char c : ca)
			{
				sb.append(c == 0 ? '.' : c).append(" ");
			}
			sb.append("\n");
		}
		return sb.toString();
	}
}