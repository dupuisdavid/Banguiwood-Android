package com.afrikawood.banguiwood;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.afrikawood.banguiwood.business.Section;
import com.afrikawood.banguiwood.business.SectionPlaylist;
import com.afrikawood.banguiwood.business.Video;
import com.afrikawood.banguiwood.tools.AlertDialogBuilder;
import com.afrikawood.banguiwood.tools.BaseFragment;
import com.afrikawood.banguiwood.tools.BounceListView;
import com.afrikawood.banguiwood.tools.HttpRestClient;
import com.afrikawood.banguiwood.tools.ListAdapter;
import com.afrikawood.banguiwood.utils.AnimationUtilities;
import com.afrikawood.banguiwood.utils.Network;
import com.afrikawood.banguiwood.utils.StringUtilities;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.loopj.android.http.JsonHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

public class PlaylistFragment extends BaseFragment {

	private PlaylistFragment self = this;
	private MainActivity context;
	private FrameLayout rootView;
	private SectionPlaylist section;
	private String breadCrumbsString;
	private TextView breadCrumbsTextView;
	private ProgressBar loadingProgressBar;
	private AdView adView;
	private int requestLoopIndex;
	
	private BounceListView listView;
	private ListAdapter listViewAdapter;
	private ArrayList<Object> dataList;
	
	public PlaylistFragment(SectionPlaylist section, String breadCrumbsString, Boolean forceBackButton) {
		
		this.section = section;
		this.breadCrumbsString = breadCrumbsString;
		
		setActionBarLeftButtonType(forceBackButton ? ButtonType.BACK : ButtonType.NONE);
		setActionBarRightButtonType(ButtonType.MENU);
		
	}
	
	public PlaylistFragment() {
		
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
		rootView = (FrameLayout) inflater.inflate(R.layout.playlist_fragment, container, false);
		rootView.setAnimationCacheEnabled(true);
		rootView.setDrawingCacheEnabled(true);
		
		breadCrumbsTextView = (TextView) rootView.findViewById(R.id.breadCrumbsTextView);
		breadCrumbsTextView.setText(breadCrumbsString);
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
								
								AnimationUtilities.fadeOut(loadingProgressBar, null);
								AnimationUtilities.fadeIn(listView, null);
								
								if (dataList.size() > 0) {
									listViewAdapter.notifyDataSetChanged();
								}
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
	
	public void setupList() {
		
		Log.i("dataList", "" + dataList);
		
		listView = (BounceListView) rootView.findViewById(R.id.listView);
        listViewAdapter = new ListAdapter(context, dataList);
        listView.setAdapter(listViewAdapter);
        
        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {   
                view.setSelected(true); // <== Will cause the highlight to remain
                
                Object data = dataList.get(position);
                
                if (data instanceof Section) {
                	SectionPlaylist sectionPlaylist = (SectionPlaylist) data;
                	String breadCrumbsString = String.format(context.getResources().getString(R.string.textBreadCrumbs), self.breadCrumbsString, StringUtilities.purgeUnwantedSpaceInText(sectionPlaylist.getName()));
                	
                	if (sectionPlaylist != null) {
	                	if (getActivity() instanceof MainActivity) {
	                		PlaylistFragment fragment  = new PlaylistFragment(sectionPlaylist, String.format(Locale.FRENCH, breadCrumbsString, self.section.getName(), sectionPlaylist.getName()), true);
	    					
	    					if (fragment != null) {
	    						MainActivity mainActivity = (MainActivity) getActivity();
	    						mainActivity.switchContent(fragment, true);
	    					}
	    					
	    				}
                	}
                	
                } else if (data instanceof Video) {
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
	    adView.setAdUnitId(getResources().getString(R.string.googleAdMobPlaylistViewBlockIdentifier));
	    adView.setAdSize(AdSize.BANNER);
	    
	    RelativeLayout adWrapperLayout = (RelativeLayout) rootView.findViewById(R.id.adWrapperLayout);
	    adWrapperLayout.addView(adView);

	    AdRequest adRequest = new AdRequest.Builder().build();
	    adView.loadAd(adRequest);
	
	}
	
	private void requestForSectionPlayListItems(String playlistIdentifier, Runnable successRunnable, Runnable failureRunnable) {
		requestForSectionPlayListItems(playlistIdentifier, "", successRunnable, failureRunnable);
	}
	
	private void requestForSectionPlayListItems(final String playlistIdentifier, String nextPageToken, final Runnable successRunnable, final Runnable failureRunnable) {
		
		if (!Network.networkIsAvailable(context)) {
			return;
		}
		
		if (!playlistIdentifier.equals("")) {
			
			String unformattedURL = context.getResources().getString(R.string.youtubeApiPlaylistItemsServiceURL);
			String youtubeBrowserApiKey = context.getResources().getString(R.string.youtubeBrowserApiKey);
				
			String url = String.format(unformattedURL, playlistIdentifier, youtubeBrowserApiKey, 50, (!nextPageToken.equals("") ? "&pageToken=" + nextPageToken : ""));
			Log.i("URL", "" + url);
			
			HttpRestClient.get(url, null, new JsonHttpResponseHandler() {
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
							
							if (nextPageToken != null && !nextPageToken.equals("")) {
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
			
			
		} else {
			
			self.parseSectionsPlayList(new JSONArray());
			
			if (successRunnable != null) {
            	successRunnable.run();
            }
		}
		
		
	}
	
	private void parseSectionsPlayList(JSONArray data) {
		
		ArrayList<Section> subSections = null;
		
		if (requestLoopIndex == 0) {
			if (section != null) {
				subSections = section.getSections();
				
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
				dataList.add(video);
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			
		}
		
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
	
	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
	
	@Override
	public void onStart(){
		super.onStart();
	    Log.i("" + this.getClass(), "onStart");
	    
	    if (section != null && !section.getName().equals("")) {
	    	context.trackView(section.getName());
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
