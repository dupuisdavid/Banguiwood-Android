package com.afrikawood.banguiwood.tools;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afrikawood.banguiwood.R;
import com.afrikawood.banguiwood.business.Article;
import com.afrikawood.banguiwood.business.SectionPlaylist;
import com.afrikawood.banguiwood.business.Video;
import com.afrikawood.banguiwood.utils.AnimationUtilities;
import com.afrikawood.banguiwood.utils.DateUtilities;
import com.afrikawood.banguiwood.utils.StringUtilities;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;

public class ListAdapter extends BaseAdapter {

	private final Context context;
	private ArrayList<Object> data;
	private LayoutInflater layoutInflater;
	private Boolean imageViewFadeRefreshEnable = true;
	
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	
	public ArrayList<Object> getData() {
		return data;
	}

	public void setData(ArrayList<Object> data) {
		this.data = data;
	}

	public ListAdapter(Context context, ArrayList<Object> data) {
		this.context = context;
		this.layoutInflater = LayoutInflater.from(context);
		this.data = data;
		
		// https://github.com/nostra13/Android-Universal-Image-Loader
		
		this.options = new DisplayImageOptions.Builder()
		.cacheInMemory(false)
		.cacheOnDisk(false)
		.considerExifParams(false)
		.build();
		
		this.imageLoader = ImageLoader.getInstance();
	}
	
	public Boolean getImageViewFadeRefreshEnable() {
		return imageViewFadeRefreshEnable;
	}

	public void setImageViewFadeRefreshEnable(Boolean imageViewFadeRefreshEnable) {
		this.imageViewFadeRefreshEnable = imageViewFadeRefreshEnable;
	}

	@Override
	public int getCount() {
		return this.data.size();
	}

	@Override
	public Object getItem(int index) {
		return this.data.get(index);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	public class ViewHolder {
		ImageView imageView;
		TextView titleTextView;
		TextView categoryNameTextView;
		TextView publicationDateTextView;
		Object data;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.playlist_item, parent, false);
			holder = new ViewHolder();
			holder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
			holder.titleTextView = (TextView) convertView.findViewById(R.id.titleTextView);
			holder.publicationDateTextView = (TextView) convertView.findViewById(R.id.publicationDateTextView);
		
			convertView.setTag(holder);
			
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		if (position % 2 == 0) {
			convertView.setBackgroundColor(Color.parseColor(context.getResources().getString(R.string.listItemBackgroundColor)));
			holder.publicationDateTextView.setTextColor(Color.parseColor(context.getResources().getString(R.string.listItemDateLabelColor)));
		} else {
			convertView.setBackgroundColor(Color.parseColor(context.getResources().getString(R.string.listItemBackgroundAlternateColor)));
			holder.publicationDateTextView.setTextColor(Color.parseColor(context.getResources().getString(R.string.listItemDateLabelAlternateColor)));
		}
		

		
		// GET DATA
		
		Object data = this.data.get(position);
		
		// VIDEO ROW CASE
		// //////////////////////////////////////
		
		if (data instanceof Video) {
			Video video = (Video) data;
			
			Boolean refreshNeeded = !data.equals(holder.data);
			
//			Log.i("HOLDER", "" + video + ", " + (holder.news != null ? holder.news : "null") + ", refresh needed : " + refreshNeeded);
			
			if (refreshNeeded) {
				
				holder.data = video;
//				Log.i("position", "position : " + position);
				
				updateVideoOrArticleHolder(holder, video.getTitle(), video.getHighThumbnailURL(), DateUtilities.convertDateToString(video.getPublicationDate(), context.getResources().getString(R.string.displayedPublicationDateFormat)));
				
			}
		
		// ARTICLE ROW CASE
		// //////////////////////////////////////

		} else if (data instanceof Article) {
			Article article = (Article) data;
			
			Boolean refreshNeeded = !data.equals(holder.data);
			
//			Log.i("HOLDER", "" + video + ", " + (holder.news != null ? holder.news : "null") + ", refresh needed : " + refreshNeeded);
			
			if (refreshNeeded) {
				
				holder.data = article;
//				Log.i("position", "position : " + position);
				
				updateVideoOrArticleHolder(holder, article.title, article.thumbnailUrl, "");
				
			}
		
		// SECTION_PLAYLIST ROW CASE
		// //////////////////////////////////////

		} else if (data instanceof SectionPlaylist) {
			SectionPlaylist section = (SectionPlaylist) data;
			
			((View) holder.imageView.getParent()).setVisibility(View.GONE);
			
			int devicePixelDensity = (int) DisplayProperties.getInstance((Activity) context).getPixelDensity();
			RelativeLayout.LayoutParams titleTextViewLayoutParams = (RelativeLayout.LayoutParams) holder.titleTextView.getLayoutParams();
			titleTextViewLayoutParams.setMargins(21 * devicePixelDensity, 4 * devicePixelDensity, 30 * devicePixelDensity, 0);
			
			holder.titleTextView.setLayoutParams(titleTextViewLayoutParams);
			holder.titleTextView.setTextSize(12);
			holder.titleTextView.setLines(3);
			holder.titleTextView.setGravity(Gravity.START | Gravity.CENTER);
			holder.titleTextView.setText(StringUtilities.purgeUnwantedSpaceInText(section.name));
			
		}
		
		return convertView;
	}
	
	private void updateVideoOrArticleHolder(final ViewHolder holder, String title, String thumbnailURL, String publicationDate) {
		
		((View) holder.imageView.getParent()).setVisibility(View.VISIBLE);
		
		if (holder.imageView != null) {
			if (imageViewFadeRefreshEnable) {
				holder.imageView.setImageBitmap(null);
				holder.imageView.setAlpha(0f);
			}
		}
		
		int devicePixelDensity = (int) DisplayProperties.getInstance((Activity) context).getPixelDensity();
		RelativeLayout.LayoutParams titleTextViewLayoutParams = (RelativeLayout.LayoutParams) holder.titleTextView.getLayoutParams();
		titleTextViewLayoutParams.setMargins(11 * devicePixelDensity, 7 * devicePixelDensity, 30 * devicePixelDensity, 0);
		
		holder.titleTextView.setLayoutParams(titleTextViewLayoutParams);
		holder.titleTextView.setTextSize(11);
		holder.titleTextView.setLines(2);
		holder.titleTextView.setGravity(Gravity.START | Gravity.TOP);
		holder.titleTextView.setText(StringUtilities.purgeUnwantedSpaceInText(title));
		
		holder.publicationDateTextView.setText(publicationDate);
		
		if (!TextUtils.isEmpty(thumbnailURL)) {
			imageLoader.displayImage(thumbnailURL, holder.imageView, options, new ImageLoadingListener() {
				
				@Override
				public void onLoadingStarted(String arg0, View arg1) {}
				
				@Override
				public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
					holder.imageView.setImageBitmap(null);
					if (imageViewFadeRefreshEnable) {
						AnimationUtilities.fadeIn(holder.imageView, null);
					}
				}
				
				@Override
				public void onLoadingComplete(String arg0, View view, Bitmap bitmap) {
					if (imageViewFadeRefreshEnable) {
						holder.imageView.animate().alpha(1f).setDuration(350);
					}
				}
				
				@Override
				public void onLoadingCancelled(String arg0, View arg1) {}
			});
			
		} else {
			
			holder.imageView.setImageBitmap(null);
			if (imageViewFadeRefreshEnable) {
				AnimationUtilities.fadeIn(holder.imageView, null);
			}
			
		}
		
	}

}
