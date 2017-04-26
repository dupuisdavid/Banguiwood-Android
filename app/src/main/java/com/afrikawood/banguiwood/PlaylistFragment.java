package com.afrikawood.banguiwood;

import android.content.Context;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

import cz.msebera.android.httpclient.Header;

public class PlaylistFragment extends BaseFragment {

	private FrameLayout rootView;

	private SectionPlaylist section;
    private String breadCrumbs;
    private boolean forceBackButton;

	private ProgressBar loadingProgressBar;
	private int requestLoopIndex;
	
	private BounceListView listView;
	private ListAdapter listViewAdapter;
	private ArrayList<Object> dataList;

    private static final String ARG_SECTION = "section";
    private static final String ARG_BREADCRUMBS = "section";
    private static final String ARG_FORCE_BACK_BUTTON = "forceBackButton";

    public static PlaylistFragment newInstance(Section section, String breadCrumbs, Boolean forceBackButton) {
        PlaylistFragment fragment = new PlaylistFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_SECTION, section);
        args.putString(ARG_BREADCRUMBS, breadCrumbs);
        args.putBoolean(ARG_FORCE_BACK_BUTTON, forceBackButton);
        fragment.setArguments(args);

        return fragment;
    }
	
	public PlaylistFragment() {}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            section = getArguments().getParcelable(ARG_SECTION);
            breadCrumbs = getArguments().getString(ARG_BREADCRUMBS);
            forceBackButton = getArguments().getBoolean(ARG_FORCE_BACK_BUTTON);
        }

        setActionBarLeftButtonType(forceBackButton ? ButtonType.BACK : ButtonType.NONE);
        setActionBarRightButtonType(ButtonType.MENU);
    }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = (FrameLayout) inflater.inflate(R.layout.playlist_fragment, container, false);
		rootView.setDrawingCacheEnabled(true);

		TextView breadCrumbsTextView = (TextView) rootView.findViewById(R.id.breadCrumbsTextView);
		breadCrumbsTextView.setText(breadCrumbs);
		loadingProgressBar = (ProgressBar) rootView.findViewById(R.id.loadingProgressBar);
		
		dataList = new ArrayList<>();
		
		setupList();
		
		if (Network.networkIsAvailable(getContext())) {
			
			if (section != null) {
				
				new Handler().postDelayed(new Runnable() {
					
					@Override
					public void run() {
						requestForSectionPlayListItems(section.youtubePlaylistIdentifier, new Runnable() {
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
								
								final String textAlertDialogViewTitle = getContext().getResources().getString(R.string.textAlertDialogViewTitle);
								final String textServerConnectionImpossible = getContext().getResources().getString(R.string.textServerConnectionImpossible);
								
								AnimationUtilities.fadeOut(loadingProgressBar, new Runnable() {
									@Override
									public void run() {
										AlertDialogBuilder.build(getContext(), textAlertDialogViewTitle, textServerConnectionImpossible);
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
        listViewAdapter = new ListAdapter(getContext(), dataList);
        listView.setAdapter(listViewAdapter);
        
        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {   
                view.setSelected(true); // <== Will cause the highlight to remain
                
                Object data = dataList.get(position);
                
                if (data instanceof Section) {
                	SectionPlaylist sectionPlaylist = (SectionPlaylist) data;
                	String breadCrumbsString = String.format(getContext().getResources().getString(R.string.textBreadCrumbs), PlaylistFragment.this.breadCrumbs, StringUtilities.purgeUnwantedSpaceInText(sectionPlaylist.name));

					if (getActivity() instanceof MainActivity) {
                        PlaylistFragment fragment  = PlaylistFragment.newInstance(sectionPlaylist, String.format(Locale.FRENCH, breadCrumbsString, PlaylistFragment.this.section.name, sectionPlaylist.name), true);

						MainActivity mainActivity = (MainActivity) getActivity();
						mainActivity.switchContent(fragment, true);

					}
                	
                } else if (data instanceof Video) {
                	Video video = (Video) data;

					if (getActivity() instanceof MainActivity) {
						PlayerFragment fragment = new PlayerFragment(video, section, pickVideosSuggestions(5, video.youtubeVideoIdentifier));
						MainActivity mainActivity = (MainActivity) getActivity();
						mainActivity.switchContent(fragment, true);
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

		AdView adView = new AdView(getContext());
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
		
		if (!Network.networkIsAvailable(getContext())) {
			return;
		}
		
		if (!playlistIdentifier.equals("")) {
			
			String unformattedURL = getContext().getResources().getString(R.string.youtubeApiPlaylistItemsServiceURL);
			String youtubeBrowserApiKey = getContext().getResources().getString(R.string.youtubeBrowserApiKey);
				
			String url = String.format(unformattedURL, playlistIdentifier, youtubeBrowserApiKey, 50, (!nextPageToken.equals("") ? "&pageToken=" + nextPageToken : ""));
			Log.i("URL", "" + url);
			
			HttpRestClient.get(url, null, new JsonHttpResponseHandler() {
	            @Override
	            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
	            	
	            	if (response != null) {
	            		
	            		try {
	            			
							JSONArray videos = response.getJSONArray("items");
							PlaylistFragment.this.parseSectionsPlayList(videos);
							

							String nextPageToken = null;
							if (response.has("nextPageToken")) {
								nextPageToken = response.getString("nextPageToken");
							}
							
							if (nextPageToken != null && !nextPageToken.equals("")) {
								requestLoopIndex = requestLoopIndex + 1;
								PlaylistFragment.this.requestForSectionPlayListItems(playlistIdentifier, nextPageToken, successRunnable, failureRunnable);
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
			
			this.parseSectionsPlayList(new JSONArray());
			
			if (successRunnable != null) {
            	successRunnable.run();
            }
		}
		
		
	}
	
	private void parseSectionsPlayList(JSONArray data) {
		
		ArrayList<Section> subSections;
		
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
				Video video = Video.getVideoDataFromJSONObject(getContext(), videoDictionary);
				dataList.add(video);
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	private ArrayList<Video> pickVideosSuggestions(int pickNumber, String youtubeVideoIdentifier) {
	    
	    int pickNumberLimit = pickNumber > (dataList.size()) ? ((dataList.size()) - 1) : pickNumber;
	    
	    HashMap<String, Video> videosSuggestions = new HashMap<>();
	    
	    int count = 0;
	    
	    while (videosSuggestions.size() < pickNumberLimit) {
	    
	    	int min = 0;
	    	int max = dataList.size() - 1;

	    	Random r = new Random();
	    	int randomIndex = r.nextInt(max - min + 1) + min;
	    	
			
			Object data = dataList.get(randomIndex);
			
			if (data instanceof Video) {
				Video pickVideo = (Video) data;
				String videoKey = pickVideo.youtubeVideoIdentifier;
				
				if (!videosSuggestions.containsKey(videoKey) && !videoKey.equals(youtubeVideoIdentifier)) {
					videosSuggestions.put(videoKey, pickVideo);
				}
				
			}
	        
			Log.i("pickVideosSuggestions", "LOOP : " + count);
			
	        count++;
	    }
	    
	    
	    
	    return new ArrayList<>(videosSuggestions.values());
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
			((MainActivity) getContext()).trackView(section.name);
	    }
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}
	
	@Override
	public void onStop() {
		super.onStop();
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
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
