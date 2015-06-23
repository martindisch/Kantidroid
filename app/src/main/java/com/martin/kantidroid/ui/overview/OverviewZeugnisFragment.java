package com.martin.kantidroid.ui.overview;


import android.content.Context;
import android.content.Intent;
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
import com.martin.kantidroid.ui.fachview.FachviewActivity;
import com.martin.kantidroid.ui.util.DividerItemDecoration;

import java.util.List;

public class OverviewZeugnisFragment extends Fragment {

    private static final String ARG_PARAM1 = "semester";
    private int mSemester;

    private RecyclerView mSubjects;
    private ZeugnisAdapter mAdapter;

    public static OverviewZeugnisFragment newInstance(int semester) {
        OverviewZeugnisFragment fragment = new OverviewZeugnisFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, semester);
        fragment.setArguments(args);
        return fragment;
    }

    public OverviewZeugnisFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mSemester = getArguments().getInt(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.overview_zeugnisfragment, container, false);

        mSubjects = (RecyclerView) rootView.findViewById(R.id.rvSubjects);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mSubjects.setLayoutManager(layoutManager);
        mSubjects.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        mSubjects.setHasFixedSize(true);

        DatabaseHandler db = new DatabaseHandler(getActivity());
        List<Fach> subjects = db.getAllFaecherSorted(getActivity(), mSemester, getActivity().getSharedPreferences("Kantidroid", Context.MODE_PRIVATE).getInt("sorting", 0));
        mAdapter = new ZeugnisAdapter(getActivity(), subjects);
        mSubjects.setAdapter(mAdapter);

        return rootView;
    }

    public void loadData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                DatabaseHandler db = new DatabaseHandler(getActivity());
                List<Fach> subjects = db.getAllFaecherSorted(getActivity(), mSemester, getActivity().getSharedPreferences("Kantidroid", Context.MODE_PRIVATE).getInt("sorting", 0));
                mAdapter.setData(subjects);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }
}
