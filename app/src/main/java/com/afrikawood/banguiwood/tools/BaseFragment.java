package com.afrikawood.banguiwood.tools;

import android.support.v4.app.Fragment;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;

import com.afrikawood.banguiwood.utils.FragmentUtils;

public class BaseFragment extends Fragment {
	
	protected enum ButtonType {
        NONE,
        BACK,
        MENU
    }
	
	private ButtonType actionBarLeftButtonType;
	private ButtonType actionBarRightButtonType;
	private CustomActionBarButtonConfiguration actionBarCustomLeftButtonConfiguration;
	private CustomActionBarButtonConfiguration actionBarCustomRightButtonConfiguration;

	@Override
	public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
//		Log.i("BaseFragment", "" + transit + ", " + enter + ", " + nextAnim);
		
		// Can I add AnimationListenser for Fragment translation
		// http://stackoverflow.com/questions/11545673/can-i-add-animationlistenser-for-fragment-translation
		
		// Very good !!!
		// Pop the fragment backstack without playing the Pop-Animation
		// http://stackoverflow.com/questions/9194311/pop-the-fragment-backstack-without-playing-the-pop-animation
		if (FragmentUtils.sDisableFragmentAnimations) {
	        Animation a = new Animation() {};
	        a.setDuration(0);
	        return a;
	    }
		
		Animation anim = super.onCreateAnimation(transit, enter, nextAnim);
	    
	    if (anim == null && nextAnim != 0) {
	    	anim = AnimationUtils.loadAnimation(getActivity(), nextAnim);
	    }
	    
//	    Log.i("BaseFragment", "anim A : " + anim);
	    
	    if (anim != null) {
//	    	Log.i("BaseFragment", "anim B : " + anim);
	    	
	    	anim.setAnimationListener(new AnimationListener() {
	            @Override
	            public void onAnimationStart(Animation animation) {
//	            	Log.i("BaseFragment", "onAnimationStart");
	            }
	            
	            @Override
	            public void onAnimationRepeat(Animation animation) {

	            }
	            
	            @Override
	            public void onAnimationEnd(Animation animation) {
//	            	Log.i("BaseFragment", "onAnimationEnd");
	            }
	        });
	    
	    	return anim;
	    }
	   
	    return null;
	}

	public ButtonType getActionBarLeftButtonType() {
		return actionBarLeftButtonType;
	}

	public ButtonType getActionBarRightButtonType() {
		return actionBarRightButtonType;
	}

	public void setActionBarLeftButtonType(ButtonType actionBarLeftButtonType) {
		this.actionBarLeftButtonType = actionBarLeftButtonType;
	}

	public void setActionBarRightButtonType(ButtonType actionBarRightButtonType) {
		this.actionBarRightButtonType = actionBarRightButtonType;
	}

	public CustomActionBarButtonConfiguration getActionBarCustomLeftButtonConfiguration() {
		return actionBarCustomLeftButtonConfiguration;
	}

	public void setActionBarCustomLeftButtonConfiguration(CustomActionBarButtonConfiguration actionBarCustomLeftButtonConfiguration) {
		this.actionBarCustomLeftButtonConfiguration = actionBarCustomLeftButtonConfiguration;
	}

	public CustomActionBarButtonConfiguration getActionBarCustomRightButtonConfiguration() {
		return actionBarCustomRightButtonConfiguration;
	}

	public void setActionBarCustomRightButtonConfiguration(CustomActionBarButtonConfiguration actionBarCustomRightButtonConfiguration) {
		this.actionBarCustomRightButtonConfiguration = actionBarCustomRightButtonConfiguration;
	}
}
