package com.texastech.bean;

public class Reminder {
	
	private int id;
	
	private String name;
	
	private String reminderTime;
	
	private String reminderType;
	
	private String reminderDateTime;
	
	private boolean isChecked= false;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public String getReminderTime() {
		return reminderTime;
	}

	public void setReminderTime(String reminderTime) {
		this.reminderTime = reminderTime;
	}

	public String getReminderType() {
		return reminderType;
	}

	public void setReminderType(String reminderType) {
		this.reminderType = reminderType;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getReminderDateTime() {
		return reminderDateTime;
	}

	public void setReminderDateTime(String reminderDateTime) {
		this.reminderDateTime = reminderDateTime;
	}
	
	
	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}
}
