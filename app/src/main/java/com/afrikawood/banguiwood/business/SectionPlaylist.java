package com.afrikawood.banguiwood.business;

import java.util.ArrayList;

public class SectionPlaylist extends Section {
	
	private String youtubePlaylistIdentifier;
	
	public String getYoutubePlaylistIdentifier() {
		return youtubePlaylistIdentifier;
	}

	public void setYoutubePlaylistIdentifier(String youtubePlaylistIdentifier) {
		this.youtubePlaylistIdentifier = youtubePlaylistIdentifier;
	}

	public SectionPlaylist() {
		super();
		this.youtubePlaylistIdentifier = new String();
	}

	public SectionPlaylist(String identifier, String name, SectionType sectionType, String websiteCategoryRootUrl, Boolean isRootSection, ArrayList<Section> sections, String youtubePlaylistIdentifier) {
		super(identifier, name, sectionType, websiteCategoryRootUrl, isRootSection, sections);
		this.youtubePlaylistIdentifier = youtubePlaylistIdentifier;
	}

}
