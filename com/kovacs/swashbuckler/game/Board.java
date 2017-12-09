package com.kovacs.swashbuckler.game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import com.kovacs.swashbuckler.Utility;
import com.kovacs.swashbuckler.Utility.Direction;
import com.kovacs.swashbuckler.game.entity.Entity;
import com.kovacs.swashbuckler.game.entity.Entity.EntityType;

/*
 * This class represents the game board.
 */
public class Board implements Serializable
{
	private static final long serialVersionUID = 1020738079583844540L;

	/*
	 * This list contains all the entities on the board.
	 */
	private ArrayList<Entity> entities = new ArrayList<>();

	public Board()
	{
		// Since windows, stairs, and balconies do not move, there are single
		// entities that hold all the appropriate coordinates.
		add(new Entity(EntityType.BALCONY, new BoardCoordinate('a', 6), new BoardCoordinate('a', 7),
				new BoardCoordinate('a', 8), new BoardCoordinate('a', 9)));
		add(new Entity(EntityType.STAIRS, new BoardCoordinate('b', 7), new BoardCoordinate('b', 8)));
		add(new Entity(EntityType.WINDOW, new BoardCoordinate('e', 1), new BoardCoordinate('k', 1),
				new BoardCoordinate('e', 14), new BoardCoordinate('k', 14), new BoardCoordinate('a', 2),
				new BoardCoordinate('a', 12), new BoardCoordinate('o', 2), new BoardCoordinate('o', 3),
				new BoardCoordinate('o', 7), new BoardCoordinate('o', 8), new BoardCoordinate('o', 11),
				new BoardCoordinate('o', 12)));

		// TODO: place carpets

		// There are 9 potential tables that can appear at D4, D8, D12, H4, H8,
		// H12, L4, L8, and L12. Each of these starting positions has a chance
		// to be bumped by 1 square in any direction. Then it is extended by 1
		// square in an unoccupied direction. Then it has a chance to be
		// extended 1 more time.
		for (int tableNumber = 0; tableNumber < 9; tableNumber++)
		{
			if (Utility.rand() > .75)
				continue;
			char letter = (char) ((int) 'd' + (tableNumber % 3) * 4);
			int number = 4 + (tableNumber / 3) * 4;
			BoardCoordinate coordinate = new BoardCoordinate(letter, number);
			if (Utility.rand() > .5)
				coordinate = coordinate.next(Utility.randomDirection());
			Entity t = new Entity(EntityType.TABLE, coordinate);
			Direction extensionDirection;
			do
			{
				extensionDirection = Utility.randomElement(Utility.cardinalDirections);
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

		// Every table square can potentially be surrounded by chairs (only in
		// cardinal directions). Each of these that are unoccupied are collected
		// and randomized. Then a subset of that group is selected for actual
		// chair placement. Mugs are done in the same way, except they can only
		// appear on table squares.
		ArrayList<BoardCoordinate> potentialChairLocations = new ArrayList<>();
		ArrayList<BoardCoordinate> potentialMugLocations = new ArrayList<>();
		for (Entity e : entities)
		{
			if (!(e.type == EntityType.TABLE))
				continue;
			for (BoardCoordinate tableCoordinate : e.coordinates)
			{
				potentialMugLocations.add(tableCoordinate);
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
			add(new Entity(EntityType.CHAIR, potentialChairLocations.get(i)));
		Utility.shuffle(potentialMugLocations);
		for (int i = 0; i < potentialMugLocations.size() * 2 / 3; i++)
			add(new Entity(EntityType.MUG, potentialMugLocations.get(i)));

		// There can be up to 1 shelf on each wall. A shelf is selected based on
		// predefined positions.
		Character[] westWallOptions = new Character[] { 'a', 'b', 'c', 'f', 'g', 'h', 'i', 'l', 'm', 'n' };
		Character[] eastWallOptions = new Character[] { 'c', 'f', 'g', 'h', 'i', 'l', 'm', 'n' };
		Integer[] northWallOptions = new Integer[] { 3, 4, 10, 13 };
		Integer[] southWallOptions = new Integer[] { 4, 5, 9 };
		if (Utility.rand() < .8)
		{
			BoardCoordinate base = new BoardCoordinate(Utility.randomElement(westWallOptions), 1);
			if (!occupied(base) && !occupied(base.next(Direction.SOUTH)))
				add(new Entity(EntityType.SHELF, base, base.next(Direction.SOUTH)));
		}
		if (Utility.rand() < .8)
		{
			BoardCoordinate base = new BoardCoordinate(Utility.randomElement(eastWallOptions), 14);
			if (!occupied(base) && !occupied(base.next(Direction.SOUTH)))
				add(new Entity(EntityType.SHELF, base, base.next(Direction.SOUTH)));
		}
		if (Utility.rand() < .8)
		{
			BoardCoordinate base = new BoardCoordinate('a', Utility.randomElement(northWallOptions));
			if (!occupied(base) && !occupied(base.next(Direction.EAST)))
				add(new Entity(EntityType.SHELF, base, base.next(Direction.EAST)));
		}
		if (Utility.rand() < .8)
		{
			BoardCoordinate base = new BoardCoordinate('o', Utility.randomElement(southWallOptions));
			if (!occupied(base) && !occupied(base.next(Direction.EAST)))
				add(new Entity(EntityType.SHELF, base, base.next(Direction.EAST)));
		}

		// TODO: place chandeliers

		// Daggers can appear on the floor and on tables. There is a small
		// chance to spawn one in every eligible square.
		forAllSquares(new Consumer<BoardCoordinate>()
		{
			@Override
			public void accept(BoardCoordinate coordinate)
			{
				for (Entity e : getEntities(coordinate))
					if (!(e.type == EntityType.TABLE) && !(e.type == EntityType.BALCONY)
							&& !(e.type == EntityType.WINDOW))
						return;
				if (Utility.rand() < .01)
					add(new Entity(EntityType.DAGGER, coordinate));
			}
		});

		// There is a small chance to spawn up to 2 swords on the floor. This is
		// done by choosing randomly in a shuffled list.
		ArrayList<BoardCoordinate> potentialSwordLocations = new ArrayList<>();
		forAllSquares(new Consumer<BoardCoordinate>()
		{
			@Override
			public void accept(BoardCoordinate coordinate)
			{
				if (getEntities(coordinate).isEmpty())
					potentialSwordLocations.add(coordinate);
			}
		});
		Utility.shuffle(potentialSwordLocations);
		double r = Utility.rand();
		int numSwords = r < .5 ? 1 : r > .9 ? 2 : 0;
		for (int i = 0; i < numSwords; i++)
			add(new Entity(EntityType.SWORD, potentialSwordLocations.get(i)));
	}

	/*
	 * Tells whether a square on the board is occupied
	 */
	public boolean occupied(BoardCoordinate coordinate)
	{
		for (Entity e : getEntities(coordinate))
			if (!(e.type == EntityType.WINDOW))
				return true;
		return false;
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

	/*
	 * Performs some action that needs to be done for all squares on the board.
	 * Yields a new BoardCoordinate instance for every square.
	 */
	private void forAllSquares(Consumer<BoardCoordinate> f)
	{
		for (char letter = 'a'; letter <= 'o'; letter++)
		{
			for (char number = 1; number <= 14; number++)
			{
				f.accept(new BoardCoordinate(letter, number));
			}
		}
	}

	/*
	 * Picks a random square for a pirate to go.
	 */
	public static BoardCoordinate randomPiratePosition()
	{
		switch (Utility.rollDoubleDigit())
		{
			case 11:
				return new BoardCoordinate('b', 2);
			case 12:
				return new BoardCoordinate('b', 5);
			case 13:
				return new BoardCoordinate('b', 8);
			case 14:
				return new BoardCoordinate('b', 11);
			case 15:
				return new BoardCoordinate('b', 14);
			case 16:
				return new BoardCoordinate('d', 1);
			case 21:
				return new BoardCoordinate('d', 4);
			case 22:
				return new BoardCoordinate('d', 7);
			case 23:
				return new BoardCoordinate('d', 10);
			case 24:
				return new BoardCoordinate('d', 13);
			case 25:
				return new BoardCoordinate('f', 3);
			case 26:
				return new BoardCoordinate('f', 6);
			case 31:
				return new BoardCoordinate('f', 9);
			case 32:
				return new BoardCoordinate('f', 12);
			case 33:
				return new BoardCoordinate('f', 14);
			case 34:
				return new BoardCoordinate('h', 1);
			case 35:
				return new BoardCoordinate('h', 4);
			case 36:
				return new BoardCoordinate('h', 7);
			case 41:
				return new BoardCoordinate('h', 10);
			case 42:
				return new BoardCoordinate('h', 13);
			case 43:
				return new BoardCoordinate('j', 2);
			case 44:
				return new BoardCoordinate('j', 5);
			case 45:
				return new BoardCoordinate('j', 8);
			case 46:
				return new BoardCoordinate('j', 11);
			case 51:
				return new BoardCoordinate('j', 14);
			case 52:
				return new BoardCoordinate('l', 3);
			case 53:
				return new BoardCoordinate('l', 6);
			case 54:
				return new BoardCoordinate('l', 9);
			case 55:
				return new BoardCoordinate('l', 12);
			case 56:
				return new BoardCoordinate('l', 14);
			case 61:
				return new BoardCoordinate('n', 2);
			case 62:
				return new BoardCoordinate('n', 5);
			case 63:
				return new BoardCoordinate('n', 7);
			case 64:
				return new BoardCoordinate('n', 10);
			case 65:
				return new BoardCoordinate('n', 12);
			case 66:
				return new BoardCoordinate('n', 14);
			default:
				throw new RuntimeException("Unreachable code.");
		}
	}

	@Override
	public String toString()
	{
		char[][] board = new char[15][14];
		for (Entity e : entities)
		{
			for (BoardCoordinate bc : e.coordinates)
			{
				board[bc.letter - 'a'][bc.number - 1] = e.type.name().charAt(0);
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

	/*
	 * Returns an iterator over certain game objects
	 */
	public List<Entity> allEntities(Class<?> type)
	{
		return entities.stream().filter(e -> type.isAssignableFrom(e.getClass())).collect(Collectors.toList());
	}
}