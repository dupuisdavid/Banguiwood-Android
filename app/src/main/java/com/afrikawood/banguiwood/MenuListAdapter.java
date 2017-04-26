package com.afrikawood.banguiwood;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.afrikawood.banguiwood.business.MenuItem;

import java.util.ArrayList;

class MenuListAdapter extends ArrayAdapter<MenuItem> {

	private ArrayList<MenuItem> items;
	
	ArrayList<MenuItem> getItems() {
		return items;
	}
	void setItems(ArrayList<MenuItem> items) {
		this.items = items;
		this.notifyDataSetChanged();
	}

	private LayoutInflater layoutInflater;
	
	MenuListAdapter(Context context, ArrayList<MenuItem> items) {
		super(context, 0);

		this.items = items;
		this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
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
	
	private static class ViewHolder {
		TextView title;
	}

	@NonNull
	public View getView(int position, View convertView, @NonNull ViewGroup parent) {
		
		ViewHolder holder;
		View view = convertView;
		
		// ListView with Sections/Separators
		// http://bartinger.at/listview-with-sectionsseparators/
		
		// Android ListView with Section Header
		// http://androidtrainningcenter.blogspot.fr/2012/03/android-listview-with-section-header.html
		
		final MenuItem menuItem = items.get(position);
		
		if (menuItem != null) {
			if (view == null) {

				if (!menuItem.isRootSection) {
					view = layoutInflater.inflate(R.layout.row, parent, false);
				} else {
					view = layoutInflater.inflate(R.layout.section_row, parent, false);
				}

				TextView title = (TextView) view.findViewById(R.id.row_title);

				holder = new ViewHolder();
				holder.title = title;

				view.setTag(holder);

			} else {

				holder = (ViewHolder) view.getTag();

			}


			holder.title.setText(menuItem.tag);
		}
		
		return view;
	}
	
}
