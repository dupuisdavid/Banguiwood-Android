package com.afrikawood.banguiwood;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.afrikawood.banguiwood.business.Section;
import com.afrikawood.banguiwood.business.Video;
import com.afrikawood.banguiwood.tools.BaseFragment;
import com.afrikawood.banguiwood.tools.CustomActionBarButtonConfiguration;
import com.afrikawood.banguiwood.tools.DisplayProperties;
import com.afrikawood.banguiwood.tools.VideoSuggestionView;
import com.afrikawood.banguiwood.tools.VideoSuggestionView.VideoSuggestionViewDelegate;
import com.afrikawood.banguiwood.utils.CustomFontTextView;
import com.afrikawood.banguiwood.utils.DateUtilities;
import com.afrikawood.banguiwood.utils.Network;
import com.afrikawood.banguiwood.utils.StringUtilities;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.youtube.player.YouTubePlayer;

import java.util.ArrayList;
import java.util.Locale;

public class PlayerFragment extends BaseFragment implements VideoSuggestionViewDelegate {

	private FrameLayout rootView;
	private Section section;
	private Video video;
	private RelativeLayout videoPlayerWrapperView;
	private CustomFontTextView videoTitleTextView;
	private CustomFontTextView videoPublicationDateTextView;
	private LinearLayout innerVideoSuggestionsListView;
	private ArrayList<Video> videoSuggestions;
	
	public PlayerFragment(Video video, Section section, ArrayList<Video> videoSuggestions) {
		
		this.video = video;
		this.section = section;
		this.videoSuggestions = videoSuggestions;
		
		setActionBarLeftButtonType(ButtonType.BACK);
//		setActionBarRightButtonType(ButtonType.MENU);
		
		CustomActionBarButtonConfiguration customActionBarRightButtonConfiguration = new CustomActionBarButtonConfiguration();
		customActionBarRightButtonConfiguration.setDrawableResId(R.drawable.share_button);
		customActionBarRightButtonConfiguration.setOnClickRunnable(new Runnable() {
			@Override
			public void run() {
				Log.i("SHARE", "CLICK");
                ((MainActivity) getActivity()).openShareDialog(PlayerFragment.this.video.title, getUrlToShare());
			}
		});
		
		setActionBarCustomRightButtonConfiguration(customActionBarRightButtonConfiguration);


		
	}
	
	private String getUrlToShare() {
		
		String urlToShare = "";
		
		if (!section.websiteCategoryRootUrl.equals("")) {
			urlToShare = urlToShare + section.websiteCategoryRootUrl;
			urlToShare = urlToShare + String.format(Locale.FRENCH, "youtubegallery?videoid=%s", video.youtubeVideoIdentifier);
		} else {
			urlToShare = String.format(Locale.FRENCH, "https://www.youtube.com/watch?v=%s", video.youtubeVideoIdentifier);
		}
		
		return urlToShare;
	}

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = (FrameLayout) inflater.inflate(R.layout.player_fragment, container, false);
		rootView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {}
		});

		CustomFontTextView sectionTitleTextView = (CustomFontTextView) rootView.findViewById(R.id.sectionTitleTextView);
		sectionTitleTextView.setText(section.name);
		videoTitleTextView = (CustomFontTextView) rootView.findViewById(R.id.videoTitleTextView);
		videoPublicationDateTextView = (CustomFontTextView) rootView.findViewById(R.id.videoPublicationDateTextView);
		innerVideoSuggestionsListView = (LinearLayout) rootView.findViewById(R.id.innerVideoSuggestionsListView);

		setVideoInformations(video);
		setupYoutubePlayerFragment();
		
		if (videoSuggestions != null && videoSuggestions.size() > 0) {
			setupVideoSuggestions();
	    }
		
		if (Network.networkIsAvailable(getContext())) {
			// AD
			setupAdMobBanner();
		}
		
		return rootView;
	}
	
	public void setVideoInformations(Video video) {
		videoTitleTextView.setText(StringUtilities.purgeUnwantedSpaceInText(video.title));
		videoPublicationDateTextView.setText(String.format(getContext().getResources().getString(R.string.textPublishedAt), DateUtilities.convertDateToString(video.publicationDate, getContext().getResources().getString(R.string.displayedPublicationDateFormat))));
	}
	
	public void setupYoutubePlayerFragment() {
		
		if (video != null) {
			
			final String videoIdentifier = video.youtubeVideoIdentifier;
			
			if (!videoIdentifier.equals("")) {
				if (videoPlayerWrapperView == null) {
					videoPlayerWrapperView = (RelativeLayout) rootView.findViewById(R.id.videoPlayerWrapperView);
				}

				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
                        ((MainActivity) getActivity()).setupYoutubePlayerFragment(videoIdentifier, videoPlayerWrapperView, true);
					}
				}, 1000);
			}
		} else {
		
			videoPlayerWrapperView.setVisibility(View.GONE);
			
		}
	}

	private void setupVideoSuggestions() {
		
		if (videoSuggestions == null) {
			return;
		}

        Activity activity = getActivity();
	    
	    int w = (int) (78 * DisplayProperties.getInstance(activity).getPixelDensity());
	    int h = (int) (73 * DisplayProperties.getInstance(activity).getPixelDensity());
	    int rightMargin = (int) (12 * DisplayProperties.getInstance(activity).getPixelDensity());
	    
	    for (int i=0; i<videoSuggestions.size(); i++) {
	        Video video = videoSuggestions.get(i);

	        LinearLayout.LayoutParams videoSuggestionViewLayoutParams = new LinearLayout.LayoutParams(w, h);
	        videoSuggestionViewLayoutParams.rightMargin = rightMargin;
	        VideoSuggestionView videoSuggestionView = new VideoSuggestionView(activity, video);
	        videoSuggestionView.setLayoutParams(videoSuggestionViewLayoutParams);
	        videoSuggestionView.setDelegate(this);
	        innerVideoSuggestionsListView.addView(videoSuggestionView);   
	    }	
	}
	
	@Override
	public void didTapVideoSuggestionView(VideoSuggestionView view, Video video) {
		setVideoInformations(video);
        ((MainActivity) getActivity()).videoPlayerView.setVideo(video.youtubeVideoIdentifier, false);
	}
	
	public void setupAdMobBanner() {
		
		// I keep getting the error 'The Google Play services resources were not found. 
		// Check your project configuration to ensure that the resources are included.'
		// https://developers.google.com/mobile-ads-sdk/kb/?hl=it#resourcesnotfound
		
		RelativeLayout.LayoutParams adViewLayoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		adViewLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

		AdView adView = new AdView(getContext());
		adView.setLayoutParams(adViewLayoutParams);
	    adView.setAdUnitId(getResources().getString(R.string.googleAdMobPlayerViewBlockIdentifier));
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
	    
	    if (section != null && !section.name.equals("")) {
            ((MainActivity) getActivity()).trackView(String.format("%s/%s", section.name, video.title));
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
		
		// OPTIMIZATION CAN BE DONE
		// MAKE A SCREENSHOT OF THIS FRAGMENT DURING ANIMATION
		// OR A SCREENSHOT OF UNDERLINE FRAGMENT
		// TODO

        MainActivity activity = ((MainActivity) getActivity());
		
		if (videoPlayerWrapperView != null) {
	
			if (activity == null) {
				Log.i(this.getClass().getSimpleName(), "context == null");
				return;
			}
			
			if (activity.videoPlayerView == null) {
				Log.i("C", "C");
				Log.i(this.getClass().getSimpleName(), "context.videoPlayerView == null");
				return;
			}

			YouTubePlayer player = activity.videoPlayerView.getPlayer();
			if (player != null && player.isPlaying()) {
				player.pause();
//				videoPlayerWrapperView.getChildAt(0).setVisibility(View.GONE);
			}
		}
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
	}
	
	@Override
	public void onDetach() {
		super.onDetach();
	}
	
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	

}
