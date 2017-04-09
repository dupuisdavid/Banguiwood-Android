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

public class ImprovedTextView extends TextView {
	
	private Runnable onSizeChangedRunnable = null;
	private static LruCache<String, Typeface> sTypefaceCache = new LruCache<String, Typeface>(12);

	public Runnable getOnSizeChangedRunnable() {
		return onSizeChangedRunnable;
	}

	public void setOnSizeChangedRunnable(Runnable onSizeChangedRunnable) {
		this.onSizeChangedRunnable = onSizeChangedRunnable;
	}

	public ImprovedTextView(Context context) {
		super(context);
	}

	public ImprovedTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public ImprovedTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void init(Context context, AttributeSet attrs) {
		
		TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.customFont, 0, 0);
		
		try {
			
			String typefaceName = a.getString(R.styleable.customFont_android_fontFamily);
			
			if (!isInEditMode() && !TextUtils.isEmpty(typefaceName)) {
				Typeface typeface = sTypefaceCache.get(typefaceName);
				
				if (typeface == null) {
					
					Boolean typeFaceIsAvailable = AssetsUtilities.assetExists(context, typefaceName);
					
					if (typeFaceIsAvailable) {
						typeface = Typeface.createFromAsset(context.getAssets(), typefaceName);
						// Cache the Typeface object
						sTypefaceCache.put(typefaceName, typeface);
					}
					
					
				}
				
				if (typeface != null) {
					setTypeface(typeface);
					// Note: This flag is required for proper typeface rendering
					setPaintFlags(getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
				}
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			a.recycle();	
		}
	}
	
	@Override
	public void onSizeChanged(int w, int h, int oldW, int oldH) {
		if (onSizeChangedRunnable != null) {
			onSizeChangedRunnable.run();
		}
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	    setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight());
	}
	
	
}
