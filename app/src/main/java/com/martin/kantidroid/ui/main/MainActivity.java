package com.martin.kantidroid.ui.main;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.martin.kantidroid.R;
import com.martin.kantidroid.logic.Primer;
import com.martin.kantidroid.ui.overview.OverviewFragment;
import com.martin.kantidroid.ui.subjects.SubjectsFragment;


public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private int mSelected, mCurrent;
    private NavigationView mNavigationView;
    private SharedPreferences mSp;
    private SharedPreferences.Editor mEditor;
    private int mSelectedItem;

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

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        if (mNavigationView != null) {
            setupDrawerContent(mNavigationView);
        }

        selectDrawerItem(mSelected);

        Primer.runEveryTime(this);
        Primer.runOnFirstTime(this);

        new Thread(new Runnable() {
            @Override
            public void run() {
                mSp = getSharedPreferences("Kantidroid", MODE_PRIVATE);
                mEditor = mSp.edit();
            }
        }).start();
    }

    private void selectDrawerItem(int i) {
        mNavigationView.getMenu().getItem(i).setChecked(true);
        updateContainer();
    }

    private void updateContainer() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = null;
        String tag = "";
        if (mSelected != mCurrent) {
            switch (mSelected) {
                case 0:
                    fragment = OverviewFragment.newInstance();
                    tag = "overview";
                    break;
                case 3:
                    fragment = SubjectsFragment.newInstance();
                    tag = "subjects";
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
                            case R.id.nav_kiss:
                                mSelected = 1;
                                break;
                            case R.id.nav_backup:
                                mSelected = 2;
                                break;
                            case R.id.nav_subjects:
                                mSelected = 3;
                                break;
                            case R.id.nav_feedback:
                                mSelected = 4;
                                break;
                            case R.id.nav_about:
                                mSelected = 5;
                                break;
                        }
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.action_sort:
                mSelectedItem = mSp.getInt("sorting", 0);

                AlertDialog.Builder dee = new AlertDialog.Builder(this);
                dee.setTitle(R.string.sort);
                dee.setNeutralButton(R.string.cancel, null);
                dee.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mEditor.putInt("sorting", mSelectedItem);
                        mEditor.commit();
                        // Let MainActivity reload the data after we've created new subjects
                        final FragmentManager fragmentManager = getSupportFragmentManager();
                        ((OverviewFragment) fragmentManager.findFragmentByTag("overview")).loadData();
                    }
                });
                dee.setSingleChoiceItems(R.array.sorting_entries, mSelectedItem, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mSelectedItem = which;
                    }
                });
                dee.show();
                break;
            case R.id.action_reset:

                break;
            case R.id.action_department:

                break;
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
            View rootView = inflater.inflate(R.layout.dummy_fragment, container, false);
            return rootView;
        }

    }

}
