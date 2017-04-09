package com.afrikawood.banguiwood.business;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.afrikawood.banguiwood.R;
import com.afrikawood.banguiwood.utils.DateUtilities;

public class Video {
	
	public static final String JSON_KEY_SNIPPET = "snippet";
	public static final String JSON_KEY_TITLE = "title";
	public static final String JSON_KEY_RESOURCE_ID = "resourceId";
	public static final String JSON_KEY_YOUTUBE_VIDEO_IDENTIFIER = "videoId";
	public static final String JSON_KEY_PUBLICATION_DATE = "publishedAt";
	public static final String JSON_KEY_DESCRIPTION = "description";
	public static final String JSON_KEY_THUMBNAILS = "thumbnails";
	public static final String JSON_KEY_DEFAULT_THUMBNAIL = "default";
	public static final String JSON_KEY_MEDIUM_THUMBNAIL = "medium";
	public static final String JSON_KEY_HIGH_THUMBNAIL = "high";
	public static final String JSON_KEY_THUMBNAIL_URL = "url";

	private String title;
	private String youtubeVideoIdentifier;
	private Date publicationDate;
	private String description;
	private String defaultThumbnailURL;
	private String mediumThumbnailURL;
	private String highThumbnailURL;
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getYoutubeVideoIdentifier() {
		return youtubeVideoIdentifier;
	}

	public void setYoutubeVideoIdentifier(String youtubeVideoIdentifier) {
		this.youtubeVideoIdentifier = youtubeVideoIdentifier;
	}

	public Date getPublicationDate() {
		return publicationDate;
	}

	public void setPublicationDate(Date publicationDate) {
		this.publicationDate = publicationDate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDefaultThumbnailURL() {
		return defaultThumbnailURL;
	}

	public void setDefaultThumbnailURL(String defaultThumbnailURL) {
		this.defaultThumbnailURL = defaultThumbnailURL;
	}

	public String getMediumThumbnailURL() {
		return mediumThumbnailURL;
	}

	public void setMediumThumbnailURL(String mediumThumbnailURL) {
		this.mediumThumbnailURL = mediumThumbnailURL;
	}

	public String getHighThumbnailURL() {
		return highThumbnailURL;
	}

	public void setHighThumbnailURL(String highThumbnailURL) {
		this.highThumbnailURL = highThumbnailURL;
	}

	public Video() {
		super();
		this.title = new String();
		this.youtubeVideoIdentifier = new String();
		this.publicationDate = new Date();
		this.description = new String();
		this.defaultThumbnailURL = new String();
		this.mediumThumbnailURL = new String();
		this.highThumbnailURL = new String();
	}

	public Video(String title, String youtubeVideoIdentifier, Date publicationDate, String description, String defaultThumbnailURL, String mediumThumbnailURL, String highThumbnailURL) {
		super();
		this.title = title;
		this.youtubeVideoIdentifier = youtubeVideoIdentifier;
		this.publicationDate = publicationDate;
		this.description = description;
		this.defaultThumbnailURL = defaultThumbnailURL;
		this.mediumThumbnailURL = mediumThumbnailURL;
		this.highThumbnailURL = highThumbnailURL;
	}
	
	public static Video getVideoDataFromJSONObject(Context context, JSONObject JSONObject) {
		
		Video video = new Video();
		// https://www.googleapis.com/youtube/v3/playlistItems?playlistId=PLzbRu3YJn3wI5o0OnjZS6Kx3vDbodnafp&key=AIzaSyBrojvIsA1RpplsA_UeWvzBnpWkaFZ8wC0&part=id,snippet,contentDetails,status
	    
	    try {
	    	
	    	if (JSONObject.has(JSON_KEY_SNIPPET)) {
	    		
	    		JSONObject snippetDictionary = JSONObject.getJSONObject(JSON_KEY_SNIPPET);
	    		
	    		if (snippetDictionary.has(JSON_KEY_TITLE)) {
	    			video.setTitle(snippetDictionary.getString(JSON_KEY_TITLE));
	    		}
	    		
	    		if (snippetDictionary.has(JSON_KEY_RESOURCE_ID)) {
	    			JSONObject resourceIdDictionary = snippetDictionary.getJSONObject(JSON_KEY_RESOURCE_ID);
	    			
	    			if (resourceIdDictionary.has(JSON_KEY_YOUTUBE_VIDEO_IDENTIFIER)) {
	    				video.setYoutubeVideoIdentifier(resourceIdDictionary.getString(JSON_KEY_YOUTUBE_VIDEO_IDENTIFIER));
	    			}
	    		}
	    		
	    		if (snippetDictionary.has(JSON_KEY_PUBLICATION_DATE)) {
	    			String publicationDateString = snippetDictionary.getString(JSON_KEY_PUBLICATION_DATE);
	    			Date publicationDate = DateUtilities.convertStringToDate(publicationDateString, context.getResources().getString(R.string.youtubePublishedAtDateFormat));
	    			video.setPublicationDate(publicationDate);
	    		}
	    		
	    		if (snippetDictionary.has(JSON_KEY_DESCRIPTION)) {
	    			video.setDescription(snippetDictionary.getString(JSON_KEY_DESCRIPTION));
	    		}
	    		
	    		if (snippetDictionary.has(JSON_KEY_THUMBNAILS)) {
	    			JSONObject thumbnailsDictionary = snippetDictionary.getJSONObject(JSON_KEY_THUMBNAILS);
	    			
	    			if (thumbnailsDictionary.has(JSON_KEY_DEFAULT_THUMBNAIL)) {
	    				if (thumbnailsDictionary.getJSONObject(JSON_KEY_DEFAULT_THUMBNAIL).has(JSON_KEY_THUMBNAIL_URL)) {
	    					video.setDefaultThumbnailURL(thumbnailsDictionary.getJSONObject(JSON_KEY_DEFAULT_THUMBNAIL).getString(JSON_KEY_THUMBNAIL_URL));
	    				}
	    			}
	    			
	    			if (thumbnailsDictionary.has(JSON_KEY_MEDIUM_THUMBNAIL)) {
	    				if (thumbnailsDictionary.getJSONObject(JSON_KEY_MEDIUM_THUMBNAIL).has(JSON_KEY_THUMBNAIL_URL)) {
	    					video.setMediumThumbnailURL(thumbnailsDictionary.getJSONObject(JSON_KEY_MEDIUM_THUMBNAIL).getString(JSON_KEY_THUMBNAIL_URL));
	    				}
	    			}
	    			
	    			if (thumbnailsDictionary.has(JSON_KEY_HIGH_THUMBNAIL)) {
	    				if (thumbnailsDictionary.getJSONObject(JSON_KEY_HIGH_THUMBNAIL).has(JSON_KEY_THUMBNAIL_URL)) {
	    					video.setHighThumbnailURL(thumbnailsDictionary.getJSONObject(JSON_KEY_HIGH_THUMBNAIL).getString(JSON_KEY_THUMBNAIL_URL));
	    				}
	    			}
	    		}
	    		
	    	}
	    	
	    } catch (JSONException e) {
	    	e.printStackTrace();
	    }
	    
		return video;
	}
	
	public static Video getVideoDataFromVideoDetailServiceJSONObject(Context context, JSONObject JSONObject) {
		
		Video video = new Video();
		// https://www.googleapis.com/youtube/v3/playlistItems?playlistId=PLzbRu3YJn3wI5o0OnjZS6Kx3vDbodnafp&key=AIzaSyBrojvIsA1RpplsA_UeWvzBnpWkaFZ8wC0&part=id,snippet,contentDetails,status
	    
		try {
			
			if (JSONObject.has("id")) {
    			video.setYoutubeVideoIdentifier(JSONObject.getString("id"));
    		}
	    	
	    	if (JSONObject.has(JSON_KEY_SNIPPET)) {
	    		
	    		JSONObject snippetDictionary = JSONObject.getJSONObject(JSON_KEY_SNIPPET);
	    		
	    		if (snippetDictionary.has(JSON_KEY_TITLE)) {
	    			video.setTitle(snippetDictionary.getString(JSON_KEY_TITLE));
	    		}
	    		
	    		if (snippetDictionary.has(JSON_KEY_PUBLICATION_DATE)) {
	    			String publicationDateString = snippetDictionary.getString(JSON_KEY_PUBLICATION_DATE);
	    			Date publicationDate = DateUtilities.convertStringToDate(publicationDateString, context.getResources().getString(R.string.youtubePublishedAtDateFormat));
	    			video.setPublicationDate(publicationDate);
	    		}
	    		
	    		if (snippetDictionary.has(JSON_KEY_DESCRIPTION)) {
	    			video.setDescription(snippetDictionary.getString(JSON_KEY_DESCRIPTION));
	    		}
	    		
	    		if (snippetDictionary.has(JSON_KEY_THUMBNAILS)) {
	    			JSONObject thumbnailsDictionary = snippetDictionary.getJSONObject(JSON_KEY_THUMBNAILS);
	    			
	    			if (thumbnailsDictionary.has(JSON_KEY_DEFAULT_THUMBNAIL)) {
	    				if (thumbnailsDictionary.getJSONObject(JSON_KEY_DEFAULT_THUMBNAIL).has(JSON_KEY_THUMBNAIL_URL)) {
	    					video.setDefaultThumbnailURL(thumbnailsDictionary.getJSONObject(JSON_KEY_DEFAULT_THUMBNAIL).getString(JSON_KEY_THUMBNAIL_URL));
	    				}
	    			}
	    			
	    			if (thumbnailsDictionary.has(JSON_KEY_MEDIUM_THUMBNAIL)) {
	    				if (thumbnailsDictionary.getJSONObject(JSON_KEY_MEDIUM_THUMBNAIL).has(JSON_KEY_THUMBNAIL_URL)) {
	    					video.setMediumThumbnailURL(thumbnailsDictionary.getJSONObject(JSON_KEY_MEDIUM_THUMBNAIL).getString(JSON_KEY_THUMBNAIL_URL));
	    				}
	    			}
	    			
	    			if (thumbnailsDictionary.has(JSON_KEY_HIGH_THUMBNAIL)) {
	    				if (thumbnailsDictionary.getJSONObject(JSON_KEY_HIGH_THUMBNAIL).has(JSON_KEY_THUMBNAIL_URL)) {
	    					video.setHighThumbnailURL(thumbnailsDictionary.getJSONObject(JSON_KEY_HIGH_THUMBNAIL).getString(JSON_KEY_THUMBNAIL_URL));
	    				}
	    			}
	    		}
	    		
	    	}
	    	
	    } catch (JSONException e) {
	    	e.printStackTrace();
	    }

		return video;
	}

}
