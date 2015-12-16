package com.voicey.model;

import android.graphics.Bitmap;

public class ChatMessages {
	
	String chatMessage,userName,time,type,isDispalyDetail="0";
	
	Bitmap imgBiteMap;

	public String getChatMessage() {
		return chatMessage;
	}

	public void setChatMessage(String chatMessage) {
		this.chatMessage = chatMessage;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getIsDispalyDetail() {
		return isDispalyDetail;
	}

	public void setIsDispalyDetail(String isDispalyDetail) {
		this.isDispalyDetail = isDispalyDetail;
	}

	public Bitmap getImgBiteMap() {
		return imgBiteMap;
	}

	public void setImgBiteMap(Bitmap imgBiteMap) {
		this.imgBiteMap = imgBiteMap;
	}
	
	
	

}
