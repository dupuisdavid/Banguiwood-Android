package com.afrikawood.banguiwood.business;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Section implements Parcelable {

	public enum SectionType {
		SectionTypeRoot,
		SectionTypePlayList,
		SectionTypeArticles
	}

	public String identifier;
	public String name;
	public SectionType sectionType;
	public String websiteCategoryRootUrl;
	public Boolean isRootSection;
	public ArrayList<Section> sections;

	public Section() {
		super();
		this.sectionType = SectionType.SectionTypeRoot;
		this.isRootSection = false;
		this.sections = new ArrayList<>();
	}

	public Section(String identifier, String name, SectionType sectionType, String websiteCategoryRootUrl, Boolean isRootSection, ArrayList<Section> sections) {
		super();
		this.identifier = identifier;
		this.name = name;
		this.sectionType = sectionType;
		this.websiteCategoryRootUrl = websiteCategoryRootUrl;
		this.isRootSection = isRootSection;
		this.sections = sections;
	}


	protected Section(Parcel in) {
		identifier = in.readString();
		name = in.readString();
		sectionType = (SectionType) in.readValue(SectionType.class.getClassLoader());
		websiteCategoryRootUrl = in.readString();
		byte isRootSectionVal = in.readByte();
		isRootSection = isRootSectionVal == 0x02 ? null : isRootSectionVal != 0x00;
		if (in.readByte() == 0x01) {
			sections = new ArrayList<>();
			in.readList(sections, Section.class.getClassLoader());
		} else {
			sections = null;
		}
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(identifier);
		dest.writeString(name);
		dest.writeValue(sectionType);
		dest.writeString(websiteCategoryRootUrl);
		if (isRootSection == null) {
			dest.writeByte((byte) (0x02));
		} else {
			dest.writeByte((byte) (isRootSection ? 0x01 : 0x00));
		}
		if (sections == null) {
			dest.writeByte((byte) (0x00));
		} else {
			dest.writeByte((byte) (0x01));
			dest.writeList(sections);
		}
	}

	@SuppressWarnings("unused")
	public static final Parcelable.Creator<Section> CREATOR = new Parcelable.Creator<Section>() {
		@Override
		public Section createFromParcel(Parcel in) {
			return new Section(in);
		}

		@Override
		public Section[] newArray(int size) {
			return new Section[size];
		}
	};
}