package com.omkar.myactivitytracker;

public class NotesModel {
	
	String date;
	String content;
	String id;
	String archieve;
	String audio;
	String picture;
	
	public String getArchieve() {
		return archieve;
	}
	public void setArchieve(String archieve) {
		this.archieve = archieve;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getAudio() {
		return audio;
	}
	public void setAudio(String audio) {
		this.audio = audio;
	}
	public String getPicture() {
		return picture;
	}
	public void setPicture(String picture) {
		this.picture = picture;
	}

}
