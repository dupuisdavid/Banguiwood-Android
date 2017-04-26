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

import com.afrikawood.banguiwood.business.Article;
import com.afrikawood.banguiwood.business.SectionArticles;
import com.afrikawood.banguiwood.tools.AlertDialogBuilder;
import com.afrikawood.banguiwood.tools.BaseFragment;
import com.afrikawood.banguiwood.tools.BounceListView;
import com.afrikawood.banguiwood.tools.HttpRestClient;
import com.afrikawood.banguiwood.tools.ListAdapter;
import com.afrikawood.banguiwood.utils.AnimationUtilities;
import com.afrikawood.banguiwood.utils.Network;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.util.TextUtils;

public class ArticlesListFragment extends BaseFragment {

	private SectionArticles section;
	private String breadCrumbsString;
    private boolean forceBackButton;

    private FrameLayout rootView;
	private ProgressBar loadingProgressBar;

	private BounceListView listView;
	private ListAdapter listViewAdapter;
	private ArrayList<Object> dataList;

    private static final String ARG_SECTION = "section";
    private static final String ARG_BREAD_CRUMBS = "breadCrumbs";
    private static final String ARG_FORCE_BACK_BUTTON = "forceBackButton";

    public static ArticlesListFragment newInstance(SectionArticles section, String breadCrumbsString, boolean forceBackButton) {
        ArticlesListFragment fragment = new ArticlesListFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_SECTION, section);
        args.putString(ARG_BREAD_CRUMBS, breadCrumbsString);
        args.putBoolean(ARG_FORCE_BACK_BUTTON, forceBackButton);
        fragment.setArguments(args);
        return fragment;
    }

    public ArticlesListFragment() {}
	
	@Override
    public void onAttach(Context context) {
		super.onAttach(context);
    }
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            section = getArguments().getParcelable(ARG_SECTION);
            breadCrumbsString = getArguments().getString(ARG_BREAD_CRUMBS);
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
		breadCrumbsTextView.setText(breadCrumbsString);
		loadingProgressBar = (ProgressBar) rootView.findViewById(R.id.loadingProgressBar);
		
		dataList = new ArrayList<>();
		
		setupList();
		
		if (Network.networkIsAvailable(getContext())) {
			
			if (section != null) {
				
				new Handler().postDelayed(new Runnable() {
					
					@Override
					public void run() {
						requestForSectionArticlesItems(section.getUrl(), new Runnable() {
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
                view.setSelected(true);
                
                Object data = dataList.get(position);
                
                if (data instanceof Article) {
                	Article article = (Article) data;

					if (getActivity() instanceof MainActivity) {
                        ArticleFragment fragment = ArticleFragment.newInstance(article, section, true);
                        if (fragment != null) {
                            MainActivity mainActivity = (MainActivity) getActivity();
                            mainActivity.switchContent(fragment, true);
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

		AdView adView = new AdView(getContext());
		adView.setLayoutParams(adViewLayoutParams);
	    adView.setAdUnitId(getResources().getString(R.string.googleAdMobArticleListViewBlockIdentifier));
	    adView.setAdSize(AdSize.BANNER);
	    
	    RelativeLayout adWrapperLayout = (RelativeLayout) rootView.findViewById(R.id.adWrapperLayout);
	    adWrapperLayout.addView(adView);

	    AdRequest adRequest = new AdRequest.Builder().build();
	    adView.loadAd(adRequest);
	
	}
	
	private void requestForSectionArticlesItems(final String sectionArticlesUrl, final Runnable successRunnable, final Runnable failureRunnable) {
		
		if (!Network.networkIsAvailable(getContext())) {
			return;
		}
		
		if (!sectionArticlesUrl.equals("")) {

			Log.i("URL", sectionArticlesUrl);
			
			HttpRestClient.get(sectionArticlesUrl, null, new JsonHttpResponseHandler() {
	            @Override
	            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
	            	if (response != null) {
	            		ArticlesListFragment.this.parseSectionArticles(response);
						if (successRunnable != null) {
		                	successRunnable.run();
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
			this.parseSectionArticles(new JSONArray());
			if (successRunnable != null) {
            	successRunnable.run();
            }
		}
		
		
	}
	
	private void parseSectionArticles(JSONArray data) {
		
		for (int i=0; i<data.length(); i++) {
	        
	        JSONObject articleDictionary;
	        
			try {
				
				articleDictionary = (JSONObject) data.get(i);
				Article article = Article.getArticleDataFromJSONObject(articleDictionary);
		        
		        dataList.add(article);
		        
			} catch (JSONException e) {
				e.printStackTrace();
			}
	    }
		
	}
	
	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
	
	@Override
	public void onStart(){
		super.onStart();
	    if (section != null && !TextUtils.isEmpty(section.name)) {
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
