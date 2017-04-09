package com.afrikawood.banguiwood.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.text.Html;

public class StringUtilities {
	
	public static String convertStreamToString(InputStream is) throws Exception {
	    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	    StringBuilder sb = new StringBuilder();
	    String line = null;

	    while ((line = reader.readLine()) != null) {
	        sb.append(line);
	    }

	    is.close();

	    return sb.toString();
	}
	
	// http://stackoverflow.com/questions/4050087/how-to-obtain-the-last-path-segment-of-an-uri
	public static String getURLLastPathComponent(String URL) {
		return URL.replaceFirst(".*/([^/?]+).*", "$1");
	}
	
	public static boolean isNumeric(String s) {
      try {
         Double.parseDouble(s);
         return true;
      } catch (Exception e) {
         return false;
      }
    }
	
	@SuppressLint("DefaultLocale")
	public static String capitalizeString(String string) {
		
		char[] chars = string.toLowerCase(Locale.getDefault()).toCharArray();
		boolean found = false;
		
		for (int i = 0; i < chars.length; i++) {
			if (!found && Character.isLetter(chars[i])) {
				chars[i] = Character.toUpperCase(chars[i]);
				found = true;
			} else if (Character.isWhitespace(chars[i]) || chars[i] == '.' || chars[i] == '\'' || chars[i] == '-') { // You can add other chars here
				found = false;
			}
		}
		
		return String.valueOf(chars);
	}
	/*
	public static ArrayList<String> extractLiContentFromHtml(String htmlContent) {
		
		ArrayList<String> contentString = new ArrayList<String>();
		
		String[] htmlContentTagBeginArray = htmlContent.split("<li>");
		
		for (int index_htmlContentTagBeginArray=0; index_htmlContentTagBeginArray<htmlContentTagBeginArray.length; index_htmlContentTagBeginArray++) {
			String _currentString = htmlContentTagBeginArray[index_htmlContentTagBeginArray];
			if (_currentString.contains("</li>")) {
				contentString.add(_currentString.substring(0, _currentString.indexOf("</li>")));
			}
		}
		
		return contentString;
	}
	*/
	
	public static String formatContentFromHtml(String htmlContent) {
		
		String contentString = htmlContent;

		contentString = contentString.replace("</li><li>", "<br>��� ");
		contentString = contentString.replace("<li>", "<br>��� ");
		contentString = contentString.replace("</li>", "<br>");
		
		return Html.fromHtml(contentString).toString();
	}
	
	public static String extractLiContentFromHtml(String htmlContent) {
		
		String contentString = "";
		
		String[] htmlContentTagBeginArray = htmlContent.split("<li>");
		
		for (int index_htmlContentTagBeginArray=0; index_htmlContentTagBeginArray<htmlContentTagBeginArray.length; index_htmlContentTagBeginArray++) {
			String _currentString = htmlContentTagBeginArray[index_htmlContentTagBeginArray];
			if (_currentString.contains("</li>")) {
				if (!contentString.equals("")) {
					contentString = contentString + "\n";
				}
				contentString = contentString + "��� " + _currentString.substring(0, _currentString.indexOf("</li>"));
			}
		}
		
		return contentString;
	}
	
	public static String prepareTrackString(String trackString) {
		return prepareTrackString(trackString, null);
	}
	
	public static String prepareTrackString(String trackString, ArrayList<String> charactersListToReplaceWithEmptyString) {
		
		String sanitizedTrackString = trackString;
		sanitizedTrackString = StringUtilities.capitalizeString(sanitizedTrackString);
		
		// DEFAULT CHARACTER REPLACEMENT CASE
		sanitizedTrackString = sanitizedTrackString.replace(" ", "");
		
		// CUSTOM CHARACTER REPLACEMENT CASE
		if (charactersListToReplaceWithEmptyString != null && charactersListToReplaceWithEmptyString.size() > 0) {
			
			for (int i = 0; i < charactersListToReplaceWithEmptyString.size(); i++) {
				sanitizedTrackString = sanitizedTrackString.replace(charactersListToReplaceWithEmptyString.get(i), "");
			}
			
		}
		
		sanitizedTrackString = Normalizer.normalize(sanitizedTrackString, Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
		
		return sanitizedTrackString;
	}
	
	public static String purgeUnwantedSpaceInText(String text) {
		String joinText = "";
		String[] tokens = text.split(" ");
		
		for (int i = 0; i < tokens.length; i++) {
			String textPart = tokens[i];
			
			if (!textPart.equals("") && !textPart.equals(" ")) {
				joinText = joinText + (i != 0 ? " " : "") + textPart;
			}
		}
		
		return joinText;
	}
}
