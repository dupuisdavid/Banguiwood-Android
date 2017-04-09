package com.afrikawood.banguiwood.business;

public class MenuItem {

	private String tag;
	private int iconRes;
	private Boolean isRootSection;
	private int parentSectionIndex;
	private int sectionIndex;

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}
	
	public int getIconRes() {
		return iconRes;
	}

	public void setIconRes(int iconRes) {
		this.iconRes = iconRes;
	}

	public Boolean getIsRootSection() {
		return isRootSection;
	}

	public void setIsRootSection(Boolean isRootSection) {
		this.isRootSection = isRootSection;
	}
	
	public int getParentSectionIndex() {
		return parentSectionIndex;
	}
	
	public void setParentSectionIndex(int parentSectionIndex) {
		this.parentSectionIndex = parentSectionIndex;
	}
	
	public int getSectionIndex() {
		return sectionIndex;
	}

	public void setSectionIndex(int sectionIndex) {
		this.sectionIndex = sectionIndex;
	}

	public MenuItem(String tag, int iconRes, Boolean isRootSection, int parentSectionIndex, int sectionIndex) {
		this.tag = tag; 
		this.iconRes = iconRes;
		this.isRootSection = isRootSection;
		this.parentSectionIndex = parentSectionIndex;
		this.sectionIndex = sectionIndex;
	}

}
