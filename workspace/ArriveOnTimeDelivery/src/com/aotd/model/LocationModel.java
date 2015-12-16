package com.aotd.model;

import java.io.Serializable;
import java.util.ArrayList;

public class LocationModel implements Serializable{
/*	<GeocodeResponse>
	<status>OK</status>
	<result>
		<type>route</type>
		<formatted_address>
			Koti Main Rd, Koti, Hyderabad, Andhra Pradesh, India
		</formatted_address>
		<address_component>
			<long_name>Koti Main Rd</long_name>
			<short_name>Koti Main Rd</short_name>
			<type>route</type>
		</address_component>
		<address_component>
			<long_name>Koti</long_name>
			<short_name>Koti</short_name>
			<type>sublocality</type>
			<type>political</type>
		</address_component>
		<address_component>
			<long_name>Hyderabad</long_name>
			<short_name>Hyderabad</short_name>
			<type>locality</type>
			<type>political</type>
		</address_component>
		<address_component>
			<long_name>Hyderabad</long_name>
			<short_name>Hyderabad</short_name>
			<type>administrative_area_level_2</type>
			<type>political</type>
		</address_component>
		<address_component>
			<long_name>Andhra Pradesh</long_name>
			<short_name>Andhra Pradesh</short_name>
			<type>administrative_area_level_1</type>
			<type>political</type>
		</address_component>
		<address_component>
			<long_name>India</long_name>
			<short_name>IN</short_name>
			<type>country</type>
			<type>political</type>
		</address_component>
		
	</result>*/
	private String status;
	public ArrayList<LocationResultModel> result;

	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
}
