package com.texastech.bean;

public class CalenderEvent {
	public int calenderId;
	public String nameOfEvent;
	public String startDate;
	public String endDate;
	public String description;

	@Override
	public String toString() {
		return nameOfEvent;
	}
}
