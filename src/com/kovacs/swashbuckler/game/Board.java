package com.kovacs.swashbuckler.game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import com.kovacs.swashbuckler.Utility;
import com.kovacs.swashbuckler.Utility.Direction;
import com.kovacs.swashbuckler.game.entity.Carpet;
import com.kovacs.swashbuckler.game.entity.Chair;
import com.kovacs.swashbuckler.game.entity.Entity;
import com.kovacs.swashbuckler.game.entity.Entity.EntityType;
import com.kovacs.swashbuckler.game.entity.Shelf;
import com.kovacs.swashbuckler.game.entity.Table;

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

		// There are 2 potential carpets. First each has a random chance to not
		// appear, then a random square (not on the edge of the room) is picked
		// for the start of the carpet. It then tries to find space to exist.
		for (int carpetNumber = 0; carpetNumber < 2; carpetNumber++)
		{
			if (Utility.rand() > .75)
				continue;
			boolean valid = false;
			BoardCoordinate[] footprint = new BoardCoordinate[12];
			Direction direction = null;
			while (!valid)
			{
				BoardCoordinate startingSquare = BoardCoordinate.random();
				footprint[0] = startingSquare;
				int letterIncrement = Utility.randomElement(new Integer[] { -1, 0, 1 });
				int numberIncrement = letterIncrement == 0 ? Utility.randomElement(new Integer[] { -1, 1 }) : 0;
				for (int i = 1; i < 6; i++)
					footprint[i] = new BoardCoordinate((char) (footprint[i - 1].letter + letterIncrement),
							footprint[i - 1].number + numberIncrement);
				direction = letterIncrement == 0
						? Utility.randomElement(new Direction[] { Direction.NORTH, Direction.SOUTH })
						: Utility.randomElement(new Direction[] { Direction.EAST, Direction.WEST });
				for (int i = 6; i < 12; i++)
					footprint[i] = footprint[i - 6].next(direction);
				valid = true;
				for (BoardCoordinate coordinate : footprint)
				{
					if (coordinate == null)
						valid = false;
					else if (coordinate.letter <= 'a' || coordinate.letter >= 'o' || coordinate.number >= 14
							|| coordinate.number <= 1)
						valid = false;
					else if (occupied(coordinate))
						valid = false;
					else
						for (Entity e : getEntities(coordinate))
							if (e.type == EntityType.CARPET)
								valid = false;
				}
			}
			add(new Carpet(EntityType.CARPET, direction == Direction.EAST || direction == Direction.WEST, footprint));
		}

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
			Entity table = new Table(EntityType.TABLE, tableNumber, coordinate);
			Direction extensionDirection;
			BoardCoordinate extensionCoordinate;
			do
			{
				extensionDirection = Utility.randomElement(Utility.cardinalDirections);
				extensionCoordinate = coordinate.next(extensionDirection);
			}
			while (occupied(extensionCoordinate));
			table.coordinates.add(extensionCoordinate);
			if (Utility.rand() > .5)
			{
				BoardCoordinate extensionCoordinate2 = extensionCoordinate.next(extensionDirection);
				if (extensionCoordinate2 != null && !occupied(extensionCoordinate2))
					table.coordinates.add(extensionCoordinate2);
			}
			add(table);
		}

		// Every table square can potentially be surrounded by chairs (only in
		// cardinal directions). Each of these that are unoccupied are collected
		// and randomized. Then a subset of that group is selected for actual
		// chair placement. Mugs are done in the same way, except they can only
		// appear on table squares.
		HashMap<BoardCoordinate, Direction> potentialChairLocationsMap = new HashMap<>();
		ArrayList<BoardCoordinate> potentialMugLocations = new ArrayList<>();
		for (Entity table : allEntities(Table.class))
		{
			for (BoardCoordinate tableCoordinate : table.coordinates)
			{
				potentialMugLocations.add(tableCoordinate);
				for (Direction nextToDirection : Utility.cardinalDirections)
				{
					BoardCoordinate newCoordinate = tableCoordinate.next(nextToDirection);
					if (newCoordinate != null && !occupied(newCoordinate))
						potentialChairLocationsMap.put(newCoordinate, nextToDirection.opposite());
				}
			}
		}
		List<Map.Entry<BoardCoordinate, Direction>> potentialChairLocations = new ArrayList<Map.Entry<BoardCoordinate, Direction>>(
				potentialChairLocationsMap.entrySet());
		Utility.shuffle(potentialChairLocations);
		for (int i = 0; i < potentialChairLocations.size() * 2 / 3; i++)
		{
			BoardCoordinate location = potentialChairLocations.get(i).getKey();
			Direction direction = potentialChairLocations.get(i).getValue();
			if (!occupied(location))
				add(new Chair(EntityType.CHAIR, direction, location));
		}
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
				add(new Shelf(EntityType.SHELF, Direction.WEST, base, base.next(Direction.SOUTH)));
		}
		if (Utility.rand() < .8)
		{
			BoardCoordinate base = new BoardCoordinate(Utility.randomElement(eastWallOptions), 14);
			if (!occupied(base) && !occupied(base.next(Direction.SOUTH)))
				add(new Shelf(EntityType.SHELF, Direction.EAST, base, base.next(Direction.SOUTH)));
		}
		if (Utility.rand() < .8)
		{
			BoardCoordinate base = new BoardCoordinate('a', Utility.randomElement(northWallOptions));
			if (!occupied(base) && !occupied(base.next(Direction.EAST)))
				add(new Shelf(EntityType.SHELF, Direction.NORTH, base, base.next(Direction.EAST)));
		}
		if (Utility.rand() < .8)
		{
			BoardCoordinate base = new BoardCoordinate('o', Utility.randomElement(southWallOptions));
			if (!occupied(base) && !occupied(base.next(Direction.EAST)))
				add(new Shelf(EntityType.SHELF, Direction.SOUTH, base, base.next(Direction.EAST)));
		}

		// The board is split into 4 regions for chandelier placement. A random
		// square in each section is chosen, and then the process restarts if
		// any chandeliers are within 3 squares of each other. Also, they can't
		// be within 2 squares of the edge of the board.
		for (int tries = 0; tries < 20; tries++)
		{
			BoardCoordinate[] chandelierCoordinates = new BoardCoordinate[4];
			chandelierCoordinates[0] = new BoardCoordinate((char) (Utility.randInt(2, 7) + 'a'), Utility.randInt(3, 7));
			chandelierCoordinates[1] = new BoardCoordinate((char) (Utility.randInt(8, 12) + 'a'),
					Utility.randInt(3, 7));
			chandelierCoordinates[2] = new BoardCoordinate((char) (Utility.randInt(2, 7) + 'a'),
					Utility.randInt(8, 12));
			chandelierCoordinates[3] = new BoardCoordinate((char) (Utility.randInt(8, 12) + 'a'),
					Utility.randInt(8, 12));
			Set<BoardCoordinate> footprintSet = new LinkedHashSet<BoardCoordinate>();
			for (Direction direction : Direction.values())
				for (int i = 0; i < 4; i++)
					footprintSet.add(chandelierCoordinates[i].next(direction));
			if (footprintSet.size() != 32)
				continue;
			for (int i = 0; i < 4; i++)
				if (Utility.rand() < .75)
					add(new Entity(EntityType.CHANDELIER, chandelierCoordinates[i]));
			break;
		}

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
		for (Entity entity : getEntities(coordinate))
			if (!(entity.type == EntityType.WINDOW) && !(entity.type == EntityType.CARPET))
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