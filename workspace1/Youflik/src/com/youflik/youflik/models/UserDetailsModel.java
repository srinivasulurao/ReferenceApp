package com.youflik.youflik.models;

public class UserDetailsModel {
	private String firstname,lastname;
	private String  bio;
	private String  website_blog;
	private String  current_city;
	private String  user_profile_photo_path;
	private String  user_cover_photo_path;
	private String  song_title;
	private String 	song_cover_photo_path;
	private String  actual_song_path;
	private String 	song_basename;
	private String 	fried_count;
	private String 	follower_count;
	private String 	is_pending_friend_request;
	private String  is_accept_friend_request;
	private String 	is_friend;
	private String  status;
	// new 
	private String username;
	private String profile_photo_like_count;
	private String profile_photo_comment_count;
	private String profile_photo_share_count;
	private String cover_photo_like_count;
	private String cover_photo_comment_count;
	private String cover_photo_share_count;
	private String profile_photo_post_id;
	private String profile_photo_id;
	private String cover_photo_post_id;
	private String cover_photo_id;
	private String jid;
	private String accept_friend_request_id;
	private String pending_friend_request_id;
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getProfile_photo_like_count() {
		return profile_photo_like_count;
	}
	public void setProfile_photo_like_count(String profile_photo_like_count) {
		this.profile_photo_like_count = profile_photo_like_count;
	}
	public String getProfile_photo_comment_count() {
		return profile_photo_comment_count;
	}
	public void setProfile_photo_comment_count(String profile_photo_comment_count) {
		this.profile_photo_comment_count = profile_photo_comment_count;
	}
	public String getProfile_photo_share_count() {
		return profile_photo_share_count;
	}
	public void setProfile_photo_share_count(String profile_photo_share_count) {
		this.profile_photo_share_count = profile_photo_share_count;
	}
	public String getCover_photo_like_count() {
		return cover_photo_like_count;
	}
	public void setCover_photo_like_count(String cover_photo_like_count) {
		this.cover_photo_like_count = cover_photo_like_count;
	}
	public String getCover_photo_comment_count() {
		return cover_photo_comment_count;
	}
	public void setCover_photo_comment_count(String cover_photo_comment_count) {
		this.cover_photo_comment_count = cover_photo_comment_count;
	}
	public String getCover_photo_share_count() {
		return cover_photo_share_count;
	}
	public void setCover_photo_share_count(String cover_photo_share_count) {
		this.cover_photo_share_count = cover_photo_share_count;
	}
	public String getProfile_photo_post_id() {
		return profile_photo_post_id;
	}
	public void setProfile_photo_post_id(String profile_photo_post_id) {
		this.profile_photo_post_id = profile_photo_post_id;
	}
	public String getProfile_photo_id() {
		return profile_photo_id;
	}
	public void setProfile_photo_id(String profile_photo_id) {
		this.profile_photo_id = profile_photo_id;
	}
	public String getCover_photo_post_id() {
		return cover_photo_post_id;
	}
	public void setCover_photo_post_id(String cover_photo_post_id) {
		this.cover_photo_post_id = cover_photo_post_id;
	}
	public String getCover_photo_id() {
		return cover_photo_id;
	}
	public void setCover_photo_id(String cover_photo_id) {
		this.cover_photo_id = cover_photo_id;
	}
	public String getJid() {
		return jid;
	}
	public void setJid(String jid) {
		this.jid = jid;
	}

	
	
	public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	public String getFirstname() {
		return firstname;
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname;
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
	public String getUser_cover_photo_path() {
		return user_cover_photo_path;
	}
	public void setUser_cover_photo_path(String user_cover_photo_path) {
		this.user_cover_photo_path = user_cover_photo_path;
	}
	public String getSong_title() {
		return song_title;
	}
	public void setSong_title(String song_title) {
		this.song_title = song_title;
	}
	public String getSong_cover_photo_path() {
		return song_cover_photo_path;
	}
	public void setSong_cover_photo_path(String song_cover_photo_path) {
		this.song_cover_photo_path = song_cover_photo_path;
	}
	public String getActual_song_path() {
		return actual_song_path;
	}
	public void setActual_song_path(String actual_song_path) {
		this.actual_song_path = actual_song_path;
	}
	public String getSong_basename() {
		return song_basename;
	}
	public void setSong_basename(String song_basename) {
		this.song_basename = song_basename;
	}
	public String getFried_count() {
		return fried_count;
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
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getAccept_friend_request_id() {
		return accept_friend_request_id;
	}
	public void setAccept_friend_request_id(String accept_friend_request_id) {
		this.accept_friend_request_id = accept_friend_request_id;
	}
	public String getPending_friend_request_id() {
		return pending_friend_request_id;
	}
	public void setPending_friend_request_id(String pending_friend_request_id) {
		this.pending_friend_request_id = pending_friend_request_id;
	}

}
