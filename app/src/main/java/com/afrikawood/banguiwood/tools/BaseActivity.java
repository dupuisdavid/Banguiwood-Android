package com.afrikawood.banguiwood.tools;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.TextView;

import com.afrikawood.banguiwood.MenuListFragment;
import com.afrikawood.banguiwood.R;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

public class BaseActivity extends SlidingFragmentActivity {

	private int mTitleRes;
	private ListFragment mFrag;
	private CustomActionBarView customActionBarView;

	public CustomActionBarView getCustomActionBarView() {
		return customActionBarView;
	}

	public void setCustomActionBarView(CustomActionBarView customActionBarView) {
		this.customActionBarView = customActionBarView;
	}

	public BaseActivity(int titleRes) {
		mTitleRes = titleRes;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
//		getSupportActionBar().hide();

		setTitle(mTitleRes);

		// set the Behind View
		setBehindContentView(R.layout.menu_frame);
		
		if (savedInstanceState == null) {
			FragmentTransaction t = getSupportFragmentManager().beginTransaction();
			mFrag = new MenuListFragment(this);
			t.replace(R.id.menu_frame, mFrag);
			t.commit();
		} else {
			mFrag = (ListFragment) this.getSupportFragmentManager().findFragmentById(R.id.menu_frame);
		}
		
		String actionBarHexColor = getResources().getString(R.string.actionBarHexColor);
		int color = (int) Long.parseLong(actionBarHexColor, 16);
		
		

		// customize the SlidingMenu
		SlidingMenu sm = getSlidingMenu();
		sm.setMode(SlidingMenu.RIGHT);
		sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		sm.setShadowWidthRes(R.dimen.shadow_width);
		sm.setShadowDrawable(R.drawable.shadowright);
		sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		sm.setFadeDegree(0.35f);
		
		
		setTheme(R.style.BanguiwoodTheme);
		
/*
		int themeResId = 0; // 0 = not set
		
		try  {
		    String packageName = getClass().getPackage().getName();
		    PackageInfo packageInfo = getPackageManager().getPackageInfo(packageName, PackageManager.GET_META_DATA);
		    themeResId = packageInfo.applicationInfo.theme;
		    Log.i("Theme", "" + getResources().getResourceEntryName(themeResId));
		    
		} catch (Exception e) { 
		    e.printStackTrace();
		}
*/
		
		
		ActionBar actionBar = getSupportActionBar();
				
		if (actionBar == null) {
			return;
		}
		
		actionBar.setDisplayHomeAsUpEnabled(false);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayUseLogoEnabled(false);
		actionBar.setIcon(android.R.color.transparent);
		actionBar.setLogo(null);
		actionBar.setBackgroundDrawable(new ColorDrawable(color));
		
		actionBar.setDisplayShowCustomEnabled(true);
		

		
		setupActionBarCustomView(actionBar);
//		actionBar.hide();


		
		// android hide action bar during startup
		// https://www.google.fr/search?q=android+android%3AshowAsAction&oq=android+android%3AshowAsAction&aqs=chrome..69i57.1830j0j7&sourceid=chrome&es_sm=119&ie=UTF-8#safe=off&q=android+hide+action+bar+during+startup

	}
	
	private void setupActionBarCustomView(ActionBar actionBar) {
		
		ActionBar.LayoutParams customActionBarViewLayout = new ActionBar.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		
		customActionBarView = new CustomActionBarView(this);
		actionBar.setCustomView(customActionBarView, customActionBarViewLayout);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		
	}
	
	protected void setContent(TextView view) {
        
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
//		getSupportMenuInflater().inflate(R.menu.main, menu);
		return true;
	} 

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.slideRightMenuButtonItem:
				toggle();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
