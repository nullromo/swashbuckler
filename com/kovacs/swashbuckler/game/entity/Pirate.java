package com.kovacs.swashbuckler.game.entity;

import java.io.Serializable;

public class Pirate extends Entity implements Serializable
{
	private static final long serialVersionUID = -1096156252334655485L;

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
	 * Enum for the three possible states of existence
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

	public Pirate(int head, int leftArm, int rightArm, int body, int strength, int endurance, int constitution,
			int expertise, Dexterity dexterity, String name)
	{
		this.head = head;
		this.leftArm = leftArm;
		this.rightArm = rightArm;
		this.body = body;
		this.strength = strength;
		this.endurance = endurance;
		this.constitution = constitution;
		this.expertise = expertise;
		this.dexterity = dexterity;
		this.name = name;
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

	public void setName(String name)
	{
		this.name = name;
	}
}