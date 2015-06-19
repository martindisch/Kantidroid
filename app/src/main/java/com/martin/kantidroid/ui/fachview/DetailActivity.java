package com.martin.kantidroid.ui.fachview;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.martin.kantidroid.R;
import com.martin.kantidroid.logic.DatabaseHandler;
import com.martin.kantidroid.logic.Fach;
import com.martin.kantidroid.logic.Util;

public class DetailActivity extends AppCompatActivity {

    private int mId, mSemester, mType;
    private Fach fach;
    private TextView mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fachview_detail_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        //ab.setHomeAsUpIndicator(R.drawable.ic_close);
        ab.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        mId = intent.getIntExtra("id", -1);
        mSemester = intent.getIntExtra("semester", -1);
        mType = intent.getIntExtra("type", -1);
        fach = new DatabaseHandler(this).getFach(mId);

        CollapsingToolbarLayout ctl = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        ctl.setTitle(fach.getName());
        ctl.setBackgroundColor(Util.getLight(this, fach.getColor()));

        mData = (TextView) findViewById(R.id.tvData);
        ((TextView) findViewById(R.id.tvSem)).setText(mSemester + getString(R.string.n_semester));
        ImageView backdrop = (ImageView) findViewById(R.id.backdrop);
        if (mType == 1) {
            backdrop.setImageResource(R.drawable.ic_school);
            findViewById(R.id.llBackground).setBackgroundColor(getResources().getColor(R.color.red_dark));
            mData.setBackgroundColor(getResources().getColor(R.color.red_light));

            String real;
            if (mSemester == 1) {
                real = fach.getMathAverage1();
            } else {
                real = fach.getMathAverage2();
            }
            if (real.contentEquals("")) {
                real = "-";
            }
            mData.setText(real);
        } else {
            backdrop.setImageResource(R.drawable.ic_timetable);
            findViewById(R.id.llBackground).setBackgroundColor(getResources().getColor(R.color.green_dark));
            mData.setBackgroundColor(getResources().getColor(R.color.green_light));

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
}
