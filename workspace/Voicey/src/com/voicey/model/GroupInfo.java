package com.voicey.model;

import java.util.ArrayList;
import java.util.List;

public class GroupInfo {

	String GroupName,GroupID;
	List<User> GroupUsers = new ArrayList<User>();
	
	public String getGroupName() {
		return GroupName;
	}
	
	public void setGroupName(String GroupName) {
		this.GroupName = GroupName;
	}
	
	public String getGroupID() {
		return GroupID;
	}
	
	public void setGroupID(String GroupID) {
		this.GroupID = GroupID;
	}
	
	public List<User> getGroupUsers() {
		return GroupUsers;
	}
	
	public void setGroupUsers(List<User> GroupUsers) {
		this.GroupUsers = GroupUsers;
	}
	
}
