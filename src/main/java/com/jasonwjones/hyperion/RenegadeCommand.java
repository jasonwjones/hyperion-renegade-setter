package com.jasonwjones.hyperion;

import com.beust.jcommander.Parameter;

/**
 * Simple POJO for JCommander to model the basic options for the renegade setter
 * program.
 * 
 * @author jasonwjones
 *
 */
public class RenegadeCommand {

	@Parameter(names = "--dimension", description = "Dimension to update", required = true)
	private String dimension;

	@Parameter(names = "--member", description = "Member to set, if updating", required = false)
	private String member;

	@Parameter(names = "--unset", description = "Unset renegade member for dimension", required = false)
	private boolean unset = false;

	public String getDimension() {
		return dimension;
	}

	public void setDimension(String dimension) {
		this.dimension = dimension;
	}

	public String getMember() {
		return member;
	}

	public void setMember(String member) {
		this.member = member;
	}

	public boolean isUnset() {
		return unset;
	}

	public void setUnset(boolean unset) {
		this.unset = unset;
	}

}
