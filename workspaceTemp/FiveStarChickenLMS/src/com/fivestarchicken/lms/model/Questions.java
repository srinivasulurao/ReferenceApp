package com.fivestarchicken.lms.model;

import java.util.ArrayList;

public class Questions {
	
	String questionId,questionText,answerCount,moduleId,languageType;
	
	ArrayList<Answer> answareList=new ArrayList<Answer>();

	public String getQuestionId() {
		return questionId;
	}

	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}

	public String getQuestionText() {
		return questionText;
	}

	public void setQuestionText(String questionText) {
		this.questionText = questionText;
	}

	public String getAnswerCount() {
		return answerCount;
	}
	
	public String getModuleId() {
		return moduleId;
	}

	public void setModuleId(String moduleId) {
		this.moduleId = moduleId;
	}

	public void setAnswerCount(String answerCount) {
		this.answerCount = answerCount;
	}

	public ArrayList<Answer> getAnswareList() {
		return answareList;
	}

	public void setAnswareList(ArrayList<Answer> answareList) {
		this.answareList = answareList;
	}
	
	public String getLanguageType() {
		return languageType;
	}

	public void setLanguageType(String languageType) {
		this.languageType = languageType;
	}
	
	
	

}
