package com.afrikawood.banguiwood;

import java.util.ArrayList;
import java.util.Locale;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.afrikawood.banguiwood.business.MenuItem;
import com.afrikawood.banguiwood.business.Section;
import com.afrikawood.banguiwood.business.SectionArticles;
import com.afrikawood.banguiwood.business.SectionPlaylist;
import com.afrikawood.banguiwood.tools.BaseActivity;

public class MenuListFragment extends ListFragment {
	
	private FragmentActivity context;
	private MenuListAdapter adapter;
	
	public MenuListAdapter getAdapter() {
		return adapter;
	}
	public void setAdapter(MenuListAdapter adapter) {
		this.adapter = adapter;
	}
	
	public MenuListFragment(BaseActivity context) {
		super();
		
		this.context = context;
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.menu_list, container, false);
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		ArrayList<MenuItem> menuItems = new ArrayList<MenuItem>();
	
		adapter = new MenuListAdapter(getActivity(), menuItems);
		setListAdapter(adapter);
	}
	
	public void refreshList(ArrayList<Section> sections) {
		
		ArrayList<MenuItem> menuItems = new ArrayList<MenuItem>();
		
		for (int i = 0; i < sections.size(); i++) {
			
			Section section = (Section) sections.get(i);
			menuItems.add(new MenuItem(section.getName(), android.R.drawable.ic_menu_search, true, -1, i));
			
			for (int j = 0; j < section.getSections().size(); j++) {
				Section subSection = (Section) section.getSections().get(j);
				menuItems.add(new MenuItem(subSection.getName(), android.R.drawable.ic_menu_search, false, i, j));
			}
		}

		adapter.setItems(menuItems);
		
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		
		MenuItem menuItem = (MenuItem) adapter.getItems().get(position);
//		Log.i("menuItem", "" + menuItem.getIsRootSection() + ", " + menuItem.getParentSectionIndex());
		
		if (!menuItem.getIsRootSection()) {
			ArrayList<Section> sections = ((MainActivity) context).getSectionsTreeData();
			Section rootSection = sections.get(menuItem.getParentSectionIndex());
//			Log.i("parentSection", "" + parentSection + ", " + parentSection.getSections().size());
			
			int sectionIndex = menuItem.getSectionIndex();
//			Log.i("sectionIndex", "" + sectionIndex);
			
			if (sectionIndex < rootSection.getSections().size()) {
				
				Section section = rootSection.getSections().get(sectionIndex);
//				Log.i("section", "" + position + ", " + parentSection.getSections().get(sectionIndex));
				
				if (section != null) {
					Log.i("getSectionType", "" + section.getSectionType());
					
					String breadCrumbsString = context.getResources().getString(R.string.textBreadCrumbs);
					
					if (menuItem.getParentSectionIndex() == 0 && sectionIndex == 0) {
						if (context instanceof MainActivity) {
							((MainActivity) context).setupHomeFragment();
						}
					} else if (section instanceof SectionPlaylist) {
						PlaylistFragment fragment = new PlaylistFragment((SectionPlaylist) section, String.format(Locale.FRENCH, breadCrumbsString, rootSection.getName(), section.getName()), false);
						switchFragment(fragment);
					} else if (section instanceof SectionArticles) {
						ArticlesListFragment fragment = new ArticlesListFragment((SectionArticles) section, String.format(Locale.FRENCH, breadCrumbsString, rootSection.getName(), section.getName()), false);
						switchFragment(fragment);
					}
				}
				
			}
		}
	}
	
	private void switchFragment(final Fragment fragment) {
		if (getActivity() == null)
			return;

		if (getActivity() instanceof MainActivity) {
			
			new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                	MainActivity mainActivity = (MainActivity) getActivity();
                	mainActivity.switchContent(fragment, false);
                }
            }, 350);
			
		}

	}

}
