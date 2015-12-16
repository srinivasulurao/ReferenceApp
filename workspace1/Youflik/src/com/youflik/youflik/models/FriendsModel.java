package com.youflik.youflik.models;

public class FriendsModel {
	private String  connection_id;
	private String  user_two;
	private String  created_date;
	private String  firstname;
	private String  lastname;
	private String  username;
	private String jid;
	private String  email;
	private String  user_profile_photo;
	private String  thumb1;
	private String  thumb2;
	private String  thumb3;
	private boolean selected;

	public String getConnection_id() {
		return connection_id;
	}
	public void setConnection_id(String connection_id) {
		this.connection_id = connection_id;
	}
	public String getUser_two() {
		return user_two;
	}
	public void setUser_two(String user_two) {
		this.user_two = user_two;
	}
	public String getCreated_date() {
		return created_date;
	}
	public void setCreated_date(String created_date) {
		this.created_date = created_date;
	}
	public String getFirstname() {
		return firstname;
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getUser_profile_photo() {
		return user_profile_photo;
	}
	public void setUser_profile_photo(String user_profile_photo) {
		this.user_profile_photo = user_profile_photo;
	}
	public String getThumb1() {
		return thumb1;
	}
	public void setThumb1(String thumb1) {
		this.thumb1 = thumb1;
	}
	public String getThumb2() {
		return thumb2;
	}
	public void setThumb2(String thumb2) {
		this.thumb2 = thumb2;
	}
	public String getThumb3() {
		return thumb3;
	}
	public void setThumb3(String thumb3) {
		this.thumb3 = thumb3;
	}
	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	public String getJid() {
		return jid;
	}
	public void setJid(String jid) {
		this.jid = jid;
	}

}
