package com.afrikawood.banguiwood.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.webkit.WebView;

public class ImprovedWebView extends WebView {

	public ImprovedWebView(Context context) {
		super(context);
	}

	public ImprovedWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ImprovedWebView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public ImprovedWebView(Context context, AttributeSet attrs, int defStyle, boolean privateBrowsing) {
		super(context, attrs, defStyle, privateBrowsing);
		
	}
	
	@Override
	public void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        
    }
	
	// Android WebView JellyBean -> Should not happen: no rect-based-test nodes found
	// http://stackoverflow.com/questions/12090899/android-webview-jellybean-should-not-happen-no-rect-based-test-nodes-found
	
	// For following webkit error
	// E/webcoreglue(27416): Should not happen: no rect-based-test nodes found
	// V/WebViewInputDispatcher(27416): blockWebkitDraw
	// V/WebViewInputDispatcher(27416): blockWebkitDraw lockedfalse
	// D/webview(27416): blockWebkitViewMessage= false
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {

	    if (event.getAction() == MotionEvent.ACTION_DOWN){
	        int temp_ScrollY = getScrollY();
	        scrollTo(getScrollX(), getScrollY() + 1);
	        scrollTo(getScrollX(), temp_ScrollY);
	    }

	    return super.onTouchEvent(event);
	}

}
