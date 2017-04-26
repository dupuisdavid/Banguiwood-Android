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
		
		ArrayList<MenuItem> menuItems = new ArrayList<>();
	
		adapter = new MenuListAdapter(getActivity(), menuItems);
		setListAdapter(adapter);
	}
	
	public void refreshList(ArrayList<Section> sections) {
		
		ArrayList<MenuItem> menuItems = new ArrayList<>();
		
		for (int i = 0; i < sections.size(); i++) {
			
			Section section = sections.get(i);
			menuItems.add(new MenuItem(section.name, android.R.drawable.ic_menu_search, true, -1, i));
			
			for (int j = 0; j < section.sections.size(); j++) {
				Section subSection = section.sections.get(j);
				menuItems.add(new MenuItem(subSection.name, android.R.drawable.ic_menu_search, false, i, j));
			}
		}

		adapter.setItems(menuItems);
		
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		
		MenuItem menuItem = adapter.getItems().get(position);
//		Log.i("menuItem", "" + menuItem.getIsRootSection() + ", " + menuItem.getParentSectionIndex());
		
		if (!menuItem.isRootSection) {
			ArrayList<Section> sections = ((MainActivity) context).getSectionsTreeData();
			Section rootSection = sections.get(menuItem.parentSectionIndex);
//			Log.i("parentSection", "" + parentSection + ", " + parentSection.sections.size());
			
			int sectionIndex = menuItem.sectionIndex;
//			Log.i("sectionIndex", "" + sectionIndex);
			
			if (sectionIndex < rootSection.sections.size()) {
				
				Section section = rootSection.sections.get(sectionIndex);
//				Log.i("section", "" + position + ", " + parentSection.sections.get(sectionIndex));
				
				if (section != null) {
					Log.i("getSectionType", "" + section.sectionType);
					
					String breadCrumbsString = context.getResources().getString(R.string.textBreadCrumbs);
					
					if (menuItem.parentSectionIndex == 0 && sectionIndex == 0) {
						if (context instanceof MainActivity) {
							((MainActivity) context).setupHomeFragment();
						}
					} else if (section instanceof SectionPlaylist) {
						switchFragment(PlaylistFragment.newInstance(section, String.format(Locale.FRENCH, breadCrumbsString, rootSection.name, section.name), false));
					} else if (section instanceof SectionArticles) {
						switchFragment(ArticlesListFragment.newInstance((SectionArticles) section, String.format(Locale.FRENCH, breadCrumbsString, rootSection.name, section.name), false));
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
