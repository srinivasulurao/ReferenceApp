package com.youflik.youflik.models;

public class ConversationsMessagesModel {

	private int convmessage_id;
	private String convmessage_time;
	private String convmessage_direction;
	private String convmessage_type;
	private String convmessage_message;
	private int conversation_id;
	private String userImage;

	public String getConvmessage_time() {
		return convmessage_time;
	}
	public void setConvmessage_time(String convmessage_time) {
		this.convmessage_time = convmessage_time;
	}
	public String getConvmessage_direction() {
		return convmessage_direction;
	}
	public void setConvmessage_direction(String convmessage_direction) {
		this.convmessage_direction = convmessage_direction;
	}
	public String getConvmessage_type() {
		return convmessage_type;
	}
	public void setConvmessage_type(String convmessage_type) {
		this.convmessage_type = convmessage_type;
	}
	public String getConvmessage_message() {
		return convmessage_message;
	}
	public void setConvmessage_message(String convmessage_message) {
		this.convmessage_message = convmessage_message;
	}

	public int getConvmessage_id() {
		return convmessage_id;
	}
	public void setConvmessage_id(int convmessage_id) {
		this.convmessage_id = convmessage_id;
	}
	public int getConversation_id() {
		return conversation_id;
	}
	public void setConversation_id(int conversation_id) {
		this.conversation_id = conversation_id;
	}
	public String getUserImage() {
		return userImage;
	}
	public void setUserImage(String userImage) {
		this.userImage = userImage;
	}



}
