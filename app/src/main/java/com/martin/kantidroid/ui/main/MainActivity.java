package com.martin.kantidroid.ui.main;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.martin.kantidroid.R;
import com.martin.kantidroid.logic.Primer;
import com.martin.kantidroid.ui.backup.BackupFragment;
import com.martin.kantidroid.ui.overview.OverviewFragment;
import com.martin.kantidroid.ui.subjects.SubjectsFragment;
import com.martin.kantidroid.ui.timetable.TimetableFragment;


public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private int mSelected, mCurrent, mExtraSelected;
    private NavigationView mNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        if (savedInstanceState != null) {
            mSelected = savedInstanceState.getInt("mSelected");
            mCurrent = savedInstanceState.getInt("mCurrent");
        } else {
            mCurrent = -1;
            mSelected = 0;
        }
        mExtraSelected = -1;

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mDrawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
            }

            @Override
            public void onDrawerOpened(View drawerView) {
            }

            @Override
            public void onDrawerStateChanged(int newState) {
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                updateContainer();
            }
        });

        Glide.with(this).load(R.drawable.drawer_backdrop).into((ImageView) mDrawerLayout.findViewById(R.id.ivHeader));

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        if (mNavigationView != null) {
            setupDrawerContent(mNavigationView);
        }

        selectDrawerItem(mSelected);

        Primer.runEveryTime(this);
        Primer.runOnFirstTime(this);
    }

    private void selectDrawerItem(int i) {
        mNavigationView.getMenu().getItem(i).setChecked(true);
        updateContainer();
    }

    private void updateContainer() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment;
        String tag;
        if (mSelected != mCurrent) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
            switch (mSelected) {
                case 0:
                    fragment = OverviewFragment.newInstance();
                    tag = "overview";
                    break;
                case 1:
                    fragment = SubjectsFragment.newInstance();
                    tag = "subjects";
                    break;
                case 3:
                    fragment = TimetableFragment.newInstance();
                    tag = "timetable";
                    break;
                case 4:
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
                    fragment = BackupFragment.newInstance();
                    tag = "backup";
                    break;
                default:
                    fragment = PlaceholderFragment.newInstance(mSelected);
                    tag = "placeholder";
                    break;
            }
            mCurrent = mSelected;
            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment, tag)
                    .commit();
        }
        if (mExtraSelected != -1) {
            switch (mExtraSelected) {
                /*case 4:
                    Intent i = new Intent(this, TimetableActivity.class);
                    startActivity(i);
                    mExtraSelected = -1;
                    break;*/
                case 5:

                    break;
                case 6:

                    break;
            }
        }
    }

    private void setupDrawerContent(final NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.nav_overview:
                                mSelected = 0;
                                break;
                            case R.id.nav_subjects:
                                mSelected = 1;
                                break;
                            case R.id.nav_kiss:
                                mSelected = 2;
                                break;
                            case R.id.nav_timetable:
                                mSelected = 3;
                                break;
                            case R.id.nav_backup:
                                mSelected = 4;
                                break;
                            case R.id.nav_feedback:
                                mExtraSelected = 5;
                                break;
                            case R.id.nav_about:
                                mExtraSelected = 6;
                                break;
                        }
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("mSelected", mSelected);
        outState.putInt("mCurrent", mCurrent);
        super.onSaveInstanceState(outState);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.dummy_fragment, container, false);
        }

    }

}
