package com.martin.noten;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar.Tab;

class TabListener<T extends Fragment> implements android.support.v7.app.ActionBar.TabListener {
    private final Activity mActivity;
    private final String mTag;
    private final Class<T> mClass;
    private Fragment mFragment;

    /**
     * Constructor used each time a new tab is created.
     *
     * @param activity The host Activity, used to instantiate the fragment
     * @param tag      The identifier tag for the fragment
     * @param clz      The fragment's Class, used to instantiate the fragment
     */
    public TabListener(Activity activity, String tag, Class<T> clz) {
        mActivity = activity;
        mTag = tag;
        mClass = clz;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onTabSelected(Tab tab, FragmentTransaction ft) {
        if (mFragment == null) {
            mFragment = Fragment.instantiate(mActivity, mClass.getName());
            ft.add(android.R.id.content, mFragment, mTag);
        } else {
            ft.attach(mFragment);
        }
    }

    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
        if (mFragment != null) {
            ft.detach(mFragment);
        }
    }

    @Override
    public void onTabReselected(Tab tab, FragmentTransaction ft) {

    }
}
