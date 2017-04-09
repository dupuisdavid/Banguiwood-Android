package com.afrikawood.banguiwood.business;

import org.json.JSONException;
import org.json.JSONObject;

public class Article {
	
	public static final String JSON_KEY_TITLE = "title";
	public static final String JSON_KEY_THUMBNAIL_URL = "thumbnailURL";
	public static final String JSON_KEY_URL = "URL";
	
	private String title;
	private String thumbnailURL;
	private String URL;
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getThumbnailURL() {
		return thumbnailURL;
	}

	private void setThumbnailURL(String thumbnailURL) {
		this.thumbnailURL = thumbnailURL;
	}

	public String getURL() {
		return URL;
	}

	private void setURL(String URL) {
		this.URL = URL;
	}

	public Article() {
		super();
		this.title = "";
		this.thumbnailURL = "";
		this.URL = "";
	}

	public Article(String title, String thumbnailURL, String URL) {
		super();
		this.title = title;
		this.thumbnailURL = thumbnailURL;
		this.URL = URL;
	}

	public static Article getArticleDataFromJSONObject(JSONObject JSONObject) {
	    
	    Article article = new Article();
	    
	    try {
	    	
	    	if (JSONObject.has(JSON_KEY_TITLE)) {
		    	article.setTitle(JSONObject.getString(JSON_KEY_TITLE));
		    }
		    
		    if (JSONObject.has(JSON_KEY_THUMBNAIL_URL)) {
		    	article.setThumbnailURL(JSONObject.getString(JSON_KEY_THUMBNAIL_URL));
		    }
		    
		    if (JSONObject.has(JSON_KEY_URL)) {
		    	article.setURL(JSONObject.getString(JSON_KEY_URL));
		    }
	    	
	    } catch (JSONException e) {
	    	e.printStackTrace();
	    }
	    
		return article;
	}
}
