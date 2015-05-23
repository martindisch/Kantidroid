package com.martin.kantidroid.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.martin.kantidroid.R;
import com.martin.kantidroid.logic.DatabaseHandler;
import com.martin.kantidroid.logic.Fach;

import java.util.ArrayList;
import java.util.List;

public class SubjectsFragment extends Fragment implements SubjectsAdapter.OnClickListener, View.OnClickListener {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private RecyclerView mSubjects;
    private SubjectsAdapter mAdapter;
    private EditText mName, mShort;
    private CheckBox mCounts;
    private Button mAdd;
    private View mColor;

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
        mColor = rootView.findViewById(R.id.vColor);
        mColor.setOnClickListener(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mSubjects.setLayoutManager(layoutManager);
        mSubjects.setHasFixedSize(true);
        mSubjects.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));

        // TODO: Request focus on first EditText

        DatabaseHandler db = new DatabaseHandler(getActivity());
        List<Fach> subjects = db.getAllFaecher(getActivity(), 1);
        mAdapter = new SubjectsAdapter(subjects, this);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.vColor:
                Intent i = new Intent(getActivity(), ColorPickerDialog.class);
                startActivityForResult(i, 1);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 1) {
            mColor.setTag(data.getStringExtra("color"));
            mColor.setBackgroundColor(Color.parseColor(data.getStringExtra("color")));
        }
    }
}
