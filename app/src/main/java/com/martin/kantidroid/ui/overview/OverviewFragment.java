package com.martin.kantidroid.ui.overview;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.martin.kantidroid.R;
import com.martin.kantidroid.logic.DatabaseHandler;
import com.martin.kantidroid.logic.Fach;
import com.martin.kantidroid.ui.main.MainActivity;
import com.martin.kantidroid.ui.subjects.EditDialog;
import com.martin.kantidroid.ui.subjects.SubjectsAdapter;
import com.martin.kantidroid.ui.util.DividerItemDecoration;

import java.util.List;

public class OverviewFragment extends Fragment implements OverviewAdapter.OnClickListener {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private RecyclerView mSubjects;
    private OverviewAdapter mAdapter;

    public static OverviewFragment newInstance(int sectionNumber) {
        OverviewFragment fragment = new OverviewFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
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
        mSubjects = (RecyclerView) rootView.findViewById(R.id.rvSubjects);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mSubjects.setLayoutManager(layoutManager);
        mSubjects.setHasFixedSize(true);

        DatabaseHandler db = new DatabaseHandler(getActivity());
        // TODO: Enable sorting
        List<Fach> subjects = db.getAllFaecherSorted(getActivity(), 1, 0);
        mAdapter = new OverviewAdapter(subjects, this);
        mSubjects.setAdapter(mAdapter);
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }

    @Override
    public void onItemClick(View v, int position) {
    }

    @Override
    public void onItemLongClick(View v, int position) {
    }
}
