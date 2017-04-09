package com.afrikawood.banguiwood;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.afrikawood.banguiwood.business.Article;
import com.afrikawood.banguiwood.business.Section;
import com.afrikawood.banguiwood.tools.BaseFragment;
import com.afrikawood.banguiwood.utils.AnimationUtilities;
import com.afrikawood.banguiwood.utils.HandlerUtilities;
import com.afrikawood.banguiwood.utils.Network;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

public class ArticleFragment extends BaseFragment {

	@SuppressWarnings("unused")
	private ArticleFragment self = this;
	private MainActivity context;
	private FrameLayout rootView;
	private Section section;
	private Article article;
	private ProgressBar loadingProgressBar;
	private WebView webView;
	private AdView adView;
	
	public ArticleFragment(Article article, Section section, Boolean forceBackButton) {
		
		this.article = article;
		this.section = section;
		
		
		setActionBarLeftButtonType(forceBackButton ? ButtonType.BACK : ButtonType.NONE);
		setActionBarRightButtonType(ButtonType.MENU);
		
	}
	
	public ArticleFragment() {
		
	}
	
	@Override
    public void onAttach(Activity activity) {
	
        if (activity instanceof MainActivity) {
        	context = (MainActivity) activity;
        }

        super.onAttach(activity);
    }
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = (FrameLayout) inflater.inflate(R.layout.article_fragment, container, false);
		rootView.setAnimationCacheEnabled(true);
		rootView.setDrawingCacheEnabled(true);
		
		loadingProgressBar = (ProgressBar) rootView.findViewById(R.id.loadingProgressBar);
		
		if (Network.networkIsAvailable(context)) {
			
			if (article != null) {
				
				HandlerUtilities.performRunnableAfterDelay(new Runnable() {
					@Override
					public void run() {
						setupWebView();
						
					}
				}, 350);
			}
			
			// AD
			setupAdMobBanner();
			
		}
		
		return rootView;
	}
	
	@SuppressWarnings("deprecation")
	@SuppressLint("SetJavaScriptEnabled")
	public void setupWebView() {
		
		if (article == null || article.getURL().equals("")) {
			return;
		}
		
		String URL = article.getURL();
		Log.i("URL", URL);   
		
		webView = (WebView) rootView.findViewById(R.id.webView);
		
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
//				super.shouldOverrideUrlLoading(view, URL);				
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(URL));
                startActivity(intent);
				
                return true;
            }
			 
			public void onPageFinished(WebView view, String URL) {
				
				String JS = "" +
					    "" +
					    "try {" +
					    "  " +
					    "	document.getElementById(\"jBar\").style.display = \"none\";" +
					    "  	document.getElementById(\"jsn-header\").style.display = \"none\";" +
					    "  	document.getElementById(\"jsn-pos-breadcrumbs\").style.display = \"none\";" +
					    "  	document.getElementsByClassName(\"jsn-article-toolbar\")[0].style.display = \"none\";" +
					    "  	document.getElementsByClassName(\"contentheading\")[0].style.fontSize = \"1.4em\";" +
						"  	document.getElementsByClassName(\"richbox-1\")[0].style.display = \"none\";" +
					    "  	document.getElementById(\"jsn-footer\").style.display = \"none\";" +
					    "  	document.getElementById(\"joomsharebar\").style.display = \"none\";" +
					    "  	document.getElementById(\"jsn-gotoplink\").style.display = \"none\";" +
					    "	" +
					    "} catch (e) {" +
					    "	alert(\"Error :\" + e);" +
					    "}" +
					    "";
				
				
				webView.loadUrl("javascript:" + JS);
				
				
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
		
		adView = new AdView(context);
		adView.setLayoutParams(adViewLayoutParams);
	    adView.setAdUnitId(getResources().getString(R.string.googleAdMobArticleViewBlockIdentifier));
	    adView.setAdSize(AdSize.BANNER);
	    
	    RelativeLayout adWrapperLayout = (RelativeLayout) rootView.findViewById(R.id.adWrapperLayout);
	    adWrapperLayout.addView(adView);

	    AdRequest adRequest = new AdRequest.Builder().build();
	    adView.loadAd(adRequest);
	
	}
	
	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
	
	@Override
	public void onStart(){
		super.onStart();
	    Log.i("" + this.getClass(), "onStart");
	    
	    if (section != null && !section.getName().equals("")) {
	    	context.trackView(String.format("%s/%s", section.getName(), article.getTitle()));
	    }
	}
	
	@Override
	public void onResume() {
		super.onResume();
		Log.i("" + this.getClass(), "onResume");
	}

	@Override
	public void onPause() {
		super.onPause();
		Log.i("" + this.getClass(), "onPause");
	}
	
	@Override
	public void onStop() {
		super.onStop();
		Log.i("" + this.getClass(), "onStop");
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Log.i("" + this.getClass(), "onDestroyView");
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i("" + this.getClass(), "onDestroy");
	}
	
	@Override
	public void onDetach() {
		super.onDetach();
		Log.i("" + this.getClass(), "onDetach");
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

}
