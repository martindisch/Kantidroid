package com.martin.kantidroid.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.martin.kantidroid.R;
import com.martin.kantidroid.logic.Fach;

import java.util.ArrayList;
import java.util.List;

public class SubjectsFragment extends Fragment implements SubjectsAdapter.OnClickListener {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private RecyclerView mSubjects;
    private SubjectsAdapter mAdapter;
    private EditText mName, mShort;
    private CheckBox mCounts;
    private Button mAdd;

    public static SubjectsFragment newInstance(int sectionNumber) {
        SubjectsFragment fragment = new SubjectsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public SubjectsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_subjects, container, false);
        mSubjects = (RecyclerView) rootView.findViewById(R.id.rvSubjects);
        mName = (EditText) rootView.findViewById(R.id.et_subj_name);
        mShort = (EditText) rootView.findViewById(R.id.et_subj_short);
        mCounts = (CheckBox) rootView.findViewById(R.id.cbCounts);
        mAdd = (Button) rootView.findViewById(R.id.bAdd);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mSubjects.setLayoutManager(layoutManager);
        mSubjects.setHasFixedSize(true);
        mSubjects.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));

        List<Fach> entries = new ArrayList<Fach>();
        mAdapter = new SubjectsAdapter(entries, this);
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
    public void onClick(View v, int position) {

    }

    @Override
    public void onLongClick(View v, int position) {

    }
}
