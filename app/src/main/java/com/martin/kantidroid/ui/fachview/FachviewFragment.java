package com.martin.kantidroid.ui.fachview;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.martin.kantidroid.R;
import com.martin.kantidroid.logic.DatabaseHandler;
import com.martin.kantidroid.logic.Fach;
import com.martin.kantidroid.logic.PromoCheck;
import com.martin.kantidroid.logic.Util;

public class FachviewFragment extends Fragment {

    private static final String ARG_PARAM1 = "id";
    private static final String ARG_PARAM2 = "semester";

    private int mId;
    private int mSemester;

    private TextView mPromo, mRealAverage, mMathAverage, mKont;
    private View mShowMarks, mShowKont;
    private Fach mFach;

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
        View rootView = inflater.inflate(R.layout.fachview_fragment, container, false);
        mPromo = (TextView) rootView.findViewById(R.id.tvPromo);
        mRealAverage = (TextView) rootView.findViewById(R.id.tvZeugnis);
        mMathAverage = (TextView) rootView.findViewById(R.id.tvSchnitt);
        mKont = (TextView) rootView.findViewById(R.id.tvKontUsage);

        mShowMarks = rootView.findViewById(R.id.rlShowMarks);
        mShowMarks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), DetailActivity.class);
                i.putExtra("id", mId);
                i.putExtra("semester", mSemester);
                startActivityForResult(i, 1);
            }
        });
        mShowKont = rootView.findViewById(R.id.rlShowKont);
        mShowKont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), DetailActivity.class);
                i.putExtra("id", mId);
                i.putExtra("semester", mSemester);
                startActivityForResult(i, 1);
            }
        });

        updateData();
        return rootView;
    }

    private void updateData() {
        mFach = new DatabaseHandler(getActivity()).getFach(mId);

        if (mFach.getPromotionsrelevant().contentEquals("true")) {
            mPromo.setText(getString(R.string.promotion));
        }
        else {
            mPromo.setText(getString(R.string.no_promotion));
        }

        if (mSemester == 1) {
            mRealAverage.setText(mFach.getRealAverage1());
            mMathAverage.setText(mFach.getMathAverage1());
            mKont.setText(Util.formatKont(mFach.getKont1(), mFach.getKont()));
        }
        else if (mSemester == 2) {
            mRealAverage.setText(mFach.getRealAverage2());
            mMathAverage.setText(mFach.getMathAverage2());
            mKont.setText(Util.formatKont(mFach.getKont1(), mFach.getKont()));
        }
    }

}
