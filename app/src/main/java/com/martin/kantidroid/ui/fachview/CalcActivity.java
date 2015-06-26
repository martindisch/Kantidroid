package com.martin.kantidroid.ui.fachview;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.EditText;

import com.martin.kantidroid.R;
import com.martin.kantidroid.logic.DatabaseHandler;
import com.martin.kantidroid.ui.util.DividerItemDecoration;

public class CalcActivity extends AppCompatActivity {

    private EditText mWeight;
    private RecyclerView mMarks;
    private int mSemester, mId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fachview_calc_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        mWeight = (EditText) findViewById(R.id.etMark);
        mMarks = (RecyclerView) findViewById(R.id.rvMarks);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mMarks.setLayoutManager(layoutManager);
        mMarks.setHasFixedSize(true);
        mMarks.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        mSemester = getIntent().getIntExtra("semester", -1);
        mId = getIntent().getIntExtra("id", -1);

        mMarks.setAdapter(new CalcAdapter(this, new DatabaseHandler(this).getFach(mId), mSemester));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return true;
    }
}
