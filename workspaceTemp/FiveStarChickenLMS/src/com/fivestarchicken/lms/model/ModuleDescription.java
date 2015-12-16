package com.fivestarchicken.lms.model;

public class ModuleDescription {
	
	String moduleId,type,detail,order,languageType;
	
	public ModuleDescription(){
		
		
	}
	public ModuleDescription(String type,String detail){
		
		this.type=type;
		this.detail=detail;
		
		
	}

	public String getModuleId() {
		return moduleId;
	}

	public void setModuleId(String moduleId) {
		this.moduleId = moduleId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}
	public String getLanguageType() {
		return languageType;
	}
	public void setLanguageType(String languageType) {
		this.languageType = languageType;
	}
	
	
	

}
