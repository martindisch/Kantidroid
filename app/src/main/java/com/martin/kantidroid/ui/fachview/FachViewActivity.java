package com.martin.kantidroid.ui.fachview;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.martin.kantidroid.R;
import com.martin.kantidroid.logic.DatabaseHandler;
import com.martin.kantidroid.logic.Fach;

import java.util.ArrayList;
import java.util.List;

public class FachviewActivity extends AppCompatActivity {

    private Adapter mAdapter;
    private int mSemester;
    private int mId;
    private int mEdited = 0;
    private Fach mFach;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fachview_activity);

        mSemester = getIntent().getIntExtra("semester", 1);
        mId = getIntent().getIntExtra("id", 0);

        DatabaseHandler db = new DatabaseHandler(this);
        mFach = db.getFach(mId);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle(mFach.getName());

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        if (viewPager != null) {
            setupViewPager(viewPager);
        }

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }



    private void setupViewPager(ViewPager viewPager) {
        mAdapter = new Adapter(getSupportFragmentManager());
        mAdapter.addFragment(FachviewFragment.newInstance(mId, 1), getString(R.string.first_semester));
        mAdapter.addFragment(FachviewFragment.newInstance(mId, 2), getString(R.string.second_semester));
        viewPager.setAdapter(mAdapter);
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

    }

    public void setEdited() {
        mEdited = 1;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setResult(mEdited);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Let's not reload the main activity
        onBackPressed();
        return true;
    }
}
