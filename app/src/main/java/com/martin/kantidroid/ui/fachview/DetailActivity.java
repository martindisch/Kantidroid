package com.martin.kantidroid.ui.fachview;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.martin.kantidroid.R;
import com.martin.kantidroid.logic.DatabaseHandler;
import com.martin.kantidroid.logic.Fach;
import com.martin.kantidroid.logic.Util;
import com.martin.kantidroid.ui.subjects.SubjectsAdapter;
import com.martin.kantidroid.ui.util.DividerItemDecoration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DetailActivity extends AppCompatActivity implements GradesAdapter.OnClickListener {

    private int mId, mSemester, mType;
    private Fach fach;
    private TextView mData;
    private RecyclerView mItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fachview_detail_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        mId = intent.getIntExtra("id", -1);
        mSemester = intent.getIntExtra("semester", -1);
        mType = intent.getIntExtra("type", -1);
        fach = new DatabaseHandler(this).getFach(mId);

        CollapsingToolbarLayout ctl = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        ctl.setTitle(fach.getName());
        ctl.setBackgroundColor(Util.getLight(this, fach.getColor()));

        mItems = (RecyclerView) findViewById(R.id.rvContents);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mItems.setLayoutManager(layoutManager);
        mItems.setHasFixedSize(true);
        mItems.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        mData = (TextView) findViewById(R.id.tvData);
        ((TextView) findViewById(R.id.tvSem)).setText(mSemester + getString(R.string.n_semester));
        ImageView backdrop = (ImageView) findViewById(R.id.backdrop);
        if (mType == 1) {
            backdrop.setImageResource(R.drawable.ic_school);
            findViewById(R.id.llBackground).setBackgroundColor(getResources().getColor(R.color.red_dark));
            mData.setBackgroundColor(getResources().getColor(R.color.red_light));
        } else {
            backdrop.setImageResource(R.drawable.ic_timetable);
            findViewById(R.id.llBackground).setBackgroundColor(getResources().getColor(R.color.green_dark));
            mData.setBackgroundColor(getResources().getColor(R.color.green_light));
        }

        updateInfo();

        FloatingActionButton mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mType == 1) {
                    Intent i = new Intent(DetailActivity.this, EditMarkDialog.class);
                    i.putExtra("id", mId);
                    i.putExtra("semester", mSemester);
                    startActivityForResult(i, 1);
                }
            }
        });
    }

    private void updateInfo() {
        if (mType == 1) {
            String real;
            if (mSemester == 1) {
                real = fach.getMathAverage1();
                mItems.setAdapter(new GradesAdapter(this, new ArrayList<String>(Arrays.asList(fach.getNoten1().split("\n"))), this));
            } else {
                real = fach.getMathAverage2();
                mItems.setAdapter(new GradesAdapter(this, new ArrayList<String>(Arrays.asList(fach.getNoten2().split("\n"))), this));
            }
            if (real.contentEquals("")) {
                real = "-";
            }
            mData.setText(real);
        }
        else {
            if (mSemester == 1) {
                mData.setText(Util.formatKont(fach.getKont1(), fach.getKont()));
            } else {
                mData.setText(Util.formatKont(fach.getKont2(), fach.getKont()));
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Let's not go back to the the main activity
        onBackPressed();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 1) {
            fach = new DatabaseHandler(this).getFach(mId);
            updateInfo();
            setResult(1);
        }
    }

    @Override
    public void onItemClick(View v, int position) {

    }

    @Override
    public void onItemLongClick(View v, int position) {

    }
}
