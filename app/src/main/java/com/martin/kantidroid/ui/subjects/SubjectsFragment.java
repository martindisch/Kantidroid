package com.martin.kantidroid.ui.subjects;

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
import com.martin.kantidroid.ui.util.DividerItemDecoration;
import com.martin.kantidroid.ui.main.MainActivity;

import java.util.List;

public class SubjectsFragment extends Fragment implements SubjectsAdapter.OnClickListener, View.OnClickListener {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private RecyclerView mSubjects;
    private SubjectsAdapter mAdapter;
    private ImageButton mFab;
    private int mEditingIndex = -1;

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
        View rootView = inflater.inflate(R.layout.subjects_fragment, container, false);
        mSubjects = (RecyclerView) rootView.findViewById(R.id.rvSubjects);
        mFab = (ImageButton) rootView.findViewById(R.id.fab);
        mFab.setOnClickListener(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mSubjects.setLayoutManager(layoutManager);
        mSubjects.setHasFixedSize(true);
        mSubjects.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));

        DatabaseHandler db = new DatabaseHandler(getActivity());
        List<Fach> subjects = db.getAllFaecherSorted(getActivity(), 1, 0);
        mAdapter = new SubjectsAdapter(subjects, this);
        mSubjects.setAdapter(mAdapter);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((MainActivity) getActivity()).updateTitle(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }

    @Override
    public void onItemClick(View v, int position) {
        if (v.getTag() == 1) {
            mEditingIndex = position;
            Fach current = mAdapter.getData().get(position);
            Intent i = new Intent(getActivity(), EditDialog.class);
            i.putExtra("name", current.getName());
            i.putExtra("short", current.getShort());
            i.putExtra("color", current.getColor());
            i.putExtra("counts", current.getPromotionsrelevant());
            i.putExtra("kontAv", current.getKont());
            startActivityForResult(i, 1);
        }
        else if (v.getTag() == 0) {
            DatabaseHandler db = new DatabaseHandler(getActivity());
            db.deleteFach(mAdapter.getData().get(position));
            mAdapter.remove(position);
        }
    }

    @Override
    public void onItemLongClick(View v, int position) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab:
                Intent i = new Intent(getActivity(), EditDialog.class);
                startActivityForResult(i, 1);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            DatabaseHandler db = new DatabaseHandler(getActivity());
            switch (resultCode) {
                case 1:
                    Fach subject = new Fach(data.getStringExtra("name"), data.getStringExtra("short"), data.getStringExtra("color"), data.getStringExtra("counts"), data.getStringExtra("kontAv"));
                    long id = db.addFach(subject);
                    subject.setID(id);
                    mAdapter.add(subject);
                    break;
                case 2:
                    Fach changed = mAdapter.getData().get(mEditingIndex);
                    changed.setName(data.getStringExtra("name"));
                    changed.setShort(data.getStringExtra("short"));
                    changed.setColor(data.getStringExtra("color"));
                    changed.setPromotionsrelevant(data.getStringExtra("counts"));
                    changed.setKont(data.getStringExtra("kontAv"));
                    db.updateFach(changed);
                    mAdapter.update(changed, mEditingIndex);
                    break;
            }
            mEditingIndex = -1;
        }
    }
}