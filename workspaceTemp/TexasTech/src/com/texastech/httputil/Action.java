package com.texastech.httputil;



 
public enum Action {

	/*http://appddictionstudio.biz/texas-tech/web/getSplashScreens

		http://appddictionstudio.biz/texas-tech/web/getContacts

		http://appddictionstudio.biz/texas-tech/web/getFacultyDirectoryList

		http://appddictionstudio.biz/texas-tech/web/getHomeScreen

		http://appddictionstudio.biz/texas-tech/web/getCampusMaps

		http://appddictionstudio.biz/texas-tech/web/getEvents

		http://appddictionstudio.biz/texas-tech/web/getResources

		http://appddictionstudio.biz/texas-tech/web/getSocialMedia

		http://appddictionstudio.biz/texas-tech/web/getMentalHealth

		http://appddictionstudio.biz/texas-tech/web/getLoginScreen
		
		http://appddictionstudio.biz/texas-tech/web/getPrograms
		
		http://appddictionstudio.biz/texas-tech/web/getPhotoGalleries
*/	
	
	GET_SPLASH_SCREEN("getSplashScreens"),
	
	GET_CONTACTS("getContacts"),
	
	GET_FACULTY_DIRECTORY_LIST("getFacultyDirectoryList"),
	
	GET_HOME_SCREEN("getHomeScreen"),
	
	GET_CAMPUSMAPS("getCampusMaps"),
	
	GET_EVENTS("getEvents"),
	
	GET_RESOURCES("getResources"),
	
	GET_SOCIAL_MEDIA("getSocialMedia"),
	
	GET_MENTAL_HEALTH("getMentalHealth"),//getMentalHealth
	
	//GET_LOGIN_SCREEN("getMentalHealth"),
	
	CREATE_ACCOUNT("signUpCreateAccount"),
	
	LOGIN("SignIn"),
	//{"success":"true","result":{"ID":"187","AccountTypeID":"0","ScreenName":"qwert","FirstName":"bbbb","MiddleInitial":"","LastName":"cccc","Password":"qwert","Email":"z@z.z","Active":"1","User_image_URL":"","DOB":null,"About":"","RN_No":"","status_id":"3","Program":"","Graduation_Date":"0","Graduation_Month":"0","Graduation_Year":"0"}}
	
	GET_PROFILE_DETAILS("getProfileDetails"),
	
	GET_STATUS_TYPE("getStatusTypes"),
	
	UPLOAD_USER_IMAGE("uploadUserImage"),
	
	GET_GUID("getGUIDByDeviceParams"),
	
	FORGET_PASSWORD("Forgetpassword"),
	
	GET_PROGRAM("getPrograms"),
	
	SAVE_EVENT_SURVERY("saveEventSurveyDataByQrCode"),
	
	GET_PHOTO_GALLERY("getPhotoGalleries"),
	
	SEND_PUSH_NOTIFICATION("sendPushNotification");
	
	private String methodName;
	/**
	 * @param s
	 */
	private Action(String s) {
		methodName = s;
	}

	public String toString() {
		return methodName;
	}
	
	
	public static Action fromString(String text) {
		for (Action b : Action.values()) {
	       if (text.equalsIgnoreCase(b.methodName)) {
	          return b;
	       }
	    }
		return null;
	 }
}
