package com.martin.kantidroid.ui.overview;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.martin.kantidroid.R;
import com.martin.kantidroid.logic.DatabaseHandler;
import com.martin.kantidroid.logic.Fach;
import com.martin.kantidroid.ui.fachview.EditMarkDialog;
import com.martin.kantidroid.ui.fachview.FachviewActivity;
import com.martin.kantidroid.ui.util.MarginDecoration;
import com.martin.kantidroid.ui.util.OnStartDragListener;

import java.util.List;

public class OverviewSubjectsFragment extends Fragment implements OverviewAdapter.OnClickListener, OnStartDragListener {

    private static final String ARG_PARAM1 = "semester";
    private int mSemester;

    private OverviewAdapter mAdapter;

    private SharedPreferences mPrefs;
    private ItemTouchHelper mTouchHelper;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.overview_subjectsfragment, container, false);

        RecyclerView mSubjects = (RecyclerView) rootView.findViewById(R.id.rvSubjects);

        mSubjects.addItemDecoration(new MarginDecoration(getActivity()));
        mSubjects.setHasFixedSize(true);

        DatabaseHandler db = new DatabaseHandler(getActivity());
        if (mPrefs == null) {
            mPrefs = getActivity().getSharedPreferences("Kantidroid", Context.MODE_PRIVATE);
        }
        List<Fach> subjects = db.getAllFaecherSorted(mSemester, mPrefs.getInt("sorting", 0));
        mAdapter = new OverviewAdapter(getActivity(), subjects, OverviewSubjectsFragment.this, OverviewSubjectsFragment.this, mSemester);
        mSubjects.setAdapter(mAdapter);

        ItemTouchHelper.Callback callback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP   | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, 0) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                // TODO: Implement
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {}
        };
        mTouchHelper = new ItemTouchHelper(callback);
        mTouchHelper.attachToRecyclerView(mSubjects);

        return rootView;
    }

    public void loadData(final Activity c) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mSemester != 0) {
                    DatabaseHandler db = new DatabaseHandler(c);
                    if (mPrefs == null) {
                        mPrefs = c.getSharedPreferences("Kantidroid", Context.MODE_PRIVATE);
                    }
                    List<Fach> subjects = db.getAllFaecherSorted(mSemester, mPrefs.getInt("sorting", 0));
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

    @Override
    public void onItemClick(int position) {
        Intent i = new Intent(getActivity(), FachviewActivity.class);
        i.putExtra("semester", mSemester);
        i.putExtra("id", mAdapter.getData().get(position).getID());
        startActivity(i);
    }

    @Override
    public void onLongItemClick(int position) {
        Intent i = new Intent(getActivity(), EditMarkDialog.class);
        i.putExtra("id", mAdapter.getData().get(position).getID());
        i.putExtra("semester", mSemester);
        startActivityForResult(i, 1);
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mTouchHelper.startDrag(viewHolder);
    }
}
