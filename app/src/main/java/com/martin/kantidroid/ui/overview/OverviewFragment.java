package com.martin.kantidroid.ui.overview;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.martin.kantidroid.R;
import com.martin.kantidroid.logic.DatabaseHandler;
import com.martin.kantidroid.logic.Fach;
import com.martin.kantidroid.logic.PromoCheck;
import com.martin.kantidroid.logic.PromoRes;

import java.util.ArrayList;
import java.util.List;

public class OverviewFragment extends Fragment {

    private Adapter mAdapter;
    private TextView mPromo, mPP, mKont;
    private ViewPager mViewPager;
    private boolean mFirsttime = false;
    private int mSelectedItem;
    private SharedPreferences mSp;
    private SharedPreferences.Editor mEditor;

    public static OverviewFragment newInstance() {
        return new OverviewFragment();
    }

    public OverviewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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

        SharedPreferences mPrefs = getActivity().getSharedPreferences("Kantidroid", Context.MODE_PRIVATE);
        mEditor = mPrefs.edit();

        mViewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
        if (mViewPager != null) {
            setupViewPager(mViewPager);
            mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int i, float v, int i1) {
                }

                @Override
                public void onPageSelected(int i) {
                }

                @Override
                public void onPageScrollStateChanged(int i) {
                    if (i == ViewPager.SCROLL_STATE_IDLE) {
                        final int position = mViewPager.getCurrentItem();
                        showInfo(position + 1);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                mEditor.putInt("semester", position);
                                mEditor.commit();
                            }
                        }).start();
                    }
                }
            });
        }
        final TabLayout tabLayout = (TabLayout) rootView.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        mViewPager.setCurrentItem(mPrefs.getInt("semester", 0));
        showInfo(mPrefs.getInt("semester", 0) + 1);

        mFirsttime = true;

        new Thread(new Runnable() {
            @Override
            public void run() {
                mSp = getActivity().getSharedPreferences("Kantidroid", Context.MODE_PRIVATE);
                mEditor = mSp.edit();
            }
        }).start();

        return rootView;
    }

    public void loadData() {
        mAdapter.loadData(getActivity());
        showInfo(mViewPager.getCurrentItem() + 1);
    }

    private void showInfo(final int semester) {
        final Resources res = getResources();
        if (semester != 3) {
            final PromoRes promo = new PromoCheck(getActivity()).getPromo(semester);
            mPromo.setText(promo.sMessage);
            mPromo.setTextColor(res.getColor(promo.iColor));
            if (promo.iColor == R.color.promo_black) {
                mPromo.setBackgroundColor(res.getColor(R.color.promo_white));
                mPP.setBackgroundColor(res.getColor(R.color.promo_black));
                mPromo.setTextSize(14);
            } else {
                mPromo.setBackgroundColor(res.getColor(R.color.highlight_dark));
                mPP.setBackgroundColor(res.getColor(R.color.highlight_light));
                mPromo.setTextSize(16);
            }
            mPP.setText(promo.sPP);
            mKont.setText(promo.sKont);
        } else {
            String[] finalResult = new PromoCheck(getActivity()).getPromoFinal();
            if (finalResult[0].contentEquals("Bestanden")) {
                mPromo.setTextSize(16);
                mPromo.setBackgroundColor(res.getColor(R.color.highlight_dark));
                mPromo.setTextColor(res.getColor(R.color.promo_white));
            } else {
                mPromo.setTextSize(14);
                mPromo.setBackgroundColor(res.getColor(R.color.promo_white));
                mPromo.setTextColor(res.getColor(R.color.promo_black));
            }
            mPromo.setText(finalResult[0]);
            mPP.setText(finalResult[1]);
            mPP.setBackgroundColor(res.getColor(R.color.highlight_light));
            mKont.setText(finalResult[2]);
            mKont.setBackgroundColor(res.getColor(R.color.highlight_dark));
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        mAdapter = new Adapter(getChildFragmentManager());
        mAdapter.addFragment(OverviewSubjectsFragment.newInstance(1), getString(R.string.first_semester));
        mAdapter.addFragment(OverviewSubjectsFragment.newInstance(2), getString(R.string.second_semester));
        mAdapter.addFragment(OverviewZeugnisFragment.newInstance(), getString(R.string.zeugnis));
        viewPager.setAdapter(mAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mFirsttime) {
            mFirsttime = false;
        } else {
            loadData();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sort:
                mSelectedItem = mSp.getInt("sorting", 0);

                AlertDialog.Builder dee = new AlertDialog.Builder(getActivity());
                dee.setTitle(R.string.sort);
                dee.setNeutralButton(R.string.cancel, null);
                dee.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mEditor.putInt("sorting", mSelectedItem);
                        mEditor.commit();
                        loadData();
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
                AlertDialog.Builder dg = new AlertDialog.Builder(getActivity());
                dg.setTitle(getString(R.string.finish_year));
                dg.setMessage(getString(R.string.finish_question));
                dg.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                DatabaseHandler db = new DatabaseHandler(getActivity());
                                List<Fach> faecher = db.getAllFaecher(getActivity(), 1);
                                Fach selected;
                                for (int z = 0; z < db.getFachCount(); z++) {
                                    selected = db.getFach(faecher.get(z).getID());
                                    selected.setMathAverage1("");
                                    selected.setNoten1("");
                                    selected.setRealAverage1("");
                                    selected.setMathAverage2("");
                                    selected.setNoten2("");
                                    selected.setRealAverage2("");
                                    selected.setKont1("");
                                    selected.setKont2("");
                                    db.updateFach(selected);
                                }

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        loadData();
                                    }
                                });
                            }
                        }).start();
                    }
                });
                dg.setNegativeButton("Nein", null);
                dg.show();
                break;
            case R.id.action_department:
                mSelectedItem = mSp.getInt("department", 0);

                AlertDialog.Builder fee = new AlertDialog.Builder(getActivity());
                fee.setTitle(R.string.department);
                fee.setNeutralButton(R.string.cancel, null);
                fee.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mEditor.putInt("department", mSelectedItem);
                        mEditor.commit();
                        // Let MainActivity reload the data after we've created new subjects
                        loadData();
                    }
                });
                fee.setSingleChoiceItems(R.array.departments, mSelectedItem, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mSelectedItem = which;
                    }
                });
                fee.show();
                break;
        }
        return super.onOptionsItemSelected(item);
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

        public void loadData(Activity c) {
            for (int i = 0; i < 2; i++) {
                ((OverviewSubjectsFragment) mFragments.get(i)).loadData(c);
            }
            ((OverviewZeugnisFragment) mFragments.get(2)).loadData(c);
        }
    }
}
