package com.youflik.youflik.models;

public class SearchModel {
	private String joined_date;
	private String user_id;
	private String firstname;
	private String lastname;
	private String bio;
	private String website_blog;
	private String current_city;
	private String user_profile_photo_path;
	private String is_pending_friend_request;
	private String is_accept_friend_request;
	private String is_friend;
	private String fried_count;
	private String follower_count;
	private String block_id;
	private String pending_friend_request_id;
	private String accept_friend_request_id;
	// for block

	public String getBlock_id() {
		return block_id;
	}
	public void setBlock_id(String block_id) {
		this.block_id = block_id;
	}
//block

	public String getJoined_date() {
		return joined_date;
	}
	public void setJoined_date(String joined_date) {
		this.joined_date = joined_date;
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
	public String getBio() {
		return bio;
	}
	public void setBio(String bio) {
		this.bio = bio;
	}
	public String getWebsite_blog() {
		return website_blog;
	}
	public void setWebsite_blog(String website_blog) {
		this.website_blog = website_blog;
	}
	public String getCurrent_city() {
		return current_city;
	}
	public void setCurrent_city(String current_city) {
		this.current_city = current_city;
	}
	public String getUser_profile_photo_path() {
		return user_profile_photo_path;
	}
	public void setUser_profile_photo_path(String user_profile_photo_path) {
		this.user_profile_photo_path = user_profile_photo_path;
	}
	public String getIs_pending_friend_request() {
		return is_pending_friend_request;
	}
	public void setIs_pending_friend_request(String is_pending_friend_request) {
		this.is_pending_friend_request = is_pending_friend_request;
	}
	public String getIs_accept_friend_request() {
		return is_accept_friend_request;
	}
	public void setIs_accept_friend_request(String is_accept_friend_request) {
		this.is_accept_friend_request = is_accept_friend_request;
	}
	public String getIs_friend() {
		return is_friend;
	}
	public void setIs_friend(String is_friend) {
		this.is_friend = is_friend;
	}
	public String getFried_count() {
		return fried_count;
	}
	public String getPending_friend_request_id() {
		return pending_friend_request_id;
	}
	public void setPending_friend_request_id(String pending_friend_request_id) {
		this.pending_friend_request_id = pending_friend_request_id;
	}
	public String getAccept_friend_request_id() {
		return accept_friend_request_id;
	}
	public void setAccept_friend_request_id(String accept_friend_request_id) {
		this.accept_friend_request_id = accept_friend_request_id;
	}
	public void setFried_count(String fried_count) {
		this.fried_count = fried_count;
	}
	public String getFollower_count() {
		return follower_count;
	}
	public void setFollower_count(String follower_count) {
		this.follower_count = follower_count;
	}

}
