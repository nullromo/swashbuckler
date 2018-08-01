package com.kovacs.swashbuckler.game;

/*
 * Represents an order that can be given to a pirate.
 */
public enum Order
{
	/*
	 * These are all the possible orders that pirates can be given.
	 */
	// @formatter:off
	THROW_DAGGER("Throw Dagger", "wd", 1, OrderType.ACTION), 
	THROW_SWORD("Throw Sword", "ws", 1, OrderType.ACTION),
	WAVE_HAT("Wave Hat", "wh", 1, OrderType.ACTION), 
	THROW_MUG("Throw Mug", "wm", 1, OrderType.ACTION), 
	SHOVE_CHAIR("Shove Chair", "vc", 1, OrderType.ACTION),
	TOPPLE_SHELF("Topple Shelf", "wt", 2, OrderType.ACTION), 
	SHOVE_TABLE("Shove Table", "vt", 1, OrderType.ACTION), 
	FLIP_TABLE("Flip Table", "ft", 2, OrderType.ACTION),
	YANK_CARPET("Yank Carpet", " y", 2, OrderType.ACTION),
	THROW_CHAIR("Throw Chair", "wc", 2, OrderType.ACTION), 
	PICK_UP_SWORD("Pick Up Sword", "ps", 1, OrderType.ACTION),
	PICK_UP_DAGGER("Pick Up Dagger", "pd", 1, OrderType.ACTION), 
	BLOCK("Block", " x", 1, OrderType.ACTION),
	LUNGE("Lunge", " n", 1, OrderType.SWORDPLAY), 
	PARRY("Parry", " p", 1, OrderType.SWORDPLAY),
	PARRY_AND_LUNGE("Parry and Lunge", "pn", 2, OrderType.SWORDPLAY), 
	RUNNING_LUNGE("Running Lunge", "1n", 1, OrderType.SWORDPLAY), 
	SLASH("Slash", " s", 1, OrderType.SWORDPLAY),
	DAGGER_STAB("Dagger Stab", "ss", 1, OrderType.SWORDPLAY),
	REST("Rest", " -", 0, OrderType.MOVEMENT), 
	TURN_LEFT("Turn Left", " l", 0, OrderType.MOVEMENT),
	TURN_RIGHT("Turn Right", " r", 0, OrderType.MOVEMENT), 
	MOVE_LEFT("Move Left", "sl", 1, OrderType.MOVEMENT), 
	MOVE_RIGHT("Move Right", "sr", 1, OrderType.MOVEMENT),
	MOVE_BACK_LEFT("Move Back Left", "bsl", 1, OrderType.MOVEMENT), 
	MOVE_BACK_RIGHT("Move Back Right", "bsr", 1, OrderType.MOVEMENT),
	MOVE_FORWARD_LEFT("Move Forward Left", "fsl", 1, OrderType.MOVEMENT), 
	MOVE_FORWARD_RIGHT("Move Forward Right", "fsr", 1, OrderType.MOVEMENT),
	MOVE_BACK("Move Back", " b", 1, OrderType.MOVEMENT), 
	MOVE_FORWARD("Move Forward", " 1", 1, OrderType.MOVEMENT),
	STEP_FORWARD_AND_KICK("Step Forward and Kick", "1k", 2, OrderType.MOVEMENT), 
	GO_PRONE("Go Prone", "pr", 1, OrderType.MOVEMENT),
	STAND_UP("Stand Up", "up", 1, OrderType.MOVEMENT),
	JUMP_UP("Jump Up", "ju", 2, OrderType.MOVEMENT),
	JUMP_DOWN("Jump Down", "jd", 2, OrderType.MOVEMENT),
	CLIMB_TABLE("Climb Table", "at", 1, OrderType.MOVEMENT),
	CLIMB_BALCONY("Climb Balcony", "ab", 2, OrderType.MOVEMENT),
	SWING_2("Swing 2", "g2", 2, OrderType.MOVEMENT),
	SWING_3("Swing 3", "g3", 2, OrderType.MOVEMENT),
	SWING_4("Swing 4", "g4", 2, OrderType.MOVEMENT),
	SWING_5("Swing 5", "g5", 2, OrderType.MOVEMENT),
	UNPLANNED("*", "", 0, OrderType.NONE);
	// @formatter:on

	/*
	 * The name is what the user will select from, whereas the abbreviation is
	 * the small code that gets put into the planning box.
	 */
	private String name, abbreviation;

	/*
	 * This is the number of required rests after the action.
	 */
	private int rests;

	/*
	 * Categorizes the order into a group.
	 */
	private OrderType orderType;

	private Order(String name, String abbreviation, int rests, OrderType orderType)
	{
		this.name = name;
		this.abbreviation = abbreviation;
		this.rests = rests;
		this.orderType = orderType;
	}

	public String getName()
	{
		return name;
	}

	public String getAbbreviation()
	{
		return abbreviation;
	}

	public int getRests()
	{
		return rests;
	}

	public OrderType getOrderType()
	{
		return orderType;
	}
}