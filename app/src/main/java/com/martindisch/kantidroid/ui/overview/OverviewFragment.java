package com.martindisch.kantidroid.ui.overview;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.martindisch.kantidroid.R;
import com.martindisch.kantidroid.logic.DatabaseHandler;
import com.martindisch.kantidroid.logic.Fach;
import com.martindisch.kantidroid.ui.main.MainActivity;

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

        mSubjects.addItemDecoration(new MarginDecoration(getActivity()));
        mSubjects.setHasFixedSize(true);

        loadData();
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
    }

    @Override
    public void onItemLongClick(View v, int position) {
    }

    public void loadData() {
        DatabaseHandler db = new DatabaseHandler(getActivity());
        // TODO: Enable sorting
        List<Fach> subjects = db.getAllFaecherSorted(getActivity(), 1, 0);
        mAdapter = new OverviewAdapter(getActivity(), subjects, this);
        mSubjects.setAdapter(mAdapter);
    }
}
