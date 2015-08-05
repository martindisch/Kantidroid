package com.martin.kantidroid.ui.timetable;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.martin.kantidroid.R;
import com.martin.kantidroid.logic.Util;

public class TimetableActivity extends AppCompatActivity implements View.OnClickListener {

    private TextInputLayout mTilClass;
    private EditText mClass;
    private Button mDownload;
    private View mLayoutImage, mLayoutList;
    private RecyclerView mDownloads;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        mTilClass = (TextInputLayout) findViewById(R.id.tilClass);
        mTilClass.setErrorEnabled(true);
        mClass = (EditText) findViewById(R.id.etClass);
        mDownload = (Button) findViewById(R.id.bDownload);
        mLayoutImage = findViewById(R.id.llImage);
        mLayoutList = findViewById(R.id.flList);
        mDownloads = (RecyclerView) findViewById(R.id.rvDownloads);

        if (/*!hasDownloads*/ true) {
            mLayoutList.setVisibility(View.INVISIBLE);
        } else {
            mLayoutImage.setVisibility(View.INVISIBLE);
        }
        Glide.with(this).load(R.drawable.kanti).into((ImageView) findViewById(R.id.ivNothing));

        mDownload.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        String classUrl = Util.getClassUrl(mClass.getText().toString());
        if (!classUrl.contentEquals("error")) {
            mTilClass.setError(null);
            // Try downloading
        }
        else {
            mTilClass.setError(getString(R.string.timetable_error_noclass));
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Let's not reload the main activity
        onBackPressed();
        return true;
    }
}
