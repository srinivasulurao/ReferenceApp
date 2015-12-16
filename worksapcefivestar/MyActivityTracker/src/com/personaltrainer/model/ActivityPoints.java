package com.personaltrainer.model;

public class ActivityPoints {
	
	
	int id;
	String morning_points;
	String noon_points;
	String evening_points;
	String night_points;
	String date;
	String message;
	String title;
	String total;
	String data;
	
	
	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getTotal() {
		return total;
	}

	public void setTotal(String total) {
		this.total = total;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getImgvalue() {
		return imgvalue;
	}

	public void setImgvalue(String imgvalue) {
		this.imgvalue = imgvalue;
	}
	String imgvalue;
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	//EMpty Constructor:
	public ActivityPoints()
	{
		
	}
	
	public ActivityPoints(int id, String morning_points, String noon_points,
			String evening_points, String night_points, String date) {
		super();
		this.id = id;
		this.morning_points = morning_points;
		this.noon_points = noon_points;
		this.evening_points = evening_points;
		this.night_points = night_points;
		this.date = date;
	}
	
	public ActivityPoints(String morning_points, String noon_points,
			String evening_points, String night_points, String date) {
		super();
		this.morning_points = morning_points;
		this.noon_points = noon_points;
		this.evening_points = evening_points;
		this.night_points = night_points;
		this.date = date;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getMorning_points() {
		return morning_points;
	}
	public void setMorning_points(String morning_points) {
		this.morning_points = morning_points;
	}
	public String getNoon_points() {
		return noon_points;
	}
	public void setNoon_points(String noon_points) {
		this.noon_points = noon_points;
	}
	public String getEvening_points() {
		return evening_points;
	}
	public void setEvening_points(String evening_points) {
		this.evening_points = evening_points;
	}
	public String getNight_points() {
		return night_points;
	}
	public void setNight_points(String night_points) {
		this.night_points = night_points;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}

}
