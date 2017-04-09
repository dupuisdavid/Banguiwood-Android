package com.afrikawood.banguiwood.business;

import java.util.ArrayList;

public class Section {
	
	public enum SectionType {
		SectionTypeRoot,
		SectionTypePlayList,
		SectionTypeArticles
	}
	
	private String identifier;
	private String name;
	private SectionType sectionType;
	private String websiteCategoryRootURL;
	private Boolean isRootSection;
	private ArrayList<Section> sections;
	
	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public SectionType getSectionType() {
		return sectionType;
	}
	
	public void setSectionType(SectionType sectionType) {
		this.sectionType = sectionType;
	}

	public String getWebsiteCategoryRootURL() {
		return websiteCategoryRootURL;
	}

	public void setWebsiteCategoryRootURL(String websiteCategoryRootURL) {
		this.websiteCategoryRootURL = websiteCategoryRootURL;
	}

	public Boolean getIsRootSection() {
		return isRootSection;
	}

	public void setIsRootSection(Boolean isRootSection) {
		this.isRootSection = isRootSection;
	}

	public ArrayList<Section> getSections() {
		return sections;
	}

	public void setSections(ArrayList<Section> sections) {
		this.sections = sections;
	}

	public Section() {
		super();
		this.identifier = new String();
		this.name = new String();
		this.sectionType = SectionType.SectionTypeRoot;
		this.websiteCategoryRootURL = new String();
		this.isRootSection = false;
		this.sections = new ArrayList<Section>();
	}

	public Section(String identifier, String name, SectionType sectionType, String websiteCategoryRootURL, Boolean isRootSection, ArrayList<Section> sections) {
		super();
		this.identifier = identifier;
		this.name = name;
		this.sectionType = sectionType;
		this.websiteCategoryRootURL = websiteCategoryRootURL;
		this.isRootSection = isRootSection;
		this.sections = sections;
	}

}
