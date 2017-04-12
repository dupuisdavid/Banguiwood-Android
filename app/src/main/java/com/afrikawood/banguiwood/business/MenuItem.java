package com.afrikawood.banguiwood.business;

public class MenuItem {

	public String tag;
	public int iconRes;
	public Boolean isRootSection;
	public int parentSectionIndex;
	public int sectionIndex;

	public MenuItem(String tag, int iconRes, Boolean isRootSection, int parentSectionIndex, int sectionIndex) {
		this.tag = tag; 
		this.iconRes = iconRes;
		this.isRootSection = isRootSection;
		this.parentSectionIndex = parentSectionIndex;
		this.sectionIndex = sectionIndex;
	}

}
