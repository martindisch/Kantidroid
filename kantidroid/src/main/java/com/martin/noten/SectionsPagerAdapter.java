package com.martin.noten;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.Locale;

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = new Fragment();
        switch (position) {
            case 0:
                return fragment = new Semester1Fragment();
            case 1:
                return fragment = new Semester2Fragment();
            case 2:
                return fragment = new ZeugnisFragment();
            default:
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Locale l = Locale.getDefault();
        switch (position) {
            case 0:
                return ("1. Semester").toUpperCase(l);
            case 1:
                return ("2. Semester").toUpperCase(l);
            case 2:
                return ("Zeugnis").toUpperCase(l);
        }
        return null;
    }
}