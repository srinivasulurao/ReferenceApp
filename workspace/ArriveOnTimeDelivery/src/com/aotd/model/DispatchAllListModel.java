package com.aotd.model;

import java.io.Serializable;

public class DispatchAllListModel implements Serializable {
	
	
	private String order_id = "";
	private String driver_id = "";
	private String RDDate ="";
	private String min = "";
	private String timezone = "";
	private String orderColor ="";
	private String accountName = "";
	private String DLDate ="";
	private String PUDate ="'";
	private String hour = "";	
	private String orderStatus="";
	private String SignRoundTrip="";	
	private String SignDelivery="";
	private String isRoundTrip="";
	private String PCRoundTrip="";
	private String DPDate="";
	
	private String company = "";
	private String address = "";
	private String suit = "";
	private String city = "";
	private String state = "";
	private String zip = "";
	private double Latitude;
	private double Longitude;
	
	private String dlcompany = "";
	private String dladdress = "";
	private String dlsuit = "";
	private String dlcity = "";
	private String dlstate = "";
	private String dlzip = "";
	private double dlLatitude;
	private double dlLongitude;
	
	private Boolean selectedItem = false;
	
	private String puCellPhone= "";
	private String puHomephone= "";
	private String dlCellPhone= "";
	private String dlHomePhone= "";
	
	private String accountnotes= "";
	private String adminnotes= "";
	private String peice= "";
	private String weight= "";
	
	private String ref= "";
	private String requestor= "";
	private String puinstruction= "";
	private String dlinstruction= "";
	
	private String id= "";
	private String Order_Status= "";
	private String username= "";
	private String password= "";
	
	private String lastname= "";
	private String notes= "";
	private String url= "";
	private String filename= "";
	private String imgstr= "";
	private String yesno= "";
	private String datetime= "";
	private String tag= "";
	
	
	public String getDatetime() {
		return datetime;
	}
	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getImgstr() {
		return imgstr;
	}
	public void setImgstr(String imgstr) {
		this.imgstr = imgstr;
	}
	public String getYesno() {
		return yesno;
	}
	public void setYesno(String yesno) {
		this.yesno = yesno;
	}
	public String getOrder_Status() {
		return Order_Status;
	}
	public void setOrder_Status(String order_Status) {
		Order_Status = order_Status;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPuinstruction() {
		return puinstruction;
	}
	public void setPuinstruction(String puinstruction) {
		this.puinstruction = puinstruction;
	}
	public String getDlinstruction() {
		return dlinstruction;
	}
	public void setDlinstruction(String dlinstruction) {
		this.dlinstruction = dlinstruction;
	}
	public String getRef() {
		return ref;
	}
	public void setRef(String ref) {
		this.ref = ref;
	}
	public String getRequestor() {
		return requestor;
	}
	public void setRequestor(String requestor) {
		this.requestor = requestor;
	}
	public String getAccountnotes() {
		return accountnotes;
	}
	public void setAccountnotes(String accountnotes) {
		this.accountnotes = accountnotes;
	}
	public String getAdminnotes() {
		return adminnotes;
	}
	public void setAdminnotes(String adminnotes) {
		this.adminnotes = adminnotes;
	}
	public String getPeice() {
		return peice;
	}
	public void setPeice(String peice) {
		this.peice = peice;
	}
	public String getWeight() {
		return weight;
	}
	public void setWeight(String weight) {
		this.weight = weight;
	}
	public String getPuCellPhone() {
		return puCellPhone;
	}
	public void setPuCellPhone(String puCellPhone) {
		this.puCellPhone = puCellPhone;
	}
	public String getPuHomephone() {
		return puHomephone;
	}
	public void setPuHomephone(String puHomephone) {
		this.puHomephone = puHomephone;
	}
	public String getDlCellPhone() {
		return dlCellPhone;
	}
	public void setDlCellPhone(String dlCellPhone) {
		this.dlCellPhone = dlCellPhone;
	}
	public String getDlHomePhone() {
		return dlHomePhone;
	}
	public void setDlHomePhone(String dlHomePhone) {
		this.dlHomePhone = dlHomePhone;
	}
	
	
	public String getDPDate() {
		return DPDate;
	}
	public void setDPDate(String dPDate) {
		DPDate = dPDate;
	}
	public String getSignRoundTrip() {
		return SignRoundTrip;
	}
	public void setSignRoundTrip(String signRoundTrip) {
		SignRoundTrip = signRoundTrip;
	}
	public String getSignDelivery() {
		return SignDelivery;
	}
	public void setSignDelivery(String signDelivery) {
		SignDelivery = signDelivery;
	}	
	
	public String getIsRoundTrip() {
		return isRoundTrip;
	}
	public void setIsRoundTrip(String isRoundTrip) {
		this.isRoundTrip = isRoundTrip;
	}
	public String getPCRoundTrip() {
		return PCRoundTrip;
	}
	public void setPCRoundTrip(String pCRoundTrip) {
		PCRoundTrip = pCRoundTrip;
	}
	
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
	public String getRDDate() {
		return RDDate;
	}
	public void setRDDate(String rDDate) {
		RDDate = rDDate;
	}
	public String getMin() {
		return min;
	}
	public void setMin(String min) {
		this.min = min;
	}
	public String getTimezone() {
		return timezone;
	}
	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}
	public String getOrderColor() {
		return orderColor;
	}
	public void setOrderColor(String orderColor) {
		this.orderColor = orderColor;
	}
	public String getAccountName() {
		return accountName;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	public String getDLDate() {
		return DLDate;
	}
	public void setDLDate(String dLDate) {
		DLDate = dLDate;
	}
	public String getPUDate() {
		return PUDate;
	}
	public void setPUDate(String pUDate) {
		PUDate = pUDate;
	}
	public String getHour() {
		return hour;
	}
	public void setHour(String hour) {
		this.hour = hour;
	}
	
	public String getOrderStatus() {
		return orderStatus;
	}
	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getSuit() {
		return suit;
	}
	public void setSuit(String suit) {
		this.suit = suit;
	}
	public String getZip() {
		return zip;
	}
	public void setZip(String zip) {
		this.zip = zip;
	}
	public String getDlcompany() {
		return dlcompany;
	}
	public void setDlcompany(String dlcompany) {
		this.dlcompany = dlcompany;
	}
	public String getDladdress() {
		return dladdress;
	}
	public void setDladdress(String dladdress) {
		this.dladdress = dladdress;
	}
	public String getDlsuit() {
		return dlsuit;
	}
	public void setDlsuit(String dlsuit) {
		this.dlsuit = dlsuit;
	}
	public String getDlcity() {
		return dlcity;
	}
	public void setDlcity(String dlcity) {
		this.dlcity = dlcity;
	}
	public String getDlstate() {
		return dlstate;
	}
	public void setDlstate(String dlstate) {
		this.dlstate = dlstate;
	}
	public String getDlzip() {
		return dlzip;
	}
	public void setDlzip(String dlzip) {
		this.dlzip = dlzip;
	}
	public double getLatitude() {
		return Latitude;
	}
	public void setLatitude(double latitude) {
		Latitude = latitude;
	}
	public double getLongitude() {
		return Longitude;
	}
	public void setLongitude(double longitude) {
		Longitude = longitude;
	}
	public double getDlLatitude() {
		return dlLatitude;
	}
	public void setDlLatitude(double dlLatitude) {
		this.dlLatitude = dlLatitude;
	}
	public double getDlLongitude() {
		return dlLongitude;
	}
	public void setDlLongitude(double dlLongitude) {
		this.dlLongitude = dlLongitude;
	}
	
	public void setSelectedItem(Boolean selectedItem) {
		this.selectedItem = selectedItem;
	}
	public Boolean getSelectedItem() {
		return selectedItem;
	}
}