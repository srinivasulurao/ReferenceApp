package com.personaltrainer.model;

public class ActivitiesModel {

	int _id;
	String _name, _time, _points, _mypoints, _date, _category;
	
	public String get_category() {
		return _category;
	}

	public void set_category(String _category) {
		this._category = _category;
	}

	public String get_date() {
		return _date;
	}

	public void set_date(String _date) {
		this._date = _date;
	}

	//Constructor:
	public ActivitiesModel() {
		// TODO Auto-generated constructor stub
	}
	
	public ActivitiesModel(int _id, String _name, String _time, String _points,String _category) {
		super();
		this._id = _id;
		this._name = _name;
		this._time = _time;
		this._points = _points;
		this._category = _category;
	}

	public ActivitiesModel(String _name, String _time, String _points, String _category) {
		super();
		this._name = _name;
		this._time = _time;
		this._points = _points;
		this._category = _category;
	}

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public String get_name() {
		return _name;
	}

	public void set_name(String _name) {
		this._name = _name;
	}

	public String get_time() {
		return _time;
	}

	public void set_time(String _time) {
		this._time = _time;
	}

	public String get_points() {
		return _points;
	}

	public void set_points(String _points) {
		this._points = _points;
	}

	public String get_mypoints() {
		return _mypoints;
	}

	public void set_mypoints(String _mypoints) {
		this._mypoints = _mypoints;
	}
	
	

}
