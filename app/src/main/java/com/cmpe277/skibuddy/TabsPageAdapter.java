package com.cmpe277.skibuddy;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class TabsPageAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public TabsPageAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                DashboardFragment tab1 = new DashboardFragment();
                return tab1;
            case 1:
                ProfileFragment tab2 = new ProfileFragment();
                return tab2;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}