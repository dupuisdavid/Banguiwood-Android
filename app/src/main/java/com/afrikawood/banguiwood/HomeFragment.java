package com.afrikawood.banguiwood;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ScrollView;

import com.afrikawood.banguiwood.business.Section;
import com.afrikawood.banguiwood.business.SectionPlaylist;
import com.afrikawood.banguiwood.business.Video;
import com.afrikawood.banguiwood.tools.AlertDialogBuilder;
import com.afrikawood.banguiwood.tools.BaseFragment;
import com.afrikawood.banguiwood.tools.BounceListView;
import com.afrikawood.banguiwood.tools.CustomActionBarButtonConfiguration;
import com.afrikawood.banguiwood.tools.DisplayProperties;
import com.afrikawood.banguiwood.tools.HttpRestClient;
import com.afrikawood.banguiwood.tools.ListAdapter;
import com.afrikawood.banguiwood.utils.AnimationUtilities;
import com.afrikawood.banguiwood.utils.CustomFontTextView;
import com.afrikawood.banguiwood.utils.DateUtilities;
import com.afrikawood.banguiwood.utils.HandlerUtilities;
import com.afrikawood.banguiwood.utils.Network;
import com.afrikawood.banguiwood.utils.StringUtilities;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.loopj.android.http.JsonHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

public class HomeFragment extends BaseFragment {

	private HomeFragment self = this;
	private MainActivity context;
	private View rootView;
	private SectionPlaylist section;
	private Video topVideo;
	private ScrollView scrollView;
	private RelativeLayout videoPlayerWrapperView;
	private ProgressBar loadingProgressBar;
	private AdView adView;
	private int requestLoopIndex;
	
	private BounceListView listView;
	private ListAdapter listViewAdapter;
	private ArrayList<Object> dataList;
	
	private HomeFragmentDelegate delegate;
	
	public HomeFragmentDelegate getDelegate() {
		return delegate;
	}

	public void setDelegate(HomeFragmentDelegate delegate) {
		this.delegate = delegate;
	}
	
	// ********************************************************************
	// INTERFACES
	// ********************************************************************

	public static interface HomeFragmentDelegate {
		void requestForSectionPlayListDidFinish(HomeFragment fragment);
	}
	
	public HomeFragment(SectionPlaylist section) {
		
		this.section = section;
	
		CustomActionBarButtonConfiguration customActionBarLeftButtonConfiguration = new CustomActionBarButtonConfiguration();
		customActionBarLeftButtonConfiguration.setDrawableResId(R.drawable.info_button);
		customActionBarLeftButtonConfiguration.setOnClickRunnable(new Runnable() {
			@Override
			public void run() {
				
				Intent intent = new Intent(context, InfoActivity.class);
				context.startActivity(intent);
				context.overridePendingTransition(R.anim.activity_open_translate_y, R.anim.activity_close_scale);
				
			}
		});
		
		setActionBarCustomLeftButtonConfiguration(customActionBarLeftButtonConfiguration);
		
		setActionBarRightButtonType(ButtonType.MENU);
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
		rootView = (FrameLayout) inflater.inflate(R.layout.home_fragment, container, false);
		scrollView = (ScrollView) rootView.findViewById(R.id.scrollView);
		
		loadingProgressBar = (ProgressBar) rootView.findViewById(R.id.loadingProgressBar);
		
		dataList = new ArrayList<Object>();
		
		setupList();
		
		if (Network.networkIsAvailable(context)) {
			
			if (section != null) {
				
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						
						requestForSectionPlayListItems(section.getYoutubePlaylistIdentifier(), new Runnable() {
							@Override
							public void run() {
								Log.i("Success", "Success");

								setupYoutubePlayerFragment();
								updateVideoTextViews();
								updateListViewHeight();								
								
								if (dataList.size() > 0) {
									
									listViewAdapter.notifyDataSetChanged();
									listView.setVisibility(View.VISIBLE);
									
									// http://stackoverflow.com/questions/9842494/how-to-prevent-a-scrollview-from-scrolling-to-a-webview-after-data-is-loaded
//									scrollView.scrollTo(0, 0);
//									scrollView.fullScroll(ScrollView.FOCUS_UP);
								}
								
								HandlerUtilities.performRunnableAfterDelay(new Runnable() {
									@Override
									public void run() {
										AnimationUtilities.fadeOut(loadingProgressBar, null);
										AnimationUtilities.fadeIn(scrollView, new Runnable() {
											@Override
											public void run() {
												if (delegate != null) {
													delegate.requestForSectionPlayListDidFinish(self);
												}
											}
										});
									}
								}, 350);
							}
							
						}, new Runnable() {
							@Override
							public void run() {
								
								final String textAlertDialogViewTitle = context.getResources().getString(R.string.textAlertDialogViewTitle);
								final String textServerConnectionImpossible = context.getResources().getString(R.string.textServerConnectionImpossible);
								
								AnimationUtilities.fadeOut(loadingProgressBar, new Runnable() {
									@Override
									public void run() {
										AlertDialogBuilder.build(context, textAlertDialogViewTitle, textServerConnectionImpossible);
									}
								});
								
							}
						});
						
					}
				}, 350);
			}
			
			// AD
			setupAdMobBanner();
			
		}

		return rootView;
	}
	
	private ArrayList<Video> pickVideosSuggestions(int pickNumber, String youtubeVideoIdentifier) {
	    
	    int pickNumberLimit = pickNumber > ((int) dataList.size()) ? (((int) dataList.size()) - 1) : pickNumber;
	    
	    HashMap<String, Video> videosSuggestions = new HashMap<String, Video>();
	    
	    int count = 0;
	    
	    while (videosSuggestions.size() < pickNumberLimit) {
	    
	    	int min = 0;
	    	int max = dataList.size() - 1;

	    	Random r = new Random();
	    	int randomIndex = r.nextInt(max - min + 1) + min;
	    	
			
			Object data = dataList.get(randomIndex);
			
			if (data instanceof Video) {
				Video pickVideo = (Video) data;
				String videoKey = pickVideo.getYoutubeVideoIdentifier();
				
				if (!videosSuggestions.containsKey(videoKey) && !videoKey.equals(youtubeVideoIdentifier)) {
					videosSuggestions.put(videoKey, pickVideo);
				}
				
			}
	        
			Log.i("pickVideosSuggestions", "LOOP : " + count);
			
	        count++;
	    }
	    
	    
	    
	    return new ArrayList<Video>(videosSuggestions.values());
	}
	
	public void setupYoutubePlayerFragment() {
		
		if (videoPlayerWrapperView == null) {
			videoPlayerWrapperView = (RelativeLayout) rootView.findViewById(R.id.videoPlayerWrapperView);
		}
		
		if (topVideo != null) {
			
			final String videoIdentifier = topVideo.getYoutubeVideoIdentifier();
			
			if (!videoIdentifier.equals("")) {
				
				context.setupYoutubePlayerFragment(videoIdentifier, videoPlayerWrapperView);

/*
				new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
					@Override
					public void run() {
						context.setupYoutubePlayerFragment(videoIdentifier, videoPlayerWrapperView);
					}
					
				}, 1);
*/

			}
			
		} else {
		
			videoPlayerWrapperView.setVisibility(View.GONE);
			
		}
		
	}
	
	private void updateVideoTextViews() {
		
		CustomFontTextView videoTitleTextView = (CustomFontTextView) rootView.findViewById(R.id.videoTitleTextView);
		CustomFontTextView videoPublicationDateTextView = (CustomFontTextView) rootView.findViewById(R.id.videoPublicationDateTextView);
		
		if (topVideo == null) {
			videoTitleTextView.setVisibility(View.GONE);
			videoPublicationDateTextView.setVisibility(View.GONE);
		}
		
		
		videoTitleTextView.setText(StringUtilities.purgeUnwantedSpaceInText(topVideo.getTitle()));
		videoPublicationDateTextView.setText(String.format(context.getResources().getString(R.string.textPublishedAt), DateUtilities.convertDateToString(topVideo.getPublicationDate(), context.getResources().getString(R.string.displayedPublicationDateFormat))));
		
	}
	
	private void updateListViewHeight() {
		
		// height : 1450

		
		int height = (int) (50 * DisplayProperties.getInstance(context).getPixelDensity()) * dataList.size();
		
		LinearLayout.LayoutParams listViewLayoutParams = (LinearLayout.LayoutParams) listView.getLayoutParams();
		listViewLayoutParams.height = height;
		listView.setLayoutParams(listViewLayoutParams);
		listView.requestLayout();
		
		Log.i("height", "" + height);
		
	}
	
	private void requestForSectionPlayListItems(String playlistIdentifier, Runnable successRunnable, Runnable failureRunnable) {
		requestForSectionPlayListItems(playlistIdentifier, "", successRunnable, failureRunnable);
	}
	
	private void requestForSectionPlayListItems(final String playlistIdentifier, String nextPageToken, final Runnable successRunnable, final Runnable failureRunnable) {
		
		if (!Network.networkIsAvailable(context)) {
			return;
		}
		
		if (playlistIdentifier.equals("")) {
			return;
		}
		
		final int nbFrontPageVideo = 30;
		
		String unformattedURL = context.getResources().getString(R.string.youtubeApiPlaylistItemsServiceURL);
		String youtubeBrowserApiKey = context.getResources().getString(R.string.youtubeBrowserApiKey);
			
		String url = String.format(unformattedURL, playlistIdentifier, youtubeBrowserApiKey, nbFrontPageVideo, (!nextPageToken.equals("") ? "&pageToken=" + nextPageToken : ""));
		Log.i("URL", "" + url);
		
		HttpRestClient.get(url, null, new JsonHttpResponseHandler() {
            @SuppressWarnings("unused")
			@Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            	
            	if (response != null) {
            		
            		try {
            			
						JSONArray videos = (JSONArray) response.getJSONArray("items");
						self.parseSectionsPlayList(videos);
						
						String nextPageToken = null;
						if (response.has("nextPageToken")) {
							nextPageToken = response.getString("nextPageToken");
						}
						
						if (nbFrontPageVideo == 0 && nextPageToken != null && !nextPageToken.equals("")) {
							requestLoopIndex = requestLoopIndex + 1;
							self.requestForSectionPlayListItems(playlistIdentifier, nextPageToken, successRunnable, failureRunnable);
							
						} else {
							if (successRunnable != null) {
			                	successRunnable.run();
			                }
						}

					} catch (JSONException e) {
						e.printStackTrace();
					}
            	}
            }
            
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            	if (failureRunnable != null) {
            		failureRunnable.run();
                }
            }
        });

		
		
	}
	
	private void parseSectionsPlayList(JSONArray data) {
		
		ArrayList<Section> subSections = null;
		
		if (requestLoopIndex == 0) {
			if (section != null) {
				subSections = section.sections;
				
				for (int i = 0; i < subSections.size(); i++) {
					Section section = subSections.get(i);
					
					dataList.add(section);
				}
			}
		}
		
		for (int i = 0; i < data.length(); i++) {
			
			try {
				
				JSONObject videoDictionary = (JSONObject) data.get(i);
				Video video = Video.getVideoDataFromJSONObject(context, videoDictionary);
				
				if (i == 0 && self.requestLoopIndex == 0) {
					topVideo = video;
				} else {
					dataList.add(video);
				}
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			
		}
		
	}
	
	public void setupList() {
		
		Log.i("dataList", "" + dataList);
		
		listView = (BounceListView) rootView.findViewById(R.id.listView);
//		listView.setScrollContainer(false);
        listViewAdapter = new ListAdapter(context, dataList);
        listView.setAdapter(listViewAdapter);
        
        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {   
                view.setSelected(true); // <== Will cause the highlight to remain
                
                Object data = dataList.get(position);
                
                if (data instanceof Video) {
                	Video video = (Video) data;
                	
                	if (video != null) {
                		if (getActivity() instanceof MainActivity) {
                    		PlayerFragment fragment = new PlayerFragment(video, section, pickVideosSuggestions(5, video.getYoutubeVideoIdentifier()));
        					
        					if (fragment != null) {
        						MainActivity mainActivity = (MainActivity) getActivity();
        						mainActivity.switchContent(fragment, true);
        					}
        					
        				}
                	}
                	
                }
            }
        });
	}
	
	public void setupAdMobBanner() {
		
		// I keep getting the error 'The Google Play services resources were not found. 
		// Check your project configuration to ensure that the resources are included.'
		// https://developers.google.com/mobile-ads-sdk/kb/?hl=it#resourcesnotfound
		
		RelativeLayout.LayoutParams adViewLayoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		adViewLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
		
		adView = new AdView(context);
		adView.setLayoutParams(adViewLayoutParams);
	    adView.setAdUnitId(getResources().getString(R.string.googleAdMobHomeBannerViewBlockIdentifier));
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
	    	context.trackView(section.name);
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
