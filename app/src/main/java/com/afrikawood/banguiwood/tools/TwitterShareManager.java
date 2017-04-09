package com.afrikawood.banguiwood.tools;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.afrikawood.banguiwood.R;

public class TwitterShareManager {

	private Context context;
	
	// TWITTER
    // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
	// Register your here app https://dev.twitter.com/apps/new and get your consumer key and secret
    // https://apps.twitter.com/app/7799560/keys

    private String TWITTER_CONSUMER_KEY;
	private String TWITTER_CONSUMER_SECRET;
	@SuppressWarnings("unused")
	private String TWITTER_OAUTH_ACCESS_TOKEN;
	@SuppressWarnings("unused")
	private String TWITTER_OAUTH_ACCESS_TOKEN_SECRET;
	
	// Preference Constants
	@SuppressWarnings("unused")
	private String PREFERENCE_NAME;
	private String PREF_KEY_OAUTH_TOKEN;
	private String PREF_KEY_OAUTH_SECRET;
	private String PREF_KEY_TWITTER_LOGIN;
	
	private String TWITTER_CALLBACK_URL;
	// Twitter oauth urls
	@SuppressWarnings("unused")
	private String URL_TWITTER_AUTH;
	private String URL_TWITTER_OAUTH_VERIFIER;
	@SuppressWarnings("unused")
	private String URL_TWITTER_OAUTH_TOKEN;
	
	// Twitter
	private static Twitter twitter;
	private static RequestToken requestToken;
	private ProgressDialog twitterProgressDialog;
	
	// Shared Preferences
	private static SharedPreferences mSharedPreferences;
	
	private String statusToShare = null;
	private Boolean tweetShareSuccess = false;
	private String callbackMethodName = "";
	
	public TwitterShareManager(Context context, String callbackMethodName) {
		this.context = context;
		this.callbackMethodName = callbackMethodName;
		
		if (this.context != null || !this.callbackMethodName.isEmpty()) {
			initTwitterConfig();
		}
	
	}
	
	private void initTwitterConfig() {
		
		Resources res = context.getResources();
		
		// TWITTER DATA CONFIG
		TWITTER_CONSUMER_KEY = res.getString(R.string.twitterConsumerKey);
		TWITTER_CONSUMER_SECRET = res.getString(R.string.twitterConsumerSecret);
		TWITTER_OAUTH_ACCESS_TOKEN = res.getString(R.string.twitterOauthAccessToken);
		TWITTER_OAUTH_ACCESS_TOKEN_SECRET = res.getString(R.string.twitterOauthAccessTokenSecret);
		
		// PREFERENCE CONSTANTS
		PREFERENCE_NAME = res.getString(R.string.twitterPreferenceName);
		PREF_KEY_OAUTH_TOKEN = res.getString(R.string.twitterPrefKeyOauthToken);
		PREF_KEY_OAUTH_SECRET = res.getString(R.string.twitterPrefKeyOauthSecret);
		PREF_KEY_TWITTER_LOGIN = res.getString(R.string.twitterPrefKeyTwitterLogin);
		
		TWITTER_CALLBACK_URL = res.getString(R.string.twitterCallbackBaseUrl) + callbackMethodName;
		// TWITTER OAUTH URLS
		URL_TWITTER_AUTH = res.getString(R.string.twitterUrlTwitterAuth);
		URL_TWITTER_OAUTH_VERIFIER = res.getString(R.string.twitterUrlTwitterOauthVerifier);
		URL_TWITTER_OAUTH_TOKEN = res.getString(R.string.twitterUrlTwitterOauthToken);

		// TWITTER PREFS
		mSharedPreferences = context.getApplicationContext().getSharedPreferences("MyPref", 0);
			
	}

	public void share(String statusToShare) {
		
		this.statusToShare = statusToShare;
    	
    	Boolean readyToTweet = true;
		
		if (!Network.networkIsAvailable(context)) {
			readyToTweet = false;
		}
		
		if (TWITTER_CONSUMER_KEY == null || TWITTER_CONSUMER_KEY.trim().length() == 0 || TWITTER_CONSUMER_SECRET == null || TWITTER_CONSUMER_SECRET.trim().length() == 0){
			readyToTweet = false;
		}
		
		// http://www.androidhive.info/2012/09/android-twitter-oauth-connect-tutorial/
		// http://stackoverflow.com/questions/17499935/android-twitter-integration-using-oauth-and-twitter4j
		// http://javatechig.com/android/how-to-integrate-twitter-in-android-application
		// https://github.com/itog/Twitter4j-android-Sample
		
		if (readyToTweet) {
			
			if (!isTwitterLoggedInAlready()) {
				Log.i("A1", "A1");
				login();
			} else {
				Log.i("A2", "A2");
				new updateTwitterStatus().execute("");
			}
			
		}
    }
	
	// Function to login twitter
	private void login() {
		// Check if already logged in
		if (!isTwitterLoggedInAlready()) {
			
			new Thread(new Runnable() { 
	            public void run(){        
	            	
	            	ConfigurationBuilder builder = new ConfigurationBuilder();
	    			builder.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
	    			builder.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);
	    			
	    			Configuration configuration = builder.build();
	    			
	    			TwitterFactory factory = new TwitterFactory(configuration);
	    			twitter = factory.getInstance();

	    			try {
	    				
	    				requestToken = twitter.getOAuthRequestToken(TWITTER_CALLBACK_URL);
	    				context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(requestToken.getAuthenticationURL())));
	    				
	    				
	    			} catch (TwitterException e) {
	    				e.printStackTrace();
	    			}
	            	
	            }
	            
	        }).start();
			
		}
	}
		
	
	// Check user already logged in your application using twitter Login flag is
	private boolean isTwitterLoggedInAlready() {
		// return twitter login status from Shared Preferences
		return mSharedPreferences.getBoolean(PREF_KEY_TWITTER_LOGIN, false);
	}
    
	// Function to update status
	class updateTwitterStatus extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			twitterProgressDialog = new ProgressDialog(context);
			twitterProgressDialog.setMessage("Publication sur twitter en cours...");
			twitterProgressDialog.setIndeterminate(false);
			twitterProgressDialog.setCancelable(true);
			twitterProgressDialog.show();
		}

		/**
		 * getting Places JSON
		 * */
		protected String doInBackground(String... args) {
			
			Log.d("Tweet Text", "> " + args[0]);
			String status = statusToShare;
			Log.d("Tweet status", "> " + status);
			
			try {
				
				ConfigurationBuilder builder = new ConfigurationBuilder();
				builder.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
				builder.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);
				
				// Access Token 
				String access_token = mSharedPreferences.getString(PREF_KEY_OAUTH_TOKEN, "");
				// Access Token Secret
				String access_token_secret = mSharedPreferences.getString(PREF_KEY_OAUTH_SECRET, "");
				
				AccessToken accessToken = new AccessToken(access_token, access_token_secret);
				Twitter twitter = new TwitterFactory(builder.build()).getInstance(accessToken);
				
				// Update status
				twitter4j.Status response = twitter.updateStatus(status);
				
				Log.d("Status", "> " + response.getText());
				tweetShareSuccess = true;
				
			} catch (TwitterException e) {
				// Error in updating status
				Log.d("Twitter Update Error", e.getMessage());
				tweetShareSuccess = false;
			}
			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog and show
		 * the data in UI Always use runOnUiThread(new Runnable()) to update UI
		 * from background thread, otherwise you will get error
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog after getting all products
			twitterProgressDialog.dismiss();
			// updating UI from Background Thread
			((Activity) context).runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(context.getApplicationContext(), tweetShareSuccess ? "Tweet publié avec succès !" : "Une erreur est survenue, publication impossible pour le moment", Toast.LENGTH_SHORT).show();
				}
			});
		}

	}
	
	// Function to logout from twitter
	// It will just clear the application shared preferences
	@SuppressWarnings("unused")
	private void logoutFromTwitter() {
		// Clear the shared preferences
		Editor e = mSharedPreferences.edit();
		e.remove(PREF_KEY_OAUTH_TOKEN);
		e.remove(PREF_KEY_OAUTH_SECRET);
		e.remove(PREF_KEY_TWITTER_LOGIN);
		e.commit();
		
		Toast.makeText(context.getApplicationContext(), "Déconnexion de Twitter effective !", Toast.LENGTH_SHORT).show();

	}
    
	public void manageTwitterCallBackUrl(Intent intent) {
    	
    	Uri uri = intent.getData();
    	
    	if (uri != null) {
    		
    		if (!isTwitterLoggedInAlready()) {
    			
    			if (uri != null && uri.toString().startsWith(TWITTER_CALLBACK_URL)) {
    				
    				Log.i("B1", "B1");
    				
    				// oAuth verifier
    				final String verifier = uri.getQueryParameter(URL_TWITTER_OAUTH_VERIFIER);

    				new Thread(new Runnable() { 
    		            public void run(){        
    		            	
    		            	try {
    							
    							// Get the access token
    							final AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, verifier);

    							// Shared Preferences
    							Editor e = mSharedPreferences.edit();

    							// After getting access token, access token secret
    							// store them in application preferences
    							e.putString(PREF_KEY_OAUTH_TOKEN, accessToken.getToken());
    							e.putString(PREF_KEY_OAUTH_SECRET, accessToken.getTokenSecret());
    							// Store login status - true
    							e.putBoolean(PREF_KEY_TWITTER_LOGIN, true);
    							e.commit(); // save changes

    							Log.e("Twitter OAuth Token", "" + accessToken.getToken());

    							// Getting user details from twitter
    							// For now i am getting his name only
    							long userID = accessToken.getUserId();
    							User user = twitter.showUser(userID);
    							@SuppressWarnings("unused")
    							final String username = user.getName();
    							
    							((Activity) context).runOnUiThread(new Runnable() {
    						        public void run() {
    						        	Log.i("B2", "B2");
    						        	new updateTwitterStatus().execute("");
    						        }
    						    });
    							
    							
    						} catch (Exception e) {
    							// Check log for login errors
    							Log.e("Twitter Login Error", "> " + e.getMessage());
    							e.printStackTrace();
    						}
    		            	
    		            }
    		            
    		        }).start();
    				
    			}
    			
    		} else {
    			
    			Log.i("B3", "B3");
    			new updateTwitterStatus().execute("");
    			
    		}
    	}
    }

}
