package com.voicey.model;

public class Friend {

	String FriendName, FriendId, userId, type, GrpName, GrpId;

	String FirstName, LastName, PhoneNumber, EmailId, Blockid, SearchName, BlockState;
	
	public String getSearchName() {
		return SearchName;
	}

	public void setSearchName(String Name) {
		SearchName = Name;
	}
	
	public String getFriendName() {
		return FriendName;
	}

	public void setFriendName(String friendName) {
		FriendName = friendName;
	}

	public String getFriendId() {
		return FriendId;
	}

	public void setFriendId(String friendId) {
		FriendId = friendId;
	}
	
	public String getBlockId() {
		return Blockid;
	}

	public void setBlockId(String Bid) {
		Blockid = Bid;
	}
	
	public String getBlockState() {
		return BlockState;
	}

	public void setBlockState(String BlkState) {
		BlockState = BlkState;
	}

	// Getters & Setters for groups
	public String getGroupName() {
		return GrpName;
	}

	public void setGroupName(String GroupName) {
		GrpName = GroupName;
	}

	public String getGroupID() {
		return GrpId;
	}

	public void setGroupID(String GroupID) {
		GrpId = GroupID;
	}

	// Getters & Setters for users
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	// Getters & Setters for Contacts

	public String getFirstName() {
		return FirstName;
	}

	public void setFirstName(String firstname) {
		FirstName = firstname;
	}

	public String getLastName() {
		return LastName;
	}

	public void setLastName(String lastname) {
		LastName = lastname;
	}

	public String getPhoneNumber() {
		return PhoneNumber;
	}

	public void setPhoneNumber(String PhNumber) {
		PhoneNumber = PhNumber;
	}

	public String getEmailId() {
		return EmailId;
	}

	public void setEmailId(String emailId) {
		EmailId = emailId;
	}

	public String getGrpName() {
		return GrpName;
	}

	public void setGrpName(String grpName) {
		GrpName = grpName;
	}

	public String getGrpId() {
		return GrpId;
	}

	public void setGrpId(String grpId) {
		GrpId = grpId;
	}
	

}
