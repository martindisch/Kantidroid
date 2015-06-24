package com.martin.kantidroid.ui.overview;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.martin.kantidroid.R;
import com.martin.kantidroid.logic.DatabaseHandler;
import com.martin.kantidroid.logic.Fach;
import com.martin.kantidroid.ui.fachview.FachviewActivity;

import java.util.List;

public class OverviewSubjectsFragment extends Fragment implements OverviewAdapter.OnClickListener {

    private static final String ARG_PARAM1 = "semester";
    private int mSemester;

    private RecyclerView mSubjects;
    private OverviewAdapter mAdapter;

    private SharedPreferences mPrefs;
    private Context mAppContext;

    public static OverviewSubjectsFragment newInstance(int semester) {
        OverviewSubjectsFragment fragment = new OverviewSubjectsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, semester);
        fragment.setArguments(args);
        return fragment;
    }

    public OverviewSubjectsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mSemester = getArguments().getInt(ARG_PARAM1);
        }
        mPrefs = getActivity().getSharedPreferences("Kantidroid", Context.MODE_PRIVATE);
        mAppContext = getActivity().getApplicationContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.overview_subjectsfragment, container, false);

        mSubjects = (RecyclerView) rootView.findViewById(R.id.rvSubjects);

        mSubjects.addItemDecoration(new MarginDecoration(getActivity()));
        mSubjects.setHasFixedSize(true);

        DatabaseHandler db = new DatabaseHandler(getActivity());
        List<Fach> subjects = db.getAllFaecherSorted(getActivity(), mSemester, mPrefs.getInt("sorting", 0));
        mAdapter = new OverviewAdapter(getActivity(), subjects, OverviewSubjectsFragment.this, mSemester);
        mSubjects.setAdapter(mAdapter);

        return rootView;
    }

    public void loadData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                DatabaseHandler db = new DatabaseHandler(mAppContext);
                List<Fach> subjects = db.getAllFaecherSorted(mAppContext, mSemester, mPrefs.getInt("sorting", 0));
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

    @Override
    public void onItemClick(View v, int position) {
        Intent i = new Intent(getActivity(), FachviewActivity.class);
        i.putExtra("semester", mSemester);
        i.putExtra("id", mAdapter.getData().get(position).getID());
        startActivity(i);
    }

    @Override
    public void onItemLongClick(View v, int position) {
    }
}
