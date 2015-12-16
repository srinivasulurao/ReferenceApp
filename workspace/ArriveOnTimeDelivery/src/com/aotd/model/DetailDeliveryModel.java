package com.aotd.model;

import java.io.Serializable;

public class DetailDeliveryModel implements Serializable{
	
	
	private String accountName = "";
	private String PUInstruction="";
	private String DLInstruction="";
	private String RDDate="";
	private String ref="";	
	private String serviceName="";
	private String piece="";
	private String weight="";
	private String roundTrip="";
	private String accountnotes = "";
	private String drivernotes = "";
	private String adminNotes = "";
	
	private String requestor="";	
	private String PUordersID="";
	private String PUsuborderID="";
	private String PUseq="";
	private String PUisPU="";
	private String PUaddressBookID="";
	private String PUcompany="";
	private String PUaddress="";
	private String PUsuit="";
	private String PUcity="";
	private String PUstate="";
	private String PUzip="";
	private String PUcellPhone="";
	private String PUhomephone="";
	
	private String DLordersID="";
	private String DLsuborderID="";
	private String DLseq;
	private String DLisPU="";
	private String DLaddressBookID="";
	private String DLcompany="";
	private String DLaddress="";
	private String DLsuit="";
	private String DLcity="";
	private String DLstate="";
	private String DLzip="";
	private String DLcellPhone="";
	private String DLhomephone="";
	
	
	public String getAccountName() {
		return accountName;
	}
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	public String getPUInstruction() {
		return PUInstruction;
	}
	public void setPUInstruction(String pUInstruction) {
		PUInstruction = pUInstruction;
	}
	public String getDLInstruction() {
		return DLInstruction;
	}
	public void setDLInstruction(String dLInstruction) {
		DLInstruction = dLInstruction;
	}
	public String getRDDate() {
		return RDDate;
	}
	public void setRDDate(String rDDate) {
		RDDate = rDDate;
	}
	public String getRef() {
		return ref;
	}
	public void setRef(String ref) {
		this.ref = ref;
	}
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public String getPiece() {
		return piece;
	}
	public void setPiece(String piece) {
		this.piece = piece;
	}
	public String getWeight() {
		return weight;
	}
	public void setWeight(String weight) {
		this.weight = weight;
	}
	public String getRoundTrip() {
		return roundTrip;
	}
	public void setRoundTrip(String roundTrip) {
		this.roundTrip = roundTrip;
	}
	public String getAccountnotes() {
		return accountnotes;
	}
	public void setAccountnotes(String accountnotes) {
		this.accountnotes = accountnotes;
	}
	public String getDrivernotes() {
		return drivernotes;
	}
	public void setDrivernotes(String drivernotes) {
		this.drivernotes = drivernotes;
	}
	public String getAdminNotes() {
		return adminNotes;
	}
	public void setAdminNotes(String adminNotes) {
		this.adminNotes = adminNotes;
	}
	public String getRequestor() {
		return requestor;
	}
	public void setRequestor(String requestor) {
		this.requestor = requestor;
	}
	public String getPUordersID() {
		return PUordersID;
	}
	public void setPUordersID(String pUordersID) {
		PUordersID = pUordersID;
	}
	public String getPUsuborderID() {
		return PUsuborderID;
	}
	public void setPUsuborderID(String pUsuborderID) {
		PUsuborderID = pUsuborderID;
	}
	public String getPUseq() {
		return PUseq;
	}
	public void setPUseq(String pUseq) {
		PUseq = pUseq;
	}
	public String getPUisPU() {
		return PUisPU;
	}
	public void setPUisPU(String pUisPU) {
		PUisPU = pUisPU;
	}
	public String getPUaddressBookID() {
		return PUaddressBookID;
	}
	public void setPUaddressBookID(String pUaddressBookID) {
		PUaddressBookID = pUaddressBookID;
	}
	public String getPUcompany() {
		return PUcompany;
	}
	public void setPUcompany(String pUcompany) {
		PUcompany = pUcompany;
	}
	public String getPUaddress() {
		return PUaddress;
	}
	public void setPUaddress(String pUaddress) {
		PUaddress = pUaddress;
	}
	public String getPUsuit() {
		return PUsuit;
	}
	public void setPUsuit(String pUsuit) {
		PUsuit = pUsuit;
	}
	public String getPUcity() {
		return PUcity;
	}
	public void setPUcity(String pUcity) {
		PUcity = pUcity;
	}
	public String getPUstate() {
		return PUstate;
	}
	public void setPUstate(String pUstate) {
		PUstate = pUstate;
	}
	public String getPUzip() {
		return PUzip;
	}
	public void setPUzip(String pUzip) {
		PUzip = pUzip;
	}
	public String getPUcellPhone() {
		return PUcellPhone;
	}
	public void setPUcellPhone(String pUcellPhone) {
		PUcellPhone = pUcellPhone;
	}
	public String getPUhomephone() {
		return PUhomephone;
	}
	public void setPUhomephone(String pUhomephone) {
		PUhomephone = pUhomephone;
	}
	public String getDLordersID() {
		return DLordersID;
	}
	public void setDLordersID(String dLordersID) {
		DLordersID = dLordersID;
	}
	public String getDLsuborderID() {
		return DLsuborderID;
	}
	public void setDLsuborderID(String dLsuborderID) {
		DLsuborderID = dLsuborderID;
	}
	public String getDLseq() {
		return DLseq;
	}
	public void setDLseq(String dLseq) {
		DLseq = dLseq;
	}
	public String getDLisPU() {
		return DLisPU;
	}
	public void setDLisPU(String dLisPU) {
		DLisPU = dLisPU;
	}
	public String getDLaddressBookID() {
		return DLaddressBookID;
	}
	public void setDLaddressBookID(String dLaddressBookID) {
		DLaddressBookID = dLaddressBookID;
	}
	public String getDLcompany() {
		return DLcompany;
	}
	public void setDLcompany(String dLcompany) {
		DLcompany = dLcompany;
	}
	public String getDLaddress() {
		return DLaddress;
	}
	public void setDLaddress(String dLaddress) {
		DLaddress = dLaddress;
	}
	public String getDLsuit() {
		return DLsuit;
	}
	public void setDLsuit(String dLsuit) {
		DLsuit = dLsuit;
	}
	public String getDLcity() {
		return DLcity;
	}
	public void setDLcity(String dLcity) {
		DLcity = dLcity;
	}
	public String getDLstate() {
		return DLstate;
	}
	public void setDLstate(String dLstate) {
		DLstate = dLstate;
	}
	public String getDLzip() {
		return DLzip;
	}
	public void setDLzip(String dLzip) {
		DLzip = dLzip;
	}
	public String getDLcellPhone() {
		return DLcellPhone;
	}
	public void setDLcellPhone(String dLcellPhone) {
		DLcellPhone = dLcellPhone;
	}
	public String getDLhomephone() {
		return DLhomephone;
	}
	public void setDLhomephone(String dLhomephone) {
		DLhomephone = dLhomephone;
	}
	
}	
	
	
	