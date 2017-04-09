package com.afrikawood.banguiwood.business;

import java.util.ArrayList;

public class SectionArticles extends Section {
	
	private String url;
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public SectionArticles() {
		super();
		this.url = new String();
	}

	public SectionArticles(String identifier, String name, SectionType sectionType, String websiteCategoryRootUrl, Boolean isRootSection, ArrayList<Section> sections, String url) {
		super(identifier, name, sectionType, websiteCategoryRootUrl, isRootSection, sections);
		this.url = url;
	}

}
