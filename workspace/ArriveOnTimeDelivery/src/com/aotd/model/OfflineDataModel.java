package com.aotd.model;

import java.io.Serializable;

public class OfflineDataModel implements Serializable{
	
	private String order_id;
	private String driver_id;
	private String rddateformat;
	private String min;
	private String timeZone;
	private String accountName;
	private String rev;
	private String PUInstruction;
	private String DLInstruction;
	private String ServiceName;
	private String Requestor;
	private String piece;
	private String weight;
	private String DPDate;
	private String DLDate;
	private String PUDate;
	private String RDDate;
	private String hour;
	private String signRoundTrip;
	private String signDelivery;
	private String isRoundTrip;
	private String pcRoundTrip;
	private String seqPU;
	private String addressBookIdPU;
	private String companyPU;
	private String addressPU;
	private String suitPU;
	private String cityPU;
	private String statePU;
	private String zipPU;
	private String cellPhonePU;
	private String homePhonePU;
	private String seqDL;
	private String addressBookIdDL;
	private String companyDL;
	private String addressDL;
	private String suitDL;
	private String cityDL;
	private String stateDL;
	private String zipDL;
	private String cellPhoneDL;
	private String homePhoneDL;
	private String waittime;
	private String vehicle;
	private String boxes;
	private String lastname;
	private String lastnamedl;
	private String notes;
	private byte[]  signaturedl;
	private byte[]  signature;
	/**
	 * @param Not stored in DB
	 */
	
	private String orderColor = null;
	private String orderStatus = null;
	
	/**
	 * Set and get method
	 * @return get methods returns String
	 */
	
	public String getOrder_id() {
		return order_id;
	}
	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}
	public String getDriver_id() {
		return driver_id;
	}
	public void setDriver_id(String driver_id) {
		this.driver_id = driver_id;
	}
	public String getRDDateFormat() {
		return rddateformat;
	}
	public void setRDDateFormat(String rddateformat) {
		this.rddateformat = rddateformat;
	}
	public String getMin() {
		return min;
	}
	public void setMin(String min) {
		this.min = min;
	}
	public String getTimeZone() {
		return timeZone;
	}
	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}
	
	public String getAccountName() {
		return accountName;
	}
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	
	public String getRev() {
		return rev;
	}
	public void setRev(String rev) {
		this.rev = rev;
	}
	
	public String getPUInstruction() {
		return PUInstruction;
	}
	public void setPUInstruction(String PUInstruction) {
		this.PUInstruction = PUInstruction;
	}
	
	public String getDLInstruction() {
		return DLInstruction;
	}
	public void setDLInstruction(String DLInstruction) {
		this.DLInstruction = DLInstruction;
	}
	
	public String getServiceName() {
		return ServiceName;
	}
	public void setServiceName(String ServiceName) {
		this.ServiceName = ServiceName;
	}
	
	public String getRequestor() {
		return Requestor;
	}
	public void setRequestor(String Requestor) {
		this.Requestor = Requestor;
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
	
	public String getDPDate() {
		return DPDate;
	}
	public void setDPDate(String DPDate) {
		this.DPDate = DPDate;
	}
	
	public String getDLDate() {
		return DLDate;
	}
	public void setDLDate(String DLDate) {
		this.DLDate = DLDate;
	}
	
	public String getPUDate() {
		return PUDate;
	}
	public void setPUDate(String PUDate) {
		this.PUDate = PUDate;
	}
	
	public String getRDDate() {
		return RDDate;
	}
	public void setRDDate(String rDDate) {
		RDDate = rDDate;
	}
	
	public String getHour() {
		return hour;
	}
	public void setHour(String hour) {
		this.hour = hour;
	}
	
	public String getSignRoundTrip() {
		return signRoundTrip;
	}
	public void setSignRoundTrip(String signRoundTrip) {
		this.signRoundTrip = signRoundTrip;
	}
	
	public String getSignDelivery() {
		return signDelivery;
	}
	public void setSignDelivery(String signDelivery) {
		this.signDelivery = signDelivery;
	}
	
	public String getIsRoundtrip() {
		return isRoundTrip;
	}
	public void setIsRoundtrip(String isRoundTrip) {
		this.isRoundTrip = isRoundTrip;
	}
	
	public String getPcRoundtrip() {
		return pcRoundTrip;
	}
	public void setPcRoundtrip(String pcRoundTrip) {
		this.pcRoundTrip = pcRoundTrip;
	}
	
	/** PU Address */
	
	public String getseqPU() {
		return seqPU;
	}
	public void setseqPU(String seqPU) {
		this.seqPU = seqPU;
	}
	
	/*public String getAddressBookIdPU() {
	return addressBookIdPU;
}
public void setAddressBookIdPU(String addressBookIdPU) {
	this.addressBookIdPU = addressBookIdPU;
}
	 */
	public String getCompanyPU() {
		return companyPU;
	}
	public void setCompanyPU(String companyPU) {
		this.companyPU = companyPU;
	}
	
	public String getAddressPU() {
		return addressPU;
	}
	public void setAddressPU(String addressPU) {
		this.addressPU = addressPU;
	}
	
	public String getSuitPU() {
		return suitPU;
	}
	public void setSuitPU(String suitPU) {
		this.suitPU = suitPU;
	}
	
	public String getCityPU() {
		return cityPU;
	}
	public void setCityPU(String cityPU) {
		this.cityPU = cityPU;
	}
	
	public String getStatePU() {
		return statePU;
	}
	public void setStatePU(String statePU) {
		this.statePU = statePU;
	}
	
	public String getZipPU() {
		return zipPU;
	}
	public void setZipPU(String zipPU) {
		this.zipPU = zipPU;
	}
	
	public String getCellPhonePU() {
		return cellPhonePU;
	}
	public void setCellPhonePU(String cellPhonePU) {
		this.cellPhonePU = cellPhonePU;
	}
	
	public String getHomePhonePU() {
		return homePhonePU;
	}
	public void setHomePhonePU(String homePhonePU) {
		this.homePhonePU = homePhonePU;
	}
	
	/** DL Address  */
	
	public String getseqDL() {
		return seqDL;
	}
	public void setseqDL(String seqDL) {
		this.seqDL = seqDL;
	}
	
	/*public String getAddressBookIdDL() {
	return addressBookIdDL;
}
public void setAddressBookIdDL(String addressBookIdDL) {
	this.addressBookIdDL = addressBookIdDL;
}
	 */
	public String getCompanyDL() {
		return companyDL;
	}
	public void setCompanyDL(String companyDL) {
		this.companyDL = companyDL;
	}
	
	public String getAddressDL() {
		return addressDL;
	}
	public void setAddressDL(String addressDL) {
		this.addressDL = addressDL;
	}
	
	public String getSuitDL() {
		return suitDL;
	}
	public void setSuitDL(String suitDL) {
		this.suitDL = suitDL;
	}
	
	public String getCityDL() {
		return cityDL;
	}
	public void setCityDL(String cityDL) {
		this.cityDL = cityDL;
	}
	
	public String getStateDL() {
		return stateDL;
	}
	public void setStateDL(String stateDL) {
		this.stateDL = stateDL;
	}
	
	public String getZipDL() {
		return zipDL;
	}
	public void setZipDL(String zipDL) {
		this.zipDL = zipDL;
	}
	
	public String getCellPhoneDL() {
		return cellPhoneDL;
	}
	public void setCellPhoneDL(String cellPhoneDL) {
		this.cellPhoneDL = cellPhoneDL;
	}
	
	public String getHomePhoneDL() {
		return homePhoneDL;
	}
	public void setHomePhoneDL(String homePhoneDL) {
		this.homePhoneDL = homePhoneDL;
	}
	
	/**
	 * get and set methods which are not stored in the db
	 */
	
	public String getOrderColor() {
		return orderColor;
	}
	public void setOrderColor(String orderColor) {
		this.orderColor = orderColor;
	}
	
	public String getOrderStatus() {
		return orderStatus;
	}
	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}
	
	public String getPickUpAddress(){
		return addressPU + "\n" + cityDL +  "\n" + statePU + "\n" + zipPU;	
	}
	
	public String getDeliveryAddress(){
		return addressDL + "\n" + cityDL + "\n" + stateDL + "\n" + zipDL;	
	}
	public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	public String getLastnamedl() {
		return lastnamedl;
	}
	public void setLastnamedl(String lastnamedl) {
		this.lastnamedl = lastnamedl;
	}
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	
	public String getWaittime() {
		return waittime;
	}
	public void setWaittime(String waittime) {
		this.waittime = waittime;
	}
	public String getVehicle() {
		return vehicle;
	}
	public void setVehicle(String vehicle) {
		this.vehicle = vehicle;
	}
	public String getBoxes() {
		return boxes;
	}
	public void setBoxes(String boxes) {
		this.boxes = boxes;
	}
	
	public byte[] getSignaturedl() {
		return signaturedl;
	}
	public void setSignaturedl(byte[] signaturedl) {
		this.signaturedl = signaturedl;
	}
	public byte[] getSignature() {
		return signature;
	}
	public void setSignature(byte[] signature) {
		this.signature = signature;
	}
	
}

