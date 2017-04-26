package com.afrikawood.banguiwood.business;

import android.content.Context;

import com.afrikawood.banguiwood.R;
import com.afrikawood.banguiwood.utils.DateUtilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class Video {
	
	private static final String JSON_KEY_SNIPPET = "snippet";
	private static final String JSON_KEY_TITLE = "title";
	private static final String JSON_KEY_RESOURCE_ID = "resourceId";
	private static final String JSON_KEY_YOUTUBE_VIDEO_IDENTIFIER = "videoId";
	private static final String JSON_KEY_PUBLICATION_DATE = "publishedAt";
	private static final String JSON_KEY_THUMBNAILS = "thumbnails";
	private static final String JSON_KEY_HIGH_THUMBNAIL = "high";
	private static final String JSON_KEY_THUMBNAIL_URL = "url";

	public String title;
	public String youtubeVideoIdentifier;
	public Date publicationDate;
	public String highThumbnailURL;

	public Video() {
		super();
		this.title = "";
		this.youtubeVideoIdentifier = "";
		this.publicationDate = new Date();
		this.highThumbnailURL = "";
	}
	
	public static Video getVideoDataFromJSONObject(Context context, JSONObject JSONObject) {
		
		Video video = new Video();
		// https://www.googleapis.com/youtube/v3/playlistItems?playlistId=PLzbRu3YJn3wI5o0OnjZS6Kx3vDbodnafp&key=AIzaSyBrojvIsA1RpplsA_UeWvzBnpWkaFZ8wC0&part=id,snippet,contentDetails,status
	    
	    try {
	    	
	    	if (JSONObject.has(JSON_KEY_SNIPPET)) {
	    		
	    		JSONObject snippetDictionary = JSONObject.getJSONObject(JSON_KEY_SNIPPET);
	    		
	    		if (snippetDictionary.has(JSON_KEY_TITLE)) {
	    			video.title = snippetDictionary.getString(JSON_KEY_TITLE);
	    		}
	    		
	    		if (snippetDictionary.has(JSON_KEY_RESOURCE_ID)) {
	    			JSONObject resourceIdDictionary = snippetDictionary.getJSONObject(JSON_KEY_RESOURCE_ID);
	    			
	    			if (resourceIdDictionary.has(JSON_KEY_YOUTUBE_VIDEO_IDENTIFIER)) {
	    				video.youtubeVideoIdentifier = resourceIdDictionary.getString(JSON_KEY_YOUTUBE_VIDEO_IDENTIFIER);
	    			}
	    		}
	    		
	    		if (snippetDictionary.has(JSON_KEY_PUBLICATION_DATE)) {
	    			String publicationDateString = snippetDictionary.getString(JSON_KEY_PUBLICATION_DATE);
					video.publicationDate = DateUtilities.convertStringToDate(publicationDateString, context.getResources().getString(R.string.youtubePublishedAtDateFormat));
	    		}
	    		
	    		if (snippetDictionary.has(JSON_KEY_THUMBNAILS)) {
	    			JSONObject thumbnailsDictionary = snippetDictionary.getJSONObject(JSON_KEY_THUMBNAILS);
	    			
	    			if (thumbnailsDictionary.has(JSON_KEY_HIGH_THUMBNAIL)) {
	    				if (thumbnailsDictionary.getJSONObject(JSON_KEY_HIGH_THUMBNAIL).has(JSON_KEY_THUMBNAIL_URL)) {
	    					video.highThumbnailURL = thumbnailsDictionary.getJSONObject(JSON_KEY_HIGH_THUMBNAIL).getString(JSON_KEY_THUMBNAIL_URL);
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
