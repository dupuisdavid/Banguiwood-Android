package com.afrikawood.banguiwood;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
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
	    LinearLayout sectionLayout;
		TextView sectionTitleTextView;
        LinearLayout itemLayout;
        TextView itemTitleTextView;
	}

	@NonNull
	public View getView(int position, View convertView, @NonNull ViewGroup parent) {
		ViewHolder holder;
		
		// ListView with Sections/Separators
		// http://bartinger.at/listview-with-sectionsseparators/
		
		// Android ListView with Section Header
		// http://androidtrainningcenter.blogspot.fr/2012/03/android-listview-with-section-header.html

        @NonNull MenuItem menuItem = items.get(position);

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.row, parent,false);

            LinearLayout sectionLayout = convertView.findViewById(R.id.section_layout);
            TextView sectionTitleTextView = convertView.findViewById(R.id.section_title_textview);
            LinearLayout itemLayout = convertView.findViewById(R.id.item_layout);
            TextView itemTitleTextView = convertView.findViewById(R.id.item_title_textview);

            holder = new ViewHolder();
            holder.sectionLayout = sectionLayout;
            holder.sectionTitleTextView = sectionTitleTextView;
            holder.itemLayout = itemLayout;
            holder.itemTitleTextView = itemTitleTextView;

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (!menuItem.isRootSection) {
            holder.sectionLayout.setVisibility(View.GONE);
            holder.sectionTitleTextView.setText("");
            holder.itemLayout.setVisibility(View.VISIBLE);
            holder.itemTitleTextView.setText(menuItem.tag);
        } else {
            holder.sectionLayout.setVisibility(View.VISIBLE);
            holder.sectionTitleTextView.setText(menuItem.tag);
            holder.itemLayout.setVisibility(View.GONE);
            holder.itemTitleTextView.setText("");
        }

		return convertView;
	}
	
}
