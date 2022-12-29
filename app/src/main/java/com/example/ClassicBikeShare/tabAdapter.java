package com.example.ClassicBikeShare;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class tabAdapter extends FragmentStatePagerAdapter {

    int tabCount;
    // tabAdapter has two parameters, a fragment manager and an integer for the number of tabs
    // by joining this class, an object inherits the Fragment manager and can access/get the # of
    // tabs (there is no setter)
    // and, can get the fragments (tabs) that will be scrolled (Profile, Users, Pictures)
    public tabAdapter(@NonNull FragmentManager fm, int numberOfTabs) {
        super(fm, FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.tabCount = numberOfTabs; // numberOfTabs is a variable we have created that holds the
                                    // value of the number of tabs open as determined by the getCount method
                                    // which returns its count to the tabCount variable, "numberOfTabs"
    }
    @Override
    // positions are 0,1,2 and correspond to the case numbers and position as tabs on the action bar
    public Fragment getItem(int position) {
        switch (position) { // similar to if stmt

            case 0: // ranks the tab as first in line (furthest to left on action bar)
                return new ProfileTab_Fragment(); // returns the ProfileTab fragment to the method
                                                // in response to selection of tab 1
            case 1:
                return new UsersTab();

            case 2:
                return new SharePictureTab_Fragment();
            default:
                return null;
        }
    }
    public int getCount() {
        return tabCount;
    }

    @Nullable
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Edit Your Profile";
            case 1:
                return "Other Bike Share Users";
            case 2:
                return "Share Pictures of Your Bikes";
            default:
                return null;
        }
    }
}




