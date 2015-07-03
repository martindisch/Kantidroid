package com.martin.kantidroid.ui.subjects;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.martin.kantidroid.R;
import com.martin.kantidroid.logic.DatabaseHandler;
import com.martin.kantidroid.logic.Fach;
import com.martin.kantidroid.ui.util.DividerItemDecoration;

import java.util.List;

public class SubjectsFragment extends Fragment implements SubjectsAdapter.OnClickListener, View.OnClickListener {

    private SubjectsAdapter mAdapter;
    private int mEditingIndex = -1;

    public static SubjectsFragment newInstance() {
        return new SubjectsFragment();
    }

    public SubjectsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mEditingIndex = savedInstanceState.getInt("mEditingIndex");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("mEditingIndex", mEditingIndex);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.subjects_fragment, container, false);

        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        final ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle(R.string.subjects);

        RecyclerView mSubjects = (RecyclerView) rootView.findViewById(R.id.rvSubjects);
        FloatingActionButton mFab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        mFab.setOnClickListener(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mSubjects.setLayoutManager(layoutManager);
        mSubjects.setHasFixedSize(true);
        mSubjects.addItemDecoration(new DividerItemDecoration(getActivity(), null, false));

        DatabaseHandler db = new DatabaseHandler(getActivity());
        List<Fach> subjects = db.getAllFaecherSorted(1, 0);
        mAdapter = new SubjectsAdapter(getActivity(), subjects, this);
        mSubjects.setAdapter(mAdapter);
        return rootView;
    }

    @Override
    public void onItemClick(View v, final int position) {
        mEditingIndex = position;
        Fach current = mAdapter.getData().get(position);
        Intent i = new Intent(getActivity(), EditDialog.class);
        i.putExtra("name", current.getName());
        i.putExtra("short", current.getShort());
        i.putExtra("color", current.getColor());
        i.putExtra("counts", current.getPromotionsrelevant());
        i.putExtra("kontAv", current.getKontAvailable());
        startActivityForResult(i, 1);
    }

    @Override
    public void onItemLongClick(View v, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.delete_question);
        builder.setNegativeButton(R.string.no, null);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                DatabaseHandler db = new DatabaseHandler(getActivity());
                db.deleteFach(mAdapter.getData().get(position));
                mAdapter.remove(position);
            }
        });
        builder.show();
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
                    int id = db.addFach(subject);
                    subject.setID(id);
                    mAdapter.add(subject);
                    break;
                case 2:
                    Fach changed = mAdapter.getData().get(mEditingIndex);
                    changed.setName(data.getStringExtra("name"));
                    changed.setShort(data.getStringExtra("short"));
                    changed.setColor(data.getStringExtra("color"));
                    changed.setPromotionsrelevant(data.getStringExtra("counts"));
                    changed.setKontAvailable(data.getStringExtra("kontAv"));
                    db.updateFach(changed);
                    mAdapter.update(changed, mEditingIndex);
                    break;
                case 3:
                    db.deleteFach(mAdapter.getData().get(mEditingIndex));
                    mAdapter.remove(mEditingIndex);
                    break;
            }
            mEditingIndex = -1;
        }
    }
}
