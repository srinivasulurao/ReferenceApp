package com.fivestarchicken.lms.model;

import java.util.ArrayList;

public class ExamModule {
	
	String categoryId,moduleName,totalQuestions,passingScore,duration,moduleId,level,description,starLevel,languageType;
	
	ArrayList<Questions> questionsList=new ArrayList<Questions>();
	
	ArrayList<ModuleDescription> descriptionList=new ArrayList<ModuleDescription>();

	

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public String getTotalQuestions() {
		return totalQuestions;
	}

	public void setTotalQuestions(String totalQuestions) {
		this.totalQuestions = totalQuestions;
	}

	public String getPassingScore() {
		return passingScore;
	}

	public void setPassingScore(String passingScore) {
		this.passingScore = passingScore;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getModuleId() {
		return moduleId;
	}

	public void setModuleId(String moduleId) {
		this.moduleId = moduleId;
	}

	public ArrayList<Questions> getQuestionsList() {
		return questionsList;
	}

	public void setQuestionsList(ArrayList<Questions> questionsList) {
		this.questionsList = questionsList;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ArrayList<ModuleDescription> getDescriptionList() {
		return descriptionList;
	}

	public void setDescriptionList(ArrayList<ModuleDescription> descriptionList) {
		this.descriptionList = descriptionList;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public String getStarLevel() {
		return starLevel;
	}

	public void setStarLevel(String starLevel) {
		this.starLevel = starLevel;
	}

	public String getLanguageType() {
		return languageType;
	}

	public void setLanguageType(String languageType) {
		this.languageType = languageType;
	}
	
	
	
	

}
