package com.voicey.model;

public class User {

	String userName, userCode, userId, GroupId;
    private boolean checked = false ;  
    
    public User() {}  
    
    public User( String name ) {  
      this.userName = name ;  
    }  
    
    public User( String name, boolean checked ) {  
        this.userName = name ;  
        this.checked = checked ;  
      }
    
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserCode() {
		return userCode;
	}

	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}

	public String getGroupId() {
		return GroupId;
	}

	public void setGroupId(String GId) {
		this.GroupId = GId;
	}
	
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public String toString() {
		return userName;
	}

	public void toggleChecked() {
		checked = !checked;
	}

}
