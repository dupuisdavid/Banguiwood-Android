package com.afrikawood.banguiwood.tools;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.afrikawood.banguiwood.R;
import com.afrikawood.banguiwood.tools.BaseFragment.ButtonType;

public class CustomActionBarView extends RelativeLayout {

	@SuppressWarnings("unused")
	private CustomActionBarView self = this;
	private FragmentActivity context;
	private RelativeLayout view;
	
	public Button backButton;
	public ImageButton customLeftButton;
	public ImageButton slideRightMenuButton;
	public ImageButton customRightButton;
	
	public CustomActionBarView(FragmentActivity context) {
		super(context);
		
		this.context = context;
		this.init();
	}
	
	public CustomActionBarView(Context context) {
		super(context);
	}

	public CustomActionBarView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
	}

	public CustomActionBarView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
	}
	
	private void init() {
	
		RelativeLayout.LayoutParams viewLayoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = (RelativeLayout) inflater.inflate(R.layout.custom_actionbar_view, this, false);
		view.setLayoutParams(viewLayoutParams);
		view.setBackgroundColor(0x00ffcc00);
		
		addView(view);
		
		
		backButton = (Button) view.findViewById(R.id.backButton);
		if (backButton != null) {
			
			backButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					context.getSupportFragmentManager().popBackStack();
				}
			});
		}
		
		slideRightMenuButton = (ImageButton) view.findViewById(R.id.slideRightMenuButton);
		
		customLeftButton = (ImageButton) view.findViewById(R.id.customLeftButton);
		customRightButton = (ImageButton) view.findViewById(R.id.customRightButton);
		
		
	}
	
	public void showButton(final View button) {
		
		if (button == null) {
			return;
		}
		
		button.setVisibility(View.VISIBLE);
		button.animate().alpha(1f).setDuration(350).setListener(new AnimatorListener() {
			@Override
			public void onAnimationStart(Animator animation) {}
			@Override
			public void onAnimationRepeat(Animator animation) {}
			@Override
			public void onAnimationEnd(Animator animation) {}
			@Override
			public void onAnimationCancel(Animator animation) {}
		});
	}
	
	public void hideButton(final View button) {
		
		if (button == null) {
			return;
		}
		
		button.animate().alpha(0f).setDuration(350).setListener(new AnimatorListener() {
			@Override
			public void onAnimationStart(Animator animation) {}
			@Override
			public void onAnimationRepeat(Animator animation) {}
			@Override
			public void onAnimationEnd(Animator animation) {
				button.setVisibility(View.GONE);
			}
			@Override
			public void onAnimationCancel(Animator animation) {}
		});
	}

	public void setupCustomLeftButton(final CustomActionBarButtonConfiguration actionBarCustomLeftButtonConfiguration) {
		
		int drawableResId = actionBarCustomLeftButtonConfiguration.getDrawableResId();
		final Runnable onClickRunnable = actionBarCustomLeftButtonConfiguration.getOnClickRunnable();
		
		if (drawableResId != 0) {
			customLeftButton.setBackground(context.getResources().getDrawable(drawableResId));
		}
		
		if (onClickRunnable != null) {
			customLeftButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					onClickRunnable.run();
				}
			});
		}
		
	}
	
	public void setupCustomRightButton(final CustomActionBarButtonConfiguration actionBarCustomRightButtonConfiguration) {
		
		int drawableResId = actionBarCustomRightButtonConfiguration.getDrawableResId();
		final Runnable onClickRunnable = actionBarCustomRightButtonConfiguration.getOnClickRunnable();
		
		if (drawableResId != 0) {
			customRightButton.setImageDrawable(context.getResources().getDrawable(drawableResId));
		}
		
		if (onClickRunnable != null) {
			customRightButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					onClickRunnable.run();
				}
			});
		}
	}
	
	public void resetCustomLeftButton() {
		customLeftButton.setOnClickListener(null);
	}
	
	public void resetCustomRightButton() {
		customRightButton.setOnClickListener(null);
	}
	
	public void updateActionBarButtonsAccordingToFragment(BaseFragment fragment) {
		
		ButtonType leftActionBarButtonType = fragment.getActionBarLeftButtonType();
		ButtonType rightActionBarButtonType = fragment.getActionBarRightButtonType();
		CustomActionBarButtonConfiguration actionBarCustomLeftButtonConfiguration = fragment.getActionBarCustomLeftButtonConfiguration();
		CustomActionBarButtonConfiguration actionBarCustomRightButtonConfiguration = fragment.getActionBarCustomRightButtonConfiguration();
		
		// LEFT
		if (leftActionBarButtonType != ButtonType.BACK) {
			hideButton(backButton);
			
			if (actionBarCustomLeftButtonConfiguration != null) {
				setupCustomLeftButton(actionBarCustomLeftButtonConfiguration);
				showButton(customLeftButton);
			} else {
				hideButton(customLeftButton);
				resetCustomLeftButton();
			}
			
		} else {
			hideButton(customLeftButton);
			resetCustomLeftButton();
			showButton(backButton);
		}
		
		// RIGHT
		if (rightActionBarButtonType != ButtonType.MENU) {
			hideButton(slideRightMenuButton);
			
			if (actionBarCustomRightButtonConfiguration != null) {
				setupCustomRightButton(actionBarCustomRightButtonConfiguration);
				showButton(customRightButton);
			} else {
				hideButton(customRightButton);
				resetCustomRightButton();
			}
			
		} else {
			hideButton(customRightButton);
			resetCustomRightButton();
			showButton(slideRightMenuButton);
		}
		
	}
	

}
