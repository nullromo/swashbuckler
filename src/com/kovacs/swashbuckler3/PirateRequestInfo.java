package com.kovacs.swashbuckler3;

import com.kovacs.swashbuckler.game.entity.Pirate.Dexterity;

/*
 * This class contains the fields of a Pirate that a player would actually need
 * to fill out.
 */
public class PirateRequestInfo
{
	String name;
	int head_hit_points, body_hit_points, left_arm_hit_points, right_arm_hit_points;
	int constitution, strength, endurance, expertise;
	Dexterity dexterity;
}