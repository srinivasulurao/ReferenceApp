package com.fivestarchicken.lms.model;

import java.util.ArrayList;
import java.util.List;

public class Exam {
	
	String categoryName,title,moduleCount,examId,level;
	
	List<ExamModule> examModuleList = new ArrayList<ExamModule>();

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getModuleCount() {
		return moduleCount;
	}

	public void setModuleCount(String moduleCount) {
		this.moduleCount = moduleCount;
	}

	public List<ExamModule> getExamModuleList() {
		return examModuleList;
	}

	public void setExamModuleList(List<ExamModule> examModuleList) {
		this.examModuleList = examModuleList;
	}

	public String getExamId() {
		return examId;
	}

	public void setExamId(String examId) {
		this.examId = examId;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}
	
	
	
	

}
