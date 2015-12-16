package com.voicey.model;

public class PhoneContacts {

	String FirstName, LastName, PhoneNumber, EmailId;
	
	public String LastName() {
		return FirstName;
	}

	public void setFirstName(String firstname) {
		FirstName = firstname;
	}
	
	public String getLastName() {
		return LastName;
	}

	public void setLastName(String lastname) {
		LastName = lastname;
	}
	
	public String getPhoneNumber() {
		return PhoneNumber;
	}

	public void setPhoneNumber(String PhNumber) {
		PhoneNumber = PhNumber;
	}
	
	public String getEmailId() {
		return EmailId;
	}

	public void setEmailId(String emailId) {
		EmailId = emailId;
	}
}
