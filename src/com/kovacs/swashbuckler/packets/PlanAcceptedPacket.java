package com.kovacs.swashbuckler.packets;

import java.io.Serializable;
import com.kovacs.swashbuckler.game.Plan;

/*
 * This packet is sent by the server when a client's plan in accepted. The
 * accepted plan is resent so that there is no mistake about what the accepted
 * plan is. Additionally, if the user somehow edits the plan on the client side
 * before this packet comes back, those changes should get overwritten by the
 * plan in this packet.
 */
public class PlanAcceptedPacket extends Packet implements Serializable
{
	private static final long serialVersionUID = 3235037832053872868L;

	/*
	 * The plan that was sent by the client and accepted by the server.
	 */
	private Plan plan;

	public PlanAcceptedPacket(Plan plan)
	{
		this.plan = plan;
	}

	public Plan getPlan()
	{
		return plan;
	}
}