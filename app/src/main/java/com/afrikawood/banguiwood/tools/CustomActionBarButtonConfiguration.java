package com.afrikawood.banguiwood.tools;

public class CustomActionBarButtonConfiguration {
	
	private String title;
	private int drawableResId;
	private Runnable onClickRunnable;
	
	public int getDrawableResId() {
		return drawableResId;
	}

	public String getTitle() {
		return title;
	}
	
	public Runnable getOnClickRunnable() {
		return onClickRunnable;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public void setDrawableResId(int drawableResId) {
		this.drawableResId = drawableResId;
	}
	
	public void setOnClickRunnable(Runnable onClickRunnable) {
		this.onClickRunnable = onClickRunnable;
	}

	public CustomActionBarButtonConfiguration() {
		this.title = "";
		this.drawableResId = 0;
		this.onClickRunnable = null;
	}

	public CustomActionBarButtonConfiguration(String title, int drawableResId, Runnable onClickRunnable) {
		super();
		this.title = title;
		this.drawableResId = drawableResId;
		this.onClickRunnable = onClickRunnable;
	}
	

}
