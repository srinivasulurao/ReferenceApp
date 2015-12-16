package com.youflik.youflik.models;

public class NotificationFriendRequestModel {
	private String friend_request_id;
	private String  created_date;
	private String  user_id;
	private String  firstname;
	private String  lastname;
	private String  user_profile_photo;
	private String  original_dimensions;
	private String  thumb1; 
	private String  thumb2;
	private String  thumb3;
	private String  status;

	
	public String getFriend_request_id() {
		return friend_request_id;
	}
	public void setFriend_request_id(String friend_request_id) {
		this.friend_request_id = friend_request_id;
	}
	public String getCreated_date() {
		return created_date;
	}
	public void setCreated_date(String created_date) {
		this.created_date = created_date;
	}
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
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
	public String getUser_profile_photo() {
		return user_profile_photo;
	}
	public void setUser_profile_photo(String user_profile_photo) {
		this.user_profile_photo = user_profile_photo;
	}
	public String getOriginal_dimensions() {
		return original_dimensions;
	}
	public void setOriginal_dimensions(String original_dimensions) {
		this.original_dimensions = original_dimensions;
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
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

}
