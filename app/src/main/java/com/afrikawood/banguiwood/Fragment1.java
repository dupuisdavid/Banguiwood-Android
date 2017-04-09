package com.afrikawood.banguiwood;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.afrikawood.banguiwood.tools.BaseFragment;
import com.afrikawood.banguiwood.tools.CustomActionBarButtonConfiguration;

public class Fragment1 extends BaseFragment {

	private FragmentActivity context;
	private View rootView;
	
	public Fragment1() {
		
		Boolean backButton = false;
		
		if (backButton) {
			setActionBarLeftButtonType(ButtonType.BACK);
		} else {
			CustomActionBarButtonConfiguration customActionBarButtonConfiguration = new CustomActionBarButtonConfiguration();
			customActionBarButtonConfiguration.setTitle("Infos");
			customActionBarButtonConfiguration.setOnClickRunnable(new Runnable() {
				@Override
				public void run() {
					Log.i("INFOS", "CLICK");
					
				}
			});
			
			setActionBarCustomLeftButtonConfiguration(customActionBarButtonConfiguration);
		}

		setActionBarRightButtonType(ButtonType.MENU);
	}
	
	@Override
    public void onAttach(Activity activity) {
        if (activity instanceof FragmentActivity) {
        	context = (FragmentActivity) activity;
        }

        super.onAttach(activity);
    }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment1, container, false);

		setupSwitchFragmentButton();
		setupBackButton();
		
		return rootView;
	}
	
	public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
	
	@Override
	public void onStart(){
	    super.onStart();
	    Log.i("" + this.getClass(), "onStart");
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
	
	private void setupSwitchFragmentButton() {
		Button button = (Button) rootView.findViewById(R.id.switchFragmentButton);
		
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (getActivity() == null)
					return;

				if (getActivity() instanceof MainActivity) {
					Fragment fragment = new PlayerFragment(null, null, null);
					
					if (fragment != null) {
						MainActivity mainActivity = (MainActivity) getActivity();
						mainActivity.switchContent(fragment, true);
					}
					
				}
				
			}
		});
	}

	private void setupBackButton() {
		Button button = (Button) rootView.findViewById(R.id.backButton);
		
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				context.getSupportFragmentManager().popBackStack();
				
			}
		});
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

}
