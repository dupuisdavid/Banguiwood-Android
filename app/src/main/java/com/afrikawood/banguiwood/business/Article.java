package com.afrikawood.banguiwood.business;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class Article implements Parcelable {

	private static final String JSON_KEY_TITLE = "title";
	private static final String JSON_KEY_THUMBNAIL_URL = "thumbnailURL";
	private static final String JSON_KEY_URL = "URL";

	public String title;
	public String thumbnailUrl;
	public String url;

	private Article() {
		super();
	}

	public static Article getArticleDataFromJSONObject(JSONObject JSONObject) {

		Article item = new Article();

		try {

			if (JSONObject.has(JSON_KEY_TITLE)) {
				item.title = JSONObject.getString(JSON_KEY_TITLE);
			}

			if (JSONObject.has(JSON_KEY_THUMBNAIL_URL)) {
				item.thumbnailUrl = JSONObject.getString(JSON_KEY_THUMBNAIL_URL);
			}

			if (JSONObject.has(JSON_KEY_URL)) {
				item.url = JSONObject.getString(JSON_KEY_URL);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return item;
	}

	private Article(Parcel in) {
		title = in.readString();
		thumbnailUrl = in.readString();
		url = in.readString();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(title);
		dest.writeString(thumbnailUrl);
		dest.writeString(url);
	}

	@SuppressWarnings("unused")
	public static final Parcelable.Creator<Article> CREATOR = new Parcelable.Creator<Article>() {
		@Override
		public Article createFromParcel(Parcel in) {
			return new Article(in);
		}

		@Override
		public Article[] newArray(int size) {
			return new Article[size];
		}
	};
}