package com.arulvakku.app.ui.wayofcross.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.arulvakku.app.ui.wayofcross.WayOfCrossFragment;

import org.json.JSONArray;

public class CustomViewPagerAdapter extends FragmentStatePagerAdapter {

    int mNumOfTabs;
    JSONArray jsonArray;
    int tabPosition;

    public CustomViewPagerAdapter(FragmentManager fm, JSONArray jsonArray, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.jsonArray = jsonArray;
        this.tabPosition = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        return new WayOfCrossFragment().newInstance(position, jsonArray.optJSONObject(position));
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}