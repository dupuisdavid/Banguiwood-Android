package com.afrikawood.banguiwood.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v4.util.LruCache;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

import com.afrikawood.banguiwood.R;

// http://androidtrainningcenter.blogspot.fr/2013/07/applying-custom-font-in-entire-android.html

public class CustomFontTextView extends TextView {
	
	private static LruCache<String, Typeface> sTypefaceCache = new LruCache<String, Typeface>(12);

	public CustomFontTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public void init(Context context, AttributeSet attrs) {
		
		TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.customFont, 0, 0);
      
		try {
			String typefaceName = a.getString(R.styleable.customFont_android_fontFamily);
	          
			if (!isInEditMode() && !TextUtils.isEmpty(typefaceName)) {
				Typeface typeface = sTypefaceCache.get(typefaceName);
		      
				if (typeface == null) {
		      	
		          typeface = Typeface.createFromAsset(context.getAssets(), typefaceName);
		      
		          // Cache the Typeface object
		          sTypefaceCache.put(typefaceName, typeface); 
				}
				setTypeface(typeface);
		  
				// Note: This flag is required for proper typeface rendering
				setPaintFlags(getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
			}
			
		} finally {
			a.recycle();	
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	    setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight());
	}
	
	
}
