package com.aotd.model;

import java.io.Serializable;

import android.graphics.Bitmap;

public class SignatureModel implements Serializable{

	
	private String lastname;
	private byte[]  bytes;
	
	
	
	public byte[] getBytes() {
		return bytes;
	}
	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}
	
	
	public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	
}
