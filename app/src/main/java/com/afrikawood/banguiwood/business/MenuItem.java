package com.afrikawood.banguiwood.business;

public class MenuItem {

	public String tag;
	public boolean isRootSection;
	public int parentSectionIndex;
	public int sectionIndex;

	public MenuItem(String tag, boolean isRootSection, int parentSectionIndex, int sectionIndex) {
		this.tag = tag;
		this.isRootSection = isRootSection;
		this.parentSectionIndex = parentSectionIndex;
		this.sectionIndex = sectionIndex;
	}

}
