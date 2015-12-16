package com.personaltrainer.model;

public class RegistrationModel {

	int _id;
	String _name, _email, _password, _confirmPassword, _phone;

	//Empty Constructor:
	public RegistrationModel()
	{}
	
	//Constructor:
	public RegistrationModel(int _id, String _name, String _email,
			String _password, String _confirmPassword, String _phone) {
		super();
		this._id = _id;
		this._name = _name;
		this._email = _email;
		this._password = _password;
		this._confirmPassword = _confirmPassword;
		this._phone = _phone;
	}

	//Constructor:
	public RegistrationModel(String _name, String _email, String _password,
			String _confirmPassword, String _phone) {
		super();
		this._name = _name;
		this._email = _email;
		this._password = _password;
		this._confirmPassword = _confirmPassword;
		this._phone = _phone;
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

	public String get_email() {
		return _email;
	}

	public void set_email(String _email) {
		this._email = _email;
	}

	public String get_password() {
		return _password;
	}

	public void set_password(String _password) {
		this._password = _password;
	}

	public String get_confirmPassword() {
		return _confirmPassword;
	}

	public void set_confirmPassword(String _confirmPassword) {
		this._confirmPassword = _confirmPassword;
	}

	public String get_phone() {
		return _phone;
	}

	public void set_phone(String _phone) {
		this._phone = _phone;
	}
	
}
