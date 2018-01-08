package com.afrikawood.banguiwood;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

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
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import cz.msebera.android.httpclient.Header;

public class HomeFragment extends BaseFragment {

    private static final String LOG_TAG = HomeFragment.class.getSimpleName();

	private View rootView;
	private Video topVideo;
	private ScrollView scrollView;
	private RelativeLayout videoPlayerWrapperView;
	private ProgressBar progressBar;
	private int requestLoopIndex;
	
	private BounceListView listView;
	private ListAdapter listViewAdapter;
	private ArrayList<Object> dataList;
	
	private HomeFragmentDelegate delegate;
	public void setDelegate(HomeFragmentDelegate delegate) {
		this.delegate = delegate;
	}
	
	// ********************************************************************
	// INTERFACES
	// ********************************************************************

    interface HomeFragmentDelegate {
		void requestForSectionPlayListDidFinish(HomeFragment fragment);
	}

    public HomeFragment() {}

    private static final String SECTION_ARG_PARAM = "section";

    private SectionPlaylist section;

    public static HomeFragment newInstance(SectionPlaylist section) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putParcelable(SECTION_ARG_PARAM, section);
        fragment.setArguments(args);
        return fragment;
    }

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            section = getArguments().getParcelable(SECTION_ARG_PARAM);
        }

        CustomActionBarButtonConfiguration config = new CustomActionBarButtonConfiguration();
        config.setDrawableResId(R.drawable.info_button);
        config.setOnClickRunnable(new Runnable() {
            @Override
            public void run() {
                Activity activity = getActivity();
                Intent intent = new Intent(activity, InfoActivity.class);
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.activity_open_translate_y, R.anim.activity_close_scale);
            }
        });

        setActionBarCustomLeftButtonConfiguration(config);
        setActionBarRightButtonType(ButtonType.MENU);
    }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.home_fragment, container, false);
		scrollView = rootView.findViewById(R.id.scrollView);

        progressBar = rootView.findViewById(R.id.progressbar);
		
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
										AnimationUtilities.fadeOut(progressBar, null);
										AnimationUtilities.fadeIn(scrollView, new Runnable() {
											@Override
											public void run() {
												if (delegate != null) {
													delegate.requestForSectionPlayListDidFinish(HomeFragment.this);
												}
											}
										});
									}
								}, 150);
							}
							
						}, new Runnable() {
							@Override
							public void run() {
								
								final String textAlertDialogViewTitle = getContext().getResources().getString(R.string.textAlertDialogViewTitle);
								final String textServerConnectionImpossible = getContext().getResources().getString(R.string.textServerConnectionImpossible);
								
								AnimationUtilities.fadeOut(progressBar, new Runnable() {
									@Override
									public void run() {
										AlertDialogBuilder.build(getContext(), textAlertDialogViewTitle, textServerConnectionImpossible);
									}
								});
								
							}
						});
						
					}
				}, 150);
			}
		}

		return rootView;
	}
	
	private ArrayList<Video> pickVideosSuggestions(String youtubeVideoIdentifier) {

        int pickNumber = 5;
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
	        
			Log.i(LOG_TAG, "LOOP : " + count);
			
	        count++;
	    }
	    
	    
	    
	    return new ArrayList<>(videosSuggestions.values());
	}
	
	public void setupYoutubePlayerFragment() {
		
		if (videoPlayerWrapperView == null) {
			videoPlayerWrapperView = rootView.findViewById(R.id.videoPlayerWrapperView);
		}
		
		if (topVideo != null) {
			
			final String videoIdentifier = topVideo.youtubeVideoIdentifier;
			
			if (!videoIdentifier.equals("")) {
                ((MainActivity) getActivity()).setupYoutubePlayerFragment(videoIdentifier, videoPlayerWrapperView);
			}
			
		} else {
			videoPlayerWrapperView.setVisibility(View.GONE);
		}
		
	}
	
	private void updateVideoTextViews() {
		
		CustomFontTextView videoTitleTextView = rootView.findViewById(R.id.videoTitleTextView);
		CustomFontTextView videoPublicationDateTextView = rootView.findViewById(R.id.videoPublicationDateTextView);
		
		if (topVideo == null) {
			videoTitleTextView.setVisibility(View.GONE);
			videoPublicationDateTextView.setVisibility(View.GONE);
		}
		
		
		videoTitleTextView.setText(StringUtilities.purgeUnwantedSpaceInText(topVideo.title));
		videoPublicationDateTextView.setText(String.format(getContext().getResources().getString(R.string.textPublishedAt), DateUtilities.convertDateToString(topVideo.publicationDate, getContext().getResources().getString(R.string.displayedPublicationDateFormat))));
		
	}
	
	private void updateListViewHeight() {
		
		// height : 1450

		
		int height = (int) (50 * DisplayProperties.getInstance(getActivity()).getPixelDensity()) * dataList.size();
		
		LinearLayout.LayoutParams listViewLayoutParams = (LinearLayout.LayoutParams) listView.getLayoutParams();
		listViewLayoutParams.height = height;
		listView.setLayoutParams(listViewLayoutParams);
		listView.requestLayout();
		
		Log.i(LOG_TAG, "Height: " + height);
		
	}
	
	private void requestForSectionPlayListItems(String playlistIdentifier, Runnable successRunnable, Runnable failureRunnable) {
		requestForSectionPlayListItems(playlistIdentifier, "", successRunnable, failureRunnable);
	}
	
	private void requestForSectionPlayListItems(final String playlistIdentifier, String nextPageToken, final Runnable successRunnable, final Runnable failureRunnable) {
		
		if (!Network.networkIsAvailable(getContext())) {
			return;
		}
		
		if (playlistIdentifier.equals("")) {
			return;
		}
		
		final int nbFrontPageVideo = 30;
		
		String unformattedURL = getContext().getResources().getString(R.string.youtubeApiPlaylistItemsServiceURL);
		String youtubeBrowserApiKey = getContext().getResources().getString(R.string.youtubeBrowserApiKey);
			
		String url = String.format(unformattedURL, playlistIdentifier, youtubeBrowserApiKey, nbFrontPageVideo, (!nextPageToken.equals("") ? "&pageToken=" + nextPageToken : ""));
		Log.i(LOG_TAG, "Url: " + url);
		
		HttpRestClient.get(url, null, new JsonHttpResponseHandler() {
            @SuppressWarnings("unused")
			@Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            	
            	if (response != null) {
            		
            		try {
            			
						JSONArray videos = response.getJSONArray("items");
						HomeFragment.this.parseSectionsPlayList(videos);
						
						String nextPageToken;
						if (response.has("nextPageToken")) {
							nextPageToken = response.getString("nextPageToken");
						}

						if (nbFrontPageVideo == 0 && nextPageToken != null && !nextPageToken.equals("")) {
							requestLoopIndex = requestLoopIndex + 1;
                            HomeFragment.this.requestForSectionPlayListItems(playlistIdentifier, nextPageToken, successRunnable, failureRunnable);

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
		if (requestLoopIndex == 0) {
			if (section != null) {
                dataList.addAll(section.sections);
			}
		}
		
		for (int i = 0; i < data.length(); i++) {
			
			try {
				
				JSONObject videoDictionary = (JSONObject) data.get(i);
				Video video = Video.getVideoDataFromJSONObject(getContext(), videoDictionary);
				
				if (i == 0 && HomeFragment.this.requestLoopIndex == 0) {
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
		
		Log.i(LOG_TAG, "Datalist: " + dataList);
		
		listView = rootView.findViewById(R.id.listView);
//		listView.setScrollContainer(false);
        listViewAdapter = new ListAdapter(getContext(), dataList);
        listView.setAdapter(listViewAdapter);
        
        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {   
                view.setSelected(true); // <== Will cause the highlight to remain
                
                Object data = dataList.get(position);
                
                if (data instanceof Video) {
                	Video video = (Video) data;

                    if (getActivity() instanceof MainActivity) {
                        PlayerFragment fragment = new PlayerFragment(video, section, pickVideosSuggestions(video.youtubeVideoIdentifier));
                        MainActivity mainActivity = (MainActivity) getActivity();
                        mainActivity.switchContent(fragment, true);
                    }

                }
            }
        });
	}
	
	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
	
	@Override
	public void onStart(){
	    super.onStart();
	    Log.i(LOG_TAG, "onStart");
	    
	    if (section != null && !section.name.equals("")) {
            ((MainActivity) getActivity()).trackView(section.name);
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
