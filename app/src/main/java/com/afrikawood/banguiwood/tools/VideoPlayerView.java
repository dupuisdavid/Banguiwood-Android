package com.afrikawood.banguiwood.tools;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.afrikawood.banguiwood.R;
import com.afrikawood.banguiwood.utils.AnimationUtilities;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.ErrorReason;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;

import net.louislam.android.L;

public class VideoPlayerView 
	extends 
		RelativeLayout 
	implements 
		YouTubePlayer.OnInitializedListener,
		YouTubePlayer.OnFullscreenListener,
		YouTubePlayer.PlaybackEventListener,
		YouTubePlayer.PlayerStateChangeListener, 
		YouTubeThumbnailView.OnInitializedListener {

	private String videoIdentifier;
	private FragmentActivity activity;
    private FrameLayout youtubePlayerFragmentWrapper;
	private YouTubePlayerSupportFragment youtubePlayerFragment;
	private YouTubePlayer player;
	private Boolean youtubePlayerIsInitialized;
	private Boolean youtubeVideoIsInitialized;
	private YouTubeThumbnailLoader youTubeThumbnailLoader;
	private FrameLayout youtubeThumbnailViewWrapper;
	private YouTubeThumbnailView youTubeThumbnailView;
    private Boolean autoplay = false;

	public Boolean getAutoplay() {
		return autoplay;
	}
	public void setAutoplay(Boolean autoplay) {
		this.autoplay = autoplay;
	}
	public FragmentActivity getActivity() {
		return activity;
	}
	public void setActivity(FragmentActivity activity) {
		this.activity = activity;
	}
	
	public VideoPlayerView(FragmentActivity activity, String videoIdentifier) {
		super(activity);
		
		this.videoIdentifier = videoIdentifier;
		this.youtubePlayerIsInitialized = false;
		this.activity = activity;
		
		if (this.activity != null && !videoIdentifier.equals("")) {
			init();
		}	
	}
	public VideoPlayerView(Context context) {
		super(context);
	}
	public VideoPlayerView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	public VideoPlayerView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public YouTubePlayer getPlayer() {
		return player;
	}

	
	private void init() {
		
		youtubePlayerIsInitialized = false;
		youtubeVideoIsInitialized = false;
		
		String googleDevelopperApiKeyString = activity.getResources().getString(R.string.googleDevelopperApiKey);
		
		RelativeLayout.LayoutParams viewLayoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		
		LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        RelativeLayout view = (RelativeLayout) inflater.inflate(R.layout.video_player_view, this, false);
		view.setLayoutParams(viewLayoutParams);
		
		
		// YOUTUBE THUMBNAIL_VIEW
		youTubeThumbnailView = (YouTubeThumbnailView) view.findViewById(R.id.youtubeThumbnailView);
		youTubeThumbnailView.initialize(googleDevelopperApiKeyString, this);
        
		// How to load YouTubePlayer using YouTubePlayerFragment inside another Fragment?? (Android)
		// http://stackoverflow.com/questions/19848142/how-to-load-youtubeplayer-using-youtubeplayerfragment-inside-another-fragment
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        int youtubePlayerFragmentWrapperId = R.id.youtubePlayerFragmentWrapper;
        youtubePlayerFragmentWrapper = (FrameLayout) view.findViewById(youtubePlayerFragmentWrapperId);

        int youtubeThumbnailViewWrapperId = R.id.youtubeThumbnailViewWrapper;
        youtubeThumbnailViewWrapper = (FrameLayout) view.findViewById(youtubeThumbnailViewWrapperId);

        youtubePlayerFragment = new YouTubePlayerSupportFragment();
        youtubePlayerFragment.initialize(googleDevelopperApiKeyString, this);
        fragmentTransaction.add(youtubePlayerFragmentWrapperId, youtubePlayerFragment);
        fragmentTransaction.commit();


        ImageButton playImageButton = (ImageButton) youtubeThumbnailViewWrapper.findViewById(R.id.playImageButton);
        playImageButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				play();
			}
		});

        addView(view);
	}
	
	public void play() {
		if (youtubePlayerIsInitialized && player != null && youtubeVideoIsInitialized) {
			
			hideYoutubeThumbnailViewWrapper(350, new Runnable() {
				@Override
				public void run() {
					player.play();
				}
			});
			
		}
	}
	
	public void attachTo(ViewGroup parentView, String videoId) {
		
		if (getParent() != null) {
			// First, remove player view from parent view
			((ViewGroup) getParent()).removeView(this);
			
			setVideo(videoId, false);
            if (player != null) {
                player.play();
            }
		}

		parentView.addView(this);

	}
	
	public void setVideo(String videoId, Boolean animation) {
		youtubeVideoIsInitialized = false;
		
		if (!animation) {
			youtubePlayerFragmentWrapper.setAlpha(0f);
			youtubeThumbnailViewWrapper.setAlpha(0f);
			setVisibility(View.VISIBLE);
			
			setVideo(videoId);
		}
		
	}
	
	public void setVideo(String videoId) {
		if (youTubeThumbnailLoader != null) {
			youTubeThumbnailLoader.setVideo(videoId);
		}

		if (player != null) {
			player.cueVideo(videoId);
		}
	}
	
	public void showYoutubeThumbnailViewWrapper(long animationDuration, final Runnable animationEndRunnable) {

		if (youtubeThumbnailViewWrapper != null) {
			AnimationUtilities.fadeIn(youtubeThumbnailViewWrapper, new Runnable() {
				@Override
				public void run() {
					if (animationEndRunnable != null) {
						animationEndRunnable.run();
					}
				}
			});
		}
	}
	
	public void hideYoutubeThumbnailViewWrapper(long animationDuration, final Runnable animationEndRunnable) {

		if (youtubeThumbnailViewWrapper != null) {
			youtubeThumbnailViewWrapper.animate().alpha(0f).setDuration(animationDuration).setListener(new AnimatorListener() {
				@Override
				public void onAnimationStart(Animator animation) {}
				@Override
				public void onAnimationRepeat(Animator animation) {}
				@Override
				public void onAnimationEnd(Animator animation) {
					if (youtubeThumbnailViewWrapper != null) {
						youtubeThumbnailViewWrapper.setVisibility(View.GONE);
					}
					
					if (animationEndRunnable != null) {
						animationEndRunnable.run();
					}
				}
				@Override
				public void onAnimationCancel(Animator animation) {}
			});
		}
	}
	
	@Override
	public void onInitializationFailure(YouTubeThumbnailView thumbnail, YouTubeInitializationResult errorReason) {

	}
	
	@Override
	public void onInitializationSuccess(YouTubeThumbnailView thumbnail, YouTubeThumbnailLoader thumbnailLoader) {
 
		youTubeThumbnailLoader = thumbnailLoader;
		thumbnailLoader.setOnThumbnailLoadedListener(new ThumbnailLoadedListener());
		youTubeThumbnailLoader.setVideo(videoIdentifier);
	}
	
	private final class ThumbnailLoadedListener implements YouTubeThumbnailLoader.OnThumbnailLoadedListener {
		@Override
		public void onThumbnailLoaded(YouTubeThumbnailView thumbnail, String videoId) {
			
			Log.i("VideoPlayerView", "onThumbnailLoaded (" + videoIdentifier + ")");

			new Handler().postDelayed(new Runnable() {
				  @Override
				  public void run() {
					  showYoutubeThumbnailViewWrapper(350, new Runnable() {
						@Override
						public void run() {
							youtubePlayerFragmentWrapper.setAlpha(1f);
						}
					});
				  }
			}, 100);
			
			
		}
		
		@Override
		public void onThumbnailError(YouTubeThumbnailView thumbnail, YouTubeThumbnailLoader.ErrorReason reason) {
			if (youtubeThumbnailViewWrapper != null) {
				youtubeThumbnailViewWrapper.setAlpha(0f);
			}

			if (youtubePlayerFragmentWrapper != null) {
				youtubePlayerFragmentWrapper.setAlpha(1f);
			}
		}
	}
	
	@Override
	public void onAdStarted() {
		
	}
	
	@Override
	public void onError(ErrorReason arg0) {
		
	}
	
	@Override
	public void onLoaded(String arg0) {

		youtubeVideoIsInitialized = true;
		
		Log.i("VideoPlayerView", "onLoaded (" + videoIdentifier + ")");
		if (autoplay) {
			play();
		}
		
	}
	
	@Override
	public void onLoading() {
		youtubeVideoIsInitialized = false;
	}
	
	@Override
	public void onVideoEnded() {
		Log.i("" + this.getClass(), "onVideoEnded");
		showYoutubeThumbnailViewWrapper(350, new Runnable() {
			@Override
			public void run() {
				
			}
		});
	}
	
	@Override
	public void onVideoStarted() {
		
	}
	
	@Override
	public void onBuffering(boolean arg0) {
		
	}
	
	@Override
	public void onPaused() {
		
	}
	
	@Override
	public void onPlaying() {
		
	}
	
	@Override
	public void onSeekTo(int arg0) {
		
	}
	
	@Override
	public void onStopped() {
		
	}
	
	@Override
	public void onFullscreen(boolean arg0) {
		
	}
	
	@Override
	public void onInitializationFailure(Provider provider, YouTubeInitializationResult result) {
		Log.i("YouTubePlayer", "onInitializationFailure : " + result);
		
		// SERVICE_INVALID
		// Explanation : It gives SERVICE_INVALID error if your clock isn't appropriately set.
		// Source : http://stackoverflow.com/questions/16183497/how-to-fix-error-youtubeinitializationresult-service-invalid
		
		L.confirmDialog((Context) activity, activity.getResources().getString(R.string.textGooglePlayServicesRequired), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// https://play.google.com/store/apps/details?id=com.google.android.gms&hl=fr_FR
				
				final String appPackageName = "com.google.android.gms";
				try {
					activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
				} catch (android.content.ActivityNotFoundException anfe) {
					activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName + "&hl=fr_FR")));
				}
			}
		});
		
	}
	
	@Override
	public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
		Log.i("YouTubePlayer", "onInitializationSuccess - " + videoIdentifier + " (" + this + "), " + youtubePlayerFragmentWrapper + ", " + youtubeThumbnailViewWrapper);
		
		this.player = player;
		this.player.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
		this.player.setPlayerStateChangeListener(this);
		
		
		if (!wasRestored) {
            player.cueVideo(videoIdentifier);
        }
		
		youtubePlayerIsInitialized = true;
	}
	
	public void destroy() {
		
		if (activity != null) {
			activity = null;
		}
		
		if (youtubePlayerFragment != null) {
			youtubePlayerFragment = null;
		}
		
		if (player != null) {
			player = null;
		}
		

		// Leak while switching activity with YouTubeThumbnailViews
		// http://stackoverflow.com/questions/14611876/leak-while-switching-activity-with-youtubethumbnailviews
		if (youTubeThumbnailLoader != null) {
			youTubeThumbnailLoader.release();
			youTubeThumbnailLoader = null;
		}
		
		if (youTubeThumbnailView != null) {
			youTubeThumbnailView = null;
		}
	}
	
	@Override
	protected void onDetachedFromWindow() {
		Log.i("VideoPlayerView", "onDetachedFromWindow");
	    super.onDetachedFromWindow();
	    
	}

}
