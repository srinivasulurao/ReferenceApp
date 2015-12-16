package com.voicey.model;

import java.util.ArrayList;

public class FriendMessages {
	
	String friendName,messageCount,messageShow="false",friendId,groupId,groupName,groupAdamin,groupCount;
	ArrayList<AudioInfo> messageList =new ArrayList<AudioInfo>();
	
	
	

	public String getFriendName() {
		return friendName;
	}

	public void setFriendName(String friendName) {
		this.friendName = friendName;
	}

	public String getMessageCount() {
		return messageCount;
	}

	public void setMessageCount(String messageCount) {
		this.messageCount = messageCount;
	}

	public ArrayList<AudioInfo> getMessageList() {
		return messageList;
	}

	public void setMessageList(ArrayList<AudioInfo> messageList) {
		this.messageList = messageList;
	}

	public String getMessageShow() {
		return messageShow;
	}

	public void setMessageShow(String messageShow) {
		this.messageShow = messageShow;
	}

	public String getFriendId() {
		return friendId;
	}

	public void setFriendId(String friendId) {
		this.friendId = friendId;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getGroupAdamin() {
		return groupAdamin;
	}

	public void setGroupAdamin(String groupAdamin) {
		this.groupAdamin = groupAdamin;
	}

	public String getGroupCount() {
		return groupCount;
	}

	public void setGroupCount(String groupCount) {
		this.groupCount = groupCount;
	}
	
	
	
	
	
	

}
