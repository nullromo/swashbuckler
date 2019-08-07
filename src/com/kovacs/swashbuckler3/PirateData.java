package com.kovacs.swashbuckler3;

import java.util.AbstractMap.SimpleEntry;
import java.util.regex.Pattern;
import java.util.ArrayList;
import com.kovacs.swashbuckler.Utility;

/*
 * Contains all the raw data that defines a Pirate.
 */
public class PirateData extends Requestable
{
	/*
	 * Stats to represent character traits.
	 */
	private final int strength, endurance, constitution, expertise;
	private final Dexterity dexterity;
	private String name;

	/*
	 * Stats to represent conditions
	 */
	private int head, leftArm, rightArm, body;
	private int numSwords = 1, numDaggers = 1;
	private Condition condition = Condition.STANDING;

	/*
	 * Enum for the possible states of existence
	 */
	public enum Condition
	{
		STANDING, PRONE, DEAD
	};

	/*
	 * Enum for handed-ness
	 */
	public enum Dexterity
	{
		RIGHT_HANDED, LEFT_HANDED, AMBIDEXTROUS;

		@Override
		public String toString()
		{
			return this.name().toLowerCase().replaceAll("_", " ");
		}
	};

	public PirateData(PirateDataArgs args)
	{
		strength = Utility.rollDie() + Utility.rollDie() + Utility.rollDie();
		endurance = Utility.rollDie() + Utility.rollDie() + Utility.rollDie();
		constitution = strength + endurance;
		expertise = Utility.rollDie() + Utility.rollDie() + Utility.rollDie();
		int dexterityRoll = Utility.rollDoubleDigit();
		dexterity = dexterityRoll == 66 ? Dexterity.AMBIDEXTROUS
				: dexterityRoll >= 62 ? Dexterity.LEFT_HANDED : Dexterity.RIGHT_HANDED;
	}

	@Override
	protected Request createRequestInternal(Player player)
	{
		ArrayList<SimpleEntry<String, Object>> requestedItems = new ArrayList<>();
		requestedItems.add(new SimpleEntry<String, Object>("Name", "rob bob"));
		int defaultHead = (int) Math.ceil(constitution / 4.0);
		requestedItems.add(new SimpleEntry<String, Object>("Head hit points", defaultHead));
		int defaultBody = (int) Math.ceil(constitution / 3.0);
		requestedItems.add(new SimpleEntry<String, Object>("Body hit points", defaultBody));
		double defaultArm = (constitution - defaultHead - defaultBody) / 2.0;
		requestedItems.add(new SimpleEntry<String, Object>("Left arm hit points",
				(int) (dexterity == Dexterity.LEFT_HANDED ? Math.ceil(defaultArm) : Math.floor(defaultArm))));
		requestedItems.add(new SimpleEntry<String, Object>("Right arm hit points",
				(int) (dexterity == Dexterity.LEFT_HANDED ? Math.floor(defaultArm) : Math.ceil(defaultArm))));
		return new Request(player, this, new Fillable(requestedItems));
	}

	@Override
	protected Requestable parseRequestInternal(Request r)
	{
		Fillable source = r.getFillable();
		name = (String) source.get("Name");
		head = (int) source.get("Head hit points");
		body = (int) source.get("Body hit points");
		leftArm = (int) source.get("Left arm hit points");
		rightArm = (int) source.get("Right arm hit points");
		return this;
	}

	/*
	 * Convenience method for creating requests.
	 */
	public static Request createRequest(Player player, RequestArgs args)
	{
		return Requestable.createRequest(PirateData.class, player, args);
	}
	
	public static Request createRequest(Player player)
	{
		return Requestable.createRequest(PirateData.class, player, PirateDataArgs.defaultArgs());
	}

	/*
	 * Convenience method for parsing requests.
	 */
	public static PirateData parseRequest(Request r)
	{
		return (PirateData) Requestable.parseRequest(r);
	}

	/*
	 * Tells whether or not this pirate's name is valid.
	 */
	public boolean nameValid()
	{
		if (name.length() > 32 || name.length() < 2)
			return false;
		if (!Pattern.compile("[a-zA-Z]+( )?([a-zA-Z]+)?").matcher(name).matches())
			return false;
		if (Utility.isProfane(name))
			return false;
		return true;
	}

	@Override
	public String toString()
	{
		return "Data for pirate: " + name + "\n\t" + "head:" + head + "\n\t" + "leftArm:" + leftArm + "\n\t"
				+ "rightArm:" + rightArm + "\n\t" + "body:" + body + "\n\t" + "strength:" + strength + "\n\t"
				+ "endurance:" + endurance + "\n\t" + "constitution:" + constitution + "\n\t" + "expertise:" + expertise
				+ "\n\t" + "dexterity:" + dexterity + "\n\t" + "condition:" + condition + "\n\t" + "numSwords:"
				+ numSwords + "\n\t" + "numDaggers:" + numDaggers;
	}

	public void damageHead(int damage)
	{
		Utility.assertPositive(damage);
		head -= damage;
	}

	public void damageLeftArm(int damage)
	{
		Utility.assertPositive(damage);
		leftArm -= damage;
	}

	public void damageRightArm(int damage)
	{
		Utility.assertPositive(damage);
		rightArm -= damage;
	}

	public void damageBody(int damage)
	{
		Utility.assertPositive(damage);
		body -= damage;
	}

	public void gainSword()
	{
		numSwords++;
	}

	public void loseSword()
	{
		numSwords--;
	}

	public void gainDagger()
	{
		numDaggers++;
	}

	public void loseDagger()
	{
		numDaggers--;
	}

	public void fallDown()
	{
		condition = Condition.PRONE;
	}

	public void standUp()
	{
		condition = Condition.STANDING;
	}

	public void die()
	{
		condition = Condition.DEAD;
	}

	public int getHead()
	{
		return head;
	}

	public int getLeftArm()
	{
		return leftArm;
	}

	public int getRightArm()
	{
		return rightArm;
	}

	public int getBody()
	{
		return body;
	}

	public int getStrength()
	{
		return strength;
	}

	public int getEndurance()
	{
		return endurance;
	}

	public int getConstitution()
	{
		return constitution;
	}

	public int getExpertise()
	{
		return expertise;
	}

	public Dexterity getDexterity()
	{
		return dexterity;
	}

	public int getNumSwords()
	{
		return numSwords;
	}

	public int getNumDaggers()
	{
		return numDaggers;
	}

	public String getName()
	{
		return name;
	}

	public Condition getCondition()
	{
		return condition;
	}
}