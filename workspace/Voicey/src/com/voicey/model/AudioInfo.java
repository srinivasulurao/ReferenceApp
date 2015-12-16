package com.voicey.model;

import java.util.ArrayList;
import java.util.List;

import com.rockerhieu.emojicon.emoji.Emojicon;

import android.graphics.Bitmap;

public class AudioInfo {

	String id, title, source, friendStr, userid, public_control, user_control,playAudio="No",forwardUserId,forwardUserName,
			user_code, fileName, counter, type, yourAd, categoryId, imageName,groupId,groupName,groupAdamin,groupCount,groupAdd,
			categoryName, fromUserName, fromUserId, Sharedate,replayMessage,shareId,videoFilePath,comments,todo,timerId,alertTime,isSnoozed;
	Bitmap imagebitmap;
	int position, isexpand = 0;
	Integer previousMessageCount=5;
	List<Friend> ccFriendList = new ArrayList<Friend>();
	List<Friend> ccGroupList = new ArrayList<Friend>();
	List<AudioInfo> previousMessageList = new ArrayList<AudioInfo>();
	List<ReplyMessages> replyMessageList = new ArrayList<ReplyMessages>();
	
	Emojicon emojicon;
	
	Boolean isDeleteRequired;
	
	
	

	public String getForwardUserId() {
		return forwardUserId;
	}

	public void setForwardUserId(String forwardUserId) {
		this.forwardUserId = forwardUserId;
	}

	public String getForwardUserName() {
		return forwardUserName;
	}

	public void setForwardUserName(String forwardUserName) {
		this.forwardUserName = forwardUserName;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getUserid() {
		return userid;
	}

	public String getFriendStr() {
		return friendStr;
	}

	public void setFriendStr(String friendStr) {
		this.friendStr = friendStr;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getPublic_control() {
		return public_control;
	}

	public void setPublic_control(String public_control) {
		this.public_control = public_control;
	}

	public String getUser_control() {
		return user_control;
	}

	public void setUser_control(String user_control) {
		this.user_control = user_control;
	}

	public String getUser_code() {
		return user_code;
	}

	public void setUser_code(String user_code) {
		this.user_code = user_code;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCounter() {
		return counter;
	}

	public void setCounter(String counter) {
		this.counter = counter;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public Boolean getIsDeleteRequired() {
		return isDeleteRequired;
	}

	public void setIsDeleteRequired(Boolean isDeleteRequired) {
		this.isDeleteRequired = isDeleteRequired;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getYourAd() {
		return yourAd;
	}

	public void setYourAd(String yourAd) {
		this.yourAd = yourAd;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public Bitmap getImagebitmap() {
		return imagebitmap;
	}

	public void setImagebitmap(Bitmap imagebitmap) {
		this.imagebitmap = imagebitmap;
	}

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getFromUserName() {
		return fromUserName;
	}

	public void setFromUserName(String fromUserName) {
		this.fromUserName = fromUserName;
	}

	public String getFromUserId() {
		return fromUserId;
	}

	public void setFromUserId(String fromUserId) {
		this.fromUserId = fromUserId;
	}

	public String getSharedate() {
		return Sharedate;
	}

	public void setSharedate(String sharedate) {
		Sharedate = sharedate;
	}

	public int getIsexpand() {
		return isexpand;
	}

	public void setIsexpand(int isexpand) {
		this.isexpand = isexpand;
	}

	public List<Friend> getCcFriendList() {
		return ccFriendList;
	}

	public void setCcFriendList(List<Friend> ccFriendList) {
		this.ccFriendList = ccFriendList;
	}

	public String getReplayMessage() {
		return replayMessage;
	}

	public void setReplayMessage(String replayMessage) {
		this.replayMessage = replayMessage;
	}

	public String getShareId() {
		return shareId;
	}

	public void setShareId(String shareId) {
		this.shareId = shareId;
	}

	public List<AudioInfo> getPreviousMessageList() {
		return previousMessageList;
	}

	public void setPreviousMessageList(List<AudioInfo> previousMessageList) {
		this.previousMessageList = previousMessageList;
	}

	public String getVideoFilePath() {
		return videoFilePath;
	}

	public void setVideoFilePath(String videoFilePath) {
		this.videoFilePath = videoFilePath;
	}

	public Integer getPreviousMessageCount() {
		return previousMessageCount;
	}

	public void setPreviousMessageCount(Integer previousMessageCount) {
		this.previousMessageCount = previousMessageCount;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getTodo() {
		return todo;
	}

	public void setTodo(String todo) {
		this.todo = todo;
	}

	public String getTimerId() {
		return timerId;
	}

	public void setTimerId(String timerId) {
		this.timerId = timerId;
	}

	public String getAlertTime() {
		return alertTime;
	}

	public void setAlertTime(String alertTime) {
		this.alertTime = alertTime;
	}

	public String getPlayAudio() {
		return playAudio;
	}

	public void setPlayAudio(String playAudio) {
		this.playAudio = playAudio;
	}

	public String getIsSnoozed() {
		return isSnoozed;
	}

	public void setIsSnoozed(String isSnoozed) {
		this.isSnoozed = isSnoozed;
	}

	public Emojicon getEmojicon() {
		return emojicon;
	}

	public void setEmojicon(Emojicon emojicon) {
		this.emojicon = emojicon;
	}

	public List<ReplyMessages> getReplyMessageList() {
		return replyMessageList;
	}

	public void setReplyMessageList(List<ReplyMessages> replyMessageList) {
		this.replyMessageList = replyMessageList;
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

	public List<Friend> getCcGroupList() {
		return ccGroupList;
	}

	public void setCcGroupList(List<Friend> ccGroupList) {
		this.ccGroupList = ccGroupList;
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

	public String getGroupAdd() {
		return groupAdd;
	}

	public void setGroupAdd(String groupAdd) {
		this.groupAdd = groupAdd;
	}
	
	

}
