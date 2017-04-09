package com.afrikawood.banguiwood;

import java.util.ArrayList;

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

import cz.msebera.android.httpclient.Header;

public class ArticlesListFragment extends BaseFragment {

	private ArticlesListFragment self = this;
	private MainActivity context;
	private FrameLayout rootView;
	private SectionArticles section;
	private String breadCrumbsString;
	private TextView breadCrumbsTextView;
	private ProgressBar loadingProgressBar;
	private AdView adView;
	
	private BounceListView listView;
	private ListAdapter listViewAdapter;
	private ArrayList<Object> dataList;
	
	public ArticlesListFragment(SectionArticles section, String breadCrumbsString, Boolean forceBackButton) {
		
		this.section = section;
		this.breadCrumbsString = breadCrumbsString;
		
		setActionBarLeftButtonType(forceBackButton ? ButtonType.BACK : ButtonType.NONE);
		setActionBarRightButtonType(ButtonType.MENU);
		
	}
	
	public ArticlesListFragment() {
		
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
                
                if (data instanceof Article) {
                	Article article = (Article) data;

					if (getActivity() instanceof MainActivity) {
                        ArticleFragment fragment = new ArticleFragment(article, section, true);

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
		
		adView = new AdView(context);
		adView.setLayoutParams(adViewLayoutParams);
	    adView.setAdUnitId(getResources().getString(R.string.googleAdMobArticleListViewBlockIdentifier));
	    adView.setAdSize(AdSize.BANNER);
	    
	    RelativeLayout adWrapperLayout = (RelativeLayout) rootView.findViewById(R.id.adWrapperLayout);
	    adWrapperLayout.addView(adView);

	    AdRequest adRequest = new AdRequest.Builder().build();
	    adView.loadAd(adRequest);
	
	}
	
	private void requestForSectionArticlesItems(final String sectionArticlesUrl, final Runnable successRunnable, final Runnable failureRunnable) {
		
		if (!Network.networkIsAvailable(context)) {
			return;
		}
		
		if (!sectionArticlesUrl.equals("")) {
			
			String url = sectionArticlesUrl;
			Log.i("URL", "" + url);
			
			HttpRestClient.get(url, null, new JsonHttpResponseHandler() {
	            @Override
	            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
	            	if (response != null) {
	            		self.parseSectionArticles(response);
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
			self.parseSectionArticles(new JSONArray());
			if (successRunnable != null) {
            	successRunnable.run();
            }
		}
		
		
	}
	
	private void parseSectionArticles(JSONArray data) {
		
		for (int i=0; i<data.length(); i++) {
	        
	        JSONObject articleDictionary = null;
	        
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
