package com.afrikawood.banguiwood;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.afrikawood.banguiwood.business.MenuItem;

import java.util.ArrayList;

public class MenuListAdapter extends ArrayAdapter<MenuItem> {
	
	@SuppressWarnings("unused")
	private Context context;
	private ArrayList<MenuItem> items;
	
	public ArrayList<MenuItem> getItems() {
		return items;
	}
	public void setItems(ArrayList<MenuItem> items) {
		this.items = items;
		this.notifyDataSetChanged();
	}

	private LayoutInflater layoutInflater;
	
	public MenuListAdapter(Context context, ArrayList<MenuItem> items) {
		super(context, 0);
		
		this.context = context;
		this.items = items;
		this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
	}
	
	public void addItem(final MenuItem menuItem) {
		items.add(menuItem);
        notifyDataSetChanged();
    }
	
	@Override
	public int getCount() {
		return this.items.size();
	}

	@Override
	public MenuItem getItem(int index) {
		return this.items.get(index);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	public static class ViewHolder {
//		ImageView icon;
		TextView title;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		
		Boolean cellRecycling = false;
		
		ViewHolder holder = null;
		View view = convertView;
		
		// ListView with Sections/Separators
		// http://bartinger.at/listview-with-sectionsseparators/
		
		// Android ListView with Section Header
		// http://androidtrainningcenter.blogspot.fr/2012/03/android-listview-with-section-header.html
		
		final MenuItem menuItem = items.get(position);
		
		if (menuItem != null) {
			
			if (cellRecycling) {
				
				if (view == null) {
					
					if (!menuItem.isRootSection) {
						view = layoutInflater.inflate(R.layout.row, parent, false);
					} else {
						view = layoutInflater.inflate(R.layout.section_row, parent, false);
					}
					
//					ImageView icon = (ImageView) view.findViewById(R.id.row_icon);
					TextView title = (TextView) view.findViewById(R.id.row_title);
					
					holder = new ViewHolder();
//					holder.icon = icon;
					holder.title = title;
					
					view.setTag(holder);
					
				} else {
					
					holder = (ViewHolder) view.getTag();
					
				}
				
				
//				holder.icon.setImageResource(getItem(position).getIconRes());
				holder.title.setText(getItem(position).tag);
				
			} else {
			
				if (!menuItem.isRootSection) {
					view = layoutInflater.inflate(R.layout.row, parent, false);
				} else {
					view = layoutInflater.inflate(R.layout.section_row, parent, false);
				}
				
	//			ImageView icon = (ImageView) view.findViewById(R.id.row_icon);
				TextView title = (TextView) view.findViewById(R.id.row_title);
				
				title.setText(getItem(position).tag);
			}

		}
		
		return view;
	}
	
}
