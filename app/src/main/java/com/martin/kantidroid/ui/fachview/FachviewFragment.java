package com.martin.kantidroid.ui.fachview;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.martin.kantidroid.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FachviewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FachviewFragment extends Fragment {

    private static final String ARG_PARAM1 = "id";
    private static final String ARG_PARAM2 = "semester";

    // TODO: Rename and change types of parameters
    private int mId;
    private int mSemester;

    public static FachviewFragment newInstance(int id, int semester) {
        FachviewFragment fragment = new FachviewFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, id);
        args.putInt(ARG_PARAM2, semester);
        fragment.setArguments(args);
        return fragment;
    }

    public FachviewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mId = getArguments().getInt(ARG_PARAM1);
            mSemester = getArguments().getInt(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fachview_fragment, container, false);
    }


}
