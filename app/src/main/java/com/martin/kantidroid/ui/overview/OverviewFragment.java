package com.martin.kantidroid.ui.overview;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.martin.kantidroid.R;
import com.martin.kantidroid.logic.PromoCheck;
import com.martin.kantidroid.logic.PromoRes;

import java.util.ArrayList;
import java.util.List;

public class OverviewFragment extends Fragment {

    private Adapter mAdapter;
    private TextView mPromo, mPP, mKont;
    private SharedPreferences mPrefs;
    private SharedPreferences.Editor mEditor;
    private ViewPager mViewPager;

    public static OverviewFragment newInstance() {
        return new OverviewFragment();
    }

    public OverviewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.overview_fragment, container, false);

        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        final ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle(R.string.overview);

        mPromo = (TextView) rootView.findViewById(R.id.tvMadeIt);
        mPP = (TextView) rootView.findViewById(R.id.tvPP);
        mKont = (TextView) rootView.findViewById(R.id.tvKont);

        mPrefs = getActivity().getSharedPreferences("Kantidroid", Context.MODE_PRIVATE);
        mEditor = mPrefs.edit();

        mViewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
        if (mViewPager != null) {
            setupViewPager(mViewPager);
            mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                @Override
                public void onPageSelected(int position) {
                    showInfo(position + 1);
                    mEditor.putInt("semester", position);
                    mEditor.commit();
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }

        TabLayout tabLayout = (TabLayout) rootView.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        // TODO: Display last used tab & show according info
        mViewPager.setCurrentItem(mPrefs.getInt("semester", 0));
        showInfo(mPrefs.getInt("semester", 0) + 1);

        return rootView;
    }

    public void loadData() {
        mAdapter.loadData();
        showInfo(mViewPager.getCurrentItem() + 1);
    }

    private void showInfo(int semester) {
        PromoRes promo = new PromoCheck(getActivity()).getGym(semester);
        mPromo.setText(promo.sMessage);
        mPromo.setTextColor(getResources().getColor(promo.iColor));
        mPP.setText(promo.sPP);
        mKont.setText(promo.sKont);
    }

    private void setupViewPager(ViewPager viewPager) {
        mAdapter = new Adapter(getChildFragmentManager());
        mAdapter.addFragment(OverviewSubjectsFragment.newInstance(1), getString(R.string.first_semester));
        mAdapter.addFragment(OverviewSubjectsFragment.newInstance(2), getString(R.string.second_semester));
        viewPager.setAdapter(mAdapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 1) {
            mAdapter.loadData();
        }
    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }

        // TODO: Maybe do this off-thread?
        public void loadData() {
            // TODO: Also do showInfo to update general information
            for (int i = 0; i < mFragments.size(); i++) {
                ((OverviewSubjectsFragment) mFragments.get(i)).loadData();
            }
        }
    }
}
