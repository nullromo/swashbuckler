package com.kovacs.swashbuckler3;

import java.util.AbstractMap.SimpleEntry;
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
	private int strength, endurance, constitution, expertise;
	private Dexterity dexterity;
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

	public PirateData()
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
		requestedItems.add(new SimpleEntry<String, Object>("Name", String.class));
		requestedItems.add(new SimpleEntry<String, Object>("Head hit points", Integer.class));
		requestedItems.add(new SimpleEntry<String, Object>("Body hit points", Integer.class));
		requestedItems.add(new SimpleEntry<String, Object>("Left arm hit points", Integer.class));
		requestedItems.add(new SimpleEntry<String, Object>("Right arm hit points", Integer.class));
		Fillable fillable = new Fillable(requestedItems);
		return new Request(player, this, fillable);
	}

	@Override
	protected Requestable parseRequestInternal(Request r)
	{
		if (!r.isComplete())
			throw new RuntimeException("You cannot parse an incomplete request.");
		Fillable source = r.getFillable();
		this.name = (String) source.get("Name");
		this.head = (int) source.get("Head hit points");
		this.body = (int) source.get("Body hit points");
		this.leftArm = (int) source.get("Left arm hit points");
		this.rightArm = (int) source.get("Right arm hit points");
		return this;
	}

	/*
	 * Convenience method for creating requests.
	 */
	public static Request createRequest(Player player)
	{
		return Requestable.createRequest(PirateData.class, player);
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
		head -= damage;
	}

	public void damageLeftArm(int damage)
	{
		leftArm -= damage;
	}

	public void damageRightArm(int damage)
	{
		rightArm -= damage;
	}

	public void damageBody(int damage)
	{
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