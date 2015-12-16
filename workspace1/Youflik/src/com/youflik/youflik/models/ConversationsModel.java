package com.youflik.youflik.models;

public class ConversationsModel {

	private int conversation_id;
	private int login_user_id;
	private String login_user_jid;
	private String login_user_resource;
	private String login_user_display_name;
	private int with_user_id;
	private String with_user_jid;
	private String with_user_resource;
	private String with_user_display_name;
	private String with_user_profilepicurl;
	private String start_time;
	private String end_time;
	private String last_message;
	private String last_message_direction;
	private String message_iseen;
	private String message_isseen_count;

	public String getMessage_iseen() {
		return message_iseen;
	}
	public void setMessage_iseen(String message_iseen) {
		this.message_iseen = message_iseen;
	}
	public String getMessage_isseen_count() {
		return message_isseen_count;
	}
	public void setMessage_isseen_count(String message_isseen_count) {
		this.message_isseen_count = message_isseen_count;
	}

	public int getConversation_id() {
		return conversation_id;
	}
	public void setConversation_id(int conversation_id) {
		this.conversation_id = conversation_id;
	}
	public int getLogin_user_id() {
		return login_user_id;
	}
	public void setLogin_user_id(int login_user_id) {
		this.login_user_id = login_user_id;
	}

	public String getLogin_user_jid() {
		return login_user_jid;
	}
	public void setLogin_user_jid(String login_user_jid) {
		this.login_user_jid = login_user_jid;
	}
	public String getLogin_user_resource() {
		return login_user_resource;
	}
	public void setLogin_user_resource(String login_user_resource) {
		this.login_user_resource = login_user_resource;
	}
	public String getLogin_user_display_name() {
		return login_user_display_name;
	}
	public void setLogin_user_display_name(String login_user_display_name) {
		this.login_user_display_name = login_user_display_name;
	}
	public int getWith_user_id() {
		return with_user_id;
	}
	public void setWith_user_id(int with_user_id) {
		this.with_user_id = with_user_id;
	}
	public String getWith_user_jid() {
		return with_user_jid;
	}
	public void setWith_user_jid(String with_user_jid) {
		this.with_user_jid = with_user_jid;
	}
	public String getWith_user_resource() {
		return with_user_resource;
	}
	public void setWith_user_resource(String with_user_resource) {
		this.with_user_resource = with_user_resource;
	}
	public String getWith_user_display_name() {
		return with_user_display_name;
	}
	public void setWith_user_display_name(String with_user_display_name) {
		this.with_user_display_name = with_user_display_name;
	}
	public String getWith_user_profilepicurl() {
		return with_user_profilepicurl;
	}
	public void setWith_user_profilepicurl(String with_user_profilepicurl) {
		this.with_user_profilepicurl = with_user_profilepicurl;
	}
	public String getStart_time() {
		return start_time;
	}
	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}
	public String getEnd_time() {
		return end_time;
	}
	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}
	public String getLast_message() {
		return last_message;
	}
	public void setLast_message(String last_message) {
		this.last_message = last_message;
	}
	public String getLast_message_direction() {
		return last_message_direction;
	}
	public void setLast_message_direction(String last_message_direction) {
		this.last_message_direction = last_message_direction;
	}
}
