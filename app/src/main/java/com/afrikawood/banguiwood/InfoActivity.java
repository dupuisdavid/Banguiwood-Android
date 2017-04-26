package com.afrikawood.banguiwood;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.afrikawood.banguiwood.utils.AnimationUtilities;
import com.afrikawood.banguiwood.utils.HandlerUtilities;
import com.afrikawood.banguiwood.utils.Network;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import java.util.Locale;

public class InfoActivity extends Activity {

	private static final String TAG = String.format(Locale.FRENCH, "[%s]", InfoActivity.class.getSimpleName());

    private ProgressBar loadingProgressBar;
	private WebView webView;

    public InfoActivity() {
		super();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Set the Above View
		setContentView(R.layout.info_activity);
        ImageButton closeButton = (ImageButton) findViewById(R.id.closeButton);
		closeButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				InfoActivity.this.finish();
				InfoActivity.this.overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate_y);
			}
		});
		
		loadingProgressBar = (ProgressBar) findViewById(R.id.loadingProgressBar);
		
		
		if (Network.networkIsAvailable(InfoActivity.this)) {
			HandlerUtilities.performRunnableAfterDelay(new Runnable() {
				@Override
				public void run() {
					setupWebView();
				}
			}, 350);
			
			// AD
			setupAdMobBanner();
		}
	}
	
	@SuppressWarnings("deprecation")
	@SuppressLint("SetJavaScriptEnabled")
	public void setupWebView() {
		
		
		String URL = "http://mobile.banguiwood.com/informations.html";
		Log.i(TAG, "URL: " + URL);
		
		webView = (WebView) findViewById(R.id.webView);
		
		if (webView == null) {
			return;
		}
		
		WebSettings webSettings = webView.getSettings();
		webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
		webSettings.setSaveFormData(true);
		webSettings.setJavaScriptEnabled(true);
		webSettings.setLoadsImagesAutomatically(true);
		webSettings.setDomStorageEnabled(true);
		webSettings.setUseWideViewPort(true);
		webSettings.setPluginState(WebSettings.PluginState.ON);
		
		webView.setFocusableInTouchMode(false);
		webView.setBackgroundColor(Color.WHITE);
		
		webView.setWebViewClient(new WebViewClient() {
			
			@Override
            public boolean shouldOverrideUrlLoading(WebView view, String URL) {
				Log.i(TAG, "shouldOverrideUrlLoading: " + URL);
				
				try {
					
//					super.shouldOverrideUrlLoading(view, URL);				
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(URL));
	                startActivity(intent);
//	              	android.content.ActivityNotFoundException: No Activity found to handle Intent { act=android.intent.action.VIEW dat=telprompt:0033972442611 }

			    } catch (Exception e) {
			        e.printStackTrace();
			    }
				

				
                return true;
            }
			 
			public void onPageFinished(WebView view, String URL) {
				AnimationUtilities.fadeOut(loadingProgressBar, null);
				AnimationUtilities.fadeIn(webView, null);
			}
			 
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                   
            }
			
			@Override
		    public void onLoadResource(WebView view, String URL) {
//		        Log.i("onLoadResource", "URL : " + URL);
		    }
		});
		
		webView.loadUrl(URL);
		
	}
	
	public void setupAdMobBanner() {
		
		// I keep getting the error 'The Google Play services resources were not found. 
		// Check your project configuration to ensure that the resources are included.'
		// https://developers.google.com/mobile-ads-sdk/kb/?hl=it#resourcesnotfound
		
		RelativeLayout.LayoutParams adViewLayoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		adViewLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

        AdView adView = new AdView(InfoActivity.this);
		adView.setLayoutParams(adViewLayoutParams);
	    adView.setAdUnitId(getResources().getString(R.string.googleAdMobInfoBannerViewBlockIdentifier));
	    adView.setAdSize(AdSize.BANNER);
	    
	    RelativeLayout adWrapperLayout = (RelativeLayout) findViewById(R.id.adWrapperLayout);
	    adWrapperLayout.addView(adView);

	    AdRequest adRequest = new AdRequest.Builder().build();
	    adView.loadAd(adRequest);
	
	}
	
	@Override
	public void onBackPressed() {
	    super.onBackPressed();
	    overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate_y);
	}

	@Override
	public void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
	}
}
