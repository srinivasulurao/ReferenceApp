package com.fivestarchicken.lms.model;

import java.util.ArrayList;
import java.util.List;

public class Blog {
	
	String id ,blogId,blogTitle,syncDate,languageType;
	List<BlogDetail> blogDetailList = new ArrayList<BlogDetail>();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBlogId() {
		return blogId;
	}

	public void setBlogId(String blogId) {
		this.blogId = blogId;
	}

	public String getBlogTitle() {
		return blogTitle;
	}

	public void setBlogTitle(String blogTitle) {
		this.blogTitle = blogTitle;
	}

	public List<BlogDetail> getBlogDetailList() {
		return blogDetailList;
	}

	public void setBlogDetailList(List<BlogDetail> blogDetailList) {
		this.blogDetailList = blogDetailList;
	}

	public String getSyncDate() {
		return syncDate;
	}

	public void setSyncDate(String syncDate) {
		this.syncDate = syncDate;
	}

	public String getLanguageType() {
		return languageType;
	}

	public void setLanguageType(String languageType) {
		this.languageType = languageType;
	}
	
		

}
