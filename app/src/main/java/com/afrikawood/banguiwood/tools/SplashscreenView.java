package com.afrikawood.banguiwood.tools;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.afrikawood.banguiwood.R;

public class SplashscreenView extends FrameLayout {
	
	private Context context;
	private RelativeLayout view;

	public SplashscreenView(Context context) {
		super(context);
		
		this.context = context;
		this.init();
	}

	public SplashscreenView(Context context, AttributeSet attrs) {
		super(context, attrs);

	}

	public SplashscreenView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

	}
	
	private void init() {
		
		FrameLayout.LayoutParams viewLayoutParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = (RelativeLayout) inflater.inflate(R.layout.splashscreen_view, this, false);
		view.setLayoutParams(viewLayoutParams);
		view.setBackgroundColor(0xffffffff);
		
		addView(view);
	}

}
