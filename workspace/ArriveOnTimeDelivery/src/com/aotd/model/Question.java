package com.aotd.model;

import java.util.ArrayList;

public class Question {
	
	public static String DLName;
	public static String DLPhoneCall;
	public static String DLHomeCall;
	public static String PUPhoneCall;
	public static String PUHomeCall;
	public static String PIECE;
	
	public static ArrayList<DetailDeliveryModel> aListClass;
	public static ArrayList<DispatchAllListModel> mListClass;
	
	public static ArrayList<DispatchAllListModel> offDel; 
	

	public static ArrayList<DispatchAllListModel> getmListClass() {
		return mListClass;
	}

	public static void setmListClass(ArrayList<DispatchAllListModel> mListClass) {
		Question.mListClass = mListClass;
	}

	public static ArrayList<DispatchAllListModel> getOffDel() {
		return offDel;
	}

	public static void setOffDel(ArrayList<DispatchAllListModel> offDel) {
		Question.offDel = offDel;
	}

	public static ArrayList<DetailDeliveryModel> getaListClass() {
		return aListClass;
	}

	public static void setaListClass(ArrayList<DetailDeliveryModel> aListClass) {
		Question.aListClass = aListClass;
	}

	public static String getPIECE() {
		return PIECE;
	}

	public static void setPIECE(String pIECE) {
		PIECE = pIECE;
	}

	public static String getDLPhoneCall() {
		return DLPhoneCall;
	}

	public static void setDLPhoneCall(String dLPhoneCall) {
		DLPhoneCall = dLPhoneCall;
	}

	public static String getDLHomeCall() {
		return DLHomeCall;
	}

	public static void setDLHomeCall(String dLHomeCall) {
		DLHomeCall = dLHomeCall;
	}

	public static String getPUPhoneCall() {
		return PUPhoneCall;
	}

	public static void setPUPhoneCall(String pUPhoneCall) {
		PUPhoneCall = pUPhoneCall;
	}

	public static String getPUHomeCall() {
		return PUHomeCall;
	}

	public static void setPUHomeCall(String pUHomeCall) {
		PUHomeCall = pUHomeCall;
	}

	public static String getDLName() {
		return DLName;
	}

	public static void setDLName(String dLName) {
		DLName = dLName;
	}

	
}
