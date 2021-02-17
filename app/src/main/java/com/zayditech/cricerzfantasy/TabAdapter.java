package com.zayditech.cricerzfantasy;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import java.util.ArrayList;
import java.util.List;

public class TabAdapter extends FragmentStatePagerAdapter {
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();
    String TeamToShow = "";
    TabAdapter(FragmentManager fm, String _teamToShow) {
        super(fm);
        TeamToShow = _teamToShow;
    }
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        if (position == 0) {
            fragment = new Tab1Fragment();
            Bundle bundle = new Bundle();
            bundle.putString("TeamToShow", TeamToShow);
            fragment.setArguments(bundle);
        } else if (position == 1) {
            fragment = new Tab2Fragment();
        }
        return fragment;
    }
    public void addFragment(Fragment fragment, String title) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }
    @Override
    public int getCount() {
        return mFragmentList.size();
    }
}
