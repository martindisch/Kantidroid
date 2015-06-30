package com.martin.kantidroid.ui.overview;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.martin.kantidroid.R;
import com.martin.kantidroid.logic.DatabaseHandler;
import com.martin.kantidroid.logic.Fach;
import com.martin.kantidroid.ui.util.DividerItemDecoration;

import java.util.List;

public class OverviewZeugnisFragment extends Fragment {

    private RecyclerView mSubjects;
    private ZeugnisAdapter mAdapter;

    public static OverviewZeugnisFragment newInstance() {
        OverviewZeugnisFragment fragment = new OverviewZeugnisFragment();
        return fragment;
    }

    public OverviewZeugnisFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.overview_zeugnisfragment, container, false);

        mSubjects = (RecyclerView) rootView.findViewById(R.id.rvSubjects);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mSubjects.setLayoutManager(layoutManager);
        mSubjects.addItemDecoration(new DividerItemDecoration(getActivity(), null, false));
        mSubjects.setHasFixedSize(true);

        DatabaseHandler db = new DatabaseHandler(getActivity());
        List<Fach> subjects = db.getAllFaecherSorted(getActivity(), 1, 0);
        mAdapter = new ZeugnisAdapter(subjects);
        mSubjects.setAdapter(mAdapter);

        return rootView;
    }

    public void loadData(final Activity c) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mAdapter != null) {
                    DatabaseHandler db = new DatabaseHandler(c);
                    List<Fach> subjects = db.getAllFaecherSorted(c, 1, 0);
                    mAdapter.setData(subjects);
                    c.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        }).start();
    }
}
