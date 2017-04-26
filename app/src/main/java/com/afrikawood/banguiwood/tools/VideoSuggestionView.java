package com.afrikawood.banguiwood.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.afrikawood.banguiwood.R;
import com.afrikawood.banguiwood.business.Video;
import com.afrikawood.banguiwood.utils.AnimationUtilities;
import com.afrikawood.banguiwood.utils.CustomFontTextView;
import com.afrikawood.banguiwood.utils.StringUtilities;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class VideoSuggestionView extends FrameLayout {

	private Context context;
	private ImageView imageView;
	private Video video;
	private VideoSuggestionViewDelegate delegate;
	public void setDelegate(VideoSuggestionViewDelegate delegate) {
		this.delegate = delegate;
	}
	
	// ********************************************************************
	// INTERFACES
	// ********************************************************************

	public interface VideoSuggestionViewDelegate {
		void didTapVideoSuggestionView(VideoSuggestionView view, Video video);
	}

	public VideoSuggestionView(Context context, Video video) {
		super(context);
		
		this.context = context;
		this.video = video;
		this.init();
	}

	public VideoSuggestionView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public VideoSuggestionView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	private void init() {

		ImageLoader imageLoader = ImageLoader.getInstance();
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.cacheInMemory(false)
				.cacheOnDisk(false)
				.considerExifParams(false)
				.build();
		
		FrameLayout.LayoutParams viewLayoutParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		RelativeLayout view = (RelativeLayout) inflater.inflate(R.layout.suggestion_view, this, false);
		view.setLayoutParams(viewLayoutParams);
		view.setBackgroundColor(0xffeeeeee);
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (delegate != null) {
					delegate.didTapVideoSuggestionView(VideoSuggestionView.this, video);
				}
			}
		});
		addView(view);
		
		imageView = (ImageView) view.findViewById(R.id.imageView);
		String imageUrl = video.highThumbnailURL;
//		Log.i("imageUrl", "imageUrl : " + imageUrl);
//		imageUrl = "http://radboudreshapecenter.com/wp-content/uploads/2014/08/4562-thumb.jpg";
		
		if (!imageUrl.isEmpty()) {
			
			imageLoader.displayImage(imageUrl, imageView, options, new ImageLoadingListener() {
				
				@Override
				public void onLoadingStarted(String arg0, View arg1) {}
				
				@Override
				public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {}
				
				@Override
				public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
					AnimationUtilities.fadeIn(imageView, 350, null);
				}
				
				@Override
				public void onLoadingCancelled(String arg0, View arg1) {}
			});
			
		}


		CustomFontTextView titleTextView = (CustomFontTextView) view.findViewById(R.id.titleTextView);
		titleTextView.setText(StringUtilities.purgeUnwantedSpaceInText(video.title));
		
		
	}

}
