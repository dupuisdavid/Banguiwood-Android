package com.afrikawood.banguiwood;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.afrikawood.banguiwood.HomeFragment.HomeFragmentDelegate;
import com.afrikawood.banguiwood.business.Section;
import com.afrikawood.banguiwood.business.Section.SectionType;
import com.afrikawood.banguiwood.business.SectionArticles;
import com.afrikawood.banguiwood.business.SectionPlaylist;
import com.afrikawood.banguiwood.tools.AlertDialogBuilder;
import com.afrikawood.banguiwood.tools.BaseActivity;
import com.afrikawood.banguiwood.tools.BaseFragment;
import com.afrikawood.banguiwood.tools.DisplayProperties;
import com.afrikawood.banguiwood.tools.HttpRestClient;
import com.afrikawood.banguiwood.tools.SplashscreenView;
import com.afrikawood.banguiwood.tools.TwitterShareManager;
import com.afrikawood.banguiwood.tools.VideoPlayerView;
import com.afrikawood.banguiwood.utils.FragmentUtils;
import com.afrikawood.banguiwood.utils.HandlerUtilities;
import com.afrikawood.banguiwood.utils.Network;
import com.afrikawood.banguiwood.utils.StringUtilities;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;
import com.google.android.gms.plus.PlusShare;
import com.loopj.android.http.JsonHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

public class MainActivity 
	extends 
		BaseActivity 
	implements 
		HomeFragmentDelegate {
	
	private MainActivity self = this;
	private Fragment currentContentFragment;
	private ArrayList<Fragment> contentFragments = new ArrayList<Fragment>();
	private int lastSavedBackStackEntryCount = 0;
	@SuppressWarnings("unused")
	private FrameLayout wrapperView;
	public SplashscreenView splashscreenView;
	public VideoPlayerView videoPlayerView;
	public ArrayList<Section> sectionsTreeData;
	public MenuListFragment menuListFragment;
	private Tracker googleAnalyticsTracker;
	private Boolean closeAndDestroySplashscreenDone = false;
	private Boolean fragmentCleanUpProcessRunning = false;
	
	// FACEBOOK
    // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    private UiLifecycleHelper uiHelper;
    
    // TWITTER
    // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
	// Register your here app https://dev.twitter.com/apps/new and get your consumer key and secret
    // https://apps.twitter.com/app/7799560/keys

    private TwitterShareManager twitterShareManager = null;
    
	
	public ArrayList<Section> getSectionsTreeData() {
		return sectionsTreeData;
	}
	public void setSectionsTreeData(ArrayList<Section> sectionsTreeData) {
		this.sectionsTreeData = sectionsTreeData;
	}

	
	public MainActivity() {
		super(R.string.homeActivityTitle);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Set the Above View
		setContentView(R.layout.main_activity);
		wrapperView = (FrameLayout) findViewById(R.id.wrapperView);
		
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		
		if (bundle != null) {
//			Log.i("bundle", "" + bundle);
			
// 			{"alert": "Nouvelles vidéos disponible sur Banguiwood !", "sound":"", "youtubeId": "JzsdNKF8Mlw"}
			
			for (String key : bundle.keySet()) {
		        Log.i("bundle", key + " = " + (bundle.get(key) instanceof String ? bundle.get(key) : bundle.get(key)));
			}
			
			try {
				
				String parseDataKey = "com.parse.Data";
				JSONObject parseJsonData = bundle.containsKey(parseDataKey) ? new JSONObject(bundle.getString(parseDataKey)) : null;
				String youtubeVideoIdKey = "youtubeId";
				String youtubeVideoId = null;
				
				if (parseJsonData != null) {
					if (parseJsonData.has(youtubeVideoIdKey)) {
						youtubeVideoId = parseJsonData.getString(youtubeVideoIdKey);
					}
				}
				
				String parseChannelKey = "com.parse.Channel";
				@SuppressWarnings("unused")
				String parseChannel = bundle.containsKey(parseChannelKey) ? bundle.getString(parseChannelKey) : null;
				
				
			} catch (JSONException e) {
				e.printStackTrace();
			}

		} else {
			Log.i("bundle", "no bundle");
		}
		
		// FACEBOOK HELPER
		uiHelper = new UiLifecycleHelper(this, null);
	    uiHelper.onCreate(savedInstanceState);
	    
	    // TWITTER INIT CONFIG
	    twitterShareManager = new TwitterShareManager(this, getResources().getString(R.string.twitterCallbackMethodName));
		
		
		// Performance Tuning On Android
		// http://blog.venmo.com/hf2t3h4x98p5e13z82pl8j66ngcmry/performance-tuning-on-android
		
		// Make Your ProgressBar Smoother
		// http://antoine-merle.com/blog/2013/11/12/make-your-progressbar-more-smooth/

		// Set the Behind View
		setBehindContentView(R.layout.menu_frame);
		
		FragmentTransaction menuFragmentTransaction = getSupportFragmentManager().beginTransaction();
		menuListFragment = new MenuListFragment(this);
		menuFragmentTransaction.replace(R.id.menu_frame, menuListFragment).commit();
		
		// LISTENER
		getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            public void onBackStackChanged() {
            	onBackStackChangedFragmentManager();
            }
        });
		
		// SETUP
		setupGoogleTracker();
		setupAndOpenSplashscreenView();
		setupSlideRightMenuButton();
		startSectionsTreeRequest(new Runnable() {
			@Override
			public void run() {
				menuListFragment.refreshList(sectionsTreeData);
				setupHomeFragment();

			}
		});
		
		
	}
	
	public void setupHomeFragment() {
		
		Section homeSection = sectionsTreeData.get(0);
		SectionPlaylist inFrontSectionPlaylist = (SectionPlaylist) homeSection.sections.get(0);
		
		HomeFragment fragment = new HomeFragment(inFrontSectionPlaylist);
		fragment.setDelegate(this);
		switchContent(fragment, false);
	}
	
	// ### HomeFragmentDelegate
	
	@Override
	public void requestForSectionPlayListDidFinish(HomeFragment fragment) {
		if (!closeAndDestroySplashscreenDone) {
			closeAndDestroySplashscreen();
			closeAndDestroySplashscreenDone = true;
		}
	}
	
	private void onBackStackChangedFragmentManager() {
		// Update your UI here.
		
    	int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();
    	Boolean fragmentAdded = false;
    	
    	Log.i("onBackStackChanged", "onBackStackChanged (" + backStackEntryCount + ")");
    	
    	if (backStackEntryCount == 0) {
    		if (!fragmentCleanUpProcessRunning) {
    			MainActivity.this.finish();
    		}
    		
    	// FRAGMENT AS BEEN POPED
    	} else if (backStackEntryCount < lastSavedBackStackEntryCount) {
    		
    		if (contentFragments.size() > 0) {
    			contentFragments.remove((contentFragments.size() - 1));
    			if (contentFragments.size() > 0) {
    				currentContentFragment = contentFragments.get((contentFragments.size() - 1));
    			}
    		} 
    		
//    		Log.i("currentContentFragment.getClass()", "" + currentContentFragment.getClass());
    		
    		if (currentContentFragment instanceof HomeFragment) {
    			HomeFragment homeFragment = (HomeFragment) currentContentFragment;
    			homeFragment.setupYoutubePlayerFragment();
    		}
    		
    		lastSavedBackStackEntryCount = (lastSavedBackStackEntryCount - 1);
//    		Log.i("onBackStackChanged [REMOVE]", "onBackStackChanged " + backStackEntryCount + ", " + lastSavedBackStackEntryCount + ", " + currentContentFragment);
    		
    	// FRAGMENT AS BEEN PUSHED
    	} else {
    		
    		lastSavedBackStackEntryCount = (lastSavedBackStackEntryCount + 1);
    		fragmentAdded = true;
//    		Log.i("onBackStackChanged [ADD]", "onBackStackChanged " + backStackEntryCount + ", " + lastSavedBackStackEntryCount + ", " + currentContentFragment);
    	}
    	


    	
    	BaseFragment currentFragment = null;
    	int fragmentIndex = (lastSavedBackStackEntryCount - 1);
    	fragmentIndex = backStackEntryCount - 1;
    	
    	Log.i("onBackStackChanged", "backStackEntryCount : " + backStackEntryCount + ", lastSavedBackStackEntryCount ("+ contentFragments.size() +")) : " + lastSavedBackStackEntryCount + ", currentContentFragment : " + currentContentFragment + ", fragmentIndex : " + fragmentIndex);
    	
    	// FRAGMENT_INDEX BECOME -1... WHY ?
    	
    	if (contentFragments != null && contentFragments.size() > fragmentIndex) {
    		
    		if (fragmentIndex > -1 && contentFragments.get(fragmentIndex) != null && contentFragments.get(fragmentIndex) instanceof BaseFragment) {
    			currentFragment = (BaseFragment) contentFragments.get(fragmentIndex);
    			if (currentFragment != null) {
    				Log.i("onBackStackChanged " + (fragmentAdded ? "[ADD]" : "[REMOVE]"), "" + contentFragments.get(fragmentIndex).getClass() + ", " + currentFragment.getActionBarLeftButtonType());
            		getCustomActionBarView().updateActionBarButtonsAccordingToFragment(currentFragment);
    			}
        		
    		}
    		
    	} else {    		
    		Log.i("MainActivity", "contentFragments error (Invalid index 0, size is 0)");
    	}

	}
	
	public void setupGoogleTracker() {
		GoogleAnalytics googleAnalyticsInstance = GoogleAnalytics.getInstance(this);
        googleAnalyticsTracker = googleAnalyticsInstance.getTracker(this.getResources().getString(R.string.googleAnalyticsTrackingId));
	}
	
	public void trackView(String viewName) {
		if (googleAnalyticsTracker != null) {
			String sanitizedViewName = StringUtilities.prepareTrackString(viewName);
			googleAnalyticsTracker.send(MapBuilder.createAppView().set(Fields.SCREEN_NAME, sanitizedViewName).build());
		}
	}
	
	private void setupAndOpenSplashscreenView() {
		
		Log.i("setupSplashscreenView", "setupSplashscreenView");
		

		FrameLayout.LayoutParams splashscreenViewLayoutParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		
		splashscreenView = new SplashscreenView(this);
		splashscreenView.setLayoutParams(splashscreenViewLayoutParams);
		
		// http://stackoverflow.com/questions/9684275/adding-dynamic-view-using-windowmanager-addview
		// http://upshots.org/android/android-windowmanager-for-overlays
		// http://stackoverflow.com/questions/17745282/windowmanager-with-animation-is-it-possible
	
		WindowManager.LayoutParams mWindowParams = new WindowManager.LayoutParams();
		mWindowParams.gravity = Gravity.TOP | Gravity.START;
		mWindowParams.x = 0;
		mWindowParams.y = 0;
		mWindowParams.height = WindowManager.LayoutParams.MATCH_PARENT;
		mWindowParams.width = WindowManager.LayoutParams.MATCH_PARENT;
		mWindowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		
		mWindowParams.format = PixelFormat.OPAQUE;
		mWindowParams.windowAnimations = android.R.style.Animation_Toast;

		final WindowManager mWindowManager = (WindowManager) getSystemService("window");
		mWindowManager.addView(splashscreenView, mWindowParams);

	}
	
	private void closeAndDestroySplashscreen() {
		
		WindowManager mWindowManager = (WindowManager) getSystemService("window");
		mWindowManager.removeView(splashscreenView);
	}
	
	public void startSectionsTreeRequest(final Runnable completionRunnable) {
		
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				getSectionsTree(new Runnable() {
					@Override
					public void run() {
						if (completionRunnable != null) {
							completionRunnable.run();
						}
					}
				});
			}
		}, 3000);
		
	}
	
	public void getSectionsTree(final Runnable completionRunnable) {
		
		if (!Network.networkIsAvailable(this)) {
			return;
		}
			
		String URL = getResources().getString(R.string.projectSectionsTreeServiceURL);
		
		
		HttpRestClient.get(URL, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                
            }
            
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
            	Log.i("response", "" + response.length());
            	parseRootSectionsTreeWithJSONArrayData(response, completionRunnable);
            }
            
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            	
            }
        });
		
	}
	
	private void parseRootSectionsTreeWithJSONArrayData(JSONArray arrayData, final Runnable completionRunnable) {
		
		if (sectionsTreeData == null) {
			sectionsTreeData = new ArrayList<Section>();
		}
		
		if (sectionsTreeData.size() > 0) {
			sectionsTreeData.clear();
		}
		
		try {
		
			int treeDeepIndex = 0;
			
			for (int i = 0; i < arrayData.length(); i++) {
				
				JSONObject data = (JSONObject) arrayData.get(i);
				Section section = new Section();
				section.identifier = data.getString("id");
				section.name = StringUtilities.purgeUnwantedSpaceInText(data.getString("name"));
				section.isRootSection = true;
				
				sectionsTreeData.add(section);
				
				if (data.has("categories")) {
					JSONArray subSections = data.getJSONArray("categories");
					if (subSections.length() > 0) {
						parseSectionWithJSONArrayData(subSections, section, (treeDeepIndex+1));
					}
				}
			}
			
			if (completionRunnable != null) {
				completionRunnable.run();
			}
		
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	private void parseSectionWithJSONArrayData(JSONArray arrayData, Section parentSection, int treeDeepIndex) {
		
		try {
			
			for (int i = 0; i < arrayData.length(); i++) {
				
				JSONObject data = (JSONObject) arrayData.get(i);
				
				SectionType sectionType = SectionType.SectionTypePlayList;
				
				if (data.has("type")) {
					sectionType = data.getString("type").equals("playlist") ? SectionType.SectionTypePlayList : SectionType.SectionTypeArticles;
				}
				
				Section section = null;
				
				if (sectionType == SectionType.SectionTypePlayList) {
					section = new SectionPlaylist();
					
					if (data.has("youtubePlaylistID")) {
						((SectionPlaylist) section).setYoutubePlaylistIdentifier(data.getString("youtubePlaylistID"));
					}
					
				} else if (sectionType == SectionType.SectionTypeArticles) {
					section = new SectionArticles();
					
					if (data.has("articlesURL")) {
						((SectionArticles) section).setUrl(data.getString("articlesURL"));
					}
					
				}
				
				section.identifier = data.getString("id");
				section.name = StringUtilities.purgeUnwantedSpaceInText(data.getString("name"));
				
				if (data.has("type")) {
					section.sectionType = sectionType;
				}

				section.websiteCategoryRootUrl = data.has("websiteCategoryRootURL") ? data.getString("websiteCategoryRootURL") : "";
				
				section.isRootSection = false;
				
				if (parentSection.sections == null) {
					parentSection.sections = new ArrayList<>();
				}
				
				parentSection.sections.add(section);
				
				String arrows = "";
				for (int j = 0; j < treeDeepIndex; j++) {
					arrows = arrows + ">";
				}
				
//				Log.i("parseSectionWithJSONArrayData", ">"+ arrows +" "+ section.getName() +" [" + treeDeepIndex + "]" + "(parent : " + parentSection.getName() + ")");
				
				
				if (data.has("categories")) {
					JSONArray subSections = data.getJSONArray("categories");
					if (subSections.length() > 0) {
						parseSectionWithJSONArrayData(subSections, section, (treeDeepIndex+1));
					}
				}
			}
		
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}

	private void setupSlideRightMenuButton() {
		
		ActionBar actionBar = getSupportActionBar();
		
		if (actionBar == null || actionBar.getCustomView() == null) {
			return;
		}
		
		ImageButton slideRightMenuButton = (ImageButton) actionBar.getCustomView().findViewById(R.id.slideRightMenuButton);
		
		if (slideRightMenuButton != null) {
			slideRightMenuButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					toggle();
				}
			});
		}
		
		
	}

	public void setupYoutubePlayerFragment(String videoID, RelativeLayout videoPlayerViewWrapper) {
		setupYoutubePlayerFragment(videoID, videoPlayerViewWrapper, false);
	}
	public void setupYoutubePlayerFragment(String videoID, RelativeLayout videoPlayerViewWrapper, Boolean autoplay) {
		
		if (videoPlayerView == null) {
			videoPlayerView = new VideoPlayerView(this, videoID);
		}
		
		if (videoPlayerView.getParent() != videoPlayerViewWrapper) {
			Log.i("videoPlayerView", "attachTo");
			if (videoPlayerView != null) {
				videoPlayerView.setAutoplay(autoplay);
				videoPlayerView.attachTo(videoPlayerViewWrapper, videoID);
			}
			
		} else {
			Log.i("videoPlayerView", "attachTo not needed");
		}
		
	}
	
 	protected void onStart() {
        super.onStart();
        trackView("MainView");
    }
 	
	@Override
	protected void onRestart() {
        super.onRestart(); 
    }
	
    @Override
    protected void onResume() {
        super.onResume();
        
        if (uiHelper != null) {
        	uiHelper.onResume();
        }
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        
        if (uiHelper != null) {
        	uiHelper.onPause();
        }
    }
    
    @Override
    protected void onStop() {
    	if (videoPlayerView != null) {
			videoPlayerView.destroy();
			videoPlayerView = null;
		}
    	
        super.onStop();
    }
    
	@Override
	public void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		
		if (uiHelper != null) {
			uiHelper.onDestroy();
		}
    }
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		// TO MANAGE LATER
//		getSupportFragmentManager().putFragment(outState, "currentContentFragment", currentContentFragment);
		
		if (uiHelper != null) {
        	uiHelper.onSaveInstanceState(outState);
        }
		
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    
	    if (uiHelper != null) {
	    	uiHelper.onActivityResult(requestCode, resultCode, data, new FacebookDialog.Callback() {
		        @Override
		        public void onError(FacebookDialog.PendingCall pendingCall, Exception error, Bundle data) {
		            Log.e("Activity", String.format("Error: %s", error.toString()));
		        }

		        @Override
		        public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data) {
		            Log.i("Activity", "Success!");
		        }
		    });
	    }
	}
	
	@Override
	protected void onNewIntent(final Intent intent) {
		super.onNewIntent(null);
        
        if (intent != null) {
        	Log.i("intent onNewIntent", "" + intent + ", flags : " + intent.getFlags() + ", action : " + intent.getAction() + ", scheme : " + intent.getScheme());
        	Bundle bundle = intent.getExtras();
        	Log.i("bundle onNewIntent", "" + bundle);
        	Uri uri = intent.getData();
        	String uriString = intent.getDataString();
        	Log.i("uriString onNewIntent", "" + uri + ", " + uriString);
        	
        	HandlerUtilities.performRunnableAfterDelay(new Runnable() {
				@Override
				public void run() {
					twitterShareManager.manageTwitterCallBackUrl(intent);
				}
			}, 1000);
        	
        }
    	
    }
	
	
	public void switchContent(Fragment fragment) {
		switchContent(fragment, false);
	}
	
	public void switchContent(final Fragment fragment, final Boolean addFragment) {
		
		Log.i("switchContent", "addFragment : " + addFragment);
		
		if (!addFragment) {
			getSlidingMenu().showContent();
		}
		
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				
				FragmentManager fm = getSupportFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();
				
				if (addFragment) {
					// Building a Flexible UI with fragments
					// http://developer.android.com/training/basics/fragments/fragment-ui.html
					// How to Reverse Fragment Animations on BackStack?
					// http://stackoverflow.com/questions/10886669/how-to-reverse-fragment-animations-on-backstack
					ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_left, R.anim.slide_out_right);
					
					ft.add(R.id.fragment_container, fragment);
					// Android fragments navigation and backstack
					// http://stackoverflow.com/questions/13210446/android-fragments-navigation-and-backstack
					ft.addToBackStack(null);
					
					contentFragments.add(fragment);
					currentContentFragment = fragment;
					
				} else {
					
					Log.i("switchContent A", "backStackEntryCount : " + fm.getBackStackEntryCount() + ", lastSavedBackStackEntryCount : " + lastSavedBackStackEntryCount + ", " + currentContentFragment);
			    	
/*
					int backStackCount = fm.getBackStackEntryCount();
					for (int i = 0; i < backStackCount; i++) {

					    // Get the back stack fragment id.
					    int backStackId = getSupportFragmentManager().getBackStackEntryAt(i).getId();

					    fm.popBackStack(backStackId, FragmentManager.POP_BACK_STACK_INCLUSIVE);

					}
*/
					
//					Log.i("contentFragments.size()", "" + contentFragments.size());
								
/*
					for (int i = 0; i < contentFragments.size(); i++) {
						Fragment currentFragment = contentFragments.get((contentFragments.size()-1));
						
						if (currentFragment != null) {
							ft.remove(currentFragment);
							Log.i("Remove fragment", "" + currentFragment.getClass());
						}
					}
*/
					fragmentCleanUpProcessRunning = true;
					FragmentUtils.sDisableFragmentAnimations = true;
					// CLEAN UP !
					while (fm.getBackStackEntryCount() > 0){
					    fm.popBackStackImmediate();
					}
					FragmentUtils.sDisableFragmentAnimations = false;
					fragmentCleanUpProcessRunning = false;
					
					
					lastSavedBackStackEntryCount = 0;
					contentFragments.clear();
					
					Log.i("switchContent B", "backStackEntryCount : " + fm.getBackStackEntryCount() + ", lastSavedBackStackEntryCount : " + lastSavedBackStackEntryCount + ", " + currentContentFragment);
			    	
					ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
					ft.replace(R.id.fragment_container, fragment);
					ft.addToBackStack(null);
					
					contentFragments.add(fragment);
					currentContentFragment = fragment;
//					lastSavedBackStackEntryCount = (lastSavedBackStackEntryCount + 1);
					
					Log.i("switchContent C", "backStackEntryCount : " + fm.getBackStackEntryCount() + ", lastSavedBackStackEntryCount : " + lastSavedBackStackEntryCount + ", " + currentContentFragment);
			    	
					
					getCustomActionBarView().updateActionBarButtonsAccordingToFragment((BaseFragment) fragment);
					
				}
				
				ft.commit();
				
					
			}
		}, !addFragment ? 550 : 1);
		
	}

	public void openShareDialog(final String title, final String uri) {
		
		final String[] actionsText = {
			getResources().getString(R.string.textFacebook),
			getResources().getString(R.string.textTwitter),
			getResources().getString(R.string.textGooglePlus),
        	getResources().getString(R.string.textCancel)
        };
	
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        
        int padding = (int) (5.0f * DisplayProperties.getInstance(this).getPixelDensity());

        TextView textView = new TextView(this);
        textView.setText(getResources().getString(R.string.textShareOn));
        textView.setTextSize(16.0f);
        textView.setGravity(Gravity.CENTER);
        textView.setPadding(padding, padding, padding, padding);
        
        alert.setCustomTitle(textView);
        alert.setItems(actionsText, new DialogInterface.OnClickListener() {
    		@Override
    		public void onClick(DialogInterface dialog, int action) {
		        dialog.dismiss();
		        
    			switch (action) {
    				case 0:
    					shareOnFacebook(title, uri);
    					break;
    				case 1:
    					if (twitterShareManager != null) {
    						twitterShareManager.share(title +  "\n" + uri);
    					} else {
    						AlertDialogBuilder.build(self, self.getResources().getString(R.string.uiAlertViewTitle), self.getResources().getString(R.string.textErrorOccursShareImpossible));
    					}
    					
    					break;
    				case 2:
    					shareOnGooglePlus(title, uri);
    					break;
    
    				default:
    					break;
    			}
    		}
        });
        
        alert.show();
		
	}
	
	private void shareOnFacebook(final String title, final String uri) {
    	
    	final Resources resources = getResources();
    	
    	// https://developers.facebook.com/docs/android/share?locale=fr_FR#linkshare-setup

		// Development Key Hashes command
		// keytool -exportcert -alias androiddebugkey -keystore ~/.android/debug.keystore | openssl sha1 -binary | openssl base64
		// Release Key Hash command (production)
		// keytool -exportcert -alias topcongo -keystore /Users/DavidDupuis/Documents/Android/WorkspaceAndroid/BanguiwoodKeyStore/banguiwood-key-store | openssl sha1 -binary | openssl base64
		
		try {
			
			FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(this)
				.setLink(uri)
				.build();
			uiHelper.trackPendingDialogCall(shareDialog.present());
		
		} catch (Exception e) {
			e.printStackTrace();
			
			new AlertDialog.Builder(this)
		        .setTitle(resources.getString(R.string.appName))
		        .setMessage(resources.getString(R.string.textFacebookApplicationRequiredToShareContent))
		        .setPositiveButton(R.string.textYes, new DialogInterface.OnClickListener() {
		            @Override
		            public void onClick(DialogInterface dialog, int which) {
		            	// https://play.google.com/store/apps/details?id=com.facebook.katana&hl=fr_FR
						
						final String appPackageName = resources.getString(R.string.textFacebookApplicationPackageName);
						try {
							startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
						} catch (android.content.ActivityNotFoundException anfe) {
							startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName + "&hl=fr_FR")));
						}   
		            }

		        })
		        .setNegativeButton(R.string.textNo, null)
		        .show();
		}
    	
    }
	
    private void shareOnGooglePlus(final String title, final String uri) {
    	
    	// Sharing to Google+ from your Android app
    	// https://developers.google.com/+/mobile/android/share/
    	
    	Intent shareIntent = new PlusShare.Builder(this)
	        .setType("text/plain")
	        .setText(title)
	        .setContentUrl(Uri.parse(uri))
	        .getIntent();

    	startActivityForResult(shareIntent, 0);
    }
}
